package io.github.hakanmtn.roborally.server;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ClientHandler implements Runnable {

  private final Socket clientSocket;
  private final OutputStreamWriter writer;
  private final ChatServer server;

  public ClientHandler(Socket clientSocket, ChatServer server) throws IOException {
    this.clientSocket = clientSocket;
    this.writer = new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8);
    this.server = server;
  }

  @Override
  public void run() {
    try (Socket socket = clientSocket) {
      var reader =
          new BufferedReader(
              new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));

      sendMessage("Welcome to RoboRally!");

      String message;
      while ((message = reader.readLine()) != null) {
        System.out.println("Received: " + message);
        server.broadcast(message);
      }
    } catch (IOException e) {
      System.err.println(
          "Connection error for " + clientSocket.getRemoteSocketAddress() + ": " + e.getMessage());
    } finally {
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
    }catch (IOException e) {
      System.err.println("Could not close client socket: " + e.getMessage());
    }
  }
}
