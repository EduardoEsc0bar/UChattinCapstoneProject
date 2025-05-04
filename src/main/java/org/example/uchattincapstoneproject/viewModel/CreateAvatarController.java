package org.example.uchattincapstoneproject.viewModel;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import org.example.uchattincapstoneproject.model.Avatar;
import org.example.uchattincapstoneproject.model.DiceBearAPI;
import java.net.HttpURLConnection;
import javax.imageio.ImageIO;
import java.io.InputStream;
import java.net.URL;

import java.util.List;

public class CreateAvatarController {
    @FXML private ImageView avatarImageView;
    @FXML private TilePane avatarStyleTP;
    @FXML private Button avatarStyleButton, saveAvatarButton, resetAvatarButton;

    private DiceBearAPI diceBearAPI = new DiceBearAPI();
    private Avatar avatar;

    //initialize controller
    @FXML
    private void initialize() {
        System.out.println("create avatar controller initialized");

        //set button actions for categories
        avatarStyleButton.setOnAction(event -> populateAvatarTilePane()); //load avatar images on tile pane

        //set button actions for avatar modification
        saveAvatarButton.setOnAction(event -> saveAvatar());
        resetAvatarButton.setOnAction(event -> resetAvatar());

        //load avatar preview (default)
        resetAvatar();
    }

    //populates tile tilePane with customization options
    private void populateAvatarTilePane(){
        avatarStyleTP.getChildren().clear();
        List<String> availableStyles = diceBearAPI.getAvailableStyles();
        System.out.println("loading available styles: " + availableStyles);

        for(String style : availableStyles){
            //String previewURL = "https://api.dicebear.com/9.x/" + style + "/png?seed=randomname&size=128";
            System.out.println("fetching avatar preview for style: " + style);
                Image avatarImage = diceBearAPI.fetchAvatar(style);
                if(avatarImage == null || avatarImage.isError()){
                    System.err.println("Error fetching avatar preview for style: " + style + " |Error: " + avatarImage.isError());
                    continue;
                }

                ImageView imageView = new ImageView(avatarImage);
                imageView.setFitHeight(150);
                imageView.setFitWidth(150);
                imageView.setPreserveRatio(true);
                imageView.setCache(true);
                imageView.setSmooth(true);

                Button button = new Button();
                button.setGraphic(imageView);
                button.setOnAction(event -> updateAvatar(style));

                avatarStyleTP.getChildren().add(button);
        }
    }


    //updates avatar preview
    private void updateAvatar(String style) {
        avatar = new Avatar(style, diceBearAPI);
        Image avatarImage = diceBearAPI.fetchAvatar(style);

       if(avatarImage == null || avatarImage.isError()){
           System.err.println("Error fetching avatar preview for style: " + style);
           return;
       }

       avatarImageView.setImage(avatarImage);
       avatarImageView.setVisible(true);



    }

    //saves avatar settings
    private void saveAvatar() {
        try{
            String avatarURL = avatar.getAvatarURL();
            System.out.println("saved avatar URL: " + avatarURL);
            showAlert(Alert.AlertType.INFORMATION, "Avatar Saved", "Your avatar has been saved");
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    //resets avatar to default
    private void resetAvatar() {
            updateAvatar("pixel-art");
            System.out.println("reset avatar preview");
    }

    //display alert
    private void showAlert(Alert.AlertType alertType, String title, String message){
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
