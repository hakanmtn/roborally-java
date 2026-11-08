package io.github.hakanmtn.roborally.client;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class RoboRallyApp extends Application {

    @Override
    public void start(Stage stage) {
        Scene scene = new Scene(new StackPane(), 640, 400);

        stage.setTitle("RoboRally");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
