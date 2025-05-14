package org.example.uchattincapstoneproject.viewModel;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.uchattincapstoneproject.model.DB;
import org.example.uchattincapstoneproject.model.User;
import org.example.uchattincapstoneproject.model.Util;

import java.io.File;
import java.util.Arrays;

public class SettingsController {
    private final DB db = DB.getInstance();
    private final Util utilities = Util.getInstance();
    private User currentUser;
    private User completeUserFromDB; // Store the complete user data from DB
    private String favoritePhrase; // Since not in User class, we'll manage it here

    //---------------------Edit Profile Tab---------------------------------\\
    @FXML
    private Tab editProfileTab;
    @FXML
    private TextField eProfileName, eUsernameTF, otherGenderSettingsTF, otherPronounsSettingsTF;
    @FXML
    private Label editUsernameLabel, editUsernameLabel1, editUsernameLabel11, editUsernameLabel12, editUsernameLabel121;
    @FXML
    private ComboBox<String> editGenderSettingsCB, editPronounsSettingsCB;
    @FXML
    private ColorPicker profileThemeColorPicker;
    @FXML
    private Button updateAvatarButton;
    @FXML
    private TextField emailTF; // Add this to your FXML if not already there

    /**
     * Called when the "Save All Changes" button in the Edit Profile tab is clicked.
     * Updates the current user's information in the database based on the input fields.
     *
     * @param event the action event triggered by the button click
     */
    @FXML
    void saveAllChangesBtnClicked(ActionEvent event) {
        try {
            // Get original username for comparison
            String originalUsername = currentUser.getUsername();

            // Get updated values from UI components
            String newUsername = eUsernameTF.getText();
            String newDisplayName = eProfileName.getText();
            String email = emailTF != null ? emailTF.getText() : completeUserFromDB.getEmail();

            // Ensure email is not null (required by database)
            if (email == null || email.trim().isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Error", "Email cannot be empty. Please provide a valid email address.");
                return;
            }

            // Check if username has changed - IMPORTANT: BLOCK ANY ATTEMPT TO CHANGE USERNAME
            if (!originalUsername.equals(newUsername)) {
                // Reset username field to original value
                eUsernameTF.setText(originalUsername);

                // Show alert that username changes aren't supported
                showAlert(Alert.AlertType.WARNING, "Username Change Not Allowed",
                        "Username cannot be changed in this version. The username field has been reset to your current username.");

                // Do not proceed with other changes until the user acknowledges
                return;
            }

            // Get gender (from combo box or text field)
            String gender = editGenderSettingsCB.getValue();
            if (gender != null) {
                if (gender.equals("Other") && !otherGenderSettingsTF.getText().isEmpty()) {
                    currentUser.setGender("Other");
                    currentUser.setSpecifiedGender(otherGenderSettingsTF.getText());
                } else {
                    currentUser.setGender(gender);
                    currentUser.setSpecifiedGender("");
                }
            }

            // Get pronouns (from combo box or text field)
            String pronouns = editPronounsSettingsCB.getValue();
            if (pronouns != null) {
                if (pronouns.equals("Other") && !otherPronounsSettingsTF.getText().isEmpty()) {
                    currentUser.setPronouns("Other");
                    currentUser.setSpecifiedPronouns(otherPronounsSettingsTF.getText());
                } else {
                    currentUser.setPronouns(pronouns);
                    currentUser.setSpecifiedPronouns("");
                }
            }

            // Get theme color
            Color themeColor = profileThemeColorPicker.getValue();
            if (themeColor != null) {
                currentUser.setThemeColor(themeColor);
            }

            // Preserve existing user data that wasn't modified in the UI
            if (completeUserFromDB != null) {
                // Preserve all fields that aren't directly edited in this tab
                currentUser.setEmail(email);

                // If these fields aren't in the UI, preserve them from the complete user data
                if (currentUser.getFirstName() == null) currentUser.setFirstName(completeUserFromDB.getFirstName());
                if (currentUser.getLastName() == null) currentUser.setLastName(completeUserFromDB.getLastName());
                if (currentUser.getDob() == null) currentUser.setDob(completeUserFromDB.getDob());
                if (currentUser.getPhoneNumber() == null) currentUser.setPhoneNumber(completeUserFromDB.getPhoneNumber());
                if (currentUser.getPasswordHash() == null) currentUser.setPasswordHash(completeUserFromDB.getPasswordHash());
            }

            // Update display name directly and check result
            boolean nameUpdated = false;
            if (newDisplayName != null && !newDisplayName.isEmpty()) {
                currentUser.setPreferredName(newDisplayName);
                nameUpdated = db.updateUserDisplayName(originalUsername, newDisplayName);
            }

            // Update properties in User object
            boolean propertiesUpdated = db.updateUser(currentUser);

            // Show confirmation
            if (propertiesUpdated || nameUpdated) {
                // Update the current user in both singletons
                db.setCurrentUser(currentUser);
                utilities.setCurrentUser(currentUser);

                showAlert(Alert.AlertType.INFORMATION, "Success", "Your profile settings have been updated successfully.");
            } else {
                showAlert(Alert.AlertType.WARNING, "Warning", "No changes were detected or saved.");
            }

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to save changes: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Called when the "Update Avatar" button is clicked.
     * Loads the Create Avatar screen and switches the current scene to it.
     *
     * @param event the action event triggered by the button click
     */
    @FXML
    void updateAvatarBtnClicked(ActionEvent event) {
        try {
            System.out.println("Attempting to start create avatar screen");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/createAvatarScreen.fxml"));
            AnchorPane root = loader.load();
            System.out.println("createAvatarScreen.fxml loaded successfully");
            Stage currentStage = (Stage) eProfileName.getScene().getWindow(); // eProfileName is a node in your current scene

            // Create a new scene with explicit dimensions that match your design
            Scene createAvatarScene = new Scene(root, 1000, 800);

            System.out.println("Create avatar screen displayed");
            currentStage.setScene(createAvatarScene);
            currentStage.setTitle("Create Avatar");
            currentStage.show();
        } catch (Exception e) {
            System.out.println("Error initializing create avatar screen");
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to open avatar screen: " + e.getMessage());
        }
    }

    //---------------------Manage Favorite Tab---------------------------------\\
    @FXML
    private Tab manageFavoritesTab;
    @FXML
    private ImageView garbageIV;
    @FXML
    private Button favoritePhraseBtn, favoritePicturesButton, saveManageFavoritesSettingButton;

    /**
     * Called when the "Favorite Phrase" button is clicked.
     * Opens a dialog to input a favorite phrase.
     *
     * @param event the action event triggered by the button click
     */
    @FXML
    void favoritePhraseBtnClicked(ActionEvent event) {
        // Show a dialog to input a favorite phrase
        TextInputDialog dialog = new TextInputDialog(favoritePhrase != null ? favoritePhrase : "");
        dialog.setTitle("Favorite Phrase");
        dialog.setHeaderText("Add a favorite phrase");
        dialog.setContentText("Enter your favorite phrase:");

        dialog.showAndWait().ifPresent(phrase -> {
            if (!phrase.isEmpty()) {
                favoritePhrase = phrase;
                showAlert(Alert.AlertType.INFORMATION, "Success", "Favorite phrase saved: " + phrase);
            }
        });
    }

    /**
     * Called when the "Clear Favorites" button is clicked.
     * Clears the user's favorite items.
     *
     * @param event the action event triggered by the button click
     */
    @FXML
    void clearFavoriteBtnClicked(ActionEvent event) {
        // Clear favorites
        favoritePhrase = null;
        currentUser.setFavoritePicture(null);

        // Show confirmation
        showAlert(Alert.AlertType.INFORMATION, "Success", "All favorites have been cleared.");
    }

    /**
     * Called when the "Favorite Picture" button is clicked.
     * Opens a file chooser dialog to allow the user to select an image as their favorite picture.
     * Sets the selected image as the user's favorite picture if chosen.
     *
     * @param event the action event triggered by the button click
     */
    @FXML
    void favoritePicBtnClicked(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Favorite Picture");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"));
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            currentUser.setFavoritePicture(selectedFile.toURI().toString());
            System.out.println("Favorite picture set to: " + selectedFile.getName());
            showAlert(Alert.AlertType.INFORMATION, "Success", "Favorite picture set to: " + selectedFile.getName());
        } else {
            System.out.println("No picture selected.");
        }
    }

    /**
     * Called when the "Save Changes" button is clicked in the application or accessibility tab.
     * Saves settings such as theme color, text size, volume, voice, theme, and notification preferences to the current user.
     *
     * @param event the action event triggered by the button click
     */
    @FXML
    void saveChangesBtnClicked(ActionEvent event) {
        try {
            // Get the source button to determine which tab's settings to save
            Button sourceButton = (Button) event.getSource();

            // Ensure complete user data is preserved
            preserveCompleteUserData();

            // Case 1: Save Manage Favorites settings
            if (sourceButton == saveManageFavoritesSettingButton) {
                // Favorites are saved immediately when buttons are clicked
                // This just confirms and persists them to the database
                boolean updated = db.updateUser(currentUser);

                if (updated) {
                    // Update singletons
                    db.setCurrentUser(currentUser);
                    utilities.setCurrentUser(currentUser);

                    showAlert(Alert.AlertType.INFORMATION, "Success", "Favorite settings saved successfully.");
                } else {
                    showAlert(Alert.AlertType.WARNING, "Warning", "Failed to save favorite settings.");
                }
            }
            // Case 2: Save Accessibility settings
            else if (sourceButton == saveAccessibilitySettingsButton) {
                // Apply UI-bound values to the user object
                currentUser.setTextSize(adjustTextSizeSlider.getValue());
                currentUser.setVolume(adjustVolumeSlider.getValue());

                if (chooseVoiceCB.getValue() != null) {
                    currentUser.setSelectedVoice(chooseVoiceCB.getValue());
                }

                // Save to database
                boolean updated = db.updateUser(currentUser);

                if (updated) {
                    // Update singletons
                    db.setCurrentUser(currentUser);
                    utilities.setCurrentUser(currentUser);

                    showAlert(Alert.AlertType.INFORMATION, "Success", "Accessibility settings saved successfully.");
                } else {
                    showAlert(Alert.AlertType.WARNING, "Warning", "Failed to save accessibility settings.");
                }
            }
            // Case 3: Save Application settings
            else if (sourceButton == saveApplicationSettingsButton) {
                // Apply UI-bound values to the user object
                if (changeAppThemeCB.getValue() != null) {
                    currentUser.setAppTheme(changeAppThemeCB.getValue());
                }

                currentUser.setNotificationsOn(notificationOnRadioButton.isSelected());

                // Save to database
                boolean updated = db.updateUser(currentUser);

                if (updated) {
                    // Update singletons
                    db.setCurrentUser(currentUser);
                    utilities.setCurrentUser(currentUser);

                    showAlert(Alert.AlertType.INFORMATION, "Success", "Application settings saved successfully.");
                } else {
                    showAlert(Alert.AlertType.WARNING, "Warning", "Failed to save application settings.");
                }
            }

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to save changes: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Helper method to ensure complete user data is preserved during updates
     */
    private void preserveCompleteUserData() {
        if (completeUserFromDB != null) {
            // Make sure required fields are preserved
            if (currentUser.getEmail() == null || currentUser.getEmail().isEmpty()) {
                currentUser.setEmail(completeUserFromDB.getEmail());
            }
            if (currentUser.getFirstName() == null) currentUser.setFirstName(completeUserFromDB.getFirstName());
            if (currentUser.getLastName() == null) currentUser.setLastName(completeUserFromDB.getLastName());
            if (currentUser.getDob() == null) currentUser.setDob(completeUserFromDB.getDob());
            if (currentUser.getPhoneNumber() == null) currentUser.setPhoneNumber(completeUserFromDB.getPhoneNumber());
            if (currentUser.getPasswordHash() == null) currentUser.setPasswordHash(completeUserFromDB.getPasswordHash());
        }
    }

    //---------------------Accessibility Options Tab---------------------------------\\
    @FXML
    private Pane accessibilityOptionsPane;
    @FXML
    private Tab accessibilityTab;
    @FXML
    private Slider adjustTextSizeSlider, adjustVolumeSlider;
    @FXML
    private ComboBox<String> chooseVoiceCB;
    @FXML
    private Button saveAccessibilitySettingsButton;


    //---------------------Applications settings Tab---------------------------------\\
    @FXML
    private Tab applicationTab;
    @FXML
    private ComboBox<String> changeAppThemeCB;
    @FXML
    private Button clearFavotitesButton;
    @FXML
    private ImageView currentAvatarImageView;
    @FXML
    private Pane editProfilePane;
    @FXML
    private Button saveApplicationSettingsButton;
    @FXML
    private RadioButton notificationOnRadioButton, notificationsOffRadioButton;

    /**
     * Initializes the controller. Loads the current user's data and populates the Edit Profile UI fields.
     * Also handles conditional display for non-predefined gender/pronoun values.
     */
    @FXML
    void initialize() {
        try {
            // Try getting current user from both possible sources
            currentUser = utilities.getCurrentUser();
            if (currentUser == null) {
                currentUser = db.getCurrentUser();
            }

            if (currentUser != null) {
                System.out.println("Current user loaded: " + currentUser.getUsername());

                // Get complete user data from database to ensure all fields are available
                completeUserFromDB = db.queryUserByName(currentUser.getUsername());
                if (completeUserFromDB != null) {
                    // Use the complete user data for initialization
                    currentUser = completeUserFromDB;

                    // Keep singletons updated
                    db.setCurrentUser(currentUser);
                    utilities.setCurrentUser(currentUser);

                    System.out.println("Complete user data loaded from database");
                }

                // Setup initial UI component states before setting values

                // Initialize gender dropdown
                editGenderSettingsCB.getItems().addAll("Male", "Female", "Non-binary", "Other", "Prefer not to say");

                // Initialize pronouns dropdown
                editPronounsSettingsCB.getItems().addAll("He/Him", "She/Her", "They/Them", "Other", "Prefer not to say");

                // Setup voice options
                chooseVoiceCB.getItems().addAll("en-US-JessaNeural", "en-US-GuyNeural", "en-US-AriaNeural");

                // Setup app theme options
                changeAppThemeCB.getItems().addAll("Light", "Dark", "High Contrast");

                // Fill Edit Profile fields
                String displayName = "";
                if (currentUser.getPreferredName() != null) {
                    displayName = currentUser.getPreferredName();
                } else if (currentUser.getFirstName() != null) {
                    displayName = currentUser.getFirstName();
                }
                eProfileName.setText(displayName);

                // Set username - and make the field read-only
                eUsernameTF.setText(currentUser.getUsername());
                eUsernameTF.setEditable(false); // Make username field read-only

                // Set email if email field exists
                if (emailTF != null && currentUser.getEmail() != null) {
                    emailTF.setText(currentUser.getEmail());
                }

                // Setup other fields initially hidden and add listeners
                otherGenderSettingsTF.setVisible(false);
                otherPronounsSettingsTF.setVisible(false);

                // Add listener for gender combobox
                editGenderSettingsCB.valueProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue != null && newValue.equals("Other")) {
                        otherGenderSettingsTF.setVisible(true);
                    } else {
                        otherGenderSettingsTF.setVisible(false);
                    }
                });

                // Add listener for pronouns combobox
                editPronounsSettingsCB.valueProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue != null && newValue.equals("Other")) {
                        otherPronounsSettingsTF.setVisible(true);
                    } else {
                        otherPronounsSettingsTF.setVisible(false);
                    }
                });

                // Handle gender from user - with null checks
                String gender = currentUser.getGender();
                String specifiedGender = currentUser.getSpecifiedGender();

                if (gender != null) {
                    if (Arrays.asList("Male", "Female", "Non-binary", "Prefer not to say").contains(gender)) {
                        editGenderSettingsCB.setValue(gender);
                    } else if ("Other".equals(gender) && specifiedGender != null && !specifiedGender.isEmpty()) {
                        editGenderSettingsCB.setValue("Other");
                        otherGenderSettingsTF.setText(specifiedGender);
                        otherGenderSettingsTF.setVisible(true);
                    } else {
                        // For any unrecognized gender, set as "Other"
                        editGenderSettingsCB.setValue("Other");
                        if (gender != null && !gender.isEmpty() && !gender.equals("Other")) {
                            otherGenderSettingsTF.setText(gender);
                        } else if (specifiedGender != null && !specifiedGender.isEmpty()) {
                            otherGenderSettingsTF.setText(specifiedGender);
                        }
                        otherGenderSettingsTF.setVisible(true);
                    }
                } else {
                    // Default to "Prefer not to say" if null
                    editGenderSettingsCB.setValue("Prefer not to say");
                }

                // Handle pronouns from user - with null checks
                String pronouns = currentUser.getPronouns();
                String specifiedPronouns = currentUser.getSpecifiedPronouns();

                if (pronouns != null) {
                    if (Arrays.asList("He/Him", "She/Her", "They/Them", "Prefer not to say").contains(pronouns)) {
                        editPronounsSettingsCB.setValue(pronouns);
                    } else if ("Other".equals(pronouns) && specifiedPronouns != null && !specifiedPronouns.isEmpty()) {
                        editPronounsSettingsCB.setValue("Other");
                        otherPronounsSettingsTF.setText(specifiedPronouns);
                        otherPronounsSettingsTF.setVisible(true);
                    } else {
                        // For any unrecognized pronouns, set as "Other"
                        editPronounsSettingsCB.setValue("Other");
                        if (pronouns != null && !pronouns.isEmpty() && !pronouns.equals("Other")) {
                            otherPronounsSettingsTF.setText(pronouns);
                        } else if (specifiedPronouns != null && !specifiedPronouns.isEmpty()) {
                            otherPronounsSettingsTF.setText(specifiedPronouns);
                        }
                        otherPronounsSettingsTF.setVisible(true);
                    }
                } else {
                    // Default to "Prefer not to say" if null
                    editPronounsSettingsCB.setValue("Prefer not to say");
                }

                // Initialize color picker with user's theme color
                if (currentUser.getThemeColor() != null) {
                    profileThemeColorPicker.setValue(currentUser.getThemeColor());
                } else {
                    // Default color if none set
                    profileThemeColorPicker.setValue(Color.web("#4286f4"));
                }

                // Set voice option
                if (currentUser.getSelectedVoice() != null) {
                    chooseVoiceCB.setValue(currentUser.getSelectedVoice());
                } else {
                    chooseVoiceCB.setValue("en-US-JessaNeural");  // Default value
                }

                // Set slider values
                adjustTextSizeSlider.setValue(currentUser.getTextSize() > 0 ? currentUser.getTextSize() : 14.0);
                adjustVolumeSlider.setValue(currentUser.getVolume() > 0 ? currentUser.getVolume() : 50.0);

                // Set app theme
                if (currentUser.getAppTheme() != null) {
                    changeAppThemeCB.setValue(currentUser.getAppTheme());
                } else {
                    changeAppThemeCB.setValue("Light");  // Default value
                }

                // Set notification preferences
                boolean notificationsEnabled = currentUser.isNotificationsOn();
                notificationOnRadioButton.setSelected(notificationsEnabled);
                notificationsOffRadioButton.setSelected(!notificationsEnabled);

                // Group radio buttons
                ToggleGroup notificationGroup = new ToggleGroup();
                notificationOnRadioButton.setToggleGroup(notificationGroup);
                notificationsOffRadioButton.setToggleGroup(notificationGroup);

                // Handle favorite phrase - with null check
                favoritePhrase = ""; // Initialize with empty string

                System.out.println("Settings UI initialized with user data");
            } else {
                System.err.println("Error: No current user found for settings initialization");
                showAlert(Alert.AlertType.ERROR, "Error", "No user is currently logged in. Please log in first.");
                // Go back to login screen
                try {
                    Stage stage = (Stage) root.getScene().getWindow();
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/loginScreen.fxml"));
                    Scene loginScene = new Scene(loader.load(), 800, 600);
                    stage.setScene(loginScene);
                    stage.show();
                } catch (Exception e) {
                    System.err.println("Error navigating to login screen: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            System.err.println("Error initializing settings: " + e.getMessage());
            e.printStackTrace();
        }
    }

    //---------------------Delete Tab---------------------------------\\
    @FXML
    private TextArea feedbackTA;
    @FXML
    private Tab deleteTab;
    @FXML
    private AnchorPane root;

    @FXML
    void deleteBtnClicked(ActionEvent event) {
        if (currentUser == null || currentUser.getUsername() == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "No user is currently logged in.");
            return;
        }

        String username = currentUser.getUsername();
        try {
            // Confirm deletion
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirm Deletion");
            confirmAlert.setHeaderText("Are you sure you want to delete your account?");
            confirmAlert.setContentText("This action cannot be undone.");

            confirmAlert.showAndWait().ifPresent(result -> {
                if (result == ButtonType.OK) {
                    // Process feedback if provided
                    String feedbackMsg = feedbackTA.getText();
                    if(feedbackMsg != null && !feedbackMsg.trim().isEmpty()) {
                        boolean Savedfeedback = DB.getInstance().insertExitFeedback(feedbackMsg);
                        if (Savedfeedback) {
                            System.out.println("Feedback saved.");
                        } else {
                            System.out.println("Feedback not saved.");
                        }
                    }

                    // Delete user account
                    boolean isDeleted = DB.getInstance().deleteUserByUsername(username);

                    // Clear user from both singletons
                    db.setCurrentUser(null);
                    utilities.setCurrentUser(null);

                    if (isDeleted) {
                        System.out.println("Delete Confirmed for: " + username);
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Account: " + username + " has been deleted");
                        alert.setHeaderText(null);
                        alert.setContentText("The account was successfully deleted. The application will close.");
                        alert.showAndWait();
                        Platform.exit();
                    } else {
                        System.out.println("Delete failed for: " + username);
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Delete unsuccessful");
                        alert.setHeaderText(null);
                        alert.setContentText("The account deletion was unsuccessful. The application will close.");
                        alert.showAndWait();
                        Platform.exit();
                    }
                }
            });
        } catch (Exception e) {
            System.err.println("Error deleting account: " + e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("An error occurred: " + e.getMessage());
            alert.showAndWait();
            throw new RuntimeException(e);
        }
    }

    /**
     * Helper method to show alerts with customizable type, title, and content
     * @param type Alert type (e.g., INFORMATION, WARNING, ERROR)
     * @param title Title of the alert
     * @param content Content message of the alert
     */
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private Button saveAllChangesButton;

}