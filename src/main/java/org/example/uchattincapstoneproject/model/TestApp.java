package org.example.uchattincapstoneproject.model;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class TestApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load the FXML file for the avatar selection screen
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/userProfile.fxml"));
        Parent root = loader.load();

        // Set the scene and show the window
        Scene scene = new Scene(root);
        primaryStage.setTitle("registration screen");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        Util.getInstance().setCurrentUser(DB.getInstance().queryUserByName("joenunez@farmingdale.edu"));
        Util.getInstance().getCurrentUser().setPreferredName(null);
        launch(args);
    }
}
