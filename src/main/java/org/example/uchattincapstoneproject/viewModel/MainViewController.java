package org.example.uchattincapstoneproject.viewModel;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.uchattincapstoneproject.model.ArasaacService;
import org.example.uchattincapstoneproject.model.SpeechService;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class MainViewController {
    @FXML
    private TilePane communicationTilePane, mainCategoriesTilePane;
    @FXML
    private TextArea sentenceBuilderTextArea;
    @FXML
    private Button saveSentenceButton, readOutLoudButton, toggleThemeQuickAccessButton, goToProfileQuickAccessButton,
    backToDirectoryPaneButton;
    @FXML
    private ImageView feelings, food, animals, activities, shapesAndColor, pronouns, emergency, transportation
            ,accessibility, hobbies, places, weather, time, selfAdvocacy;
    @FXML
    private ImageView categoriesIV, favoritesIV, keyboardIV, quickAccessIV, settingsIV;
    @FXML
    private Pane directoryPane, quickAccessPane, favoritesPane;
    @FXML
    private Slider volumeQuickAccessSlider;
    private String username;


    private final ArasaacService arasaacService = new ArasaacService();
    private SpeechService speechService;
    private int userID;

    @FXML
    private void initialize() {
        showPane(directoryPane); //initially show directory pane
        try {
            speechService = new SpeechService(); //Initialize speech service
        } catch (Exception e) {
            e.printStackTrace();
        }

        categoriesIV.setOnMouseClicked(event -> showPane(mainCategoriesTilePane));
        settingsIV.setOnMouseClicked(event -> navigateToSettingsScreen());

        feelings.setOnMouseClicked(event -> fetchCategoryData("Feelings"));
        hobbies.setOnMouseClicked(event -> fetchCategoryData("Hobbies"));
        activities.setOnMouseClicked(event -> fetchCategoryData("Activities"));
        food.setOnMouseClicked(event -> fetchCategoryData("Food"));
        shapesAndColor.setOnMouseClicked(event -> fetchCategoryData("Colors"));
        pronouns.setOnMouseClicked(event -> fetchCategoryData("Pronouns"));
        emergency.setOnMouseClicked(event -> fetchCategoryData("Emergency"));
        transportation.setOnMouseClicked(event -> fetchCategoryData("Transportation"));
        animals.setOnMouseClicked(event -> fetchCategoryData("Animals"));
        accessibility.setOnMouseClicked(event -> fetchCategoryData("Accessibility"));
        places .setOnMouseClicked(event -> fetchCategoryData("Places"));
        weather.setOnMouseClicked(event -> fetchCategoryData("Weather"));
        time.setOnMouseClicked(event -> fetchCategoryData("Time"));
        selfAdvocacy.setOnMouseClicked(event -> fetchCategoryData("Self Advocacy"));
        readOutLoudButton.setOnAction(event -> speakPhrase());
    }

    public void setUsername(String username){
        this.username = username;
        Label welcomeLabel = new Label("Welcome " + username + "!");
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    private void showPane(Pane pane) {
        directoryPane.setVisible(pane == directoryPane);
        mainCategoriesTilePane.setVisible(pane == mainCategoriesTilePane);
        backToDirectoryPaneButton.setVisible(pane == directoryPane);
    }


    private void navigateToSettingsScreen() {
        try {
            Parent settingsRoot = FXMLLoader.load(getClass().getResource("/views/settingsScreen.fxml"));
            Scene currentScene = settingsIV.getScene();

            //Fade out effect
            FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.5), currentScene.getRoot());
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setOnFinished(event -> {
                currentScene.setRoot(settingsRoot);

                //Fade in effect after switching scenes
                FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.5), settingsRoot);
                fadeIn.setFromValue(0);
                fadeIn.setToValue(1);
                fadeIn.play();
            });

            fadeOut.play();

        } catch (IOException e) {
            System.err.println("Error loading settings screen.");
            e.printStackTrace();
        }
    }


    private void fetchCategoryData(String category) {
        String pictogramResponse = arasaacService.fetchPictograms(category); //Fetch phrases & images
        System.out.println("api response for category: " + category);
        populateTilePane(pictogramResponse);
    }
    private void populateTilePane(String pictograms) {
        communicationTilePane.getChildren().clear();
        System.out.println("Updating TilePane with pictograms...");

        String[] pictogramList = pictograms.split("\n");
        int loadedCount = 0;

        for (String item : pictogramList) {
            String[] parts = item.split(":", 2);

            if (parts.length < 2) continue;

            String phrase = parts[0].trim();
            String imageUrl = parts[1].trim();

            if (imageUrl.isEmpty() || !imageUrl.startsWith("https://static.arasaac.org/pictograms/")) continue;

            Button pictogramButton = new Button(phrase);
            pictogramButton.setPrefSize(100, 100);

            try {
                Image pictoImage = new Image(imageUrl, 100, 100, true, true, false);
                if (pictoImage.isError()) continue;

                ImageView imageView = new ImageView(pictoImage);
                imageView.setFitHeight(100);
                imageView.setFitWidth(100);

                pictogramButton.setGraphic(imageView);
                pictogramButton.setOnAction(event -> {
                    addWordToSentence(phrase);
                    speechService.synthesizeText(phrase);
                });

                communicationTilePane.getChildren().add(pictogramButton);
                loadedCount++;

            } catch (Exception e) {
                System.out.println("Failed to load pictogram: " + phrase);
            }
        }

        System.out.println("Loaded " + loadedCount + " pictograms into the TilePane.");
    }


    private void addWordToSentence(String phrase) {
        sentenceBuilderTextArea.appendText(phrase + " ");
    }

    private void speakPhrase() {
        String sentence = sentenceBuilderTextArea.getText().trim();
        if (!sentence.isEmpty()) {
            System.out.println("speaking " + sentence);
            speechService.synthesizeText(sentence); // Read phrase aloud
        }
    }

    public static void main(String[] args) {
        try {
            // Initialize services
            ArasaacService arasaacService = new ArasaacService();
            SpeechService service = new SpeechService();

            // Fetch test pictograms
            String testPictogramData = arasaacService.fetchPictograms("feelings");
            System.out.println("Test Pictogram Response: \n" + testPictogramData);

            // Check if response is valid
            if (testPictogramData == null || testPictogramData.isEmpty()) {
                System.out.println("No pictograms found for the category.");
                return;
            }

            // Process pictogram data
            String[] pictogramList = testPictogramData.split("\n");

            if (pictogramList.length == 0) {
                System.out.println("No pictograms available after parsing.");
                return;
            }

            // Extract first pictogram's phrase
            String firstPictogram = pictogramList[0];
            String[] parts = firstPictogram.split(":");

            if (parts.length < 2) {
                System.out.println("Invalid pictogram format: " + firstPictogram);
                return;
            }

            String label = parts[0];  // Extract associated word
            String imageUrl = parts[1]; // Extract associated image

            // Print extracted details
            System.out.println("First Pictogram Phrase: " + label);
            System.out.println("Image URL: " + imageUrl);

            // Test Speech Synthesis
            service.synthesizeText(label);
        } catch (Exception e) {
            System.err.println("Error during execution: " + e.getMessage());
            e.printStackTrace();
        }
    }
}



