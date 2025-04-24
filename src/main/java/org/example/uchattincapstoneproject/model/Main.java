package org.example.uchattincapstoneproject.model;

import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.CosmosException;
import com.azure.cosmos.models.CosmosItemResponse;
import com.azure.cosmos.models.PartitionKey;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.cognitiveservices.speech.ResultReason;
import com.microsoft.cognitiveservices.speech.SpeechSynthesisResult;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource(
                "main-view.fxml"));

        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        System.out.println("Starting Tests...\n");
        System.out.println("Cosmos DB Endpoint: " + KeyVaultClient.getSecret("Cosmo-DB-EndPoint"));
        //testCosmosDB();
//        try {
//            SpeechService speechService = new SpeechService();
//            speechService.synthesizeText("Hello, world!");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        System.out.println("\nAll tests completed.");
        launch();
    }


    private static void testCosmosDB() {
        System.out.println("\n🔹 Testing Azure Cosmos DB...");
        try {
            CosmosContainer container = CosmoAzureDB.getContainer();
            if (container == null) {
                System.out.println("Cosmos DB Test Failed: Container is null.");
                return;
            }

            // Define test item
            String itemId = "test2";
            String partitionKey = "/Id";
            String json = "{ \"id\": \"" + itemId + "\", \"partitionKey\": \"" + partitionKey + "\", \"name\": \"Test Item\" }";

            // Convert JSON string to Jackson JsonNode
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(json);

            // Upsert item (insert if not exists, otherwise update)
            container.upsertItem(jsonNode);
            System.out.println("✅ Item upserted successfully.");
            try {
                Thread.sleep(5000); // Sleep for 500 milliseconds (0.5 seconds)
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Preserve interrupt status
                e.printStackTrace();
            }
            // Read item
            CosmosItemResponse<JsonNode> retrievedItem = container.readItem(itemId, new PartitionKey(partitionKey), JsonNode.class);
            if (retrievedItem != null) {
                System.out.println("Item retrieved successfully: " + retrievedItem.getItem().toPrettyString());
            } else {
                System.out.println("Item retrieval failed.");
            }
        } catch (CosmosException e) {
            System.out.println("Cosmos DB Exception: " + e.getStatusCode() + " - " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("General Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

}