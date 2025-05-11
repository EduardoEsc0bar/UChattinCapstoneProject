package org.example.uchattincapstoneproject.viewModel;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.uchattincapstoneproject.model.Avatar;
import org.example.uchattincapstoneproject.model.DB;
import org.example.uchattincapstoneproject.model.DiceBearAPI;
import org.example.uchattincapstoneproject.model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class CreateAvatarController {
    @FXML private ImageView avatarImageView;
    @FXML private TilePane avatarStyleTP;
    @FXML private Button avatarStyleButton, saveAvatarButton, resetAvatarButton, backToRegistrationButton, profilePicButton;
    @FXML private AnchorPane root;
    @FXML private Pane createAvatarPane;

    private DiceBearAPI diceBearAPI = new DiceBearAPI();
    private Avatar avatar;
    private User user;
    private DB dbInstance;

    final String DB_URL = "jdbc:mysql://commapp.mysql.database.azure.com:3306/communication_app";
    final String DB_USER = "commapp_db_user";
    final String DB_PASSWORD = "farm9786$";

    //initialize controller
    @FXML
    private void initialize() {
        System.out.println("create avatar controller initialized");

        Platform.runLater(() -> {
            UIUtilities.centerContent(root, createAvatarPane);

            root.widthProperty().addListener((observable, oldValue, newValue) -> UIUtilities.centerContent(root, createAvatarPane));
            root.heightProperty().addListener((observable, oldValue, newValue) -> UIUtilities.centerContent(root, createAvatarPane));
        });

        // Get DB instance
        dbInstance = DB.getInstance();
        User stored = dbInstance.getCurrentUser();
        Avatar storedAvatar = dbInstance.getCurrentAvatar();

        if(stored != null) {
            setUser(stored);
        }

        if(storedAvatar != null) {
            setAvatar(storedAvatar);
        }

        //set button actions for categories
        avatarStyleButton.setOnAction(event -> populateAvatarTilePane()); //load avatar images on tile pane

        //set button actions for avatar modification
        saveAvatarButton.setOnAction(event -> registerUser());
        resetAvatarButton.setOnAction(event -> resetAvatar());
        backToRegistrationButton.setOnAction(event -> navigateToRegistrationScreen());
        profilePicButton.setOnAction(event -> selectProfilePicture());

        //load avatar preview (default)
        //resetAvatar();
    }

    public void setUser(User user) {
        this.user = user;
        System.out.println("User set in CreateAvatarController: " + (user != null ? user.getUsername() : "null"));
    }

    public void setAvatar(Avatar avatar) {
        this.avatar = avatar;
        dbInstance.setCurrentAvatar(avatar);
        dbInstance.getCurrentUser().setAvatarURL(avatar.getAvatarURL());
        avatarImageView.setImage(new Image(avatar.getAvatarURL()));

        System.out.println("avatar set successfully");
        System.out.println("style: " + avatar.getStyle());
        System.out.println("avatar url: " + avatar.getAvatarURL());
    }

    @FXML
    private void selectProfilePicture(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));

        File selectedFile = fileChooser.showOpenDialog(null);
        if(selectedFile != null){
            String filePath = selectedFile.toURI().toString();
            Image profileImage = new Image(filePath);
            if(profileImage.isError()){
                System.err.println("error loading profile image " + filePath);
            }else{
                avatarImageView.setImage(new Image(filePath));
                dbInstance.getCurrentUser().setAvatarURL(filePath);
                System.out.println("profile picture set: " + filePath);
            }

        }
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
                    "Your avatar has been saved. Please log in!");

            // Navigate to main screen after saving
            UIUtilities.navigateToScreen("/views/entranceScreen.fxml", avatarStyleButton.getScene(), false);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error Saving Avatar",
                    "An error occurred while saving your avatar: " + e.getMessage());
        }
    }

    @FXML
    private void registerUser() {

        if (user == null || avatar == null) {
            System.err.println("user or avatar object is null");
            showAlert(Alert.AlertType.ERROR, "Registration Error", "All required fields must be filled.");
            return;
        }

        //if no profile, check avatar
        String finalURL = dbInstance.getCurrentAvatar().getAvatarURL();
        if(finalURL == null || finalURL.isEmpty()){
            finalURL = (dbInstance.getCurrentUser().getAvatarURL() != null) ? dbInstance.getCurrentAvatar().getAvatarURL(): null;
        }

        //ensure at least one image is seleced before saving
        if(finalURL == null || finalURL.isEmpty()){
            showAlert(Alert.AlertType.ERROR, "Error", "Please select an avatar or upload a profile picture");
        }

        // Hash the password before storing it
        String hashedPassword = BCrypt.hashpw(user.getPasswordHash().trim(), BCrypt.gensalt());

        System.out.println("Attempting to register user:");
        System.out.println("Username: " + user.getUsername());
        System.out.println("Hashed Password: " + hashedPassword);
        System.out.println("first name: " + user.getFirstName());
        System.out.println("last name: " + user.getLastName());
        System.out.println("email: " + user.getEmail());
        System.out.println("phone number: " + user.getPhoneNumber());
        System.out.println("dob: " + user.getDob());
        System.out.println("gender: " + user.getGender());
        System.out.println("pronouns: " + user.getPronouns());

        //verify before proceeding
        if(user.getFirstName().isEmpty() || user.getLastName().isEmpty() || user.getEmail().isEmpty() || user.getPhoneNumber().isEmpty()
        || user.getDob().isEmpty() || user.getGender().isEmpty() || user.getPronouns().isEmpty() || user.getUsername().isEmpty() || user.getPasswordHash().isEmpty()){
            showAlert(Alert.AlertType.ERROR, "Registration Error", "All required fields must be filled.");
            return;
        }


        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "INSERT INTO Users (first_name, last_name, preferred_name, phone_number, email, dob, gender, specified_pronouns, username, password_hash, avatar_url) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?)";
            PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            stmt.setString(1, user.getFirstName());
            stmt.setString(2, user.getLastName());
            stmt.setString(3, user.getPreferredName().isEmpty() ? user.getFirstName() : user.getPreferredName());
            stmt.setString(4, user.getPhoneNumber());
            stmt.setString(5, user.getEmail());
            stmt.setString(6, user.getDob());
            stmt.setString(7, user.getGender().equals("Other") ? user.getSpecifiedGender() : user.getGender());
            stmt.setString(8, user.getPronouns().equals("Other") ? user.getSpecifiedPronouns() : user.getPronouns());
            stmt.setString(9, user.getUsername());
            stmt.setString(10, hashedPassword);
            stmt.setString(11, finalURL);

            int rowsAffected = stmt.executeUpdate();
            if(rowsAffected == 0){
                showAlert(Alert.AlertType.ERROR, "Registration Failed", "User was not saved");
                return;
            }

            //get generated user id
            int userID = -1;
            try(var generatedKeys = stmt.getGeneratedKeys()) {
                if(generatedKeys.next()){
                    userID = generatedKeys.getInt(1);
                    System.out.println("user registered with id: " + userID);
                }
            }

            if(userID == -1){
                showAlert(Alert.AlertType.ERROR, "Registration Failed", "User was not saved");
                return;
            }

            //insert avatar into avatar table
            if(dbInstance.getCurrentAvatar() != null){
                String avatarSQL = "INSERT INTO Avatars (user_id, style, avatar_url, created_at) VALUES (?, ?, ?, NOW())";
                PreparedStatement avatarStmt = conn.prepareStatement(avatarSQL, PreparedStatement.RETURN_GENERATED_KEYS);
                avatarStmt.setInt(1, userID);
                avatarStmt.setString(2, avatar.getStyle());
                avatarStmt.setString(3, avatar.getAvatarURL());
                int avatarRows = avatarStmt.executeUpdate();
                if(avatarRows == 0){
                    showAlert(Alert.AlertType.ERROR, "Avatar Error", "Avatar was not saved");
                    return;
                }

                //generate avatar id
                int avatarID = -1;
                try(var generatedKeys = avatarStmt.getGeneratedKeys()) {
                    if(generatedKeys.next()){
                        avatarID = generatedKeys.getInt(1);
                        System.out.println("avatar registered with id: " + avatarID);
                    }
                }
                //update user table with avatar
                String updateUserSQL = "UPDATE Users SET avatar_id = ? WHERE id = ?";
                PreparedStatement updateUserStmt = conn.prepareStatement(updateUserSQL);
                updateUserStmt.setInt(1, avatarID);
                updateUserStmt.setInt(2, userID);


                int updateRows = updateUserStmt.executeUpdate();
                if(updateRows > 0){
                    showAlert(Alert.AlertType.INFORMATION, "Registration Complete", "Account Created! Please log in!");
                    navigateToEntranceScreen();
                }
            }

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to register user.");
        }
    }

    //resets avatar to default
    private void resetAvatar() {
        updateAvatar("pixel-art");
        System.out.println("reset avatar preview");
    }

    private void navigateToEntranceScreen(){
        System.out.println("navigating to entrance screen from create avatar");
        UIUtilities.navigateToScreen("/views/entranceScreen.fxml", root.getScene(), false);
    }

    @FXML
    private void navigateToRegistrationScreen(){
        System.out.println("navigating back to registration screen");
        UIUtilities.navigateToScreen("/views/registrationScreen.fxml", root.getScene(), false);
    }


    // Display alert
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}