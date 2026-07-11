package com.solidtoxic.gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * JavaFX application entry point.
 * Run with: mvn javafx:run  (from the gui/ directory)
 */
public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        MainWindow root = new MainWindow();
        Scene scene = new Scene(root, 1100, 720);
        primaryStage.setTitle("SolidToxic — Toxic Waste Management System");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(500);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
