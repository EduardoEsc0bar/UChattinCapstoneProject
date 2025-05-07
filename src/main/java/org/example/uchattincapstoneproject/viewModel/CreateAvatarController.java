package org.example.uchattincapstoneproject.viewModel;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.uchattincapstoneproject.model.Avatar;
import org.example.uchattincapstoneproject.model.DB;
import org.example.uchattincapstoneproject.model.DiceBearAPI;
import org.example.uchattincapstoneproject.model.User;

import java.io.IOException;
import java.util.List;

public class CreateAvatarController {
    @FXML private ImageView avatarImageView;
    @FXML private TilePane avatarStyleTP;
    @FXML private Button avatarStyleButton, saveAvatarButton, resetAvatarButton;

    private DiceBearAPI diceBearAPI = new DiceBearAPI();
    private Avatar avatar;
    private User user;
    private DB dbInstance;

    //initialize controller
    @FXML
    private void initialize() {
        System.out.println("create avatar controller initialized");

        // Get DB instance
        dbInstance = DB.getInstance();

        //set button actions for categories
        avatarStyleButton.setOnAction(event -> populateAvatarTilePane()); //load avatar images on tile pane

        //set button actions for avatar modification
        saveAvatarButton.setOnAction(event -> saveAvatar());
        resetAvatarButton.setOnAction(event -> resetAvatar());

        //load avatar preview (default)
        resetAvatar();
    }

    /**
     * Sets the user for this controller.
     * This method is called from the registration controller to pass user data.
     *
     * @param user The user object containing registration information
     */
    public void setUser(User user) {
        this.user = user;
        System.out.println("User set in CreateAvatarController: " + (user != null ? user.getUsername() : "null"));
    }

    //populates tile tilePane with customization options
    private void populateAvatarTilePane(){
        avatarStyleTP.getChildren().clear();
        List<String> availableStyles = diceBearAPI.getAvailableStyles();
        System.out.println("loading available styles: " + availableStyles);

        for(String style : availableStyles){
            System.out.println("fetching avatar preview for style: " + style);
            Image avatarImage = diceBearAPI.fetchAvatar(style);
            if(avatarImage == null || avatarImage.isError()){
                System.err.println("Error fetching avatar preview for style: " + style + " |Error: " + avatarImage.isError());
                continue;
            }

            ImageView imageView = new ImageView(avatarImage);
            imageView.setFitHeight(150);
            imageView.setFitWidth(150);
            imageView.setPreserveRatio(true);
            imageView.setCache(true);
            imageView.setSmooth(true);

            Button button = new Button();
            button.setGraphic(imageView);
            button.setOnAction(event -> updateAvatar(style));

            avatarStyleTP.getChildren().add(button);
        }
    }

    //updates avatar preview
    private void updateAvatar(String style) {
        avatar = new Avatar(style, diceBearAPI);
        Image avatarImage = diceBearAPI.fetchAvatar(style);

        if(avatarImage == null || avatarImage.isError()){
            System.err.println("Error fetching avatar preview for style: " + style);
            return;
        }

        avatarImageView.setImage(avatarImage);
        avatarImageView.setVisible(true);
    }

    //saves avatar settings and navigates to main screen
    private void saveAvatar() {
        try {
            if (avatar == null) {
                showAlert(Alert.AlertType.WARNING, "No Avatar Selected", "Please select an avatar style first.");
                return;
            }

            String avatarURL = avatar.getAvatarURL();
            System.out.println("saved avatar URL: " + avatarURL);

            // Update user with avatar information
            if (user != null) {
                user.setAvatar(avatar);
                dbInstance.setCurrentUser(user);
            } else {
                // If user is null, try to get from DB
                user = dbInstance.getCurrentUser();
                if (user != null) {
                    user.setAvatar(avatar);
                }
            }

            showAlert(Alert.AlertType.INFORMATION, "Avatar Saved",
                    "Your avatar has been saved. Proceeding to the main application.");

            // Navigate to main screen after saving
            navigateToMainScreen();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error Saving Avatar",
                    "An error occurred while saving your avatar: " + e.getMessage());
        }
    }

    //resets avatar to default
    private void resetAvatar() {
        updateAvatar("pixel-art");
        System.out.println("reset avatar preview");
    }

    // Navigate to main screen
    private void navigateToMainScreen() {
        try {
            System.out.println("Navigating to main screen");

            // Get the current scene for transition
            Scene currentScene = avatarImageView.getScene();
            if (currentScene == null) {
                throw new RuntimeException("Cannot get current scene");
            }

            // Create fade transition
            FadeTransition fadeout = new FadeTransition(Duration.seconds(1));
            fadeout.setFromValue(1.0);
            fadeout.setToValue(0.0);
            fadeout.setNode(currentScene.getRoot());

            fadeout.setOnFinished(event -> {
                try {
                    // Load main screen - using AnchorPane instead of BorderPane
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/mainScreen.fxml"));
                    AnchorPane mainScreen = loader.load();

                    // Get the controller and pass user information
                    MainViewController mainViewController = loader.getController();

                    if (user != null) {
                        mainViewController.setUsername(user.getUsername());
                        // If you have user ID handling in your MainViewController, set it here
                        // mainViewController.setUserID(...);
                    } else if (dbInstance.getCurrentUser() != null) {
                        // Try to get user from DB if not set directly
                        User currentUser = dbInstance.getCurrentUser();
                        mainViewController.setUsername(currentUser.getUsername());
                    }

                    // Set the scene
                    Stage currentStage = (Stage) currentScene.getWindow();
                    Scene mainScene = new Scene(mainScreen, 800, 600);
                    currentStage.setScene(mainScene);
                    currentStage.setTitle("UChattIn");
                    currentStage.setMaximized(true);

                    // Create fade-in transition
                    FadeTransition fadein = new FadeTransition(Duration.seconds(1));
                    fadein.setFromValue(0.0);
                    fadein.setToValue(1.0);
                    fadein.setNode(mainScreen);
                    fadein.play();

                } catch (IOException e) {
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Navigation Error",
                            "An error occurred while navigating to the main screen: " + e.getMessage());
                }
            });

            fadeout.play();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error",
                    "An error occurred while preparing to navigate to the main screen: " + e.getMessage());
        }
    }

    // Display alert
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}