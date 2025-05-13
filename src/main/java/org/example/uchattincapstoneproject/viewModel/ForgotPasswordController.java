package org.example.uchattincapstoneproject.viewModel;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.*;

import org.mindrot.jbcrypt.BCrypt;

public class ForgotPasswordController {
    private static final String DB_URL = "jdbc:mysql://commapp.mysql.database.azure.com:3306/communication_app";
    private static final String DB_USERNAME = "commapp_db_user";
    private static final String DB_PASSWORD = "farm9786$";

    @FXML
    private TextField enterNewPasswordTF, confirmPasswordTF, resetUsernameTF;
    @FXML
    private Button resetPasswordButton, backToEntranceButton;
    @FXML
    private Label confirmPasswordErrorLabel, newPasswordErrorLabel;
    private Stage stage;


    @FXML
    private void initialize() {
        System.out.println("ForgotPasswordController initialized.");
        resetPasswordButton.setOnAction(event -> handlePasswordReset());
        backToEntranceButton.setOnAction(event -> UIUtilities.navigateToScreen("/views/entranceScreen.fxml", resetPasswordButton.getScene(), false));
    }

    private int getCurrentUserID(String username){
        if(username.isEmpty()){
            showAlert(Alert.AlertType.ERROR, "Error", "Please enter a valid username");
            return -1;
        }

        String sql = "SELECT id FROM Users WHERE username = ?";
        try(Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setString(1,username);
            ResultSet resultSet = stmt.executeQuery();
            if(resultSet.next()){
                return resultSet.getInt("id");
            }else{
                showAlert(Alert.AlertType.ERROR, "Error", "User does not exist");
                return -1;
            }
        }catch (SQLException e){
            System.err.println("error retrieving user ID: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Unable to retrieve user id");
            return -1;
        }
    }

    private void handlePasswordReset() {
        String username = resetUsernameTF.getText().trim();
        String newPassword = enterNewPasswordTF.getText().trim();
        String confirmPassword = confirmPasswordTF.getText().trim();

        // Validate input
        if (!validatePassword(newPassword, confirmPassword)) {
            return;
        }

        // Update password in database
        if (updatePasswordInDatabase(username, newPassword)) {
            showAlert(Alert.AlertType.INFORMATION, "Password Updated", "Password updated successfully.");
            Platform.runLater(()->{
                UIUtilities.navigateToScreen("/views/entranceScreen.fxml", resetPasswordButton.getScene(), false);
            });

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

    private boolean updatePasswordInDatabase(String username, String newPassword) {
        int userID = getCurrentUserID(username);
        if(userID == -1){
            return false;
        }

        String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
        String sql = "UPDATE Users SET password_hash = ? WHERE id = ?";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, hashedPassword);
            statement.setInt(2, userID); //Ensure user ID is retrieved correctly

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating password.");
            e.printStackTrace();
            return false;
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}