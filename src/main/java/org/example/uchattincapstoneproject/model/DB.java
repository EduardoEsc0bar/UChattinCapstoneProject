package org.example.uchattincapstoneproject.model;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Singleton class for managing database interactions in the communication application.
 * <p>
 * This class handles connecting to a MySQL database hosted on Azure,
 * managing users in the "Users" table, and storing the currently authenticated user.
 * It ensures a single instance of the DB class is used throughout the application
 * (Singleton Pattern) and provides methods to insert, query, list, update, and delete users.
 * </p>
 *
 * <p>
 * Key Features:
 * <ul>
 *     <li>Thread-safe singleton instantiation</li>
 *     <li>Automatic database and table creation if not present</li>
 *     <li>Basic user management (CRUD operations)</li>
 *     <li>Password hashing before storage</li>
 * </ul>
 * </p>
 *
 * <p>
 * Example usage:
 * <pre>{@code
 *     DB db = DB.getInstance();
 *     db.insertUser("jdoe", "jdoe@example.com", "password123", "John Doe");
 * }</pre>
 * </p>
 *
 * <p><b>Note:</b> This class stores database credentials in plain text and should be
 * refactored to use secure storage mechanisms such as environment variables or Azure Key Vault in production.</p>
 */
public class DB {
    private static DB instance;  // Singleton instance
    private static User currentUser;
    final String MYSQL_SERVER_URL = "jdbc:mysql://commapp.mysql.database.azure.com/";
    final String DB_URL = MYSQL_SERVER_URL + "communication_app";
    final String USERNAME = "commapp_db_user";
    final String PASSWORD = "farm9786$";

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
     * @param name          The username of the new user.
     * @param email         The email address of the new user.
     * @param passwordPT    hashed password of the new user.
     * @param displayName   The display name of the new user.
     */
    public  void insertUser(String name, String email, String passwordPT, String displayName) {
        try {
            String passwordHash = hashPassword(passwordPT);
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            String sql = "INSERT INTO Users (username, email, password_hash, displayName) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, passwordHash);
            preparedStatement.setString(4, displayName);

            int row = preparedStatement.executeUpdate();

            if (row > 0) {
                System.out.println("A new user was inserted successfully.");
            }

            preparedStatement.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
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
    }