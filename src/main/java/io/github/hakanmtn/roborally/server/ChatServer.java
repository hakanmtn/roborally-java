package io.github.hakanmtn.roborally.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChatServer {

  private final CopyOnWriteArrayList<ClientHandler> activeClients = new CopyOnWriteArrayList<>();

  public static void main(String[] args) throws IOException {
    new ChatServer().start();
  }

  private void start() throws IOException {
    try (ServerSocket serverSocket = new ServerSocket(5500)) {
      System.out.println("Listening on port 5500...");

      while (true) {
        Socket clientSocket = serverSocket.accept();
        System.out.println("Client connected: " + clientSocket.getRemoteSocketAddress());

        var handler = new ClientHandler(clientSocket, this);
        activeClients.add(handler);
        new Thread(handler).start();
      }
    }
  }

  void removeClient(ClientHandler handler) {
    if (activeClients.remove(handler)) {
      System.out.println("Client disconnected: Active clients: " + activeClients.size());
    }
  }

  void broadcast(String message) {
    for (ClientHandler client : activeClients) {

      try {
        client.sendMessage(message);
      } catch (IOException e) {
        System.err.println("Could not send to client: " + e.getMessage());
        client.disconnect();
      }
    }
  }
}
