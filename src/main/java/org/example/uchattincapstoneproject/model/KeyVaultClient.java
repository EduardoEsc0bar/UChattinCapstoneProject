package org.example.uchattincapstoneproject.model;

import com.azure.core.http.HttpClient;
import com.azure.core.http.HttpHeaders;
import com.azure.core.http.HttpMethod;
import com.azure.core.http.HttpRequest;
import com.azure.core.http.HttpResponse;
import com.azure.core.http.netty.NettyAsyncHttpClientBuilder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.logging.Logger;

/**
 * KeyVaultClient is a utility class to retrieve secrets from Azure Key Vault using a bearer token.
 *
 * <p>It uses an HTTP client to make REST API calls to Azure Key Vault and parse the JSON response
 * to extract the value of the requested secret.</p>
 *
 * <p><strong>Warning:</strong> Storing access tokens in code is insecure. In production environments,
 * consider using Managed Identities or a secure credential store.</p>
 */
public class KeyVaultClient {
    private static final String KEY_VAULT_URL = "https://communicationkeysvault.vault.azure.net/";

    // Updated access token with longer expiration
    private static final String ACCESS_TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsIng1dCI6IkNOdjBPSTNSd3FsSEZFVm5hb01Bc2hDSDJYRSIsImtpZCI6IkNOdjBPSTNSd3FsSEZFVm5hb01Bc2hDSDJYRSJ9.eyJhdWQiOiJjZmE4YjMzOS04MmEyLTQ3MWEtYTNjOS0wZmMwYmU3YTQwOTMiLCJpc3MiOiJodHRwczovL3N0cy53aW5kb3dzLm5ldC81YzUzZjdlNC1mMWI4LTQzOWItOTc2Yi0zN2I1OGZiYWQ5NzEvIiwiaWF0IjoxNzQ1NTI4OTI2LCJuYmYiOjE3NDU1Mjg5MjYsImV4cCI6MTc0NTUzMzcxMywiYWNyIjoiMSIsImFpbyI6IkFXUUFtLzhaQUFBQTgzbFk0YW1rYWhFQWhHUHlkdDMreHpsMlk1YUlLY1lQcGVOeSt4YWxlRmY3US94Q1pod2JZUGZRMlhIMllPSFN5a21Qb3pYTU9LdzBNSUtma1ZrTGkyL0paUWVrY0VNMFdiUmZzZ1NWcGVaYTc2d3JRK2lHMVpiS01NUmI2STd6IiwiYW1yIjpbInB3ZCIsIm1mYSJdLCJhcHBpZCI6IjA0YjA3Nzk1LThkZGItNDYxYS1iYmVlLTAyZjllMWJmN2I0NiIsImFwcGlkYWNyIjoiMCIsImZhbWlseV9uYW1lIjoiRXNjb2JhciIsImdpdmVuX25hbWUiOiJFZHVhcmRvIiwiZ3JvdXBzIjpbIjE1NzEzZDNjLTFjZTMtNGZiMS1iNjYyLWE0ZmYxOTczNGVkMCIsIjljN2IxZWEyLWVkMzQtNDNjNi05Yzc5LTcyNjRhNDE4ZTdiNCIsIjVlZmFmYmI0LTUzYjctNDUyNS05MzJkLWJiOTgyMTM2NmY2OCIsIjg4MGEwMmM2LTQyMjctNDE1ZC1iNTdmLWMwNDg5NGJlNDJkNSJdLCJpZHR5cCI6InVzZXIiLCJpcGFkZHIiOiIyNjAwOjQwNDE6NWEzZjoxODAwOmYwOTI6OWE1MDplZTYwOjU2NyIsIm5hbWUiOiJFZHVhcmRvIEVzY29iYXIiLCJvaWQiOiJhZTNmNWI4OC05NWQ5LTQxNmItODZkYS1mNWFmMjgzNDU5MTEiLCJvbnByZW1fc2lkIjoiUy0xLTUtMjEtNDY1MzkwMDIxLTM4MDc3MjgzMi05MzU3NTA0MjktNDExODA4IiwicHVpZCI6IjEwMDMyMDAzODMzMjdCMTQiLCJyaCI6IjEuQVJjQTVQZFRYTGp4bTBPWGF6ZTFqN3JaY1RtenFNLWlnaHBIbzhrUHdMNTZRSk5vQVdRWEFBLiIsInNjcCI6InVzZXJfaW1wZXJzb25hdGlvbiIsInNpZCI6IjAwNDBiYTY5LTNhYmUtODZmOC04ZWNhLWZhNGYwMDI1ZWFhNSIsInN1YiI6ImhXUDhvdWttY2tqN3lWcjVRTG9xNVZUWUhLc0RaQ2tZczhZNGszOW1lY3MiLCJ0aWQiOiI1YzUzZjdlNC1mMWI4LTQzOWItOTc2Yi0zN2I1OGZiYWQ5NzEiLCJ1bmlxdWVfbmFtZSI6IkVTQ09FMjJAZmFybWluZ2RhbGUuZWR1IiwidXBuIjoiRVNDT0UyMkBmYXJtaW5nZGFsZS5lZHUiLCJ1dGkiOiJFWXl1QmJFUzdrV1ZMVmNsV1JOVEFBIiwidmVyIjoiMS4wIiwid2lkcyI6WyJiNzlmYmY0ZC0zZWY5LTQ2ODktODE0My03NmIxOTRlODU1MDkiXSwieG1zX2Z0ZCI6InU3VDFCb0FFaGdQTU9tWEstTkVIUW1QQXVvMExnaE9tdUJUSEFkUWJmSDQiLCJ4bXNfaWRyZWwiOiIxIDE4In0.HKVfIKHYSjEX0E_zZ937inDe4dg90LksNVB8qnGl1m7VqlrO6zEwGH88ViBW1BQQHMYtQ9d81CjROvdDCvCQ1cNJkdZ0VjFVRkOe81Pp379pBxTOndyCUy7leV6DxGihTqjMw0dYa0-5BnVBil9s0ynTKnhdAlmEXXECR5-3iNWudLCiUSKDpGUEMJ5Sduqy5QSA3iZQVat84nhffwkY9OG4brTndH5u3i2w5m4KK-lrZfU5wBPZP65uN9R8UfM-tgCDJaW_DWfMpQq6b_sRwXXEal4zLG8tQkYsqhEYyaMJpnLVTjDhiyhH_WG-_4Uhq2BcoDmm0kywrakaD96Qug";

    // Create an HTTP client
    private static final HttpClient httpClient = new NettyAsyncHttpClientBuilder().build();
    private static final ObjectMapper objectMapper = new ObjectMapper();
    /**
     * Retrieves a secret value from Azure Key Vault by its name.
     *
     * @param secretName the name of the secret stored in Azure Key Vault
     * @return the value of the secret if found, or null if an error occurs
     */
    public static String getSecret(String secretName) {
        try {
            System.out.println("Attempting to get secret: " + secretName);

            // Build the URL to fetch the secret
            String url = KEY_VAULT_URL + "secrets/" + secretName + "?api-version=7.3";

            // Create HTTP request with the token in the Authorization header
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + ACCESS_TOKEN);

            HttpRequest request = new HttpRequest(HttpMethod.GET, url)
                    .setHeaders(headers);

            // Send the request and get the response
            HttpResponse response = httpClient.send(request).block();

            if (response.getStatusCode() == 200) {
                // Parse the response body
                String responseBody = response.getBodyAsString().block();
                JsonNode json = objectMapper.readTree(responseBody);

                // Extract the secret value
                String secretValue = json.get("value").asText();
                System.out.println("Successfully retrieved secret");
                return secretValue;
            } else {
                System.err.println("Error retrieving secret. Status code: " + response.getStatusCode());
                System.err.println("Response: " + response.getBodyAsString().block());
                return null;
            }
        } catch (Exception e) {
            System.err.println("Error retrieving secret: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    private static final Logger logger = Logger.getLogger(KeyVaultClient.class.getName());
}