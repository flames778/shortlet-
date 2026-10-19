package com.shortlet;

import com.shortlet.config.TomcatEmbeddedServer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class DesktopLauncher extends Application {

    public static void main(String[] args) {
        // Start Tomcat in a background thread
        Thread serverThread = new Thread(() -> {
            try {
                TomcatEmbeddedServer.main(args);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        serverThread.setDaemon(true);
        serverThread.start();

        // Launch the JavaFX GUI
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        WebView webView = new WebView();

        // Add a small delay to allow the embedded Tomcat server to bind to the port
        try {
            System.out.println("Waiting for local server to warm up...");
            Thread.sleep(1500); 
        } catch (InterruptedException ignored) {}
        
        // Set a user agent to ensure modern rendering
        webView.getEngine().setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");

        // Point to our local server
        webView.getEngine().load("http://localhost:8080");

        BorderPane root = new BorderPane(webView);
        Scene scene = new Scene(root, 1280, 850);

        primaryStage.setTitle("Shortlet Platform - Desktop");
        primaryStage.setScene(scene);
        
        // Handle clean shutdown
        primaryStage.setOnCloseRequest(e -> {
            Platform.exit();
            System.exit(0);
        });

        primaryStage.show();
    }
}