package org.example.uchattincapstoneproject.viewModel;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

import javafx.util.Duration;
import org.example.uchattincapstoneproject.model.HandlePasswordHash;
/**
 * Controller class for managing user login interactions in the JavaFX UI.
 */
public class LoginController {
    //--------------------------UI ELEMENTS -----------------------------------------
    /**
     * TextField for entering password
     * TextField for entering username
     * */
    @FXML
    private TextField usernameTextField, passwordTextField;
    /**
     * Button to initiate login
     * Button to exit program
     *
     * */
    @FXML
    private Button logInButton, forgotPasswordButton, exitButton, createAccountButton;
    private Stage stage;
    private int authenticatedUserID = -1;
    private String TEST_USERNAME = "testuser";
    private String TEST_PASSWORD = "testpassword123";

    // ---------------------------- END OF UI ELEMENTS --------------------------------------

    //initialize login controller
    @FXML
    private void initialize() {
        System.out.println("log in controller initialized");

        //button actions
        logInButton.setOnAction((ActionEvent event) -> handleLogin());
        forgotPasswordButton.setOnAction((ActionEvent event) -> navigateToForgotPassword());
        createAccountButton.setOnAction((ActionEvent event) -> navigateToRegistration());

        //ensure stage is initialized
        stage = (Stage) logInButton.getScene().getWindow();
    }

    //handle user login
    @FXML
    private void handleLogin() {
        String username = usernameTextField.getText();
        String password = passwordTextField.getText();

        if(username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Login Error", "Please enter all fields");
            return;
        }

        System.out.println("attempting to login user: " + username);

        if(validateDatabaseCredentials(username,password)){
            try{
                showAlert(Alert.AlertType.INFORMATION, "Login Successful", "Welcome " + username + "!");
                navigateToMainScreen();
            }catch(Exception e){
                e.printStackTrace();
            }

        }else{
            showAlert(Alert.AlertType.ERROR, "Login failed", "Please try again");
        }
    }

    //validate user credentials from database
    private boolean validateDatabaseCredentials(String username, String password) {
        /*try(Connection connection = Database.connect()){ //upload database class
            if (connection == null) {
                System.out.println("database connection failed");
                return false;
            }
            String query = "SELECT id, password FROM users WHERE username = ? OR email = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, username);

            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                String storePasswordHash = resultSet.getString("password");
                if(HandlePasswordHash.checkPassword(password, storePasswordHash)){
                    authenticatedUserID = resultSet.getInt("id");
                    System.out.println("user authentication with id " + authenticatedUserID);
                    return true;
                }
            }
        }catch(SQLException e){
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "An error occurred while validating credentials");
        }
        return false;

         */
        System.out.println("simulated log in attempt for user " + username);
        if(TEST_USERNAME.equals(username) && TEST_PASSWORD.equals(password)){
            authenticatedUserID = 1;
            System.out.println("user authenticated with id: " + authenticatedUserID);
            return true;
        }else{
            System.out.println("invalid credentials");
            return false;
        }
    }

    //navigates to main screen
    @FXML
    private void navigateToMainScreen() {
        try{
            FadeTransition fadeout = new FadeTransition(Duration.seconds(1));
            fadeout.setFromValue(1.0);
            fadeout.setToValue(0.0);
            fadeout.setOnFinished(event -> {
                try{
                    System.out.println("navigating to main screen");
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/mainScreen.fxml"));
                    BorderPane mainScreen = loader.load();

                    MainViewController mainViewController = loader.getController();
                    mainViewController.setUsername(usernameTextField.getText());
                    mainViewController.setUserID(authenticatedUserID);

                    Stage currentStage = (Stage)logInButton.getScene().getWindow();
                    Scene mainScene = new Scene(mainScreen, 800, 600);
                    currentStage.setScene(mainScene);
                    currentStage.setMaximized(true);

                    FadeTransition fadein = new FadeTransition(Duration.seconds(1));
                    fadein.setFromValue(0.0);
                    fadein.setToValue(1.0);
                    fadein.play();
                }catch(IOException e){
                    e.printStackTrace();
                }
            });
            fadeout.play();
        }catch(Exception e){
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "An error occurred while navigating to main screen");

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
            fadeout.setOnFinished(event -> {
                try{
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/registrationScreen.fxml"));
                    AnchorPane registrationScreen = loader.load();
                    Scene registrationScene = new Scene(registrationScreen, 800, 600);
                    stage.setScene(registrationScene);

                    FadeTransition fadein = new FadeTransition(Duration.seconds(1));
                    fadein.setFromValue(0.0);
                    fadein.setToValue(1.0);
                    fadein.play();
                }catch(IOException e){
                    e.printStackTrace();
                }
            });
            fadeout.play();
        }catch(Exception e){
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "An error occurred while navigating to registration screen");
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
            fadeout.setOnFinished(event -> {
                try{
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/forgotPasswordScreen.fxml"));
                    AnchorPane forgotPasswordScreen = loader.load();
                    Scene forgotPasswordScene = new Scene(forgotPasswordScreen, 800, 600);
                    stage.setScene(forgotPasswordScene);

                    FadeTransition fadein = new FadeTransition(Duration.seconds(1));
                    fadein.setFromValue(0.0);
                    fadein.setToValue(1.0);
                    fadein.play();
                }catch(IOException e){
                    e.printStackTrace();
                }
            });
            fadeout.play();
        }catch(Exception e){
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "An error occurred while navigating to forgot password screen");
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
