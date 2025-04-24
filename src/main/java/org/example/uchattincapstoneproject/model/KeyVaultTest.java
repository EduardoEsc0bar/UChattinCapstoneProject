package org.example.uchattincapstoneproject.model;

public class KeyVaultTest {
    public static void main(String[] args) {
        try {
            System.out.println("Testing Key Vault access...");
            // Try accessing one of your known secrets
            String secretValue = KeyVaultClient.getSecret("Cosmo-DB-API");
            if (secretValue != null) {
                System.out.println("Successfully retrieved secret!");
                // Don't print the actual secret value for security reasons
            } else {
                System.out.println("Failed to retrieve secret - returned null");
            }
        } catch (Exception e) {
            System.err.println("Error testing Key Vault access: " + e.getMessage());
            e.printStackTrace();
        }
    }
}