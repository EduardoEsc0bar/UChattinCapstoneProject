package org.example.uchattincapstoneproject.model;
import javafx.scene.paint.Color;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;


public class User {
    private String username;
    private String passwordHash;
    private String firstName;
    private String lastName;
    private String dob;
    private String email;
    private String phoneNumber;
    private String pronouns;
    private String gender;
    private String specifiedGender;
    private String specifiedPronouns;
    private String preferredName;
    private int avatarID;
    private Color themeColor;
    private double textSize;
    private double volume;
    private String selectedVoice;
    private String appTheme;
    private boolean notificationsOn;
    private String favoritePicture;


    public User(String username, String passwordHash, String firstName, String lastName, String dob, String email, String phoneNumber,
                String pronouns, String gender, String preferredName, int avatarID){
        this.username = username;
        this.passwordHash = passwordHash;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dob = dob;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.pronouns = pronouns;
        this.gender = gender;
        this.preferredName = preferredName;
        this.avatarID = avatarID;
    }



    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getDob() { return dob; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getPronouns() { return pronouns; }
    public String getGender() { return gender; }
    public String getSpecifiedGender() { return specifiedGender; }
    public String getSpecifiedPronouns() { return specifiedPronouns; }
    public String getPreferredName() { return preferredName; }
    public int getAvatarID() { return avatarID; }
    public Color getThemeColor() { return themeColor; }
    public double getTextSize() { return textSize; }
    public double getVolume() { return volume; }
    public String getSelectedVoice() { return selectedVoice; }
    public String getAppTheme() { return appTheme; }
    public boolean isNotificationsOn() { return notificationsOn; }
    public String getFavoritePicture() { return favoritePicture; }


    public void setUsername(String username) { this.username = username; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setDob(String dob) { this.dob = dob; }
    public void setEmail(String email) { this.email = email; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setPronouns(String pronouns) { this.pronouns = pronouns; }
    public void setGender(String gender) { this.gender = gender; }
    public void setSpecifiedGender(String specifiedGender) { this.specifiedGender = specifiedGender; }
    public void setSpecifiedPronouns(String specifiedPronouns) { this.specifiedPronouns = specifiedPronouns; }
    public void setPreferredName(String preferredName) { this.preferredName = preferredName; }
    public void setAvatarID(int avatarID) { this.avatarID = avatarID; }
    public void setThemeColor(Color themeColor) { this.themeColor = themeColor; }
    public void setTextSize(double textSize) { this.textSize = textSize; }
    public void setVolume(double volume) { this.volume = volume; }
    public void setSelectedVoice(String selectedVoice) { this.selectedVoice = selectedVoice; }
    public void setAppTheme(String appTheme) { this.appTheme = appTheme; }
    public void setNotificationsOn(boolean notificationsOn) { this.notificationsOn = notificationsOn; }
    public void setFavoritePicture(String favoritePicture) { this.favoritePicture = favoritePicture; }

    public boolean validate() {
        if (username == null || username.isEmpty() ||
                passwordHash == null || passwordHash.isEmpty() ||
                email == null || !email.matches("[^@ ]+@[^@ ]+\\.[^@ ]+") ||
                firstName == null || firstName.isEmpty() ||
                lastName == null || lastName.isEmpty()) {

            System.out.println("All required fields must be filled in.");
            return false;
        }
        return true;
    }

    public boolean register() {
        String query = "INSERT INTO Users (username, password_hash, email, first_name, last_name, gender, specified_pronouns, dob, " +
                "phone_number, avatar_id, theme_color, text_size, volume, selected_voice, app_theme, notifications_on, " +
                "favorite_picture, created_at, last_login) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);";

        try (Connection connection = DriverManager.getConnection(
                "jdbc:mysql://uchattin-csc311.mysql.database.azure.com:3306/uchattin-userinfo",
                "username", "password");
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, username);
            statement.setString(2, passwordHash);
            statement.setString(3, email);
            statement.setString(4, firstName);
            statement.setString(5, lastName);
            statement.setString(6, gender);
            statement.setString(7, pronouns);
            statement.setString(8, dob);
            statement.setString(9, phoneNumber);
            statement.setInt(10, avatarID);
            statement.setString(11, themeColor.toString());
            statement.setDouble(12, textSize);
            statement.setDouble(13, volume);
            statement.setString(14, selectedVoice);
            statement.setString(15, appTheme);
            statement.setBoolean(16, notificationsOn);
            statement.setString(17, favoritePicture);

            int rowInserted = statement.executeUpdate();
            return rowInserted > 0;
        } catch (SQLException e) {
            System.err.println("Error inserting user into database.");
            e.printStackTrace();
            return false;
        }
    }
}