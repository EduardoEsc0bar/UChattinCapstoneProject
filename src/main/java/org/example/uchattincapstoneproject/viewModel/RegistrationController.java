package org.example.uchattincapstoneproject.viewModel;

import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.uchattincapstoneproject.model.DB;
import org.example.uchattincapstoneproject.model.User;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

public class RegistrationController {
    // FXML annotated fields - exactly as in your FXML
    @FXML
    public Pane createAccountPane;
    @FXML
    public TextField firstNameTF, lastNameTF, emailTF, dobTF, preferredNameTF, phoneNumberTF, createUsernameTF, createPasswordTF, specifyGenderTF, specifyPronounsTF;
    @FXML
    public ComboBox<String> genderCB, pronounsCB;
    @FXML
    public Button toCreateAvatarButton, backBTN;
    @FXML
    public Label fnErrorLabel, lnErrorLabel, emailErrorLabel, pNumberErrorLabel, dobErrorLabel;

    // Non-FXML fields
    private DB dbInstance;
    private static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@(.+)$";
    private static final String PHONE_PATTERN = "^(\\+\\d{1,3}( )?)?((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    @FXML
    public void initialize() {
        System.out.println("Registration controller initialized");

        // Initialize DB instance
        dbInstance = DB.getInstance();

        // Set up ComboBoxes
        setupComboBoxes();

        // Enable error labels but clear their text
        enableAndClearErrorLabels();

        // Set up button actions
        // backBTN already has its action set in FXML
        toCreateAvatarButton.setOnAction(this::handleRegistration);

        // Set up text field listeners for validation
        setupTextFieldListeners();
    }

    private void setupComboBoxes() {
        // Set up gender ComboBox
        ObservableList<String> genderOptions = FXCollections.observableArrayList(
                "Male", "Female", "Non-binary", "Prefer not to say", "Specify"
        );
        genderCB.setItems(genderOptions);
        genderCB.getSelectionModel().selectFirst();

        // Set up pronouns ComboBox
        ObservableList<String> pronounOptions = FXCollections.observableArrayList(
                "He/Him", "She/Her", "They/Them", "Prefer not to say", "Specify"
        );
        pronounsCB.setItems(pronounOptions);
        pronounsCB.getSelectionModel().selectFirst();

        // Add listeners to show/hide specify text fields
        genderCB.valueProperty().addListener((obs, oldVal, newVal) -> {
            specifyGenderTF.setVisible("Specify".equals(newVal));
            specifyGenderTF.setManaged("Specify".equals(newVal));
        });

        pronounsCB.valueProperty().addListener((obs, oldVal, newVal) -> {
            specifyPronounsTF.setVisible("Specify".equals(newVal));
            specifyPronounsTF.setManaged("Specify".equals(newVal));
        });

        // Initially hide specify fields
        specifyGenderTF.setVisible(false);
        specifyGenderTF.setManaged(false);
        specifyPronounsTF.setVisible(false);
        specifyPronounsTF.setManaged(false);
    }

    private void setupTextFieldListeners() {
        // First name validation
        firstNameTF.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) { // when focus lost
                validateFirstName();
            }
        });

        // Last name validation
        lastNameTF.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) { // when focus lost
                validateLastName();
            }
        });

        // Email validation
        emailTF.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) { // when focus lost
                validateEmail();
            }
        });

        // Phone number validation
        phoneNumberTF.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) { // when focus lost
                validatePhoneNumber();
            }
        });

        // Date of birth validation
        dobTF.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) { // when focus lost
                validateDateOfBirth();
            }
        });
    }

    private void enableAndClearErrorLabels() {
        fnErrorLabel.setDisable(false);
        lnErrorLabel.setDisable(false);
        emailErrorLabel.setDisable(false);
        pNumberErrorLabel.setDisable(false);
        dobErrorLabel.setDisable(false);

        fnErrorLabel.setText("");
        lnErrorLabel.setText("");
        emailErrorLabel.setText("");
        pNumberErrorLabel.setText("");
        dobErrorLabel.setText("");
    }

    private boolean validateFirstName() {
        if (firstNameTF.getText().trim().isEmpty()) {
            fnErrorLabel.setText("First name is required");
            return false;
        }
        fnErrorLabel.setText("");
        return true;
    }

    private boolean validateLastName() {
        if (lastNameTF.getText().trim().isEmpty()) {
            lnErrorLabel.setText("Last name is required");
            return false;
        }
        lnErrorLabel.setText("");
        return true;
    }

    private boolean validateEmail() {
        String email = emailTF.getText().trim();
        if (email.isEmpty()) {
            emailErrorLabel.setText("Email is required");
            return false;
        }

        if (!Pattern.matches(EMAIL_PATTERN, email)) {
            emailErrorLabel.setText("Invalid email format");
            return false;
        }

        emailErrorLabel.setText("");
        return true;
    }

    private boolean validatePhoneNumber() {
        String phoneNumber = phoneNumberTF.getText().trim();
        if (!phoneNumber.isEmpty() && !Pattern.matches(PHONE_PATTERN, phoneNumber)) {
            pNumberErrorLabel.setText("Invalid phone number format");
            return false;
        }

        pNumberErrorLabel.setText("");
        return true;
    }

    private boolean validateDateOfBirth() {
        String dob = dobTF.getText().trim();
        if (dob.isEmpty()) {
            dobErrorLabel.setText("Date of birth is required");
            return false;
        }

        try {
            // Parse the date to check if it's valid
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
            LocalDate.parse(dob, inputFormatter);

            dobErrorLabel.setText("");
            return true;
        } catch (DateTimeParseException e) {
            dobErrorLabel.setText("Invalid date format (MM/DD/YYYY)");
            return false;
        }
    }

    private boolean validateUsername() {
        String username = createUsernameTF.getText().trim();
        if (username.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Username is required");
            return false;
        }

        if (username.length() < 4) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Username must be at least 4 characters");
            return false;
        }

        return true;
    }

    private boolean validatePassword() {
        String password = createPasswordTF.getText();
        if (password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Password is required");
            return false;
        }

        if (password.length() < 8) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Password must be at least 8 characters");
            return false;
        }

        return true;
    }

    @FXML
    private void handleRegistration(ActionEvent event) {
        // Validate all required fields
        boolean isFirstNameValid = validateFirstName();
        boolean isLastNameValid = validateLastName();
        boolean isEmailValid = validateEmail();
        boolean isPhoneValid = validatePhoneNumber();
        boolean isDobValid = validateDateOfBirth();
        boolean isUsernameValid = validateUsername();
        boolean isPasswordValid = validatePassword();

        if (isFirstNameValid && isLastNameValid && isEmailValid && isPhoneValid &&
                isDobValid && isUsernameValid && isPasswordValid) {

            try {
                // Get all field values
                String firstName = firstNameTF.getText().trim();
                String lastName = lastNameTF.getText().trim();
                String email = emailTF.getText().trim();
                String phoneNumber = phoneNumberTF.getText().trim();
                // Get date in correct format for database
                String dob = dobTF.getText().trim();
                String formattedDob = dob;
                try {
                    DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
                    DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    LocalDate date = LocalDate.parse(dob, inputFormatter);
                    formattedDob = date.format(outputFormatter);
                } catch (Exception e) {
                    // If parsing fails, just use the original value
                    System.err.println("Error formatting date: " + e.getMessage());
                }
                String username = createUsernameTF.getText().trim();
                String password = createPasswordTF.getText();
                String preferredName = preferredNameTF.getText().trim();

                // Handle gender selection
                String gender = genderCB.getValue();
                String specifiedGender = "";
                if ("Specify".equals(gender)) {
                    specifiedGender = specifyGenderTF.getText().trim();
                    if (specifiedGender.isEmpty()) {
                        showAlert(Alert.AlertType.WARNING, "Validation Error",
                                "Please specify your gender or select an option from the dropdown");
                        return;
                    }
                }

                // Handle pronouns selection
                String pronouns = pronounsCB.getValue();
                String specifiedPronouns = "";
                if ("Specify".equals(pronouns)) {
                    specifiedPronouns = specifyPronounsTF.getText().trim();
                    if (specifiedPronouns.isEmpty()) {
                        showAlert(Alert.AlertType.WARNING, "Validation Error",
                                "Please specify your pronouns or select an option from the dropdown");
                        return;
                    }
                }

                // Get DB instance
                DB dbInstance = DB.getInstance();

                // Hash the password properly using the same method as DBTest
                String hashedPassword = dbInstance.hashPassword(password);

                // Create the user object with hashed password
                User newUser = new User(
                        username,
                        hashedPassword,
                        firstName,
                        lastName,
                        formattedDob, // Use formatted date
                        email,
                        phoneNumber,
                        pronouns,
                        gender,
                        specifiedGender,
                        specifiedPronouns,
                        preferredName
                );

                // Use the direct insertUser method that worked in DBTest
                boolean registrationSuccess = dbInstance.insertUser(newUser);

                if (registrationSuccess) {
                    // Store current user in DB singleton for later use
                    dbInstance.setCurrentUser(newUser);

                    showAlert(Alert.AlertType.INFORMATION, "Registration Successful",
                            "Your account has been created successfully. Please proceed to create your avatar.");

                    // Navigate to the create avatar screen
                    navigateToCreateAvatarScreen();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Registration Failed",
                            "There was an error registering your account. The username or email might already be in use.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Registration Error",
                        "There was an error during registration: " + e.getMessage());
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Validation Error",
                    "Please fix the errors in the form before proceeding.");
        }
    }

    private void navigateToCreateAvatarScreen() {
        try {
            System.out.println("Navigating to avatar creation screen");
            FadeTransition fadeout = new FadeTransition(Duration.seconds(1));
            fadeout.setFromValue(1.0);
            fadeout.setToValue(0.0);
            fadeout.setNode(createAccountPane);
            fadeout.setOnFinished(event -> {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/createAvatarScreen.fxml"));
                    AnchorPane createAvatarScreen = loader.load();

                    // Get the controller and pass the current user
                    CreateAvatarController avatarController = loader.getController();

                    // Check if the controller has the setUser method
                    try {
                        avatarController.setUser(dbInstance.getCurrentUser());
                    } catch (Exception e) {
                        System.err.println("Warning: Could not set user on CreateAvatarController: " + e.getMessage());
                        // Continue anyway - the user is already stored in DB singleton
                    }

                    Scene createAvatarScene = new Scene(createAvatarScreen, 800, 600);
                    Stage stage = (Stage) toCreateAvatarButton.getScene().getWindow();
                    stage.setScene(createAvatarScene);
                    stage.setTitle("Create Your Avatar");

                    FadeTransition fadeIn = new FadeTransition(Duration.seconds(1));
                    fadeIn.setFromValue(0.0);
                    fadeIn.setToValue(1.0);
                    fadeIn.setNode(createAvatarScreen);
                    fadeIn.play();
                } catch (IOException e) {
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Navigation Error",
                            "An error occurred while navigating to the avatar creation screen: " + e.getMessage());
                }
            });
            fadeout.play();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error",
                    "An error occurred while preparing to navigate to the avatar creation screen: " + e.getMessage());
        }
    }

    //navigating to entrance screen
    @FXML
    public void navigateToEntranceScreen(ActionEvent actionEvent) {
        try{
            System.out.println("navigating to entrance scene");
            FadeTransition fadeout = new FadeTransition(Duration.seconds(1));
            fadeout.setFromValue(1.0);
            fadeout.setToValue(0.0);
            fadeout.setNode(createAccountPane);
            fadeout.setOnFinished(event -> {
                try{
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/entranceScreen.fxml"));
                    AnchorPane entranceScreen = loader.load();
                    Scene entranceScene = new Scene(entranceScreen, 800, 600);
                    Stage stage = (Stage) backBTN.getScene().getWindow();
                    stage.setScene(entranceScene);
                    stage.setTitle("Welcome to UChattIn");

                    FadeTransition fadein = new FadeTransition(Duration.seconds(1));
                    fadein.setFromValue(0.0);
                    fadein.setToValue(1.0);
                    fadein.setNode(entranceScreen);
                    fadein.play();
                }catch(IOException e){
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Navigation Error",
                            "An error occurred while navigating to the entrance screen: " + e.getMessage());
                }
            });
            fadeout.play();
        }catch(Exception e){
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error",
                    "An error occurred while preparing to navigate to the entrance screen: " + e.getMessage());
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