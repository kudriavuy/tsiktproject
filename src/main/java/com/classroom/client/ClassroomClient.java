package com.classroom.client;

import com.classroom.client.controller.MainController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ClassroomClient extends Application {
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Classroom App");
        primaryStage.setWidth(1200);
        primaryStage.setHeight(800);

        MainController mainController = new MainController(primaryStage);
        Scene scene = new Scene(mainController.getRoot(), 1200, 800);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

