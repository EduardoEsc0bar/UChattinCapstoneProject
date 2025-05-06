package org.example.uchattincapstoneproject.viewModel;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.sql.*;

public class LoginController {
    //-------------------------- UI ELEMENTS -----------------------------------------
    @FXML
    private TextField usernameTextField, passwordTextField;
    @FXML
    private Button logInButton, forgotPasswordButton, exitButton, createAccountButton;

    private Stage stage;
    private int authenticatedUserID = -1;
    private static final String DB_URL = "jdbc:mysql://uchattin-csc311.mysql.database.azure.com:3306/uchattin-userinfo";
    private static final String DB_USER = "username";
    private static final String DB_PASSWORD = "password";

    // -------------------------- END OF UI ELEMENTS --------------------------------------

    @FXML
    private void initialize() {
        System.out.println("Login Controller initialized.");

        logInButton.setOnAction(event -> handleLogin());
        forgotPasswordButton.setOnAction(event -> navigateToForgotPassword());
        createAccountButton.setOnAction(event -> navigateToRegistration());
        exitButton.setOnAction(actionEvent -> exitButtonClicked());

        stage = (Stage) logInButton.getScene().getWindow();
    }


    @FXML
    private void handleLogin() {
        String username = usernameTextField.getText();
        String password = passwordTextField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Login Error", "Please enter all fields.");
            return;
        }

        System.out.println("Attempting login for user: " + username);

        if (validateDatabaseCredentials(username, password)) {
            showAlert(Alert.AlertType.INFORMATION, "Login Successful", "Welcome " + username + "!");
            navigateToMainScreen();
        } else {
            showAlert(Alert.AlertType.ERROR, "Login Failed", "Incorrect username or password. Please try again.");
        }
    }


    private boolean validateDatabaseCredentials(String username, String password) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT id, password_hash FROM Users WHERE username = ? OR email = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, username);
                statement.setString(2, username);

                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    String storedPasswordHash = resultSet.getString("password_hash");
                    if (BCrypt.checkpw(password, storedPasswordHash)) {
                        authenticatedUserID = resultSet.getInt("id");
                        System.out.println("User authenticated with ID: " + authenticatedUserID);
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "An error occurred while validating credentials.");
        }
        return false;
    }


    @FXML
    private void navigateToMainScreen() {
        try {
            System.out.println("Navigating to main screen.");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/mainScreen.fxml"));
            BorderPane mainScreen = loader.load();

            MainViewController mainViewController = loader.getController();
            mainViewController.setUsername(usernameTextField.getText());
            mainViewController.setUserID(authenticatedUserID);

            Stage currentStage = (Stage) logInButton.getScene().getWindow();
            Scene mainScene = new Scene(mainScreen, 800, 600);
            currentStage.setScene(mainScene);
            currentStage.setMaximized(true);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "An error occurred while navigating to the main screen.");
        }
    }


    @FXML
    private void navigateToRegistration() {
        try {
            System.out.println("Navigating to registration.");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/registrationScreen.fxml"));
            AnchorPane registrationScreen = loader.load();
            Scene registrationScene = new Scene(registrationScreen, 800, 600);
            stage.setScene(registrationScene);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "An error occurred while navigating to the registration screen.");
        }
    }


    @FXML
    private void navigateToForgotPassword() {
        try {
            System.out.println("Navigating to forgot password screen.");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/forgotPasswordScreen.fxml"));
            AnchorPane forgotPasswordScreen = loader.load();
            Scene forgotPasswordScene = new Scene(forgotPasswordScreen, 800, 600);
            stage.setScene(forgotPasswordScene);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "An error occurred while navigating to the forgot password screen.");
        }
    }

    @FXML
    private void exitButtonClicked() {
        System.out.println("Exiting application.");
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}