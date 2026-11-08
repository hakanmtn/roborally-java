package io.github.hakanmtn.roborally.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ChatClient {

  public static void main(String[] args) throws IOException {
    try (Socket socket = new Socket("localhost", 5500)) {
      System.out.println("Connected to server: " + socket.getRemoteSocketAddress());

      var writer = new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8);

      var reader =
          new BufferedReader(
              new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));

      Thread listenerThread =
          new Thread(
              () -> {
                try {
                  String message;
                  while ((message = reader.readLine()) != null) {
                    System.out.println("Server: " + message);
                  }
                } catch (IOException e) {
                  if (!socket.isClosed()) {
                    System.err.println("Could not read from server: " + e.getMessage());
                  }
                }
              },
              "server-listener");
      listenerThread.start();

      BufferedReader console =
          new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));

      System.out.println("Enter a message, or /quit to disconnect.");

      String message;
      while ((message = console.readLine()) != null) {
        if (message.equals("/quit")) {
          break;
        }

        writer.write(message + "\n");
        writer.flush();
      }
    }
  }
}
