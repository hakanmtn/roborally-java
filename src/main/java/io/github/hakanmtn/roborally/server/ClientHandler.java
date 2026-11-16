package io.github.hakanmtn.roborally.server;

import com.google.gson.JsonObject;
import io.github.hakanmtn.roborally.protocol.*;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ClientHandler implements Runnable {

  private final Socket clientSocket;
  private final OutputStreamWriter writer;
  private final ChatServer server;
  private static final JsonMessageCodec CODEC = new JsonMessageCodec();

  public ClientHandler(Socket clientSocket, ChatServer server) throws IOException {
    this.clientSocket = clientSocket;
    this.writer = new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8);
    this.server = server;
  }

  @Override
  public void run() {
    ScheduledExecutorService heartbeat = Executors.newSingleThreadScheduledExecutor();
    Integer clientId = null;
    try (Socket socket = clientSocket) {
      var reader =
          new BufferedReader(
              new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));

      HelloClientBody body = new HelloClientBody("Version 0.1");
      ProtocolMessage<HelloClientBody> greeting = new ProtocolMessage<>("HelloClient", body);

      String json = CODEC.encode(greeting);

      sendMessage(json);

      String firstLine = reader.readLine();
      if (firstLine == null) {
        return;
      }

      ProtocolMessage<JsonObject> reply = CODEC.decode(firstLine);

      if (!"HelloServer".equals(reply.messageType())) {
        System.err.println("Expected HelloServer, received: " + reply.messageType());
        return;
      }

      HelloServerBody clientBody = CODEC.decodeBody(reply.messageBody(), HelloServerBody.class);
      System.out.println("Client group: " + clientBody.group());

      if (!"Version 0.1".equals(clientBody.protocol())) {
        ErrorBody errorBody = new ErrorBody("Unsupported protocol version. Expected Version 0.1.");
        sendMessage(CODEC.encode(new ProtocolMessage<>("Error", errorBody)));
        return;
      }

      clientId = server.assignClientId();
      WelcomeBody welcomeBody = new WelcomeBody(clientId);
      ProtocolMessage<WelcomeBody> welcome = new ProtocolMessage<>("Welcome", welcomeBody);
      sendMessage(CODEC.encode(welcome));

      for(PlayerAddedBody player : server.getSelectedPlayers()) {
        ProtocolMessage<PlayerAddedBody> message = new ProtocolMessage<>("PlayerAdded", player);
        sendMessage(CODEC.encode(message));
      }

      String aliveMessage = CODEC.encode(new ProtocolMessage<>("Alive", new AliveBody()));

      heartbeat.scheduleAtFixedRate(
          () -> {
            try {
              sendMessage(aliveMessage);
            } catch (IOException e) {
              disconnect();
            }
          },
          5,
          5,
          TimeUnit.SECONDS);

      String message;
      while ((message = reader.readLine()) != null) {
        System.out.println("Received: " + message);

        if (CODEC.isAliveMessage(message)) {
          continue;
        }

        ProtocolMessage<JsonObject> incoming;

        try {
          incoming = CODEC.decode(message);
        } catch (IllegalArgumentException e) {
          server.broadcast(message);
          continue;
        }

        if ("PlayerValues".equals(incoming.messageType())) {
          PlayerValuesBody selection =
              CODEC.decodeBody(incoming.messageBody(), PlayerValuesBody.class);
          handlePlayerValues(clientId, selection);
          continue;
        }

        ErrorBody error = new ErrorBody("Unsupported message type: " + incoming.messageType());
        sendMessage(CODEC.encode(new ProtocolMessage<>("Error", error)));
      }
    } catch (IOException e) {
      System.err.println(
          "Connection error for " + clientSocket.getRemoteSocketAddress() + ": " + e.getMessage());
    } finally {
      heartbeat.shutdownNow();
      if(clientId != null) {
        server.removePlayerSelection(clientId);
      }
      server.removeClient(this);
    }
  }

  public synchronized void sendMessage(String message) throws IOException {
    writer.write(message + "\n");
    writer.flush();
  }

  private void handlePlayerValues(int clientId, PlayerValuesBody selection) throws IOException {
    if (!server.trySelectFigure(clientId, selection)) {
      ErrorBody errorBody = new ErrorBody("Figure already taken.");
      sendMessage(CODEC.encode(new ProtocolMessage<>("Error", errorBody)));
      return;
    }

    PlayerAddedBody added = new PlayerAddedBody(clientId, selection.name(), selection.figure());
    server.broadcast(CODEC.encode(new ProtocolMessage<>("PlayerAdded", added)));
  }

  void disconnect() {
    try {
      clientSocket.close();
    } catch (IOException e) {
      System.err.println("Could not close client socket: " + e.getMessage());
    }
  }
}
