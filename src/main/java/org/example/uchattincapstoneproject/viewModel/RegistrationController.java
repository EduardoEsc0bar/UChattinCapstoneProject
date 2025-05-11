package org.example.uchattincapstoneproject.viewModel;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.uchattincapstoneproject.model.DB;
import org.example.uchattincapstoneproject.model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RegistrationController {

    @FXML private TextField firstNameTF, lastNameTF, preferredNameTF, phoneNumberTF, emailTF, specifyGenderTF, dobTF, specifyPronounsTF, createUsernameTF, createPasswordTF;
    @FXML private ComboBox<String> pronounsCB, genderCB;
    @FXML private Button toCreateAvatarButton, backBTN;
    @FXML private AnchorPane root;
    @FXML private Pane createAccountPane;

    private static final String DB_URL = "jdbc:mysql://commapp.mysql.database.azure.com:3306/communication_app";
    private static final String DB_USER = "commapp_db_user";
    private static final String DB_PASSWORD = "farm9786$";

    @FXML
    public void initialize() {
        System.out.println("RegistrationController Initialized");

        genderCB.getItems().addAll("Male", "Female", "Non-binary", "Other");
        pronounsCB.getItems().addAll("He/Him/His", "She/Her/Hers", "They/Them/Theirs", "Other");

        genderCB.setOnAction(event -> specifyGenderTF.setDisable(!genderCB.getValue().equals("Other")));
        pronounsCB.setOnAction(event -> specifyPronounsTF.setDisable(!pronounsCB.getValue().equals("Other")));

        toCreateAvatarButton.setOnAction(event -> navigateToAvatarSelection());
        backBTN.setOnAction(event -> navigateToEntranceScreen());


        // Ensure content stays centered dynamically
        Platform.runLater(() -> {
            UIUtilities.centerContent(root, createAccountPane);
            root.widthProperty().addListener((obs, oldVal, newVal) -> UIUtilities.centerContent(root, createAccountPane));
            root.heightProperty().addListener((obs, oldVal, newVal) -> UIUtilities.centerContent(root, createAccountPane));
        });
    }


    @FXML
    private void navigateToAvatarSelection() {
        try{
            System.out.println("Navigating to Avatar Selection...");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/createAvatarScreen.fxml"));
            Parent avatarScreen = loader.load();

            CreateAvatarController controller = loader.getController();
            controller.setUser(collectUserData());

            Scene avatarScene = new Scene(avatarScreen);
            Stage stage = (Stage) root.getScene().getWindow();
            stage.setScene(avatarScene);
            stage.setTitle("Choose Your Avatar");
        } catch (Exception e) {
            System.err.println("error loading avatar screen " + e.getMessage());
            e.printStackTrace();
        }
    }

    private User collectUserData(){
        return new User(firstNameTF.getText().trim(),
        lastNameTF.getText().trim(),
        preferredNameTF.getText().trim(),
        phoneNumberTF.getText().trim(),
        emailTF.getText().trim(),
        dobTF.getText().trim(), genderCB.getValue(),
        specifyGenderTF.getText().trim(),
        pronounsCB.getValue(),
        specifyPronounsTF.getText().trim(),
        createUsernameTF.getText().trim(),
        createPasswordTF.getText().trim()
        );
    }


    @FXML
    private void navigateToEntranceScreen(){
        System.out.println("Navigating to Entrance Screen...");
        UIUtilities.navigateToScreen("/views/entranceScreen.fxml", root.getScene(), false);
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}