package org.example.uchattincapstoneproject.viewModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.example.uchattincapstoneproject.model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RegistrationController {
    @FXML
    private TextField firstNameTF, lastNameTF, dobTF, emailTF, preferredNameTF, phoneNumberTF,
            createUsernameTF, createPasswordTF, specifyGenderTF, specifyPronounsTF, avatarUrlTF;
    @FXML
    private ComboBox<String> genderCB, pronounsCB;
    @FXML
    private Button toCreateAvatarButton;
    @FXML
    private Label fnErrorLabel, lnErrorLabel, dobErrorLabel, pNumberErrorLabel, emailErrorLabel;

    private Stage stage;
    private static final String DB_URL = "jdbc:mysql://uchattin-csc311.mysql.database.azure.com:3306/uchattin-userinfo";
    private static final String DB_USER = "username";
    private static final String DB_PASSWORD = "password";

    @FXML
    public void initialize() {
        System.out.println("Registration Controller initialized.");

        stage = (Stage) toCreateAvatarButton.getScene().getWindow();
        toCreateAvatarButton.setOnAction(e -> {
            if (validateAllInputs()) {
                System.out.println("Validation complete.");
                handleRegistration();
            }
        });

        genderCB.getItems().addAll("Male", "Female", "Non-binary", "Other");
        pronounsCB.getItems().addAll("He/Him/His", "She/Her/Hers", "They/Them/Theirs", "Other");

        genderCB.setOnAction(event -> toggleSpecifyFields());
        pronounsCB.setOnAction(event -> toggleSpecifyFields());
    }

    private void toggleSpecifyFields() {
        specifyGenderTF.setDisable(!"Other".equals(genderCB.getValue()));
        specifyPronounsTF.setDisable(!"Other".equals(pronounsCB.getValue()));
    }


    private boolean validateFirstName() {
        String firstName = firstNameTF.getText().trim();
        if (firstName.isEmpty() || !firstName.matches("[a-zA-Z]{2,25}$")) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "First name must be between 2-25 letters.");
            return false;
        }
        return true;
    }


    private boolean validateLastName() {
        String lastName = lastNameTF.getText().trim();
        if (lastName.isEmpty() || !lastName.matches("[a-zA-Z]{2,25}$")) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Last name must be between 2-25 letters.");
            return false;
        }
        return true;
    }


    private boolean validateDOB() {
        String dob = dobTF.getText().trim();
        if (!dob.matches("(0[1-9]|1[0-2])/(0[1-9]|[1-2][0-9]|3[0-1])/([0-9]{4})")) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Date of birth must be in MM/DD/YYYY format.");
            return false;
        }
        return true;
    }


    private boolean validateEmail() {
        String email = emailTF.getText().trim();
        if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$")) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Please enter a valid email address.");
            return false;
        }
        return true;
    }

    private boolean validatePhoneNumber() {
        String phone = phoneNumberTF.getText().trim();
        if (!phone.matches("\\d{3}-\\d{3}-\\d{4}$")) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Phone number must be in XXX-XXX-XXXX format.");
            return false;
        }
        return true;
    }

    private boolean validateAllInputs() {
        return validateFirstName() & validateLastName() & validateDOB() & validateEmail() & validatePhoneNumber();
    }

    private void handleRegistration() {
        String firstName = firstNameTF.getText().trim();
        String lastName = lastNameTF.getText().trim();
        String dob = dobTF.getText().trim();
        String email = emailTF.getText().trim();
        String preferredName = preferredNameTF.getText().trim();
        String phoneNumber = phoneNumberTF.getText().trim();
        String gender = genderCB.getValue();
        String pronouns = pronounsCB.getValue();
        String username = createUsernameTF.getText().trim();
        String password = createPasswordTF.getText().trim();
        int avatarID = 0;

        // Hash password securely
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        // Store user in database
        User newUser = new User(username, hashedPassword, firstName, lastName, dob, email, phoneNumber, pronouns,
                gender, preferredName, avatarID);

        if (saveUserToDatabase(newUser)) {
            showAlert(Alert.AlertType.INFORMATION, "Registration Successful!", "Your account has been created.");
            navigateToCreateAvatar();
        } else {
            showAlert(Alert.AlertType.ERROR, "Registration Failed", "An error occurred while saving your information.");
        }
    }

    private boolean saveUserToDatabase(User user) {
        String query = "INSERT INTO Users (username, password_hash, first_name, last_name, dob, email, phone_number, "
                + "pronouns, gender, specified_pronouns, preferred_name, avatar_id, created_at, last_login) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPasswordHash());
            statement.setString(3, user.getFirstName());
            statement.setString(4, user.getLastName());
            statement.setString(5, user.getDob());
            statement.setString(6, user.getEmail());
            statement.setString(7, user.getPhoneNumber());
            statement.setString(8, user.getPronouns());
            statement.setString(9, user.getGender());
            statement.setString(10, user.getPreferredName());
            statement.setInt(11, user.getAvatarID());

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error inserting user into database.");
            e.printStackTrace();
            return false;
        }
    }

    private void navigateToCreateAvatar() {
        try {
            System.out.println("Navigating to Create Avatar screen.");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/createAvatarScreen.fxml"));
            AnchorPane avatarScreen = loader.load();
            stage.setScene(new Scene(avatarScreen, 800, 600));
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "An error occurred while navigating to Create Avatar.");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}