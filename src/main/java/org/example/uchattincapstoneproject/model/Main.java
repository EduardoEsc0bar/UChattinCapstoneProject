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
            System.out.println("attempting to start create avatar screen");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/createAvatarScreen.fxml"));
            AnchorPane root = loader.load();
            System.out.println("createAvatarScreen.fxml load successful");
            Scene scene = new Scene(root, 800, 600);
            stage.setScene(scene);
            stage.setTitle("Create Avatar");
            stage.show();
            System.out.println("create avatar screen displayed");
        }catch (Exception e){
            System.out.println("Error initializing log in screen");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        User u = new User("joeFarmingdale","farm123$","Joe","Farm","5/5/2005","joe@Farm.edu","(123)456-7891", "He/Him","Male", "Male","He/Him", "Joe");
//        DB.getInstance().insertUser(u);
//        u.setFirstName("it was joe before");
//        DB.getInstance().updateUserDisplayName();
        //DB.getInstance().removeUserByUsername(u.getUsername());
        launch(args);
    }

}