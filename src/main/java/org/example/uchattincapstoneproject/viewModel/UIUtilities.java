package org.example.uchattincapstoneproject.viewModel;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;

public class UIUtilities {

    /**
     * center contents is a method used in the controllers to make sure all content is cenetered on the screen
     * so user can display full screen
     * @param root - root pane
     * @param content - content pane
     */
     public static void centerContent(Pane root, Pane content) {
        Platform.runLater(() -> {
            if(root.getWidth() > 0 && root.getHeight() > 0) {
                double rootW = root.getWidth();
                double rootH = root.getHeight();
                double contentW = content.getPrefWidth();
                double contentH = content.getPrefHeight();

                content.setLayoutX((rootW - contentW) / 2);
                content.setLayoutY((rootH - contentH) / 2);
            }else {
                System.out.println("root dimensions are not set yet");
            }
        });


    }

    public static void navigateToScreen(String fxmlPath, Scene currentScene, boolean isSmallScreen) {
        try {

            FXMLLoader loader = new FXMLLoader(UIUtilities.class.getResource(fxmlPath));
            Parent newRoot = loader.load();

            Scene newScene = new Scene(newRoot);
            Stage stage = (Stage) currentScene.getWindow();
            stage.setScene(newScene);

            //maintain stage dimensions
            if(isSmallScreen){
                stage.setWidth(800);
                stage.setHeight(600);
            }else{
                stage.setWidth(1000);
                stage.setHeight(800);
            }

            //Fade out effect
            FadeTransition fadeIn = new FadeTransition(Duration.millis(500), newRoot);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        } catch (IOException e) {
            System.err.println("Error loading FXML: " + fxmlPath);
            e.printStackTrace();
        }
    }

}
