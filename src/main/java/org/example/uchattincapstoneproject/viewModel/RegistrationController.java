package org.example.uchattincapstoneproject.viewModel;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.example.uchattincapstoneproject.model.DB;
import org.example.uchattincapstoneproject.model.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;


public class RegistrationController {

    @FXML private TextField firstNameTF, lastNameTF, preferredNameTF, phoneNumberTF, emailTF, specifyGenderTF, dobTF, specifyPronounsTF, createUsernameTF, createPasswordTF;
    @FXML private ComboBox<String> pronounsCB, genderCB;
    @FXML private Button toCreateAvatarButton, backBTN;
    @FXML private AnchorPane root;
    @FXML private Pane createAccountPane;
    @FXML private Label fNameErrorLabel, lNameErrorLabel, emailErrorLabel, genderErrorLabel, pronounsErrorLabel, dobErrorLabel,pNumberErrorLabel,
            usernameErrorLabel, passwordErrorLabel, specifiedPErrorLabel, specifiedGErrorLabel,preferredNameErrorLabel;

    private static final String FN_NAME_PATTERN = "^[a-zA-Z]{2,25}$"; //First name: 2-25 letters
    private static final String LN_NAME_PATTERN = "^[a-zA-Z]{2,25}$"; //Last name: 2-25 letters
    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$"; //Validates any email
    private static final String USERNAME_PATTERN = "^[a-zA-Z0-9]{2,25}$"; //Username: 2-25 characters, only letters & numbers
    private static final String PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$"; //Password: 8+ chars, 1 uppercase, 1 lowercase, 1 number, 1 special char
    private static final String DOB_PATTERN = "^(0[1-9]|1[0-2])/(0[1-9]|[1-2][0-9]|3[0-1])/([0-9]{4})$"; //DOB in MM/DD/YYYY format
    private static final String PHONE_PATTERN = "^\\d{3}-\\d{3}-\\d{4}$"; //Phone number in XXX-XXX-XXXX format

    private static final String DB_URL = "jdbc:mysql://commapp.mysql.database.azure.com:3306/communication_app";
    private static final String DB_USER = "commapp_db_user";
    private static final String DB_PASSWORD = "farm9786$";

    private DB dbInstance;

    @FXML
    public void initialize() {
        System.out.println("RegistrationController Initialized");

        dbInstance = DB.getInstance();
        //keep user inputs in text box
        User stored = dbInstance.getCurrentUser();
        if(stored == null){
            System.out.println("no stored user found, creating one");
            stored = new User("", "", "", "", "", "", "", "", "", "", "", "");
            dbInstance.setCurrentUser(stored);
        }

        if(stored != null) {
            firstNameTF.setText(stored.getFirstName());
            lastNameTF.setText(stored.getLastName());
            preferredNameTF.setText(stored.getPreferredName());
            emailTF.setText(stored.getEmail());
            dobTF.setText(stored.getDob());
            phoneNumberTF.setText(stored.getPhoneNumber());
            genderCB.setValue(stored.getGender());
            pronounsCB.setValue(stored.getPronouns());
            createUsernameTF.setText(stored.getUsername());
            createPasswordTF.setText(stored.getPasswordHash());

            specifyGenderTF.setText(stored.getSpecifiedGender());
            specifyPronounsTF.setText(stored.getSpecifiedPronouns());
            specifyGenderTF.setVisible("Other".equals(stored.getGender()));
            specifyPronounsTF.setVisible("Other".equals(stored.getPronouns()));

        }

        genderCB.getItems().addAll("Male", "Female", "Non-binary", "Other");
        pronounsCB.getItems().addAll("He/Him/His", "She/Her/Hers", "They/Them/Theirs", "Other");

        genderCB.setOnAction(event -> handleComboBoxSelection(genderCB, specifyGenderTF, specifiedGErrorLabel, "must select a gender"));//specifyGenderTF.setDisable(!genderCB.getValue().equals("Other"))
        pronounsCB.setOnAction(event -> handleComboBoxSelection(pronounsCB, specifyPronounsTF, specifiedPErrorLabel, "must select pronouns"));//specifyPronounsTF.setDisable(!pronounsCB.getValue().equals("Other"))

        toCreateAvatarButton.setOnAction(event -> navigateToAvatarSelection());
        backBTN.setOnAction(event -> navigateToEntranceScreen());

        setupValidationObservers();

        // Ensure content stays centered dynamically
        Platform.runLater(() -> {
            UIUtilities.centerContent(root, createAccountPane);
            root.widthProperty().addListener((obs, oldVal, newVal) -> UIUtilities.centerContent(root, createAccountPane));
            root.heightProperty().addListener((obs, oldVal, newVal) -> UIUtilities.centerContent(root, createAccountPane));
        });
    }

    //handles selection validation for CB
    private void handleComboBoxSelection(ComboBox<String> cB, TextField tF, Label l, String ruleMessage) {
        if(cB.getValue() == null || cB.getValue().isEmpty()) {
            l.setText(ruleMessage);
            l.setVisible(true);
        }else{
            l.setVisible(false);
            tF.setVisible(cB.getValue().equals("Other"));
        }

        if(cB.getValue().equals("Other") && (tF.getText().trim().isEmpty())) {
            l.setText("Please specify your choice");
            l.setVisible(true);
        }else{
            l.setVisible(false);
        }
    }

    //observable validations
    private void setupValidationObservers() {
        setupValidationListener(firstNameTF, FN_NAME_PATTERN, fNameErrorLabel, "must be 2-25 characters, only letters");
        setupValidationListener(lastNameTF, LN_NAME_PATTERN, lNameErrorLabel, "must be 2-25 characters, only letters");
        setupValidationListener(emailTF, EMAIL_PATTERN, emailErrorLabel, "must be a valid email address");
        setupValidationListener(createUsernameTF, USERNAME_PATTERN, usernameErrorLabel, "2-25 characters, only letters & numbers");
        setupValidationListener(createPasswordTF, PASSWORD_PATTERN, passwordErrorLabel, "8+ chars, 1 uppercase, 1 lowercase, 1 number, 1 special char");
        setupValidationListener(dobTF, DOB_PATTERN, dobErrorLabel, "must be format mm/dd/yyyy");
        setupValidationListener(phoneNumberTF, PHONE_PATTERN, pNumberErrorLabel, "must be format 000-000-0000");

        //Make sure edits update stored values immediately
        firstNameTF.textProperty().addListener((obs, oldVal, newVal) -> {
            if(dbInstance.getCurrentUser() != null) dbInstance.getCurrentUser().setFirstName(newVal);
        });

        lastNameTF.textProperty().addListener((obs, oldVal, newVal) -> {
            if(dbInstance.getCurrentUser()!= null) dbInstance.getCurrentUser().setLastName(newVal);
        });

        emailTF.textProperty().addListener((obs, oldVal, newVal) -> {
            if(dbInstance.getCurrentUser() != null) dbInstance.getCurrentUser().setEmail(newVal);
        });

        phoneNumberTF.textProperty().addListener((obs, oldVal, newVal) -> {
            if(dbInstance.getCurrentUser() != null) dbInstance.getCurrentUser().setPhoneNumber(newVal);
        });

        dobTF.textProperty().addListener((obs, oldVal, newVal) -> {
            if(dbInstance.getCurrentUser() != null) dbInstance.getCurrentUser().setDob(newVal);
        });

        createUsernameTF.textProperty().addListener((obs, oldVal, newVal) -> {
            if(dbInstance.getCurrentUser() != null) dbInstance.getCurrentUser().setUsername(newVal);
        });

        createPasswordTF.textProperty().addListener((obs, oldVal, newVal) -> {
            if(dbInstance.getCurrentUser() != null) dbInstance.getCurrentUser().setPasswordHash(newVal);
        });

        genderCB.valueProperty().addListener((obs, oldVal, newVal) -> {
           if(dbInstance.getCurrentUser() != null) {
               dbInstance.getCurrentUser().setGender(newVal.equals("Other") ? specifyGenderTF.getText().trim() : newVal);
           }
        });

        pronounsCB.valueProperty().addListener((obs, oldVal, newVal) -> {
            if(dbInstance.getCurrentUser() != null) {
                dbInstance.getCurrentUser().setPronouns(newVal.equals("Other") ? specifyPronounsTF.getText().trim() : newVal);
            }
        });

        specifyGenderTF.textProperty().addListener((obs, oldVal, newVal) -> {
            if(dbInstance.getCurrentUser() != null) dbInstance.getCurrentUser().setSpecifiedGender(newVal);
        });

        specifyPronounsTF.textProperty().addListener((obs, oldVal, newVal) -> {
            if(dbInstance.getCurrentUser() != null) dbInstance.getCurrentUser().setSpecifiedPronouns(newVal);
        });
    }

    //listener
    private void setupValidationListener(TextField tF, String pattern, Label l, String ruleMessage) {
        tF.textProperty().addListener((observable, oldValue, newValue) -> {
            boolean isValid = newValue.matches(pattern);
            l.setVisible(!isValid); //show error when invalid
            l.setText(isValid ? "" : ruleMessage);
        });
    }

    @FXML
    private void navigateToAvatarSelection() {
        try{
            System.out.println("Navigating to Avatar Selection...");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/createAvatarScreen.fxml"));
            Parent avatarScreen = loader.load();

            CreateAvatarController controller = loader.getController();

            //pass or retrieve stored data
            User userData = collectUserData();
            if(userData != null) {
                dbInstance.setCurrentUser(userData);
                System.out.println("stored user before navigation");
                System.out.println("username: " + userData.getUsername());
                System.out.println("email: " + userData.getEmail());
                System.out.println("phone number: " + userData.getPhoneNumber());
                System.out.println("dob: " + userData.getDob());
                System.out.println("gender: " + userData.getGender());
                System.out.println("pronouns: " + userData.getPronouns());
                controller.setUser(userData);
            }

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


        String formattedDOB = validateAndFormatDOB(dobTF.getText().trim());
        if(formattedDOB == null || formattedDOB.isEmpty()) {
            return null;
        }

        if(genderCB.getValue() == null || genderCB.getValue().isEmpty()) {
            genderErrorLabel.setText("Must select a gender");
            genderErrorLabel.setVisible(true);
            return null;
        }

        if(pronounsCB.getValue() == null || pronounsCB.getValue().isEmpty()) {
            pronounsErrorLabel.setText("Must select pronouns");
            pronounsErrorLabel.setVisible(true);
            return null;
        }

        //if other custom input
        String genderSelection = genderCB.getValue().equals("Other") ? specifyGenderTF.getText().trim() : genderCB.getValue();
        String specifiedGender = genderCB.getValue().equals("Other") ? specifyGenderTF.getText().trim() : null;

        String pronounsSelection = pronounsCB.getValue().equals("Other") ? specifyPronounsTF.getText().trim() : pronounsCB.getValue();
        String specifiedPronouns = pronounsCB.getValue().equals("Other") ? specifyPronounsTF.getText().trim() : null;

        User updatedUser = new User(
                firstNameTF.getText().trim(),
                lastNameTF.getText().trim(),
                preferredNameTF.getText().trim(),
                phoneNumberTF.getText().trim(),
                emailTF.getText().trim(),
                formattedDOB,
                genderSelection,
                specifiedGender,
                pronounsSelection,
                specifiedPronouns,
                createUsernameTF.getText().trim(),
                createPasswordTF.getText().trim()
        );
        //if user already exist, retrieve stored data
        dbInstance.setCurrentUser(updatedUser);
        return updatedUser;
    }

    //convert dob from mm/dd/yyyy to yyyy-mm-dd for sql
    private String validateAndFormatDOB(String dob){
        try{
            SimpleDateFormat input = new SimpleDateFormat("MM/dd/yyyy");
            SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd");
            return output.format(input.parse(dob));
        } catch (ParseException e){
            dobErrorLabel.setText("Use format MM/DD/YYYY");
            dobErrorLabel.setVisible(true);
            return null;
        }
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