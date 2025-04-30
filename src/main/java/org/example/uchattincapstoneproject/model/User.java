package org.example.uchattincapstoneproject.model;

import java.sql.*;
import java.sql.Connection;

public class User {
    private int userID;
    private String username;
    private String passwordHash;
    private String firstName;
    private String lastName;
    private String dob;
    private String email;
    private String phoneNumber;
    private String pronouns;
    private String gender;
    private String preferredName;

    public User(int userID, String username, String passwordHash, String firstName, String lastName, String dob, String email, String phoneNumber, String pronouns, String gender, String preferredName) {
        this.userID = userID;
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
    }

    public int getUserID() {return userID;}
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
    public String getPreferredName() {
        return preferredName;
    }
    public void setUserID(int userID) {this.userID = userID;}
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
    public void setPreferredName(String preferredName) {
        this.preferredName = preferredName;
    }

    public boolean validate(){
        if(username == null || username.isEmpty() ||
                passwordHash == null || passwordHash.isEmpty()||
                email==null || !email.matches("[^@ ] + @[^@ ]+\\.[^@ ]+") ||
               firstName == null || firstName.isEmpty() ||
                lastName == null || lastName.isEmpty()){
            System.out.println("All required fields must be filled in");

            return false;

        }
        return true;
    }

    public boolean register(){
        String query = "insert into user(username, password, email, first_name, last_name, gender, pronouns, date_of_birth, phone_number, created_at, last_loggin_in)\n" +
                "values (?, ?, ?, ?, ?, ?, ?, ?, ?, current_timestamp, current_timestamp);";

        try(Connection connection = DriverManager.getConnection("jdbc:mysql://uchattin-csc311.mysql.database.azure.com:3306/uchattin-userinfo", "username", "password");
            PreparedStatement statement = connection.prepareStatement(query)){

            statement.setString(1, username);
            statement.setString(2, passwordHash);
            statement.setString(3, email);
            statement.setString(4, firstName);
            statement.setString(5, lastName);
            statement.setString(6, gender);
            statement.setString(7, pronouns);
            statement.setString(8, dob);
            statement.setString(9, phoneNumber);

            int rowInserted = statement.executeUpdate();
            return rowInserted < 0;
        } catch (SQLException e){
        e.printStackTrace();
        return false;
        }
    }
}
