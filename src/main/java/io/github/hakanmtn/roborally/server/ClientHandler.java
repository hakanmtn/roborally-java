package io.github.hakanmtn.roborally.server;

import com.google.gson.JsonObject;
import io.github.hakanmtn.roborally.protocol.*;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ClientHandler implements Runnable {

  private final Socket clientSocket;
  private final OutputStreamWriter writer;
  private final ChatServer server;
  private static final JsonMessageCodec CODEC = new JsonMessageCodec();
  private volatile Integer clientId;

  public ClientHandler(Socket clientSocket, ChatServer server) throws IOException {
    this.clientSocket = clientSocket;
    this.writer = new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8);
    this.server = server;
  }

  @Override
  public void run() {
    ScheduledExecutorService heartbeat = Executors.newSingleThreadScheduledExecutor();

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

      for (PlayerAddedBody player : server.getSelectedPlayers()) {
        ProtocolMessage<PlayerAddedBody> message = new ProtocolMessage<>("PlayerAdded", player);
        sendMessage(CODEC.encode(message));
      }

      for (PlayerStatusBody status : server.getPlayerStatuses()) {
        ProtocolMessage<PlayerStatusBody> message = new ProtocolMessage<>("PlayerStatus", status);
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

        if ("SetStatus".equals(incoming.messageType())) {
          SetStatusBody statusBody = CODEC.decodeBody(incoming.messageBody(), SetStatusBody.class);
          handleSetStatus(clientId, statusBody);
          continue;
        }

        if ("MapSelected".equals(incoming.messageType())) {
          MapSelectedBody selection =
              CODEC.decodeBody(incoming.messageBody(), MapSelectedBody.class);
          handleMapSelected(clientId, selection);
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

      Integer previousSelectorId = server.getMapSelectorId();
      if (clientId != null) {
        server.removePlayerSelection(clientId);
      }
      server.removeClient(this);

      Integer currentSelectorId = server.getMapSelectorId();
      if(!Objects.equals(previousSelectorId, currentSelectorId)) {
        server.requestMapSelection();
      }
    }
  }

  public synchronized void sendMessage(String message) throws IOException {
    writer.write(message + "\n");
    writer.flush();
  }

  Integer getClientId() {
    return clientId;
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

  private void handleSetStatus(int clientId, SetStatusBody status) throws IOException {

    Integer previousSelectorId = server.getMapSelectorId();
    if (!server.trySetReady(clientId, status.ready())) {
      ErrorBody errorBody = new ErrorBody("Select a figure before setting your ready status.");
      sendMessage(CODEC.encode(new ProtocolMessage<>("Error", errorBody)));
      return;
    }

    PlayerStatusBody updatedStatus = new PlayerStatusBody(clientId, status.ready());
    server.broadcast(CODEC.encode(new ProtocolMessage<>("PlayerStatus", updatedStatus)));

    Integer currentSelectorId = server.getMapSelectorId();
    if (!Objects.equals(previousSelectorId, currentSelectorId)) {
      server.requestMapSelection();
    }
  }

  private void handleMapSelected(int clientId, MapSelectedBody selection) throws IOException {

    if (!server.trySelectMap(clientId, selection.map())) {
      ErrorBody error =
          new ErrorBody("Map selection rejected. Check your selection turn and map name.");
      sendMessage(CODEC.encode(new ProtocolMessage<>("Error", error)));
      return;
    }

    server.broadcast(CODEC.encode(new ProtocolMessage<>("MapSelected", selection)));
  }

  void disconnect() {
    try {
      clientSocket.close();
    } catch (IOException e) {
      System.err.println("Could not close client socket: " + e.getMessage());
    }
  }
}
