package org.example.uchattincapstoneproject.viewModel;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
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
import org.mindrot.jbcrypt.BCrypt;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class entranceController {
    @FXML
    private TextField usernameField, passwordField; // Changed from PasswordField to TextField to match FXML
    @FXML
    private Button logInBTN, createAccountBTN;
    @FXML
    private Hyperlink forgotPasswordBTN;
    @FXML
    private AnchorPane root;
    @FXML
    private ImageView communicationIV;
    @FXML
    private Pane contentPane;

    private int authenticatedUserID = -1;
    private String TEST_USERNAME = "testuser";
    private String TEST_PASSWORD = "testpassword123";
    private DB dbInstance;

    @FXML
    private void initialize() {
        System.out.println("entrance controller initialized");

        // Initialize DB instance
        dbInstance = DB.getInstance();

        //button actions
        logInBTN.setOnAction((ActionEvent event) -> handleLogin());
        forgotPasswordBTN.setOnAction((ActionEvent event) -> UIUtilities.navigateToScreen("/views/forgotPasswordScreen.fxml", root.getScene(), true));
        createAccountBTN.setOnAction((ActionEvent event) -> UIUtilities.navigateToScreen("/views/registrationScreen.fxml", root.getScene(), false));

        //set logo
        Image logo = new Image(getClass().getResource("/imagesIcon/communication.png").toExternalForm());

        //bind content pane to stay centered
        Platform.runLater(()->{
            UIUtilities.centerContent(root, contentPane); //force center in startup
            root.widthProperty().addListener((observable, oldValue, newValue) -> UIUtilities.centerContent(root, contentPane));
            root.heightProperty().addListener((observable,oldValue, newValue) -> UIUtilities.centerContent(root, contentPane));

        });
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if(username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Login Error", "Please enter all fields");
            return;
        }

        System.out.println("attempting to login user: " + username);

        // Use the same authentication method that worked in DBTest
        DB dbInstance = DB.getInstance();
        User authenticatedUser = dbInstance.authenticateUser(username, password);

        if(authenticatedUser != null) {
            try {
                showAlert(Alert.AlertType.INFORMATION, "Login Successful", "Welcome " + username + "!");
                Platform.runLater(()->{
                    if(root.getScene() == null) {
                        UIUtilities.navigateToScreen("/views/mainScreen.fxml", root.getScene(), false);
                    }else{
                        System.out.println("scene not initialized");
                    }
                });

            } catch(Exception e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Navigation Error",
                        "Error navigating to main screen: " + e.getMessage());
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Login failed", "Invalid username or password. Please try again.");
        }
    }

    private boolean validateDatabaseCredentials(String username, String password) {
        System.out.println("Attempting login with username: " + username);

        DB dbInstance = DB.getInstance();
        User authenticatedUser = dbInstance.authenticateUser(username, password);

        if (authenticatedUser != null) {
            System.out.println("Authentication successful for: " + username);
            authenticatedUserID = 1; // Or get actual ID
            return true;
        }

        System.out.println("Authentication failed for: " + username);
        return false;
    }

    //navigates to main screen
    @FXML
    private void navigateToMainScreen() {
        try{
            FadeTransition fadeout = new FadeTransition(Duration.seconds(1));
            fadeout.setFromValue(1.0);
            fadeout.setToValue(0.0);
            fadeout.setNode(usernameField.getScene().getRoot());
            fadeout.setOnFinished(event -> {
                try{
                    System.out.println("navigating to main screen");
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/mainScreen.fxml"));
                    AnchorPane mainScreen = loader.load(); // Using AnchorPane based on previous error

                    MainViewController mainViewController = loader.getController();
                    User currentUser = dbInstance.getCurrentUser();

                    if (currentUser != null) {
                        mainViewController.setUsername(currentUser.getUsername());
                        // If MainViewController has a setUserID method, set the ID here
                        // mainViewController.setUserID(authenticatedUserID);
                    } else {
                        // Fallback to using text field value if no user in DB singleton
                        mainViewController.setUsername(usernameField.getText());
                    }

                    Stage currentStage = (Stage) logInBTN.getScene().getWindow();
                    Scene mainScene = new Scene(mainScreen, 800, 600);
                    currentStage.setScene(mainScene);
                    currentStage.setMaximized(true);

                    FadeTransition fadein = new FadeTransition(Duration.seconds(1));
                    fadein.setFromValue(0.0);
                    fadein.setToValue(1.0);
                    fadein.setNode(mainScreen);
                    fadein.play();
                }catch(IOException e){
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Navigation Error",
                            "Error loading main screen: " + e.getMessage());
                }
            });
            fadeout.play();
        }catch(Exception e){
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error",
                    "Error preparing transition to main screen: " + e.getMessage());
        }
    }

    //navigate to registration
    @FXML
    private void navigateToRegistration(){
        try{
            System.out.println("navigating to registration");
            FadeTransition fadeout = new FadeTransition(Duration.seconds(1));
            fadeout.setFromValue(1.0);
            fadeout.setToValue(0.0);
            fadeout.setNode(usernameField.getScene().getRoot());
            fadeout.setOnFinished(event -> {
                try{
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/registrationScreen.fxml"));
                    AnchorPane registrationScreen = loader.load();
                    Scene registrationScene = new Scene(registrationScreen, 800, 600);

                    Stage stage = (Stage) logInBTN.getScene().getWindow();
                    stage.setScene(registrationScene);
                    stage.setTitle("Create Account");

                    FadeTransition fadein = new FadeTransition(Duration.seconds(1));
                    fadein.setFromValue(0.0);
                    fadein.setToValue(1.0);
                    fadein.setNode(registrationScreen);
                    fadein.play();
                }catch(IOException e){
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Navigation Error",
                            "Error loading registration screen: " + e.getMessage());
                }
            });
            fadeout.play();
        }catch(Exception e){
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error",
                    "Error preparing transition to registration screen: " + e.getMessage());
        }
    }

    //navigating to forget password screen
    @FXML
    private void navigateToForgotPassword(){
        try{
            System.out.println("navigating to forgot password");
            FadeTransition fadeout = new FadeTransition(Duration.seconds(1));
            fadeout.setFromValue(1.0);
            fadeout.setToValue(0.0);
            fadeout.setNode(usernameField.getScene().getRoot());
            fadeout.setOnFinished(event -> {
                try{
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/forgotPasswordScreen.fxml"));
                    AnchorPane forgotPasswordScreen = loader.load();
                    Scene forgotPasswordScene = new Scene(forgotPasswordScreen, 800, 600);

                    Stage stage = (Stage) logInBTN.getScene().getWindow();
                    stage.setScene(forgotPasswordScene);
                    stage.setTitle("Forgot Password");

                    FadeTransition fadein = new FadeTransition(Duration.seconds(1));
                    fadein.setFromValue(0.0);
                    fadein.setToValue(1.0);
                    fadein.setNode(forgotPasswordScreen);
                    fadein.play();
                }catch(IOException e){
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Navigation Error",
                            "Error loading forgot password screen: " + e.getMessage());
                }
            });
            fadeout.play();
        }catch(Exception e){
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error",
                    "Error preparing transition to forgot password screen: " + e.getMessage());
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