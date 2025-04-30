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
import org.example.uchattincapstoneproject.model.MainController;
import org.example.uchattincapstoneproject.model.User;

public class LogInController {
    //----------------- Panes --------------------------------------------------------------------------------------------------
    @FXML
    private StackPane LoginScreenStackPane, customizationStackPane;
    @FXML
    private Pane LoginScreenPane, createAvatarPane, createAccountPane, forgotPasswordPane;
    @FXML
    private TilePane avatarStyleTP, hairTP, clothingTP, facialFeaturesTP, skinTP, accessoriesTP, backgroundTP;
    @FXML
    private AnchorPane displayAvatarPane, customizationPane;
    //------------Login Screen FXMLs -------------------------------------------------------------------------------------
    @FXML
    private TextField usernameTextField, passwordTextField;
    @FXML
    private Button logInButton, forgotPasswordButton, exitButton, createAccountButton;

    //----------------------- Create Account FXMLs --------------------------------------------------
    @FXML
    private TextField firstNameTF, lastNameTF, dobTF, preferredNameTF, phoneNumberTF,createUsernameTF, createPasswordTF,
            specifyGenderTF, specifyPronounsTF, emailTF;
    @FXML
    private ComboBox<String> genderCB, pronounsCB;
    @FXML
    private Button toCreateAvatarButton;


    //--------------------------- Forgot Password FXMLs ----------------------------------------------------------------
    @FXML
    private TextField enterNewPasswordTF, confirmPasswordTF;
    @FXML
    private Button confirmPasswordButton;

    private Stage stage;
    private int authenticatedUserID = -1;
    User user;
    // ------------------------------------------------- end of FXML and members --------------------------------------

    @FXML
    private void initialize() {
        logInButton.setOnAction((ActionEvent event) -> handleLogin());
        forgotPasswordButton.setOnAction((ActionEvent event) -> navigateToForgotPassword());
        createAccountButton.setOnAction((ActionEvent event) -> navigateToRegistration());
    }

    @FXML
    private void handleLogin() {
        String username = usernameTextField.getText();
        String password = passwordTextField.getText();

        if(username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Login Error", "Please enter all fields");
            return;
        }

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

    private boolean validateDatabaseCredentials(String username, String password) {
        try(Connection connection = Database.connect()){
            String query = "SELECT id, password FROM users WHERE username = ? OR email = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, password);

            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                String storePasswordHash = resultSet.getString("password");
                if(HandlePasswordHash.checkPassword(password, storePasswordHash)){
                    authenticatedUserID = resultSet.getInt("id");
                    return true;
                }
            }
        }catch(SQLException e){
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "An error occurred while validating credentials");
        }
        return false;
    }

    @FXML
    private void navigateToMainScreen() {
        try{
            FadeTransition fadeout = new FadeTransition(Duration.seconds(1));
            fadeout.setFromValue(1.0);
            fadeout.setToValue(0.0);
            fadeout.setOnFinished(event -> {
                try{
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

    @FXML
    private void navigateToRegistration(){
        try{
            FadeTransition fadeout = new FadeTransition(Duration.seconds(1));
            fadeout.setFromValue(1.0);
            fadeout.setToValue(0.0);
            fadeout.setOnFinished(event -> {
                try{
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/registrationScreen.fxml"));
                }
            })
        }
    }

}
