package org.example.uchattincapstoneproject.viewModel;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.uchattincapstoneproject.model.DB;
import org.example.uchattincapstoneproject.model.User;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class entranceController {
    @FXML
    public TextField usernameField, passwordField; // Changed from PasswordField to TextField to match FXML
    @FXML
    public Button logInBTN, createAccountBTN;
    @FXML
    public Hyperlink forgotPasswordBTN;

    private int authenticatedUserID = -1;
    private String TEST_USERNAME = "testuser";
    private String TEST_PASSWORD = "testpassword123";
    private DB dbInstance;

    @FXML
    private void initialize() {
        System.out.println("entrance controller initialized");

        // Initialize DB instance
        dbInstance = DB.getInstance();

        //button actions
        logInBTN.setOnAction((ActionEvent event) -> handleLogin());
        forgotPasswordBTN.setOnAction((ActionEvent event) -> navigateToForgotPassword());
        createAccountBTN.setOnAction((ActionEvent event) -> navigateToRegistration());
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if(username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Login Error", "Please enter all fields");
            return;
        }

        System.out.println("attempting to login user: " + username);

        if(validateDatabaseCredentials(username, password)){
            try{
                showAlert(Alert.AlertType.INFORMATION, "Login Successful", "Welcome " + username + "!");
                navigateToMainScreen();
            }catch(Exception e){
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Navigation Error",
                        "Error navigating to main screen: " + e.getMessage());
            }
        }else{
            showAlert(Alert.AlertType.ERROR, "Login failed", "Invalid username or password. Please try again.");
        }
    }

    //validate user credentials from database
    private boolean validateDatabaseCredentials(String username, String password) {
        try {
            // First try real database authentication
            Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://uchattin-csc311.mysql.database.azure.com:3306/uchattin-userinfo",
                    "username", "password");

            String query = "SELECT * FROM user WHERE username = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String storedPasswordHash = resultSet.getString("password");
                if (dbInstance.checkPassword(password, storedPasswordHash)) {
                    // If password matches, build a User object from the result set
                    authenticatedUserID = resultSet.getInt("id");

                    User authenticatedUser = new User(
                            resultSet.getString("username"),
                            storedPasswordHash,
                            resultSet.getString("first_name"),
                            resultSet.getString("last_name"),
                            resultSet.getString("date_of_birth"),
                            resultSet.getString("email"),
                            resultSet.getString("phone_number"),
                            resultSet.getString("pronouns"),
                            resultSet.getString("gender"),
                            "", // specified_gender might not exist in DB
                            "", // specified_pronouns might not exist in DB
                            resultSet.getString("preferred_name") // might be null
                    );

                    // Store the authenticated user in DB singleton
                    dbInstance.setCurrentUser(authenticatedUser);
                    System.out.println("User authenticated with id: " + authenticatedUserID);
                    return true;
                }
            }
            connection.close();
            return false;
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();

            // Try local file authentication as a fallback
            System.out.println("Database connection failed, trying fallback authentication");

            try {
                String userHome = System.getProperty("user.home");
                String filePath = userHome + "/uchattin_users.csv";
                File file = new File(filePath);

                if (file.exists()) {
                    BufferedReader reader = new BufferedReader(new FileReader(filePath));
                    String line;

                    // Skip header line if it exists
                    line = reader.readLine();
                    if (line == null || !line.startsWith("username,password")) {
                        reader.close();
                        return false;
                    }

                    System.out.println("Reading users from: " + filePath);

                    while ((line = reader.readLine()) != null) {
                        String[] parts = line.split(",");

                        if (parts.length >= 2 && parts[0].equals(username)) {
                            String storedPasswordHash = parts[1];
                            System.out.println("Found user in file: " + username);
                            boolean passwordMatches = dbInstance.checkPassword(password, storedPasswordHash);

                            if (passwordMatches) {
                                System.out.println("Password matches! User authenticated from local file: " + username);

                                // Create user from CSV data
                                User authenticatedUser = new User(
                                        parts[0], // username
                                        storedPasswordHash, // passwordHash
                                        parts.length > 2 ? parts[2] : "", // firstName
                                        parts.length > 3 ? parts[3] : "", // lastName
                                        parts.length > 5 ? parts[5] : "", // dob
                                        parts.length > 4 ? parts[4] : "", // email
                                        parts.length > 6 ? parts[6] : "", // phoneNumber
                                        parts.length > 8 ? parts[8] : "", // pronouns
                                        parts.length > 7 ? parts[7] : "", // gender
                                        "", // specifiedGender (not in CSV)
                                        "", // specifiedPronouns (not in CSV)
                                        parts.length > 9 ? parts[9] : "" // preferredName
                                );

                                // Store in DB singleton
                                dbInstance.setCurrentUser(authenticatedUser);
                                authenticatedUserID = 1; // Use a placeholder ID

                                reader.close();
                                return true;
                            } else {
                                System.out.println("Password does not match for user: " + username);
                            }
                            break; // Username found but password incorrect
                        }
                    }
                    System.out.println("User not found in file: " + username);
                    reader.close();
                } else {
                    System.out.println("User file does not exist at: " + filePath);
                }
            } catch (IOException ioEx) {
                System.err.println("File IO error: " + ioEx.getMessage());
                ioEx.printStackTrace();
            }

            // Final fallback to test credentials - useful for demo/development
            if (TEST_USERNAME.equals(username) && TEST_PASSWORD.equals(password)) {
                System.out.println("User authenticated with test credentials: " + username);
                authenticatedUserID = 1;

                // Create a mock user for the test account
                User mockUser = new User(
                        username,
                        dbInstance.hashPassword(password),
                        "Test",
                        "User",
                        "01/01/2000",
                        "test@example.com",
                        "555-123-4567",
                        "They/Them",
                        "Prefer not to say",
                        "",
                        "",
                        "Test User"
                );
                dbInstance.setCurrentUser(mockUser);
                return true;
            }
        }

        System.out.println("Invalid credentials");
        return false;
    }

    //navigates to main screen
    @FXML
    private void navigateToMainScreen() {
        try{
            FadeTransition fadeout = new FadeTransition(Duration.seconds(1));
            fadeout.setFromValue(1.0);
            fadeout.setToValue(0.0);
            fadeout.setNode(usernameField.getScene().getRoot());
            fadeout.setOnFinished(event -> {
                try{
                    System.out.println("navigating to main screen");
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/mainScreen.fxml"));
                    AnchorPane mainScreen = loader.load(); // Using AnchorPane based on previous error

                    MainViewController mainViewController = loader.getController();
                    User currentUser = dbInstance.getCurrentUser();

                    if (currentUser != null) {
                        mainViewController.setUsername(currentUser.getUsername());
                        // If MainViewController has a setUserID method, set the ID here
                        // mainViewController.setUserID(authenticatedUserID);
                    } else {
                        // Fallback to using text field value if no user in DB singleton
                        mainViewController.setUsername(usernameField.getText());
                    }

                    Stage currentStage = (Stage) logInBTN.getScene().getWindow();
                    Scene mainScene = new Scene(mainScreen, 800, 600);
                    currentStage.setScene(mainScene);
                    currentStage.setMaximized(true);

                    FadeTransition fadein = new FadeTransition(Duration.seconds(1));
                    fadein.setFromValue(0.0);
                    fadein.setToValue(1.0);
                    fadein.setNode(mainScreen);
                    fadein.play();
                }catch(IOException e){
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Navigation Error",
                            "Error loading main screen: " + e.getMessage());
                }
            });
            fadeout.play();
        }catch(Exception e){
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error",
                    "Error preparing transition to main screen: " + e.getMessage());
        }
    }

    //navigate to registration
    @FXML
    private void navigateToRegistration(){
        try{
            System.out.println("navigating to registration");
            FadeTransition fadeout = new FadeTransition(Duration.seconds(1));
            fadeout.setFromValue(1.0);
            fadeout.setToValue(0.0);
            fadeout.setNode(usernameField.getScene().getRoot());
            fadeout.setOnFinished(event -> {
                try{
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/registrationScreen.fxml"));
                    AnchorPane registrationScreen = loader.load();
                    Scene registrationScene = new Scene(registrationScreen, 800, 600);

                    Stage stage = (Stage) logInBTN.getScene().getWindow();
                    stage.setScene(registrationScene);
                    stage.setTitle("Create Account");

                    FadeTransition fadein = new FadeTransition(Duration.seconds(1));
                    fadein.setFromValue(0.0);
                    fadein.setToValue(1.0);
                    fadein.setNode(registrationScreen);
                    fadein.play();
                }catch(IOException e){
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Navigation Error",
                            "Error loading registration screen: " + e.getMessage());
                }
            });
            fadeout.play();
        }catch(Exception e){
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error",
                    "Error preparing transition to registration screen: " + e.getMessage());
        }
    }

    //navigating to forget password screen
    @FXML
    private void navigateToForgotPassword(){
        try{
            System.out.println("navigating to forgot password");
            FadeTransition fadeout = new FadeTransition(Duration.seconds(1));
            fadeout.setFromValue(1.0);
            fadeout.setToValue(0.0);
            fadeout.setNode(usernameField.getScene().getRoot());
            fadeout.setOnFinished(event -> {
                try{
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/forgotPasswordScreen.fxml"));
                    AnchorPane forgotPasswordScreen = loader.load();
                    Scene forgotPasswordScene = new Scene(forgotPasswordScreen, 800, 600);

                    Stage stage = (Stage) logInBTN.getScene().getWindow();
                    stage.setScene(forgotPasswordScene);
                    stage.setTitle("Forgot Password");

                    FadeTransition fadein = new FadeTransition(Duration.seconds(1));
                    fadein.setFromValue(0.0);
                    fadein.setToValue(1.0);
                    fadein.setNode(forgotPasswordScreen);
                    fadein.play();
                }catch(IOException e){
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Navigation Error",
                            "Error loading forgot password screen: " + e.getMessage());
                }
            });
            fadeout.play();
        }catch(Exception e){
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error",
                    "Error preparing transition to forgot password screen: " + e.getMessage());
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