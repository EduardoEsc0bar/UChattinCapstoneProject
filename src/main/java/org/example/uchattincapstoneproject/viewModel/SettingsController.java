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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.uchattincapstoneproject.model.DB;
import org.example.uchattincapstoneproject.model.User;
import org.example.uchattincapstoneproject.model.Util;

import java.io.File;

public class SettingsController {
    DB db = DB.getInstance();
    Util utilities = Util.getInstance();
    User currentUser;



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

    /**
     * Called when the "Save All Changes" button in the Edit Profile tab is clicked.
     * Updates the current user's display name in the database based on the input fields.
     *
     * @param event the action event triggered by the button click
     */
    @FXML
    void saveAllChangesBtnClicked(ActionEvent event) {
        String username = eUsernameTF.getText();
        String displayName = eProfileName.getText();
        db.updateUserDisplayName(username, displayName);
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
            Scene createAvatarScene = new Scene(root, 800, 600);
            System.out.println("Create avatar screen displayed");
            currentStage.setScene(createAvatarScene);
            currentStage.setTitle("Create Avatar");
            currentStage.show();
        } catch (Exception e) {
            System.out.println("Error initializing create avatar screen");
            e.printStackTrace();
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
     * This method is currently commented out, but would be used to set a favorite phrase for the user.
     *
     * @param event the action event triggered by the button click
     */
    @FXML
    void favoritePhraseBtnClicked(ActionEvent event) {
//        String selectedPhrase = favoritePhraseTextArea.getText(); // example component
//        if (selectedPhrase != null && !selectedPhrase.isEmpty()) {
//            currentUser.setFavoritePhrase(selectedPhrase);
//            System.out.println("Favorite phrase set: " + selectedPhrase);
//        } else {
//            System.out.println("No phrase entered.");
//        }
    }

    /**
     * Called when the "Clear Favorites" button is clicked.
     * This method is currently a placeholder for functionality to clear user's favorite items.
     *
     * @param event the action event triggered by the button click
     */
    @FXML
    void clearFavoriteBtnClicked(ActionEvent event) {

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
        // Example: apply UI-bound values to the user object
        currentUser.setThemeColor(profileThemeColorPicker.getValue());
        currentUser.setTextSize(adjustTextSizeSlider.getValue());
        currentUser.setVolume(adjustVolumeSlider.getValue());
        currentUser.setSelectedVoice(chooseVoiceCB.getValue());
        currentUser.setAppTheme(changeAppThemeCB.getValue());
        currentUser.setNotificationsOn(notificationOnRadioButton.isSelected());

        // Persist the user object (e.g., save to file or database)
//        userDAO.save(currentUser); // Assuming you have a DAO class
        System.out.println("Changes saved.");
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
        db = DB.getInstance();
        currentUser = db.getCurrentUser();
        if (currentUser != null) {
            // Fill Edit Profile fields
            eProfileName.setText(currentUser.getFirstName());
            eUsernameTF.setText(currentUser.getUsername());
            // Assuming your User object has gender and pronouns fields
            if (currentUser.getGender() != null) {
                editGenderSettingsCB.setValue(currentUser.getGender());
                // If gender is not a predefined choice, show otherGenderSettingsTF
                if (!editGenderSettingsCB.getItems().contains(currentUser.getGender())) {
                    otherGenderSettingsTF.setText(currentUser.getGender());
                    otherGenderSettingsTF.setVisible(true);
                }
            }
            if (currentUser.getPronouns() != null) {
                editPronounsSettingsCB.setValue(currentUser.getPronouns());
                if (!editPronounsSettingsCB.getItems().contains(currentUser.getPronouns())) {
                    otherPronounsSettingsTF.setText(currentUser.getPronouns());
                    otherPronounsSettingsTF.setVisible(true);
                }
            }
            //Change voice//
//            chooseVoiceCB.getItems().addAll("en-US-JessaNeural", "en-US-GuyNeural", "en-US-AriaNeural"); // Add more voices as needed
//            chooseVoiceCB.setValue("en-US-JessaNeural");  // Default value
//            // Add listener to ComboBox for when the user changes the voice
//            chooseVoiceCB.valueProperty().addListener((observable, oldValue, newValue) -> {
//                if (newValue != null) {
//                    speechService.setVoice(newValue);  // Update the voice in SpeechService
//                    System.out.println("Voice changed to: " + newValue);
//                }
//            });
//            // If User class supports a theme color (assuming stored as hex or Color), set it
//            if (currentUser.getThemeColor() != null) {
//                profileThemeColorPicker.setValue(currentUser.getThemeColor()); // assuming getThemeColor() returns javafx.scene.paint.Color
//            }
//            // You can also load avatar into currentAvatarImageView if stored
//            if (currentUser.getAvatarImage() != null) {
//                currentAvatarImageView.setImage(currentUser.getAvatarImage());
//            }

        }
    }

    //---------------------Delete Tab---------------------------------\\
    @FXML
    private TextArea feedbackTA;
    @FXML
    private Tab deleteTab;

    @FXML
    void deleteBtnClicked(ActionEvent event) {
        String username = utilities.getCurrentUser().getUsername();
        if (username != null) {
            try {
                String feedbackMsg = feedbackTA.getText();
                if(feedbackMsg != null && !feedbackMsg.trim().isEmpty()) {
                    boolean Savedfeedback = DB.getInstance().insertExitFeedback(feedbackMsg);
                    if (Savedfeedback) {
                        System.out.println("Feedback saved.");
                    }else{
                        System.out.println("Feedback not saved.");
                    }
                }
                boolean isDeleted = DB.getInstance().deleteUserByUsername(username);
                utilities.setCurrentUser(null);
                if (isDeleted) {
                    System.out.println("Delete Confirmed for: " + username);
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Account: " + username + " has been deleted");
                    alert.setHeaderText(null);
                    alert.setContentText("The account was successfully deleted. The application will close.");
                    alert.showAndWait();
                    Platform.exit();
                }else{
                    System.out.println("Delete failed for: " + username);
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("delete unsuccessful");
                    alert.setHeaderText(null);
                    alert.setContentText("The account deletion was unsuccessful. The application will close.");
                    alert.showAndWait();
                    Platform.exit();
                }
            } catch (Exception e) {
                System.err.println("Error deleting account: " + e.getMessage());
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("An error occured: " + e.getMessage());
                alert.showAndWait();
                throw new RuntimeException(e);
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Account was not deleted");
            alert.setHeaderText(null);
            alert.setContentText("The account was not successfully deleted. The application will close.");
            alert.showAndWait();
            Platform.exit();
        }
    }

    @FXML
    private Button saveAllChangesButton;

}
