package org.example.uchattincapstoneproject.viewModel;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.RotateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.uchattincapstoneproject.model.ArasaacService;
import org.example.uchattincapstoneproject.model.FavoritePhrase;
import org.example.uchattincapstoneproject.model.FavoritePhraseStorage;
import org.example.uchattincapstoneproject.model.SpeechService;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class MainViewController {
    @FXML
    private TilePane communicationTilePane;
    @FXML
    private AnchorPane root;
    @FXML
    BorderPane contentPane;
    @FXML
    private TextArea sentenceBuilderTextArea;
    @FXML
    private Button saveSentenceButton, readOutLoudButton, toggleThemeQuickAccessButton, goToProfileQuickAccessButton,
    backToDirectoryPaneButton, backToDirectoryButton2, backToDirectoryPane3;
    @FXML
    private ImageView feelings, food, animals, activities, colors, shapes, pronouns, emergency, vehicles
            ,social, places, weather, time, verbs, loadingIV;
    @FXML
    private ImageView categoriesIV, favoritesIV, keyboardIV, quickAccessIV, settingsIV;
    @FXML
    private Pane directoryPane, quickAccessPane, favoritesPane, categoriesPane, splashPane;
    @FXML
    private Slider volumeQuickAccessSlider;
    private String username;

    private final ArasaacService arasaacService = new ArasaacService();
    private SpeechService speechService;
    private int userID;

    //keyboard
    private static final double KEYBOARD_FULL = 1.0;
    private static final double KEYBOARD_DIM = 0.3;
    private boolean isKeyBoardFull = false;


    @FXML
    private void initialize() {
        //bind content to center
        Platform.runLater(()->{
            UIUtilities.centerContent(root, contentPane); //force center in startup
            root.widthProperty().addListener((observable, oldValue, newValue) -> UIUtilities.centerContent(root, contentPane));
            root.heightProperty().addListener((observable,oldValue, newValue) -> UIUtilities.centerContent(root, contentPane));

        });

        //initially show directory pane
        showPane(directoryPane);

        Platform.runLater(()->{
            backToDirectoryButton2.setVisible(true);

            System.out.println("backToDirectoryPaneButton is visible " + backToDirectoryPaneButton.isVisible());
            System.out.println("backToDirectoryButton2 is visible" + backToDirectoryButton2.isVisible());
            System.out.println("directory Pane visisble " + directoryPane.isVisible());
        });

        try {
            speechService = new SpeechService(); //Initialize speech service
        } catch (Exception e) {
            e.printStackTrace();
        }

        categoriesIV.setOnMouseClicked(event -> showPane(categoriesPane));
        settingsIV.setOnMouseClicked(event -> UIUtilities.navigateToScreen("/views/settingsScreen.fxml", root.getScene(), true));
        favoritesIV.setOnMouseClicked(event -> {
            loadFavorites();
            showPane(favoritesPane);
        });
        quickAccessIV.setOnMouseClicked(event -> showPane(quickAccessPane));
        backToDirectoryPaneButton.setOnAction(even -> showPane(directoryPane));
        backToDirectoryButton2.setOnAction(even -> showPane(directoryPane));
        backToDirectoryPane3.setOnAction(even -> showPane(directoryPane));

        keyboardIV.setOpacity(KEYBOARD_FULL);
        keyboardIV.setOnMouseClicked(event -> toggleKeyboard());

        feelings.setOnMouseClicked(event -> fetchCategoryData("Feelings"));
        activities.setOnMouseClicked(event -> fetchCategoryData("Things to do"));
        food.setOnMouseClicked(event -> fetchCategoryData("Food"));
        colors.setOnMouseClicked(event -> fetchCategoryData("Colors"));
        shapes.setOnMouseClicked(event -> fetchCategoryData("Shapes"));
        pronouns.setOnMouseClicked(event -> fetchCategoryData("Pronouns"));
        emergency.setOnMouseClicked(event -> fetchCategoryData("Emergency"));
        vehicles.setOnMouseClicked(event -> fetchCategoryData("Vehicles"));
        animals.setOnMouseClicked(event -> fetchCategoryData("Animals"));
        social.setOnMouseClicked(event -> fetchCategoryData("Social"));
        places .setOnMouseClicked(event -> fetchCategoryData("Places"));
        weather.setOnMouseClicked(event -> fetchCategoryData("Weather"));
        time.setOnMouseClicked(event -> fetchCategoryData("Time"));
        verbs.setOnMouseClicked(event -> fetchCategoryData("Verbs"));

        readOutLoudButton.setOnAction(event -> speakPhrase());

        saveSentenceButton.setOnAction(event -> saveCurrentPhraseAsFavorite());
        sentenceBuilderTextArea.setWrapText(true);

        goToProfileQuickAccessButton.setOnAction(event -> UIUtilities.navigateToScreen("/views/userProfile.fxml", root.getScene(), false));

        if(splashPane != null){
            splashPane.setVisible(false);
            splashPane.toFront();
        }else{
            System.out.println("splash pane is null");
        }
    }

    public void setUsername(String username){
        this.username = username;
        Label welcomeLabel = new Label("Welcome " + username + "!");
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    private void showSplashScreen(){

        Platform.runLater(()->{
            splashPane.setVisible(true);
            splashPane.toFront();
            System.out.println("splash screen should be visible");
        });

        RotateTransition rotate = new RotateTransition(Duration.millis(200), loadingIV);
        rotate.setByAngle(360);
        rotate.setCycleCount(Animation.INDEFINITE);
        rotate.play();
    }

    private void showPane(Pane pane) {
        directoryPane.setVisible(pane == directoryPane);
        categoriesPane.setVisible(pane == categoriesPane);
        quickAccessPane.setVisible(pane == quickAccessPane);
        favoritesPane.setVisible(pane == favoritesPane);

        //bring pane to front
        pane.toFront();

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
        showSplashScreen();
        String pictogramResponse = arasaacService.fetchPictograms(category); //Fetch phrases & images
        System.out.println("api response for category: " + category);
        Platform.runLater(()->{
            populateTilePane(pictogramResponse);
            splashPane.setVisible(false);
        });

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
            pictogramButton.setPrefSize(120, 140);
            pictogramButton.setWrapText(true);
            pictogramButton.setContentDisplay(ContentDisplay.TOP);

            try {
                Image pictoImage = new Image(imageUrl, 120, 120, true, true, false);
                if (pictoImage.isError()) continue;

                ImageView imageView = new ImageView(pictoImage);
                imageView.setFitHeight(120);
                imageView.setFitWidth(120);

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

    //toggle keyboard
    private void toggleKeyboard(){
        isKeyBoardFull = !isKeyBoardFull;
        double targetOpacity = isKeyBoardFull ? KEYBOARD_FULL: KEYBOARD_DIM;
        FadeTransition fade = new FadeTransition(Duration.millis(300), keyboardIV);
        fade.setFromValue(keyboardIV.getOpacity());
        fade.setToValue(targetOpacity);
        fade.play();

        //enable keyboard
        sentenceBuilderTextArea.setEditable(isKeyBoardFull);
        //request focus when toggled on
        if(isKeyBoardFull){
            sentenceBuilderTextArea.requestFocus();
        }
    }

    private void saveCurrentPhraseAsFavorite() {
        String currentPhrase = sentenceBuilderTextArea.getText().trim();

        if (currentPhrase.isEmpty()) {
            return; // No phrase to save, silently exit.
        }

        // Fetch ONE pictogram related to this phrase.
        String imageUrl = fetchPictogramForPhrase(currentPhrase);

        // Store the favorite with the phrase and one matching pictogram.
        FavoritePhrase fav = new FavoritePhrase(currentPhrase, imageUrl);
        FavoritePhraseStorage.getInstance().addFavoritePhrase(userID, fav);
    }

    private String fetchPictogramForPhrase(String phrase) {
        //Try fetching a pictogram for the full phrase first.
        String encodedPhrase = URLEncoder.encode(phrase, StandardCharsets.UTF_8);
        String pictograms = arasaacService.fetchPictograms(encodedPhrase);

        if (pictograms != null && !pictograms.isEmpty()) {
            String[] pictogramList = pictograms.split("\n");
            for (String item : pictogramList) {
                String[] parts = item.split(":", 2);
                if (parts.length < 2) continue;

                String imageUrl = parts[1].trim();
                if (imageUrl.startsWith("https://static.arasaac.org/pictograms/")) {
                    return imageUrl; // Return first valid pictogram found
                }
            }
        }

        //If no full phrase pictogram found, search for individual words separately.
        String[] words = phrase.split("\\s+");
        for (String word : words) {
            String encodedWord = URLEncoder.encode(word, StandardCharsets.UTF_8);
            String wordPictograms = arasaacService.fetchPictograms(encodedWord);

            if (wordPictograms != null && !wordPictograms.isEmpty()) {
                String[] pictogramList = wordPictograms.split("\n");
                for (String item : pictogramList) {
                    String[] parts = item.split(":", 2);
                    if (parts.length < 2) continue;

                    String imageUrl = parts[1].trim();
                    if (imageUrl.startsWith("https://static.arasaac.org/pictograms/")) {
                        return imageUrl; // Return the first valid pictogram
                    }
                }
            }
        }

        return ""; // No valid pictogram found, return empty string instead of null
    }


    //load favorite phrases into pane
    private void loadFavorites() {
        communicationTilePane.getChildren().clear(); // Clear previous content

        List<FavoritePhrase> favorites = FavoritePhraseStorage.getInstance().getFavoritePhrases(userID);
        if (favorites.isEmpty()) {
            System.out.println("No favorites found for user: " + userID);
            splashPane.setVisible(false);
            return;
        }

        for (FavoritePhrase fav : favorites) {
            // Create a button for the phrase itself.
            Button favButton = new Button(fav.getPhrase());
            favButton.setPrefSize(120, 140);
            favButton.setWrapText(true);
            favButton.setContentDisplay(javafx.scene.control.ContentDisplay.TOP);

            try {
                // Display a pictogram next to the phrase, if available.
                if (!fav.getImageURL().isEmpty()) {
                    Image pictoImage = new Image(fav.getImageURL(), 120, 120, true, true, false);
                    ImageView pictoView = new ImageView(pictoImage);
                    pictoView.setFitHeight(120);
                    pictoView.setFitWidth(120);
                    favButton.setGraphic(pictoView);

                    pictoView.setOnMouseClicked(event -> {
                        sentenceBuilderTextArea.appendText(fav.getPhrase() + " ");
                        speechService.synthesizeText(fav.getPhrase());
                    });

                    communicationTilePane.getChildren().add(pictoView); // Add pictogram to tile pane
                }
            } catch (Exception e) {
                System.out.println("Error loading pictogram for favorite: " + fav.getPhrase());
            }

            // Clicking the phrase button adds it to the sentence builder and speaks it aloud.
            favButton.setOnAction(event -> {
                sentenceBuilderTextArea.appendText(fav.getPhrase() + " ");
                speechService.synthesizeText(fav.getPhrase());
            });

            // Add both phrase button and pictogram to the tile pane.
            communicationTilePane.getChildren().add(favButton);
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
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



