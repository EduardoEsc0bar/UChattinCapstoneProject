package org.example.uchattincapstoneproject.viewModel;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SplashScreenController {

    @FXML
    private AnchorPane splashScreenID;

    @FXML
    public void initialize() {
        PauseTransition pause = new PauseTransition(Duration.seconds(3));
        pause.setOnFinished(event -> switchToRegistrationScreen());
        pause.play();
    }
    private void switchToRegistrationScreen() {
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("RegistrationScreen.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = (Stage) splashScreenID.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
