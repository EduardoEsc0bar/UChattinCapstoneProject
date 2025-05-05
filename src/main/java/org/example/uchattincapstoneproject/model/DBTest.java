package org.example.uchattincapstoneproject.model;

public class DBTest {
    public static void main(String[] args) {
        DB db = DB.getInstance();

        // Try login first
        testLogin();

        // Create a properly hashed password
        String rawPassword = "FARM123$";
        String hashedPassword = db.hashPassword(rawPassword);
        System.out.println("Hashed password: " + hashedPassword);

        // Create user with properly hashed password
        User testUser = new User(
                "inigo", // Different username to avoid conflicts
                hashedPassword, // BCrypt hashed password
                "Inigo",
                "Martinez",
                "2000-01-01",
                "inigom@example.com",
                "000-000-0000",
                "they/them",
                "other",
                "",
                "",
                "Test User"
        );

        boolean inserted = db.insertUser(testUser);
        System.out.println("User inserted with proper hashing: " + inserted);

        // Try login with the new user
        boolean loginSuccess = db.authenticateUser("inigo", rawPassword) != null;
        System.out.println("Login test for new user: " + loginSuccess);
    }

    public static void testLogin() {
        try {
            DB db = DB.getInstance();
            User user = db.authenticateUser("inigo", "FARM123$");
            System.out.println("Login test for inigo: " + (user != null));
        } catch (Exception e) {
            System.out.println("Error testing login: " + e.getMessage());
            e.printStackTrace();
        }
    }


}
