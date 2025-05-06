package org.example.uchattincapstoneproject.viewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.mindrot.jbcrypt.BCrypt;

public class ForgotPasswordController {
    private static final String DB_URL = "jdbc:mysql://commapp.mysql.database.azure.com:3306/communication_app";
    private static final String USERNAME = "commapp_db_user";
    private static final String PASSWORD = "farm9786$";

    @FXML
    private TextField enterNewPasswordTF, confirmPasswordTF;
    @FXML
    private Button resetPasswordButton;
    @FXML
    private Label confirmPasswordErrorLabel, newPasswordErrorLabel;
    private Stage stage;


    @FXML
    private void initialize() {
        System.out.println("ForgotPasswordController initialized.");
        resetPasswordButton.setOnAction(event -> handlePasswordReset());
    }


    private void handlePasswordReset() {
        String newPassword = enterNewPasswordTF.getText().trim();
        String confirmPassword = confirmPasswordTF.getText().trim();

        // Validate input
        if (!validatePassword(newPassword, confirmPassword)) {
            return;
        }

        // Update password in database
        if (updatePasswordInDatabase(newPassword)) {
            showAlert(Alert.AlertType.INFORMATION, "Password Updated", "Password updated successfully.");
        } else {
            showAlert(Alert.AlertType.ERROR, "Update Failed", "An error occurred while updating the password.");
        }
    }


    private boolean validatePassword(String newPassword, String confirmPassword) {
        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            newPasswordErrorLabel.setText("Both fields must be filled.");
            return false;
        }

        if (!newPassword.equals(confirmPassword)) {
            confirmPasswordErrorLabel.setText("Passwords do not match.");
            return false;
        }

        if (newPassword.length() < 8 || !newPassword.matches(".*\\d.*") || !newPassword.matches(".*[a-zA-Z].*")) {
            newPasswordErrorLabel.setText("Must be at least 8 characters, contain a letter, and a number.");
            return false;
        }

        newPasswordErrorLabel.setText("");
        confirmPasswordErrorLabel.setText("");
        return true;
    }

    private boolean updatePasswordInDatabase(String newPassword) {
        String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
        String sql = "UPDATE Users SET password_hash = ? WHERE id = ?";

        try (Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, hashedPassword);
            statement.setInt(2, getCurrentUserID()); //Ensure user ID is retrieved correctly

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating password.");
            e.printStackTrace();
            return false;
        }
    }


    private int getCurrentUserID() {
        return 1; //Replace this with actual logic to get the user's ID.
    }


    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}