package org.example.uchattincapstoneproject.model;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DB {
    private static DB instance;  // Singleton instance
    private static User currentUser;

    // Azure MySQL connection details
    final String MYSQL_SERVER_URL = "jdbc:mysql://commapp.mysql.database.azure.com:3306/";
    final String DB_URL = MYSQL_SERVER_URL + "communication_app";
    final String USERNAME = "commapp_db_user";
    final String PASSWORD = "farm9786$";
    final String SSL_PARAMS = "?useSSL=true&requireSSL=true&verifyServerCertificate=false";

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

        try {
            // Connect to the database with SSL parameters
            Connection conn = DriverManager.getConnection(DB_URL + SSL_PARAMS, USERNAME, PASSWORD);
            Statement statement = conn.createStatement();

            // Check if we have users in the table Users
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
            System.err.println("Database connection error: " + e.getMessage());
            e.printStackTrace();
        }

        return hasRegistredUsers;
    }

    /**
     * Queries and prints information of a user by their username.
     *
     * @param username The username of the user to be queried.
     */
    public User queryUserByName(String username) {
        try {
            Connection conn = DriverManager.getConnection(DB_URL + SSL_PARAMS, USERNAME, PASSWORD);
            String sql = "SELECT * FROM Users WHERE username = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, username);

            ResultSet resultSet = preparedStatement.executeQuery();

            User user = null;
            if (resultSet.next()) {
                int id = resultSet.getInt("id");
                String email = resultSet.getString("email");
                String passwordHash = resultSet.getString("password_hash");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String dob = resultSet.getString("dob");
                String phoneNumber = resultSet.getString("phone_number");
                String gender = resultSet.getString("gender");
                String pronouns = resultSet.getString("specified_pronouns");
                String preferredName = resultSet.getString("preferred_name");

                user = new User(
                        username,
                        passwordHash,
                        firstName,
                        lastName,
                        dob,
                        email,
                        phoneNumber,
                        pronouns,
                        gender,
                        "",
                        "",
                        preferredName
                );

                System.out.println("Found user: ID: " + id + ", Name: " + username + ", Email: " + email);
            }

            preparedStatement.close();
            conn.close();
            return user;
        } catch (SQLException e) {
            System.err.println("Error querying user: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Retrieves and prints all users in the Users table.
     */
    public void listAllUsers() {
        try {
            Connection conn = DriverManager.getConnection(DB_URL + SSL_PARAMS, USERNAME, PASSWORD);
            String sql = "SELECT * FROM Users";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("username");
                String email = resultSet.getString("email");
                String preferredName = resultSet.getString("preferred_name");
                System.out.println("ID: " + id + ", Name: " + name + ", Email: " + email + ", Preferred Name: " + preferredName);
            }

            preparedStatement.close();
            conn.close();
        } catch (SQLException e) {
            System.err.println("Error listing users: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Inserts a new user into the Users table.
     *
     * @param user The User object containing all user information
     * @return true if insertion was successful, false otherwise
     */
    public boolean insertUser(User user) {
        try {
            // Format the date correctly for MySQL
            String dobFormatted = user.getDob();

            // Check if the date is in MM/DD/YYYY format and convert it
            if (dobFormatted.matches("\\d{2}/\\d{2}/\\d{4}")) {
                String[] parts = dobFormatted.split("/");
                // Convert from MM/DD/YYYY to YYYY-MM-DD
                dobFormatted = parts[2] + "-" + parts[0] + "-" + parts[1];
            }

            Connection conn = DriverManager.getConnection(DB_URL + SSL_PARAMS, USERNAME, PASSWORD);
            String sql = "INSERT INTO Users (username, email, password_hash, first_name, last_name, " +
                    "dob, phone_number, gender, specified_pronouns, preferred_name) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getEmail());
            preparedStatement.setString(3, user.getPasswordHash());
            preparedStatement.setString(4, user.getFirstName());
            preparedStatement.setString(5, user.getLastName());
            preparedStatement.setString(6, dobFormatted); // Use the formatted date
            preparedStatement.setString(7, user.getPhoneNumber());
            preparedStatement.setString(8, user.getGender());
            preparedStatement.setString(9, user.getSpecifiedPronouns());
            preparedStatement.setString(10, user.getPreferredName());

            int row = preparedStatement.executeUpdate();

            preparedStatement.close();
            conn.close();

            if (row > 0) {
                System.out.println("User registered successfully");
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Error inserting user: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Removes a user from the Users table by their username.
     *
     * @param username The username of the user to remove.
     */
    public boolean removeUserByUsername(String username) {
        try {
            Connection conn = DriverManager.getConnection(DB_URL + SSL_PARAMS, USERNAME, PASSWORD);
            String sql = "DELETE FROM Users WHERE username = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, username);

            int rowsDeleted = preparedStatement.executeUpdate();

            preparedStatement.close();
            conn.close();

            if (rowsDeleted > 0) {
                System.out.println("User with username '" + username + "' was deleted successfully.");
                return true;
            } else {
                System.out.println("No user found with username '" + username + "'.");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Error removing user: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Updates user information in the database
     *
     * @param user The User object with updated information
     * @return true if update was successful, false otherwise
     */
    public boolean updateUser(User user) {
        try {
            Connection conn = DriverManager.getConnection(DB_URL + SSL_PARAMS, USERNAME, PASSWORD);
            String sql = "UPDATE Users SET " +
                    "email = ?, " +
                    "first_name = ?, " +
                    "last_name = ?, " +
                    "dob = ?, " +
                    "phone_number = ?, " +
                    "gender = ?, " +
                    "specified_pronouns = ?, " +
                    "preferred_name = ?, " +
                    "theme_color = ?, " +
                    "text_size = ?, " +
                    "volume = ?, " +
                    "app_theme = ?, " +
                    "notifications_on = ? " +
                    "WHERE username = ?";

            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, user.getEmail());
            preparedStatement.setString(2, user.getFirstName());
            preparedStatement.setString(3, user.getLastName());
            preparedStatement.setString(4, user.getDob());
            preparedStatement.setString(5, user.getPhoneNumber());
            preparedStatement.setString(6, user.getGender());
            preparedStatement.setString(7, user.getSpecifiedPronouns());
            preparedStatement.setString(8, user.getPreferredName());

            // Optional settings - convert to strings/numbers as needed
            String colorString = user.getThemeColor() != null ?
                    String.format("#%02X%02X%02X",
                            (int)(user.getThemeColor().getRed() * 255),
                            (int)(user.getThemeColor().getGreen() * 255),
                            (int)(user.getThemeColor().getBlue() * 255)) :
                    null;

            preparedStatement.setString(9, colorString);
            preparedStatement.setDouble(10, user.getTextSize());
            preparedStatement.setDouble(11, user.getVolume());
            preparedStatement.setString(12, user.getAppTheme());
            preparedStatement.setBoolean(13, user.isNotificationsOn());

            // Where clause
            preparedStatement.setString(14, user.getUsername());

            int rowsUpdated = preparedStatement.executeUpdate();

            preparedStatement.close();
            conn.close();

            if (rowsUpdated > 0) {
                System.out.println("User '" + user.getUsername() + "' was updated successfully.");
                return true;
            } else {
                System.out.println("No user found with username '" + user.getUsername() + "'.");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Error updating user: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Saves an avatar for a user
     *
     * @param username Username of the user
     * @param style Avatar style
     * @param url Avatar URL
     * @return true if avatar was saved successfully, false otherwise
     */
    public boolean saveAvatar(String username, String style, String url) {
        try {
            Connection conn = DriverManager.getConnection(DB_URL + SSL_PARAMS, USERNAME, PASSWORD);

            // First, get the user_id
            String userQuery = "SELECT id FROM Users WHERE username = ?";
            PreparedStatement userStatement = conn.prepareStatement(userQuery);
            userStatement.setString(1, username);

            ResultSet userResult = userStatement.executeQuery();
            if (!userResult.next()) {
                System.out.println("User not found: " + username);
                userStatement.close();
                conn.close();
                return false;
            }

            int userId = userResult.getInt("id");
            userStatement.close();

            // Check if user already has an avatar
            String checkQuery = "SELECT id FROM Avatars WHERE user_id = ?";
            PreparedStatement checkStatement = conn.prepareStatement(checkQuery);
            checkStatement.setInt(1, userId);

            ResultSet checkResult = checkStatement.executeQuery();
            boolean avatarExists = checkResult.next();
            checkStatement.close();

            PreparedStatement statement;
            if (avatarExists) {
                // Update existing avatar
                String updateSql = "UPDATE Avatars SET style = ?, avatar_url = ? WHERE user_id = ?";
                statement = conn.prepareStatement(updateSql);
                statement.setString(1, style);
                statement.setString(2, url);
                statement.setInt(3, userId);
            } else {
                // Insert new avatar
                String insertSql = "INSERT INTO Avatars (user_id, style, avatar_url) VALUES (?, ?, ?)";
                statement = conn.prepareStatement(insertSql);
                statement.setInt(1, userId);
                statement.setString(2, style);
                statement.setString(3, url);
            }

            int rowsAffected = statement.executeUpdate();
            statement.close();

            // Update the avatar_id in Users table
            if (rowsAffected > 0) {
                String avatarIdQuery = "SELECT id FROM Avatars WHERE user_id = ?";
                PreparedStatement avatarIdStatement = conn.prepareStatement(avatarIdQuery);
                avatarIdStatement.setInt(1, userId);

                ResultSet avatarIdResult = avatarIdStatement.executeQuery();
                if (avatarIdResult.next()) {
                    int avatarId = avatarIdResult.getInt("id");

                    String updateUserSql = "UPDATE Users SET avatar_id = ? WHERE id = ?";
                    PreparedStatement updateUserStatement = conn.prepareStatement(updateUserSql);
                    updateUserStatement.setInt(1, avatarId);
                    updateUserStatement.setInt(2, userId);

                    updateUserStatement.executeUpdate();
                    updateUserStatement.close();
                }

                avatarIdStatement.close();
            }

            conn.close();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error saving avatar: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }


    /**
     * Authenticates a user by username and password
     *
     * @param username Username to check
     * @param password Plain text password to verify
     * @return User object if authentication successful, null otherwise
     */
    public User authenticateUser(String username, String password) {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            String sql = "SELECT id, username, password_hash FROM Users WHERE username = ?";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, username);

            System.out.println("Executing query for user: " + username);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String storedHash = resultSet.getString("password_hash");
                System.out.println("Found user: " + username);
                System.out.println("Stored password hash: " + storedHash);

                boolean passwordVerified = false;

                try {
                    // First try BCrypt verification
                    if (BCrypt.checkpw(password.trim(), storedHash.trim())) {
                        System.out.println("Password verified successfully with BCrypt!");
                        passwordVerified = true;
                    } else {
                        System.out.println("BCrypt password verification failed.");
                    }
                } catch (IllegalArgumentException e) {
                    System.out.println("Error during BCrypt verification: " + e.getMessage());

                    // If the stored password is not a valid BCrypt hash, try direct comparison
                    // IMPORTANT: This is for development only and should be removed in production!
                    if (password.equals(storedHash)) {
                        System.out.println("WARNING: Plain text password matched. This is insecure!");
                        passwordVerified = true;
                    }
                }

                if (passwordVerified) {
                    // Build and return User object
                    User authenticatedUser = new User(resultSet.getString("username"), storedHash);

                    // Update last login time
                    String updateSql = "UPDATE Users SET last_login = CURRENT_TIMESTAMP WHERE username = ?";
                    PreparedStatement updateStatement = conn.prepareStatement(updateSql);
                    updateStatement.setString(1, username);
                    updateStatement.executeUpdate();
                    updateStatement.close();

                    // Close resources
                    resultSet.close();
                    statement.close();
                    //conn.close();

                    return authenticatedUser;
                }
            } else {
                System.out.println("No user found with username: " + username);
            }

            // Close resources
            resultSet.close();
            statement.close();
            //conn.close();

        } catch (SQLException e) {
            System.err.println("Database error during authentication: " + e.getMessage());
            e.printStackTrace();
        }

        return null; // Authentication failed
    }

    /**
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

    /**
     * Updates the display name of a user by their username.
     *
     * @param username       The username of the user whose display name is to be updated.
     * @param newDisplayName The new display name to assign to the user.
     * @return true if update was successful, false otherwise
     */
    public boolean updateUserDisplayName(String username, String newDisplayName) {
        try {
            Connection conn = DriverManager.getConnection(DB_URL + SSL_PARAMS, USERNAME, PASSWORD);
            String sql = "UPDATE Users SET preferred_name = ? WHERE username = ?";

            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, newDisplayName);
            preparedStatement.setString(2, username);

            int rowsUpdated = preparedStatement.executeUpdate();

            preparedStatement.close();
            conn.close();

            if (rowsUpdated > 0) {
                System.out.println("Display name for user '" + username + "' was updated successfully.");
                return true;
            } else {
                System.out.println("No user found with username '" + username + "'.");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Error updating display name: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public void testSQLQuery(String username) {
        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD)) {
            String sql = "SELECT username, password_hash FROM Users WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);

            ResultSet resultSet = stmt.executeQuery();

            if(!resultSet.next()){
                System.out.println("no user found with username: " + username);
            }
            else{
                do{
                    System.out.println("user found: " + resultSet.getString("username"));
                    System.out.println("stored password: " + resultSet.getString("password_hash"));
                }  while (resultSet.next());
            }
            resultSet.close();
            stmt.close();
        } catch (SQLException e) {
            System.err.println("SQL Query Error: " + e.getMessage());
        }
    }

    public static void main(String[] args){
        DB db = DB.getInstance();

        db.testSQLQuery("anniep8");
    }
}