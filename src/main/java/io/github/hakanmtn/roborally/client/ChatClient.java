package io.github.hakanmtn.roborally.client;

import com.google.gson.JsonObject;
import io.github.hakanmtn.roborally.protocol.*;

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

      String firstLine = reader.readLine();
      if (firstLine == null) {
        System.out.println("Server disconnected before sending HelloClient.");
        return;
      }

      JsonMessageCodec codec = new JsonMessageCodec();
      ProtocolMessage<JsonObject> greeting = codec.decode(firstLine);

      if (!"HelloClient".equals(greeting.messageType())) {
        System.err.println("Expected HelloClient, received" + greeting.messageType());
        return;
      }

      HelloClientBody body =
              codec.decodeBody(greeting.messageBody(), HelloClientBody.class);
      System.out.println("Server protocol: " + body.protocol());

      HelloServerBody clientBody = new HelloServerBody("RoboRally", false, "Version 0.1");
      ProtocolMessage<HelloServerBody> reply = new ProtocolMessage<>("HelloServer", clientBody);
      sendMessage(writer, codec.encode(reply));

      String responseLine = reader.readLine();
      if (responseLine == null) {
        System.out.println("Server disconnected during handshake.");
        return;
      }

      ProtocolMessage<JsonObject> response = codec.decode(responseLine);

      if("Error".equals(response.messageType())) {
        ErrorBody error = codec.decodeBody(response.messageBody(), ErrorBody.class);
        System.err.println("Server error: " + error.error());
        return;
      }

      if(!"Welcome".equals(response.messageType())) {
        System.err.println("Expected Welcome, received" + response.messageType());
        return;
      }

      WelcomeBody welcomeBody = codec.decodeBody(response.messageBody(), WelcomeBody.class);
      System.out.println("My client ID: " + welcomeBody.clientID());

      Thread listenerThread =
          new Thread(
              () -> {
                try {

                  String message;
                  while ((message = reader.readLine()) != null) {

                    if(codec.isAliveMessage(message)) {
                      sendMessage(writer,message);
                      continue;
                    }

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

        sendMessage(writer, message);
      }
    }
  }

  static void sendMessage(OutputStreamWriter writer, String message) throws IOException {
    writer.write(message + "\n");
    writer.flush();
  }
}
