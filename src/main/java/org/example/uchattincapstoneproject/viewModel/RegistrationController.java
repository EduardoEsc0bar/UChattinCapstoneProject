package org.example.uchattincapstoneproject.viewModel;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.apache.qpid.proton.reactor.impl.IO;
import org.example.uchattincapstoneproject.model.HandlePasswordHash;
import org.example.uchattincapstoneproject.model.User;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RegistrationController {
    @FXML
    private TextField firstNameTF, lastNameTF, dobTF, emailTF, preferredNameTF, phoneNumberTF,
    createUsernameTF, createPasswordTF, specifyGenderTF, specifyPronounsTF;
    @FXML
    private ComboBox<String> genderCB, pronounsCB;
    @FXML
    private Button toCreateAvatarButton;
    @FXML
    private Label fnErrorLabel, lnErrorLabel, dobErrorLabel, pNumberErrorLabel, emailErrorLabel;
    private Stage stage;
    private static final String FN_NAME_PATTERN = "[a-zA-Z]{2,25}$";
    private static final String LN_NAME_PATTERN = "[a-zA-Z]{2,25}$";
    private static final String DOB_PATTERN = "(0[0-9]|1[0-2])/(0[1-9]|[1-2][0-9]|3[0-1])/([0-9]{4})$";
    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
    private static final String PHONE_NUMBER_PATTERN = "\\d{3}-\\d{3}-\\d{4}$";



    @FXML
    public void initialize() {
        System.out.println("registration controller initialized");
        //navigate to create avatar screen
        stage = (Stage) toCreateAvatarButton.getScene().getWindow();
        toCreateAvatarButton.setOnAction(e ->{
            if(validateAllInputs()){
                System.out.println("validation complete");
                handleRegistration();
            }
        });

        //listeners for regex
        firstNameTF.textProperty().addListener((observable, oldValue, newValue) -> validateFirstName());
        lastNameTF.textProperty().addListener((observableValue, oldValue, newValue) -> validateLastName());
        dobTF.textProperty().addListener(((observableValue, oldValue, newValue) -> validateDOB()));
        emailTF.textProperty().addListener((observableValue, oldValue, newValue)-> validateEmail());
        phoneNumberTF.textProperty().addListener((observableValue, oldValue, newValue) -> validatePhoneNumber());
        //disables until "other" is clicked
        specifyGenderTF.setDisable(true);
        specifyPronounsTF.setDisable(true);

        //populate gender combobox
        genderCB.getItems().addAll("Male", "Female", "Non-binary", "Other");

        //populate pronouns combobox
        pronounsCB.getItems().addAll("He/Him/His", "She/Her/Hers", "They/Them/Theirs", "Other");

        //add listener to enable "other" option
        genderCB.setOnAction(event -> toggleSpecifyFields());
        pronounsCB.setOnAction(event -> toggleSpecifyFields());

    }

    //toggle gender and pronoun options
    private void toggleSpecifyFields(){
        //enable specify gender field if other is selected
        specifyGenderTF.setDisable(!"Other".equals(genderCB.getValue()));
        specifyGenderTF.clear();

        //enables specify pronouns field if other is selected
        specifyPronounsTF.setDisable(!"Other".equals(pronounsCB.getValue()));
        specifyPronounsTF.clear();
    }

    public void validateFirstName(){
        //first name between 2-25 chars
        String firstName = firstNameTF.getText();
        System.out.println("Validating first name: " + firstName);
        if(firstName == null || firstName.isEmpty() || !firstName.matches(FN_NAME_PATTERN)){
            fnErrorLabel.setText("must be 2-25 characters");
        }
        else{
            fnErrorLabel.setText("");
        }
    }

    public void validateLastName(){
        String lastName = lastNameTF.getText();
        System.out.println("validating last name: " + lastName);
        if(lastName == null || lastName.isEmpty() || !lastName.matches(LN_NAME_PATTERN)){
            lnErrorLabel.setText("must be 2-25 characters");
        }else{
            lnErrorLabel.setText("");
        }
    }

    public void validateDOB(){
        String dob = dobTF.getText();
        System.out.println("Validating DOB: " + dob);
        if(dob == null || dob.isEmpty() || !dob.matches(DOB_PATTERN)){
            dobErrorLabel.setText("must be MM/DD/YYYY");
        }else{
            dobErrorLabel.setText("");
        }
    }

    public void validateEmail(){
        String email = emailTF.getText();
        System.out.println("Validating email: " + email);
        if(email == null || email.isEmpty() || !email.matches(EMAIL_PATTERN)){
            emailErrorLabel.setText("must be a valid email address");
        }
        else{
            emailErrorLabel.setText("");
        }
    }

    public void validatePhoneNumber(){
        String phoneNumber = phoneNumberTF.getText();
        System.out.println("Validating phone number: " + phoneNumber);
        if(phoneNumber == null || phoneNumber.isEmpty() || !phoneNumber.matches(PHONE_NUMBER_PATTERN)){
            pNumberErrorLabel.setText("must be XXX-XXX-XXXX");
        }else{
            pNumberErrorLabel.setText("");
        }
    }

    private boolean validateAllInputs(){
        validateFirstName();
        validateLastName();
        validateDOB();
        validateEmail();
        validatePhoneNumber();

        boolean isValid = fnErrorLabel.getText().isEmpty() &&
                lnErrorLabel.getText().isEmpty() &&
                dobErrorLabel.getText().isEmpty() &&
                emailErrorLabel.getText().isEmpty() &&
                pNumberErrorLabel.getText().isEmpty();
        System.out.println("validation status: " + isValid);
        return isValid;
    }

    //handles user registration
    private void handleRegistration() {
        //retrieve input values
        String firstName = firstNameTF.getText().trim();
        String lastName = lastNameTF.getText().trim();
        String dob = dobTF.getText().trim();
        String email = emailTF.getText().trim();
        String preferredName = preferredNameTF.getText().trim();
        String phoneNumber = phoneNumberTF.getText().trim();
        String gender = genderCB.getValue();
        String pronouns = pronounsCB.getValue();
        String specifiedGender = specifyGenderTF.getText().trim();
        String specifiedPronouns = specifyPronounsTF.getText().trim();
        String username = createUsernameTF.getText().trim();
        String password = createPasswordTF.getText().trim();


        //store user in database
        if(saveUserToDatabase(new User(username, password, firstName, lastName, dob, email, phoneNumber, pronouns, gender, specifiedGender, specifiedPronouns, preferredName ))){
            showAlert(Alert.AlertType.INFORMATION, "Registration Successful!", "Your account has been created");
            navigateToCreateAvatar();
        }else{
            showAlert(Alert.AlertType.ERROR, "Registration failed", "An error occurred while saving your information");
        }
    }

    //saves user data to database
    private boolean saveUserToDatabase(User user) {
        try (Connection connection = Database.connect()) {
            if (connection == null) {
                System.out.println("registration database connection failed");
                return false;
            }

            String query = "INSERT INTO users (username, password, first_name, last_name, dob, email, phone_number, pronouns, gender, specifiedGender, specifiedPronouns, preferred_name) " +
                    "VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, user.getUsername());
            statement.setString(2, HandlePasswordHash.hashPassword(user.getPasswordHash()));
            statement.setString(3, user.getFirstName());
            statement.setString(4, user.getLastName());
            statement.setString(5, user.getDob());
            statement.setString(6, user.getEmail());
            statement.setString(7, user.getPhoneNumber());
            statement.setString(8, user.getPronouns());
            statement.setString(9, user.getGender());
            statement.setString(10, user.getSpecifiedGender());
            statement.setString(11, user.getSpecifiedPronouns());
            statement.setString(12, user.getPreferredName());

            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    //navigates to create avatar screen
    private void navigateToCreateAvatar(){
        try{
            System.out.println("navigating to create avatar screen");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/createAvatarScreen.fxml"));
            AnchorPane avatarScreen = loader.load();

            stage = (Stage) toCreateAvatarButton.getScene().getWindow();
            stage.setScene(new Scene(avatarScreen, 800, 600));
        }catch (IOException e){
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "An error occurred while navigating to create avatar");
        }
    }

    //displays alert
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }



}
