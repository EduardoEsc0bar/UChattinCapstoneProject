package org.example.uchattincapstoneproject.viewModel;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ForgotPasswordController {

    @FXML
    private TextField enterNewPasswordTF, confirmPasswordTF;
    @FXML
    private Button resetPasswordButton;
    @FXML
    private Label confirmPasswordErrorLabel, newPasswordErrorLabel;
    private Stage stage;

    //initialize controller
    @FXML
    private void initialize() {
        System.out.println("forgotPasswordController initialize");

        //handle reset
        resetPasswordButton.setOnAction(event -> handlePasswordReset());
/*
        //handle password confirmation
        String newPassword = enterNewPasswordTF.getText();
        String confirmPassword = confirmPasswordTF.getText();

        //validate password input
        if(!validatePassword(newPassword, confirmPassword)){
            return;
        }

        if(updatePasswordInDatabase(newPassword)){
            showAlert(Alert.AlertType.INFORMATION, "Success", "Your new password has been updated");
        }else{
            showAlert(Alert.AlertType.ERROR, "Update failed", "An error occurred while updating password");
        }

 */
    }

    //validate and update password
    private void handlePasswordReset() {
        String newPassword = enterNewPasswordTF.getText().trim();
        String confirmPassword = confirmPasswordTF.getText().trim();

        //validate input
        if(!validatePassword(newPassword, confirmPassword)){
            return;
        }

        System.out.println("simulated password update: " + newPassword);
        showAlert(Alert.AlertType.INFORMATION, "Password updated", "Password updated successfully");
    }

    //validate new password
    private boolean validatePassword(String newPassword, String confirmPassword) {
        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            newPasswordErrorLabel.setText("Both fields must be filled");
            return false;
        }

        if (!newPassword.equals(confirmPassword)) {
            confirmPasswordErrorLabel.setText("Passwords do not match");
            return false;
        }

        if ((newPassword.length() < 8) || (!newPassword.matches(".*\\d.*")) || (!newPassword.matches(".*[a-zA-Z].*"))){
            newPasswordErrorLabel.setText("must be at least 8 characters, an uppercase letter and contain a number");
            return false;
        }
        newPasswordErrorLabel.setText("");
        confirmPasswordErrorLabel.setText("");
        return true;
    }
/*
    //update password in database
    private boolean updatePasswordInDatabase(String newPassword) {
        try(Connection connection = Database.connect()){
            if(connection == null){
                System.out.println("database connection failed");
                return false;
            }

            String hashedPassord = HandlePasswordHash.hashPassword(newPassword);
            String query = "UPDATE password SET hashedPassword = ? WHERE id = ?";

            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, hashedPassord);
            statement.setInt(2, getCurrentUserID());

            int rowsAffected  = statement.executeUpdate();
            return rowsAffected > 0;
        }catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }

 */

    //mock method get current user id
    private int getCurrentUserID() {
        return 1;
    }

    //display alert
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
