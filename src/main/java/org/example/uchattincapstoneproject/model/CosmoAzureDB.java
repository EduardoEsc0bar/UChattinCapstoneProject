package org.example.uchattincapstoneproject.model;

import com.azure.cosmos.*;

public class CosmoAzureDB {
    private static final String DATABASE_NAME = CosmoConfig.DATABASE_NAME;
    private static final String CONTAINER_NAME = CosmoConfig.CONTAINER_NAME;
    private static CosmosClient cosmosClient;
    private static CosmosDatabase cosmosDatabase;
    private static CosmosContainer cosmosContainer;

    // Initialize Cosmos DB Connection
    static {
        try {
            String cosmosEndpoint = KeyVaultClient.getSecret("Cosmo-DB-EndPoint");
            String cosmosKey = KeyVaultClient.getSecret("Cosmo-DB-API");

            cosmosClient = new CosmosClientBuilder()
                    .endpoint(cosmosEndpoint)
                    .key(cosmosKey)
                    .gatewayMode()
                    .consistencyLevel(ConsistencyLevel.SESSION)
                    .buildClient();

            // Create or get the database
            cosmosDatabase = cosmosClient.getDatabase(DATABASE_NAME);
            cosmosContainer = cosmosDatabase.getContainer(CONTAINER_NAME);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Get the container instance
    public static CosmosContainer getContainer() {
        return cosmosContainer;
    }

    // Close the connection
    public static void close() {
        cosmosClient.close();
    }
}
