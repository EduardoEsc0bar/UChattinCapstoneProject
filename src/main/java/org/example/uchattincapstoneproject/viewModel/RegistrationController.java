package org.example.uchattincapstoneproject.viewModel;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RegistrationController {

    @FXML
    private TextField firstNameTF;
    @FXML
    private TextField lastNameTF;
    @FXML
    private TextField preferredNameTF;
    @FXML
    private TextField phoneNumberTF;
    @FXML
    private TextField emailTF;
    @FXML
    private TextField dobTF;
    @FXML
    private TextField specifyGenderTF;
    @FXML
    private TextField specifyPronounsTF;
    @FXML
    private TextField createUsernameTF;
    @FXML
    private TextField createPasswordTF;
    @FXML
    private ComboBox<String> genderCB;
    @FXML
    private ComboBox<String> pronounsCB;
    @FXML
    private Button toCreateAvatarButton;
    @FXML
    private Button backBTN;
    @FXML
    private AnchorPane root;
    @FXML
    private Pane createAccountPane;
    private Parent entranceScreen;

    private static final String DB_URL = "jdbc:mysql://commapp.mysql.database.azure.com:3306/communication_app";
    private static final String DB_USER = "commapp_db_user";
    private static final String DB_PASSWORD = "farm9786$";

    @FXML
    public void initialize() {
        System.out.println("RegistrationController initialized!");

        //Populate Gender Dropdown
        genderCB.getItems().addAll("Male", "Female", "Non-binary", "Other");

        //Populate Pronouns Dropdown
        pronounsCB.getItems().addAll("He/Him/His", "She/Her/Hers", "They/Them/Theirs", "Other");

        // Show Specify Gender Field if "Other" is Selected
        genderCB.setOnAction(event -> specifyGenderTF.setDisable(!genderCB.getValue().equals("Other")));

        //Show Specify Pronouns Field if "Other" is Selected
        pronounsCB.setOnAction(event -> specifyPronounsTF.setDisable(!pronounsCB.getValue().equals("Other")));

        //navigates to create an avatar screen
        toCreateAvatarButton.setOnAction(event -> navigateToCreateAvatarScreen());

        //return to entrance screen
        backBTN.setOnAction(event -> navigateToEntranceScreen());

        //makes sure content is centered
        Platform.runLater(() -> {
            UIUtilities.centerContent(root, createAccountPane);

            root.widthProperty().addListener((observable, oldValue, newValue) -> {UIUtilities.centerContent(root, createAccountPane);});
            root.heightProperty().addListener((observable, oldValue, newValue) -> {UIUtilities.centerContent(root, createAccountPane);});
        });
    }

    @FXML
    private void registerUser() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "INSERT INTO users (first_name, last_name, preferred_name, gender, specified_pronouns, dob, phone_number, email, username, password_hash) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, firstNameTF.getText());
            stmt.setString(2, lastNameTF.getText());
            stmt.setString(3, preferredNameTF.getText().isEmpty() ? null : preferredNameTF.getText());
            stmt.setString(4, genderCB.getValue().equals("Other") ? specifyGenderTF.getText() : genderCB.getValue());
            stmt.setString(5, pronounsCB.getValue().equals("Other") ? specifyPronounsTF.getText() : pronounsCB.getValue());
            stmt.setString(6, dobTF.getText());
            stmt.setString(7, phoneNumberTF.getText());
            stmt.setString(8, emailTF.getText());
            stmt.setString(9, createUsernameTF.getText());
            stmt.setString(10, createPasswordTF.getText());

            stmt.executeUpdate();
            System.out.println("User registered successfully!");

            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle("Registration Success");
            successAlert.setHeaderText(null);
            successAlert.setContentText("Account created successfully!");
            successAlert.showAndWait();

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }

    @FXML
    private void navigateToEntranceScreen() {
        UIUtilities.navigateToScreen("/views/entranceScreen.fxml", root.getScene(), false);
        UIUtilities.centerContent(root,(Pane)entranceScreen.lookup("#contentPane"));
    }

    @FXML
    private void navigateToCreateAvatarScreen() {
        UIUtilities.navigateToScreen("/views/createAvatarScreen.fxml", root.getScene(), false);
    }

    public boolean registerUser (String firstName, String lastName, String preferredName, String gender, String
            pronouns, String dob, String phone, String email, String username, String password){
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "INSERT INTO users (first_name, last_name, preferred_name, gender, specified_pronouns, dob, phone_number, email, username, password_hash) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, preferredName.isEmpty() ? null : preferredName);
            stmt.setString(4, gender);
            stmt.setString(5, pronouns);
            stmt.setString(6, dob);
            stmt.setString(7, phone);
            stmt.setString(8, email);
            stmt.setString(9, username);
            stmt.setString(10, password);

            int rowsAffected = stmt.executeUpdate();
            System.out.println("User registered successfully!");
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            return false;
        }
    }

    //Main Method for Testing Database Connection
    public static void main (String[]args){
        RegistrationController controller = new RegistrationController();

        boolean success = controller.registerUser(
                "John", "Doe", "Johnny", "Male", "He/Him/His",
                "1990/01/01", "123-456-7890", "john.doe@example.com",
                "johndoe123", "securepassword"
        );

        if (success) {
            System.out.println("User registration test successful!");
        } else {
            System.out.println("User registration test failed.");
        }
    }
}



