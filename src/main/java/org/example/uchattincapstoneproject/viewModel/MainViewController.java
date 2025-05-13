package org.example.uchattincapstoneproject.viewModel;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.uchattincapstoneproject.model.ArasaacService;
import org.example.uchattincapstoneproject.model.SpeechService;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

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
            ,social, places, weather, time, verbs;
    @FXML
    private ImageView categoriesIV, favoritesIV, keyboardIV, quickAccessIV, settingsIV;
    @FXML
    private Pane directoryPane, quickAccessPane, favoritesPane, categoriesPane;
    @FXML
    private Slider volumeQuickAccessSlider;
    private String username;
    private static final int MAX_CONCURRENT_LOADS = 3;
    private int activeLoads = 0;

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
        favoritesIV.setOnMouseClicked(event -> showPane(favoritesPane));
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

        sentenceBuilderTextArea.setWrapText(true);
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
        categoriesPane.setVisible(pane == categoriesPane);
        quickAccessPane.setVisible(pane == quickAccessPane);
        backToDirectoryPaneButton.setVisible(pane == categoriesPane);
        backToDirectoryButton2.setVisible(pane == quickAccessPane);
        backToDirectoryPane3.setVisible(pane == favoritesPane);

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
        String pictogramResponse = arasaacService.fetchPictograms(category);
        System.out.println("api response for category: " + category);
        populateTilePane(pictogramResponse);
    }



    private void populateTilePane(String pictograms) {
        communicationTilePane.getChildren().clear();
        System.out.println("Updating TilePane with pictograms...");
        List<String> sortedPictograms = Arrays.stream(pictograms.split("\n"))
                .map(line -> new AbstractMap.SimpleEntry<>(
                        line.split(":", 2)[0].trim().toLowerCase(), line))
                .sorted(Comparator.comparing(Map.Entry::getKey))
                .map(Map.Entry::getValue) // return the original line
                .collect(Collectors.toList());
        Queue<String> queue = new LinkedList<>(sortedPictograms);
        for (int i = 0; i < MAX_CONCURRENT_LOADS; i++) {
            loadNextPictogram(queue);
        }
    }

    private void loadNextPictogram(Queue<String> queue) {
        if (queue.isEmpty()) return;
        String item = queue.poll();
        if (item == null || !item.contains(":")) {
            loadNextPictogram(queue);
            return;
        }
        String[] parts = item.split(":", 2);
        String phrase = parts[0].trim();
        String imageUrl = parts[1].trim();
        if (imageUrl.isEmpty() || !imageUrl.startsWith("https://static.arasaac.org/pictograms/")) {
            loadNextPictogram(queue);
            return;
        }
        activeLoads++;
        Image image = new Image(imageUrl, 120, 120, true, true, true);
        image.progressProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.doubleValue() == 1.0) {
                Platform.runLater(() -> {
                    ImageView imageView = new ImageView(image);
                    imageView.setFitHeight(90);
                    imageView.setFitWidth(90);
                    Label label = new Label(phrase);
                    label.setWrapText(true);
                    label.setTextAlignment(TextAlignment.CENTER);
                    label.setMaxWidth(100);
                    label.setAlignment(Pos.CENTER);
                    VBox vBox = new VBox(5, imageView, label);
                    vBox.setAlignment(Pos.CENTER);
                    Button button = new Button();
                    button.setPrefSize(120, 120);
                    button.setGraphic(vBox);
                    button.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                    button.setOnAction(event -> {
                        addWordToSentence(phrase);
                        speechService.synthesizeText(phrase);
                    });
                    communicationTilePane.getChildren().add(button);
                    activeLoads--;
                    loadNextPictogram(queue); // Continue loading next
                });
            }
        });
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



