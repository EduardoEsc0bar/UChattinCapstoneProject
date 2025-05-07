package org.example.uchattincapstoneproject.model;

import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.CosmosException;
import com.azure.cosmos.models.CosmosItemResponse;
import com.azure.cosmos.models.PartitionKey;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        try{
            System.out.println("attempting to start entrance screen");
            //FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/createAvatarScreen.fxml"));
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/entranceScreen.fxml"));
            AnchorPane root = loader.load();
            //System.out.println("createAvatarScreen.fxml load successful");
            System.out.println("entranceScreen.fxml load successful");
            Scene scene = new Scene(root, 800, 600);
            stage.setScene(scene);
            //stage.setTitle("Create Avatar");
            stage.setTitle("Entrance Screen");
            stage.show();
            //System.out.println("create avatar screen displayed");
        }catch (Exception e){
            //System.out.println("Error initializing log in screen");
            System.out.println("Error initializing entrance screen");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

}