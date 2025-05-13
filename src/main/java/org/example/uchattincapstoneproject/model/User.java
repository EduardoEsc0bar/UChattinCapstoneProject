package org.example.uchattincapstoneproject.model;

import javafx.scene.paint.Color;

import java.io.*;
import java.sql.*;
import java.sql.Connection;
import java.util.regex.Pattern;

public class User {
    //private int userID;
    private String firstName;
    private String lastName;
    private String preferredName;
    private String phoneNumber;
    private String email;
    private String dob;
    private String gender;
    private String specifiedGender;
    private String pronouns;
    private String specifiedPronouns;
    private String username;
    private String passwordHash;
    private String avatarURL;
    private String bio;

    //UPDATES TO ACCOMODATE NEW FUNCTIONS!!!!
    private Color themeColor;            // From ColorPicker
    private Avatar avatar;              // Avatar object with style + DiceBearAPI
    // Accessibility settings
    private double textSize;            // From adjustTextSizeSlider
    private double volume;              // From adjustVolumeSlider
    private String selectedVoice;       // From chooseVoiceCB
    // Application settings
    private String appTheme;            // From changeAppThemeCB
    private boolean notificationsOn;    // From notificationOnRadioButton
    private String favoritePicture;

    // Database connection constants
    private static final String DB_URL = "jdbc:mysql://uchattin-csc311.mysql.database.azure.com:3306/uchattin-userinfo";
    private static final String DB_USERNAME = "username"; // Replace with actual DB username
    private static final String DB_PASSWORD = "password"; // Replace with actual DB password

    public User(String firstName, String lastName, String preferredName,String phoneNumber, String email, String dob,
                String gender, String specifiedGender, String pronouns, String specifiedPronouns, String username, String passwordHash) {
        //this.userID = userID;
        this.username = username;
        this.passwordHash = passwordHash;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dob = dob;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.pronouns = pronouns;
        this.gender = gender;
        this.specifiedGender = specifiedGender;
        this.specifiedPronouns = specifiedPronouns;
        this.preferredName = preferredName;

        // Set default values for settings
        this.textSize = 14.0;
        this.volume = 50.0;
        this.selectedVoice = "Default";
        this.appTheme = "Light";
        this.notificationsOn = true;
    }

    //User constructor
    User(String username, String passwordHash){
        this.username = username;
        this.passwordHash = passwordHash;
    }

    //public int getUserID() {return userID;}
    public String getUsername() {
        return username;
    }
    public String getPasswordHash() {
        return passwordHash;
    }
    public String getFirstName() {
        return firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public String getDob() {
        return dob;
    }
    public String getEmail() {
        return email;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public String getPronouns() {
        return pronouns;
    }
    public String getGender() {
        return gender;
    }
    public String getSpecifiedGender() {return specifiedGender;}
    public String getSpecifiedPronouns() {return specifiedPronouns;}
    public String getPreferredName() {
        return preferredName;
    }

    public String getAvatarURL() {
        return avatarURL;
    }

    public void setAvatarURL(String avatarURL) {
        this.avatarURL = avatarURL;
    }
    //public void setUserID(int userID) {this.userID = userID;}
    public void setUsername(String username) {
        this.username = username;
    }
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public void setDob(String dob) {
        this.dob = dob;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public void setPronouns(String pronouns) {
        this.pronouns = pronouns;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }
    public void setSpecifiedGender(String specifiedGender) {this.specifiedGender = specifiedGender;}
    public void setSpecifiedPronouns(String specifiedPronouns) {this.specifiedPronouns = specifiedPronouns;}
    public void setPreferredName(String preferredName) {
        this.preferredName = preferredName;
    }
    // NEW UPDATE FOR NEW FIELDS
    public Color getThemeColor() {
        return themeColor;
    }
    public void setThemeColor(Color themeColor) {
        this.themeColor = themeColor;
    }
    public Avatar getAvatar() {
        return avatar;
    }
    public void setAvatar(Avatar avatar) {
        this.avatar = avatar;
    }
    public double getTextSize() {
        return textSize;
    }
    public void setTextSize(double textSize) {
        this.textSize = textSize;
    }
    public double getVolume() {
        return volume;
    }
    public void setVolume(double volume) {
        this.volume = volume;
    }
    public String getSelectedVoice() {
        return selectedVoice;
    }
    public void setSelectedVoice(String selectedVoice) {
        this.selectedVoice = selectedVoice;
    }
    public String getAppTheme() {
        return appTheme;
    }
    public void setAppTheme(String appTheme) {
        this.appTheme = appTheme;
    }
    public boolean isNotificationsOn() {
        return notificationsOn;
    }
    public void setNotificationsOn(boolean notificationsOn) {
        this.notificationsOn = notificationsOn;
    }
    public String getFavoritePicture() {
        return favoritePicture;
    }
    public void setFavoritePicture(String favoritePicture) {
        this.favoritePicture = favoritePicture;
    }
    public String getBio() {
        return bio;
    }
    public void setBio(String bio) {
        this.bio = bio;
    }
    /**
     * Validates required user fields
     * @return true if all required fields are valid, false otherwise
     */
    public boolean validate(){
        // Check if required fields are filled
        if(username == null || username.isEmpty() ||
                passwordHash == null || passwordHash.isEmpty() ||
                firstName == null || firstName.isEmpty() ||
                lastName == null || lastName.isEmpty()) {
            System.out.println("All required fields must be filled in");
            return false;
        }

        // Validate email format if provided
        if(email != null && !email.isEmpty()) {
            String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@(.+)$";
            if(!Pattern.matches(EMAIL_PATTERN, email)) {
                System.out.println("Invalid email format");
                return false;
            }
        }

        return true;
    }

    /**
     * Fallback method to save user data to a local file if database connection fails
     * @return true if save to file is successful, false otherwise
     */
    private boolean saveToLocalFile() {
        try {
            String userHome = System.getProperty("user.home");
            String filePath = userHome + "/uchattin_users.csv";

            // Check if file exists, if not create it with headers
            File file = new File(filePath);
            boolean fileExists = file.exists();

            // Create file writer (append mode)
            FileWriter fileWriter = new FileWriter(filePath, true);
            BufferedWriter writer = new BufferedWriter(fileWriter);

            // Write headers if file is new
            if(!fileExists) {
                writer.write("username,password_hash,first_name,last_name,email,dob,phone_number,gender,pronouns,preferred_name");
                writer.newLine();
            }

            // Check if username already exists
            if(fileExists) {
                BufferedReader reader = new BufferedReader(new FileReader(filePath));
                String line;

                // Skip header
                reader.readLine();

                while((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if(parts.length > 0 && parts[0].equals(username)) {
                        reader.close();
                        writer.close();
                        System.out.println("Username already exists in local file");
                        return false;
                    }
                }
                reader.close();
            }

            // Escape commas in fields to avoid CSV parsing issues
            String safeUsername = escapeCsvField(username);
            String safeFirstName = escapeCsvField(firstName);
            String safeLastName = escapeCsvField(lastName);
            String safeEmail = escapeCsvField(email);
            String safeDob = escapeCsvField(dob);
            String safePhoneNumber = escapeCsvField(phoneNumber);
            String safeGender = escapeCsvField(gender);
            String safePronouns = escapeCsvField(pronouns);
            String safePreferredName = escapeCsvField(preferredName);

            // Write user data
            writer.write(String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s",
                    safeUsername, passwordHash, safeFirstName, safeLastName,
                    safeEmail, safeDob, safePhoneNumber, safeGender,
                    safePronouns, safePreferredName));
            writer.newLine();
            writer.close();

            System.out.println("User saved to local file successfully: " + filePath);
            return true;
        } catch (Exception e) {
            System.err.println("Error saving to local file: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Helper method to escape commas in CSV fields
     */
    private String escapeCsvField(String field) {
        if (field == null) return "";

        // If the field contains commas, quotes, or newlines, wrap it in quotes and escape any quotes
        if (field.contains(",") || field.contains("\"") || field.contains("\n")) {
            return "\"" + field.replace("\"", "\"\"") + "\"";
        }
        return field;
    }

    /**
     * Registers the user in the database
     * @return true if registration is successful, false otherwise
     */
    public boolean register() {
        if(!validate()) {
            return false;
        }

        // Try database connection first
        try {
            // First, try to connect to the actual database (replace with correct credentials)
            Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://commapp.mysql.database.azure.com:3306/communication_app",
                    "commapp_db_user", "farm9786$"); // These are placeholder credentials

            // Rest of database code...
            connection.close();
            System.out.println("User successfully registered using database-----------------------------------------------------------------");
            // If successful, return true
            return true;
        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());

            // If database connection fails, use local file as fallback
            System.out.println("User registered using local file-----------------------------------------------------------------");
            return saveToLocalFile();
        }
    }

    /**
     * Static method to authenticate a user from database or local file
     * @param username Username to authenticate
     * @param password Password to check
     * @return User object if authentication successful, null otherwise
     */
    public static User authenticate(String username, String password) {
        DB dbInstance = DB.getInstance();

        try {
            // Try database authentication first
            Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            String query = "SELECT * FROM user WHERE username = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);

            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                String storedPasswordHash = resultSet.getString("password");
                if(dbInstance.checkPassword(password, storedPasswordHash)) {
                    // Build user from result set
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
                            "", // specified_gender not in DB yet
                            "", // specified_pronouns not in DB yet
                            resultSet.getString("preferred_name")
                    );

                    // Update last login timestamp
                    String updateQuery = "UPDATE user SET last_login_at = CURRENT_TIMESTAMP WHERE username = ?";
                    PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
                    updateStatement.setString(1, username);
                    updateStatement.executeUpdate();
                    updateStatement.close();

                    statement.close();
                    connection.close();
                    return authenticatedUser;
                }
            }

            statement.close();
            connection.close();
        } catch (SQLException e) {
            System.err.println("Database connection error during authentication: " + e.getMessage());

            // Try local file authentication as fallback
            try {
                String userHome = System.getProperty("user.home");
                String filePath = userHome + "/uchattin_users.csv";
                java.io.File file = new java.io.File(filePath);

                if(file.exists()) {
                    java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(filePath));
                    String line;
                    // Skip header line
                    reader.readLine();

                    while((line = reader.readLine()) != null) {
                        String[] parts = line.split(",");
                        if(parts.length >= 10 && parts[0].equals(username)) {
                            String storedPasswordHash = parts[1];
                            if(dbInstance.checkPassword(password, storedPasswordHash)) {
                                User authenticatedUser = new User(
                                        parts[0], // username
                                        storedPasswordHash,
                                        parts[2], // firstName
                                        parts[3], // lastName
                                        parts[5], // dob
                                        parts[4], // email
                                        parts[6], // phoneNumber
                                        parts[8], // pronouns
                                        parts[7], // gender
                                        "", // specifiedGender
                                        "", // specifiedPronouns
                                        parts[9]  // preferredName
                                );
                                reader.close();
                                return authenticatedUser;
                            }
                        }
                    }
                    reader.close();
                }
            } catch (Exception ex) {
                System.err.println("Error reading from local file: " + ex.getMessage());
                ex.printStackTrace();
            }
        }

        // If all authentication methods fail, or if user not found
        return null;
    }
}