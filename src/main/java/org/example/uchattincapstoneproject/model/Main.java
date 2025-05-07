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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        try{
            System.out.println("attempting to start entrance screen");
            //FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/createAvatarScreen.fxml"));
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/mainScreen.fxml"));
            AnchorPane root = loader.load();
            //System.out.println("createAvatarScreen.fxml load successful");
            System.out.println("mainScreen.fxml load successful");
            Scene scene = new Scene(root, 1000, 800);
            stage.setScene(scene);
            //stage.setTitle("Create Avatar");
            stage.setTitle("Pictogram App");
            stage.show();
            //System.out.println("create avatar screen displayed");
        }catch (Exception e){
            //System.out.println("Error initializing log in screen");
            System.out.println("Error initializing main screen");
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        launch(args);
    }
}

        /*        String imageUrl = "https://static.arasaac.org/pictograms/35531/35531_500.png";
        Image image = new Image(imageUrl, false);

        if (image.isError()) {
            System.out.println("Error loading image: " + image.getException());
        }

        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(200);
        imageView.setFitWidth(200);

        StackPane root = new StackPane(imageView);
        Scene scene = new Scene(root, 400, 400);
        stage.setScene(scene);
        stage.setTitle("Image Load Test");
        stage.show();*/
