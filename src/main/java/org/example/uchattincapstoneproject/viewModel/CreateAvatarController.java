package org.example.uchattincapstoneproject.viewModel;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;
import javafx.stage.FileChooser;
import org.example.uchattincapstoneproject.model.Avatar;
import org.example.uchattincapstoneproject.model.DB;
import org.example.uchattincapstoneproject.model.DiceBearAPI;
import org.example.uchattincapstoneproject.model.User;
import org.example.uchattincapstoneproject.model.Util;
import org.mindrot.jbcrypt.BCrypt;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CreateAvatarController {
    @FXML
    private ImageView avatarImageView;
    @FXML
    private TilePane avatarStyleTP;
    @FXML
    private Button avatarStyleButton, saveAvatarButton, resetAvatarButton, backToRegistrationButton, profilePicButton;
    @FXML
    private AnchorPane root;
    @FXML
    private Pane createAvatarPane;
    private Map<String, Image> avatarCache = new HashMap<>();
    private DiceBearAPI diceBearAPI = new DiceBearAPI();
    private Avatar avatar;
    private User user;
    private DB dbInstance;
    private Util utilities;

    // Flag to determine if we're updating an existing user
    private boolean isUpdatingExistingUser = false;

    // For custom profile pictures (not from DiceBear)
    private String customProfilePictureUrl = null;

    final String DB_URL = "jdbc:mysql://commapp.mysql.database.azure.com:3306/communication_app";
    final String DB_USER = "commapp_db_user";
    final String DB_PASSWORD = "farm9786$";

    private FileChooser fileChooser;
    private Image profileImage;

    //initialize controller
    @FXML
    private void initialize() {
        System.out.println("create avatar controller initialized");

        Platform.runLater(() -> {
            UIUtilities.centerContent(root, createAvatarPane);

            root.widthProperty().addListener((observable, oldValue, newValue) -> UIUtilities.centerContent(root, createAvatarPane));
            root.heightProperty().addListener((observable, oldValue, newValue) -> UIUtilities.centerContent(root, createAvatarPane));
        });

        // Get DB instance and Util instance
        dbInstance = DB.getInstance();
        utilities = Util.getInstance();

        // Try to get current user from both sources
        user = utilities.getCurrentUser();
        if (user == null) {
            user = dbInstance.getCurrentUser();
        }

        // Determine if we're updating an existing user (already logged in)
        isUpdatingExistingUser = (user != null && user.getUsername() != null);

        System.out.println("Create Avatar mode: " + (isUpdatingExistingUser ? "UPDATE EXISTING USER" : "NEW USER REGISTRATION"));

        // Get avatar if available
        Avatar storedAvatar = dbInstance.getCurrentAvatar();
        if(storedAvatar != null) {
            setAvatar(storedAvatar);
        }

        //set button actions for categories
        avatarStyleButton.setOnAction(event -> {
            populateAvatarTilePane();
        }); //load avatar images on tile pane

        //set button actions for avatar modification
        saveAvatarButton.setOnAction(event -> saveAvatarChanges());
        resetAvatarButton.setOnAction(event -> resetAvatar());

        // Set back button behavior based on context
        if (isUpdatingExistingUser) {
            // Rename button if necessary
            if (backToRegistrationButton.getText().equals("Back to Registration")) {
                backToRegistrationButton.setText("Back to Settings");
            }

            backToRegistrationButton.setOnAction(event -> navigateToSettingsScreen());
        } else {
            backToRegistrationButton.setOnAction(event -> navigateToRegistrationScreen());
        }

        profilePicButton.setOnAction(event -> selectProfilePicture());

        //load avatar preview (default)
        if (avatar == null) {
            resetAvatar();
        }
    }

    public void loadAvatar(String userId, ImageView avatarImageView) {
        if (avatarCache.containsKey(userId)) {
            avatarImageView.setImage(avatarCache.get(userId));  // Load from cache
        } else {
            Task<Image> loadAvatarTask = new Task<Image>() {
                @Override
                protected Image call() throws Exception {
                    // Simulate image loading (e.g., from file or database)
                    Thread.sleep(500);  // Simulating delay for network/database
                    return new Image("file:/path/to/avatar.png", 100, 100, false, true);  // Resize during load
                }
            };

            loadAvatarTask.setOnSucceeded(event -> {
                Image avatarImage = loadAvatarTask.getValue();
                avatarCache.put(userId, avatarImage);  // Cache the loaded avatar
                avatarImageView.setImage(avatarImage);  // Set avatar to ImageView
            });

            new Thread(loadAvatarTask).start();  // Run image loading in background
        }
    }

    public void setUser(User user) {
        this.user = user;
        System.out.println("User set in CreateAvatarController: " + (user != null ? user.getUsername() : "null"));
    }

    public void setAvatar(Avatar avatar) {
        if(avatar != null){
            this.avatar = avatar;
            dbInstance.setCurrentAvatar(avatar);

            if (dbInstance.getCurrentUser() != null) {
                String avatarUrl = avatar.getAvatarURL();
                dbInstance.getCurrentUser().setAvatarURL(avatarUrl);

                // Update the avatar image view
                avatarImageView.setImage(new Image(avatarUrl));
            }

            System.out.println("avatar set successfully");
            System.out.println("style: " + avatar.getStyle());
            System.out.println("avatar url: " + avatar.getAvatarURL());
        } else {
            System.err.println("error, avatar object is null");
        }
    }

    @FXML
    private void selectProfilePicture(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Profile Picture");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));

        File selectedFile = fileChooser.showOpenDialog(null);
        if(selectedFile != null){
            try{
                String filePath = selectedFile.getAbsolutePath();
                String formattedPath = "file:" + filePath.replace("\\", "/");

                Image profileImage = new Image(formattedPath);
                if(profileImage.isError()){
                    System.err.println("error loading profile image " + formattedPath);
                } else {
                    avatarImageView.setImage(profileImage);

                    // Store the custom profile picture URL
                    customProfilePictureUrl = formattedPath;

                    // Clear the avatar to indicate we're using a custom profile picture
                    avatar = null;

                    if (dbInstance.getCurrentUser() != null) {
                        dbInstance.getCurrentUser().setAvatarURL(formattedPath);
                    }

                    System.out.println("profile picture set: " + formattedPath);
                }
            } catch (Exception e){
                System.err.println("error handling profile image: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    //populates tile tilePane with customization options
    private void populateAvatarTilePane() {
        avatarStyleTP.getChildren().clear();
        List<String> availableStyles = diceBearAPI.getAvailableStyles();
        System.out.println("Loading available styles: " + availableStyles);

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        for (int i = 0; i < availableStyles.size(); i++) {
            String style = availableStyles.get(i);
            int delay = i * 200; // 200ms between each request

            scheduler.schedule(() -> {
                Task<Image> fetchAvatarTask = new Task<>() {
                    @Override
                    protected Image call() {
                        System.out.println("Fetching avatar preview for style: " + style);
                        return diceBearAPI.fetchAvatar(style);
                    }
                };

                fetchAvatarTask.setOnSucceeded(event -> {
                    Image avatarImage = fetchAvatarTask.getValue();
                    if (avatarImage == null || avatarImage.isError()) {
                        System.err.println("Error fetching avatar for style: " + style);
                        return;
                    }

                    ImageView imageView = new ImageView(avatarImage);
                    imageView.setFitHeight(150);
                    imageView.setFitWidth(150);
                    imageView.setPreserveRatio(true);
                    imageView.setCache(true);
                    imageView.setSmooth(true);

                    Button button = new Button();
                    button.setGraphic(imageView);
                    button.setOnAction(e -> updateAvatar(style));

                    Platform.runLater(() -> avatarStyleTP.getChildren().add(button));
                });

                fetchAvatarTask.setOnFailed(event -> {
                    Throwable ex = fetchAvatarTask.getException();
                    System.err.println("Failed to load avatar for style " + style + ": " + ex.getMessage());
                });

                Thread thread = new Thread(fetchAvatarTask);
                thread.setDaemon(true);
                thread.start();
            }, delay, TimeUnit.MILLISECONDS);
        }

        // Optional: Shutdown the scheduler after all tasks are scheduled
        scheduler.schedule(scheduler::shutdown, availableStyles.size() * 200 + 1000, TimeUnit.MILLISECONDS);
    }

    //updates avatar preview
    private void updateAvatar(String style) {
        avatar = new Avatar(style, diceBearAPI);
        Image avatarImage = diceBearAPI.fetchAvatar(style);

        if(avatarImage == null || avatarImage.isError()){
            System.err.println("Error fetching avatar preview for style: " + style);
            return;
        }

        dbInstance.setCurrentAvatar(avatar);

        // Clear custom profile picture since we're using a DiceBear avatar
        customProfilePictureUrl = null;

        if (dbInstance.getCurrentUser() != null) {
            dbInstance.getCurrentUser().setAvatarURL(avatar.getAvatarURL());
        }

        avatarImageView.setImage(avatarImage);
        avatarImageView.setVisible(true);
        System.out.println("avatar set successfully: " + avatar.getAvatarURL());
    }

    /**
     * Main method to save avatar changes - decides whether to update existing user or register new user
     */
    private void saveAvatarChanges() {
        // Check if we have either an avatar or a custom profile picture
        if (avatar == null && customProfilePictureUrl == null) {
            showAlert(Alert.AlertType.WARNING, "No Avatar Selected", "Please select an avatar style or upload a profile picture.");
            return;
        }

        // Check if we're updating an existing user or registering a new one
        if (isUpdatingExistingUser) {
            updateExistingUserAvatar();
        } else {
            registerUser();
        }
    }

    /**
     * Updates avatar for an existing user
     */
    private void updateExistingUserAvatar() {
        try {
            if (user == null || user.getUsername() == null || user.getUsername().isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Error", "Unable to identify current user.");
                return;
            }

            String avatarUrl;
            String style;

            // Determine if we're using a custom profile picture or a DiceBear avatar
            if (customProfilePictureUrl != null) {
                avatarUrl = customProfilePictureUrl;
                style = "profile_picture";
            } else if (avatar != null) {
                avatarUrl = avatar.getAvatarURL();
                style = avatar.getStyle();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "No avatar selected.");
                return;
            }

            // Update avatar in the database
            boolean success = dbInstance.saveAvatar(user.getUsername(), style, avatarUrl);

            if (success) {
                // Update user object
                user.setAvatarURL(avatarUrl);

                // If using DiceBear avatar, set the avatar object
                if (avatar != null) {
                    user.setAvatar(avatar);
                }

                // Update in singletons
                dbInstance.setCurrentUser(user);
                utilities.setCurrentUser(user);

                showAlert(Alert.AlertType.INFORMATION, "Success", "Avatar updated successfully!");

                // Navigate back to settings screen
                navigateToSettingsScreen();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to save avatar changes.");
            }
        } catch (Exception e) {
            System.err.println("Error updating avatar: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to update avatar: " + e.getMessage());
        }
    }

    /**
     * Registers a new user with the selected avatar
     */
    @FXML
    private void registerUser() {
        user = dbInstance.getCurrentUser();

        if (user == null) {
            System.err.println("user object is null");
            showAlert(Alert.AlertType.ERROR, "Registration Error", "All required fields must be filled.");
            return;
        }

        // Determine image source - prefer custom profile picture if set
        String finalImageSource;
        boolean isProfilePicture;

        if (customProfilePictureUrl != null) {
            finalImageSource = customProfilePictureUrl;
            isProfilePicture = true;
        } else if (avatar != null) {
            finalImageSource = avatar.getAvatarURL();
            isProfilePicture = false;
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Please select an avatar or upload a profile picture");
            return;
        }

        System.out.println("finalAvatarURL = " + finalImageSource);

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
            String sql = "INSERT INTO Users (first_name, last_name, preferred_name, phone_number, email, dob, gender, specified_pronouns, username, password_hash, avatar_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?)";
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
            stmt.setInt(11, 0); //temp value

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

            String avatarStyle;
            if(isProfilePicture){
                avatarStyle = "profile_picture";
            }else{
                avatarStyle = avatar.getStyle();
            }

            //insert avatar into avatar table
            String avatarSQL = "INSERT INTO Avatars (user_id, style, avatar_url, created_at) VALUES (?, ?, ?, NOW())";
            PreparedStatement avatarStmt = conn.prepareStatement(avatarSQL, PreparedStatement.RETURN_GENERATED_KEYS);
            avatarStmt.setInt(1, userID);
            avatarStmt.setString(2, avatarStyle);
            avatarStmt.setString(3, finalImageSource);
            int avatarRows = avatarStmt.executeUpdate();
            if (avatarRows == 0) {
                showAlert(Alert.AlertType.ERROR, "Avatar Error", "Avatar was not saved");
                return;
            }

            //generate avatar id
            int avatarID = -1;
            try (var generatedKeys = avatarStmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    avatarID = generatedKeys.getInt(1);
                    System.out.println("avatar registered with id: " + avatarID);
                }
            }

            if(avatarID == -1){
                showAlert(Alert.AlertType.ERROR, "Avatar Error", "Avatar was not saved");
                return;
            }
            //update user table with avatar
            String updateUserSQL = "UPDATE Users SET avatar_id = ? WHERE id = ?";
            PreparedStatement updateUserStmt = conn.prepareStatement(updateUserSQL);
            updateUserStmt.setInt(1, avatarID);
            updateUserStmt.setInt(2, userID);
            int updateRows = updateUserStmt.executeUpdate();
            if (updateRows > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Registration Complete", "Account Created! Please log in!");
                navigateToEntranceScreen();
            }else{
                showAlert(Alert.AlertType.ERROR, "Registration Failed", "User was not saved");
                return;
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to register user: " + e.getMessage());
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

    private void navigateToSettingsScreen(){
        System.out.println("navigating to settings screen from create avatar");
        UIUtilities.navigateToScreen("/views/settingsScreen.fxml", root.getScene(), false);
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