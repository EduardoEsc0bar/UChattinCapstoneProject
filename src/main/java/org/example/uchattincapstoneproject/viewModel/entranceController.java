package org.example.uchattincapstoneproject.viewModel;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.uchattincapstoneproject.model.DB;
import org.example.uchattincapstoneproject.model.User;
import org.example.uchattincapstoneproject.model.Util;

import java.io.*;

public class entranceController {
    @FXML private TextField usernameField, visiblePasswordTextField;
    @FXML private PasswordField passwordField;
    @FXML private Button logInBTN, createAccountBTN;
    @FXML private Hyperlink forgotPasswordBTN;
    @FXML private AnchorPane root;
    @FXML private ImageView communicationIV, visibilityOn, visibilityOff;
    @FXML private Pane contentPane;

    Util utilities = Util.getInstance();
    private DB dbInstance;
    final String DB_URL = "jdbc:mysql://commapp.mysql.database.azure.com:3306/communication_app";
    final String USERNAME = "commapp_db_user";
    final String PASSWORD = "farm9786$";

    @FXML
    private void initialize() {
        System.out.println("entrance controller initialized");

        // Initialize DB instance
        dbInstance = DB.getInstance();

        //password is hidden by default
        visiblePasswordTextField.setVisible(false);
        visiblePasswordTextField.setManaged(false);

        //set visibility image state
        visibilityOn.setVisible(false);
        visibilityOff.setVisible(true);

        //button actions
        logInBTN.setOnAction((ActionEvent event) -> handleLogin());
        forgotPasswordBTN.setOnAction((ActionEvent event) -> navigateToForgetPassword());
        createAccountBTN.setOnAction((ActionEvent event) -> navigateToRegistrationScreen());

        //set logo
        Image logo = new Image(getClass().getResource("/imagesIcon/communication.png").toExternalForm());

        //bind content pane to stay centered
        Platform.runLater(()->{
            UIUtilities.centerContent(root, contentPane); //force center in startup
            root.widthProperty().addListener((observable, oldValue, newValue) -> UIUtilities.centerContent(root, contentPane));
            root.heightProperty().addListener((observable,oldValue, newValue) -> UIUtilities.centerContent(root, contentPane));

        });
        verifySceneInitialization();
    }

    private void verifySceneInitialization(){
        Platform.runLater(() -> {
            if(root.getScene() == null){
                System.out.println("scene is null at initialization");
            }else{
                System.out.println("scene is initialized");
            }
        });
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if(username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Login Error", "Please enter all fields");
            return;
        }

        System.out.println("attempting to login user: " + username);

        // Use the same authentication method that worked in DBTest
        User authenticatedUser = dbInstance.authenticateUser(username, password);

        if(authenticatedUser != null) {
            showAlert(Alert.AlertType.INFORMATION, "Login Successful", "Welcome " + username + "!");
            utilities.setCurrentUser(authenticatedUser);
            navigateToMainScreen();
        }else{
            System.out.println("authentication failed");
            showAlert(Alert.AlertType.ERROR, "Login failed", "Invalid username or password. Please try again.");
        }
    }

    @FXML
    private void navigateToForgetPassword(){
        System.out.println("attempting to navigate to forget password");
        UIUtilities.navigateToScreen("/views/forgotPasswordScreen.fxml", root.getScene(), true);
        UIUtilities.centerContent(root, contentPane);
    }

    @FXML
    private void navigateToRegistrationScreen(){
        System.out.println("attempting to navigate to registration screen");
        UIUtilities.navigateToScreen("/views/registrationScreen.fxml", root.getScene(), false);
    }

    @FXML
    private void navigateToMainScreen(){
        System.out.println("attempting to navigate to main screen");
        UIUtilities.navigateToScreen("/views/mainScreen.fxml", root.getScene(), false);
        UIUtilities.centerContent(root, contentPane);
    }

    @FXML
    private void togglePasswordVisibility(){
        if(passwordField.isVisible()){
            //show password
            visiblePasswordTextField.setText(passwordField.getText());
            visiblePasswordTextField.setVisible(true);
            visiblePasswordTextField.setManaged(true);
            passwordField.setVisible(false);
            passwordField.setManaged(false);

            //update visibility icons
            visibilityOn.setVisible(true);
            visibilityOff.setVisible(false);
        }else{
            //hide password
            passwordField.setText(visiblePasswordTextField.getText());
            passwordField.setVisible(true);
            passwordField.setManaged(true);
            visiblePasswordTextField.setVisible(false);
            visiblePasswordTextField.setManaged(false);

            //update icons
            visibilityOn.setVisible(false);
            visibilityOff.setVisible(true);
        }
    }

    //display alerts
    private void showAlert(Alert.AlertType alertType, String title, String message){
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

}