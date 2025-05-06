package org.example.uchattincapstoneproject.model;

import org.mindrot.jbcrypt.BCrypt;

import java.security.SecureRandom;
import java.sql.*;
import java.util.Base64;

public class DB {
    private static DB instance;  // Singleton instance
    private static User currentUser;
    final String MYSQL_SERVER_URL = "jdbc:mysql://commapp.mysql.database.azure.com:3306/";
    final String DB_URL = MYSQL_SERVER_URL + "communication_app";
    final String USERNAME = "commapp_db_user";
    final String PASSWORD = "farm9786$";


    public boolean testDatabaseConnection(){
        try(Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD)){
            if(connection != null){
                System.out.println("connection established");
                return true;
            }
        }catch (SQLException e){
            System.err.println("connection failed " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    public void setCurrentUser(User user) {
        currentUser = user;
    }

    public User getCurrentUser() {
        return currentUser;
    }
    /**
     * Private constructor for the DB class.
     * Prevents instantiation from outside the class.
     * Used to enforce the Singleton design pattern.
     */
    private DB() {
        // Initialize DB connection or setup here if needed
    }
    /**
     * Returns the singleton instance of the DB class.
     * Uses double-checked locking to ensure thread-safe lazy initialization.
     *
     * @return the single instance of the DB class
     */
    public static DB getInstance() {
        if (instance == null) {
            synchronized (DB.class) {
                if (instance == null) {
                    instance = new DB();
                }
            }
        }
        return instance;
    }
    /**
     * Connects to the MySQL server and ensures the database and Users table exist.
     * Also checks if there are any registered users in the Users table.
     *
     * @return true if there are registered users in the Users table, false otherwise.
     */
    public boolean connectToDatabase() {
        boolean hasRegistredUsers = false;
        //Class.forName("com.mysql.jdbc.Driver");
        try {
            //First, connect to MYSQL server and create the database if not created
            Connection conn = DriverManager.getConnection(MYSQL_SERVER_URL, USERNAME, PASSWORD);
            Statement statement = conn.createStatement();
            statement.executeUpdate("CREATE DATABASE IF NOT EXISTS communication_app");
            statement.close();
            conn.close();
            //Second, connect to the database and create the table "users" if cot created
            conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            statement = conn.createStatement();
            //check if we have users in the table users
            statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM Users");
            if (resultSet.next()) {
                int numUsers = resultSet.getInt(1);
                if (numUsers > 0) {
                    hasRegistredUsers = true;
                }
            }
            statement.close();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return hasRegistredUsers;
    }
    /**
     * Queries and prints information of a user by their username.
     *
     * @param username The username of the user to be queried.
     */
    public  void queryUserByName(String username) {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            String sql = "SELECT * FROM Users WHERE username = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, username);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String email = resultSet.getString("email");
                String displayName = resultSet.getString("displayName");
                System.out.println("ID: " + id + ", Name: " + username + ", Email: " + email + ", Display Name: " + displayName);
            }
            preparedStatement.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /**
     * Retrieves and prints all users in the Users table.
     */
    public  void listAllUsers() {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            String sql = "SELECT * FROM Users ";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("username");
                String email = resultSet.getString("email");
                String displayName = resultSet.getString("displayName");
                System.out.println("ID: " + id + ", Name: " + name + ", Email: " + email + ", Display Name: " + displayName);
            }

            preparedStatement.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /**
     * Inserts a new user into the Users table.
     *
     */
    public boolean insertUser(User user) {
        String sql = "INSERT INTO Users (username, password_hash, first_name, last_name, dob, email, phone_number, gender, specified_pronouns, preferred_name, avatar_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NULL)";

        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
             PreparedStatement statement = conn.prepareStatement(sql)) {

            // Secure password hashing
            String hashedPassword = hashPassword(user.getPasswordHash());

            // Set user details in the database
            statement.setString(1, user.getUsername());
            statement.setString(2, hashedPassword);
            statement.setString(3, user.getFirstName());
            statement.setString(4, user.getLastName());
            statement.setString(5, user.getDob());
            statement.setString(6, user.getEmail());
            statement.setString(7, user.getPhoneNumber());
            statement.setString(8, user.getGender());
            statement.setString(9, user.getPronouns());
            statement.setString(10, user.getPreferredName());

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error inserting user.");
            e.printStackTrace();
            return false;
        }
    }

    //store avatar in database
    public int storeAvatarInDatabase(String avatarURL) {
        String sql = "INSERT INTO Avatars (avatar_url) VALUES (?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
             PreparedStatement statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, avatarURL);
            int affectedRows = statement.executeUpdate();

            if (affectedRows > 0) {
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1); //Return the new avatar ID
                }
            }
        } catch (SQLException e) {
            System.err.println("Error inserting avatar.");
            e.printStackTrace();
        }
        return -1; // return -1 if insertion fails
    }

    public boolean updateUserAvatar(String username, int avatarId) {
        String sql = "UPDATE Users SET avatar_id = ? WHERE username = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
             PreparedStatement statement = conn.prepareStatement(sql)) {

            statement.setInt(1, avatarId);
            statement.setString(2, username);

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating user avatar.");
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateUserDetails(String username, String field, String value) {
        String sql = "UPDATE Users SET " + field + " = ? WHERE username = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
             PreparedStatement statement = conn.prepareStatement(sql)) {

            statement.setString(1, value);
            statement.setString(2, username);

            return statement.executeUpdate() > 0; //Returns true if update successful
        } catch (SQLException e) {
            System.err.println("Error updating user: " + field);
            e.printStackTrace();
            return false;
        }
    }
    /**
     * Removes a user from the Users table by their username.
     *
     * @param username The username of the user to remove.
     */
    public void removeUserByUsername(String username) {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            String sql = "DELETE FROM Users WHERE username = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, username);

            int rowsDeleted = preparedStatement.executeUpdate();

            if (rowsDeleted > 0) {
                System.out.println("User with username '" + username + "' was deleted successfully.");
            } else {
                System.out.println("No user found with username '" + username + "'.");
            }

            preparedStatement.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /**
     * Updates the display name of a user by their username.
     *
     * @param username       The username of the user whose display name is to be updated.
     * @param newDisplayName The new display name to assign to the user.
     */
    public void updateUserDisplayName(String username, String newDisplayName) {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            String sql = "UPDATE Users SET displayName = ? WHERE username = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, newDisplayName);
            preparedStatement.setString(2, username);

            int rowsUpdated = preparedStatement.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("Display name for user '" + username + "' was updated successfully.");
            } else {
                System.out.println("No user found with username '" + username + "'.");
            }

            preparedStatement.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //check if user is registered
    public boolean isUserRegistered(String username, String email) {
        String sql = "SELECT COUNT(*) FROM Users WHERE username = ? OR email = ?";

        try(Connection conn = DriverManager.getConnection(DB_URL,USERNAME,PASSWORD);
        PreparedStatement statement = conn.prepareStatement(sql)){
            statement.setString(1, username);
            statement.setString(2, email);

            ResultSet rS = statement.executeQuery();
            if(rS.next()){
                int count = rS.getInt(1);
                return count > 0;
            }
        }catch (SQLException e) {
            System.err.println("Database error while checking for user");
            e.printStackTrace();
        }
        return false;
    }

    /**
     *
     * Hashes a plaintext password using BCrypt.
     *
     * @param password The plaintext password.
     * @return The hashed password.
     */
    public String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    /**
     * Verifies a plaintext password against a hashed password.
     *
     * @param password The plaintext password.
     * @param hashedPassword The hashed password.
     * @return true if the password matches the hash, false otherwise.
     */
    public boolean checkPassword(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }


    public String generateResetToken() {
        byte[] randomBytes = new byte[32];
        new SecureRandom().nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    public boolean storeResetToken(String username) {
        String sql = "INSERT INTO PasswordResets (user_id, reset_token, expires_at) VALUES ((SELECT id FROM Users WHERE username = ?), ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
             PreparedStatement statement = conn.prepareStatement(sql)) {

            String resetToken = generateResetToken();
            Timestamp expiresAt = new Timestamp(System.currentTimeMillis() + (15 * 60 * 1000)); // Expires in 15 minutes

            statement.setString(1, username);
            statement.setString(2, resetToken);
            statement.setTimestamp(3, expiresAt);

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error storing reset token.");
            e.printStackTrace();
            return false;
        }
    }

    public boolean verifyResetToken(String token) {
        String sql = "SELECT user_id FROM PasswordResets WHERE reset_token = ? AND expires_at > NOW()";

        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
             PreparedStatement statement = conn.prepareStatement(sql)) {

            statement.setString(1, token);
            ResultSet resultSet = statement.executeQuery();

            return resultSet.next(); // Token is valid
        } catch (SQLException e) {
            System.err.println("Error verifying reset token.");
            e.printStackTrace();
        }
        return false;
    }

    public boolean updatePassword(String resetToken, String newPassword) {
        if (!verifyResetToken(resetToken)) {
            System.err.println("Invalid or expired reset token.");
            return false;
        }

        String sql = "UPDATE Users SET password_hash = ? WHERE id = (SELECT user_id FROM PasswordResets WHERE reset_token = ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
             PreparedStatement statement = conn.prepareStatement(sql)) {

            String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());

            statement.setString(1, hashedPassword);
            statement.setString(2, resetToken);

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating password.");
            e.printStackTrace();
        }
        return false;
    }


}