package com.techfix.techfixapp;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class App extends Application {

    public static final double WINDOW_WIDTH = 1000;
    public static final double WINDOW_HEIGHT = 700;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/Login.fxml"));
        
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        
        primaryStage.setTitle("TechFix Workspace");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false); 
        
        primaryStage.show();
        
        Platform.runLater(() -> {
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            double centerX = (screenBounds.getWidth() - primaryStage.getWidth()) / 2;
            double centerY = (screenBounds.getHeight() - primaryStage.getHeight()) / 2;
            
            primaryStage.setX(centerX);
            primaryStage.setY(centerY);
        });
    }

    public static void main(String[] args) {
        launch();
    }
}