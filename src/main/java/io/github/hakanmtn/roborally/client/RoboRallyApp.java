package io.github.hakanmtn.roborally.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

public class RoboRallyApp extends Application {

  private Socket socket = new Socket();
  private OutputStreamWriter writer;
  private boolean connected;

  @Override
  public void start(Stage stage) {
    TextArea chatHistory = new TextArea();
    chatHistory.setEditable(false);
    chatHistory.setWrapText(true);

    TextField messageInput = new TextField();
    messageInput.setPromptText("Enter a message");

    Button sendButton = new Button("Send");
    sendButton.setDisable(true);

    sendButton.setOnAction(
        event -> {
          String message = messageInput.getText();

          if (message.isBlank()) {
            return;
          }
          sendButton.setDisable(true);
          messageInput.setEditable(false);

          Thread.startVirtualThread(
              () -> {
                try {
                  ChatClient.sendMessage(writer, message);

                  Platform.runLater(
                      () -> {
                        messageInput.clear();
                        messageInput.setEditable(true);
                        sendButton.setDisable(!connected);
                        messageInput.requestFocus();
                      });
                } catch (IOException e) {
                  Platform.runLater(
                      () -> {
                        chatHistory.appendText("Send failed: " + e.getMessage() + "\n");
                        messageInput.setEditable(true);
                      });
                }
              });
        });

    messageInput.setOnAction(event -> sendButton.fire());

    HBox inputRow = new HBox(8, messageInput, sendButton);
    HBox.setHgrow(messageInput, Priority.ALWAYS);

    BorderPane root = new BorderPane();
    root.setPadding(new Insets(12));
    root.setCenter(chatHistory);
    root.setBottom(inputRow);
    BorderPane.setMargin(inputRow, new Insets(8, 0, 0, 0));

    stage.setTitle("RoboRally");
    stage.setScene(new Scene(root, 640, 400));
    stage.show();

    Thread connectionThread =
        new Thread(
            () -> {
              try {
                socket.connect(new InetSocketAddress("localhost", 5500));
                writer = new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8);

                Platform.runLater(() -> {
                  connected = true;
                  sendButton.setDisable(false);
                });

                var reader =
                    new BufferedReader(
                        new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));

                while (true) {
                  String message = reader.readLine();

                  if (message == null) {
                    break;
                  }
                  Platform.runLater(() -> chatHistory.appendText(message + "\n"));
                }

                Platform.runLater(
                    () -> {
                      connected = false;
                      sendButton.setDisable(true);
                      chatHistory.appendText("Disconnected from server.\n");
                    });

              } catch (IOException e) {
                Platform.runLater(
                    () -> {
                      String prefix = connected ? "Connection lost: " : "Connection failed: ";
                      connected = false;
                      sendButton.setDisable(true);
                      chatHistory.appendText(prefix + e.getMessage() + "\n");
                    });
              }
            },
            "server-connection");
    connectionThread.start();
  }

  @Override
  public void stop() throws IOException {
    socket.close();
  }

  public static void main(String[] args) {
    launch(args);
  }
}
