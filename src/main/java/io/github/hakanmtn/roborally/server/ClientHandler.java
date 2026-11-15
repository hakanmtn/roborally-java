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

      int clientId = server.assignClientId();
      WelcomeBody welcomeBody = new WelcomeBody(clientId);
      ProtocolMessage<WelcomeBody> welcome = new ProtocolMessage<>("Welcome", welcomeBody);
      sendMessage(CODEC.encode(welcome));

      String aliveMessage = CODEC.encode(new ProtocolMessage<>("Alive", new AliveBody()));

      heartbeat.scheduleAtFixedRate( () -> {
        try {
          sendMessage(aliveMessage);
        }catch (IOException e) {
          disconnect();
        }
      },5,5, TimeUnit.SECONDS);

      String message;
      while ((message = reader.readLine()) != null) {
        System.out.println("Received: " + message);

        if (CODEC.isAliveMessage(message)) {
          continue;
        }
        server.broadcast(message);
      }
    } catch (IOException e) {
      System.err.println(
          "Connection error for " + clientSocket.getRemoteSocketAddress() + ": " + e.getMessage());
    } finally {
      heartbeat.shutdownNow();
      server.removeClient(this);
    }
  }

  public synchronized void sendMessage(String message) throws IOException {
    writer.write(message + "\n");
    writer.flush();
  }



  void disconnect() {
    try {
      clientSocket.close();
    } catch (IOException e) {
      System.err.println("Could not close client socket: " + e.getMessage());
    }
  }
}
