package org.example.uchattincapstoneproject.viewModel;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;
import org.example.uchattincapstoneproject.model.ArasaacService;
import org.example.uchattincapstoneproject.model.SpeechService;

public class MainViewController {
    @FXML
    private TilePane communicationTilePane, mainCategoriesTilePane;
    @FXML
    private TextArea sentenceBuilderTextArea;
    @FXML
    private Button saveSentenceButton, readOutLoudButton, toggleThemeQuickAccessButton, goToProfileQuickAccessButton;
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

    @FXML
    private void initialize() {
        try {
            speechService = new SpeechService(); //Initialize speech service
        } catch (Exception e) {
            e.printStackTrace();
        }

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
    }

    private void fetchCategoryData(String category) {
        String pictogramResponse = arasaacService.fetchPictograms(category); //Fetch phrases & images
        populateTilePane(pictogramResponse);
    }

    private void populateTilePane(String pictograms) {
        communicationTilePane.getChildren().clear();
        String[] pictogramList = pictograms.split("\n");

        for (String item : pictogramList) {
            String[] parts = item.split(":");
            String label = parts[0]; //extracts associated word
            String imageURL = parts[1]; //extract pictogram url

            ImageView imageView = new ImageView(new Image(imageURL));
            imageView.setFitWidth(100);
            imageView.setFitHeight(100);

            Label wordLabel = new Label(label); //displays text below image
            wordLabel.setStyle("-fx-font-size: 16; -fx-text-fill: black; -fx-font-family: monospaced");

            Button phraseButton = new Button(label);
            phraseButton.setOnAction(event -> {
                addWordToSentence(label);
                speakPhrase();
            });

            communicationTilePane.getChildren().addAll(imageView, phraseButton);
        }
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

    public static void main(String[] args) throws Exception {
        ArasaacService arasaacService = new ArasaacService();
        SpeechService service = new SpeechService();

        String testPictogram = arasaacService.fetchPictograms("Feelings");
        System.out.println("arasaac pictogram response: " + testPictogram);

        //extract speech synthesis
        if(!testPictogram.isEmpty()){
            String[] pictogramList = testPictogram.split("\n");

            if(pictogramList.length > 0){
                String firstPictogram = pictogramList[0];
                String[] parts = firstPictogram.split(":");
                String label = parts[0]; //extract associated word

                System.out.println("Speaking: " + label);
                service.synthesizeText(label);
            }else{
                System.out.println("No pictogram found");
            }
        }
    }

}

