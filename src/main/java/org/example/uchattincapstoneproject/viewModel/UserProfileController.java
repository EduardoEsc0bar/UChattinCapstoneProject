package org.example.uchattincapstoneproject.viewModel;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.uchattincapstoneproject.model.DB;
import org.example.uchattincapstoneproject.model.User;
import org.example.uchattincapstoneproject.model.Util;

import java.io.IOException;

public class UserProfileController {

    @FXML
    private TextArea bioTextArea;

    @FXML
    private Label fNameLbl, genderLbl, lNameLbl, pNameLbl, saveBioLbl, userNameLbl, bioLbl, pronounsLbl;

    @FXML
    private ImageView profileAvatarIV, saveImgView;

    @FXML
    private HBox pNameHbox;

    private Image originalImg, loadingDotsImg, checkmarkImg;

    DB db = DB.getInstance();
    Util utilities = Util.getInstance();
    User currentUser;

    @FXML
    public void initialize() {
        currentUser = utilities.getCurrentUser();
        if(currentUser!= null) {
            if (currentUser.getAvatarURL() != null) {
                String avatarUrl = currentUser.getAvatarURL();
                if (!avatarUrl.isEmpty()) {
                    profileAvatarIV.setImage(new Image(avatarUrl, true)); // background loading
                }
            }
            fNameLbl.setText(currentUser.getFirstName());
            lNameLbl.setText(currentUser.getLastName());
            genderLbl.setText(currentUser.getGender());
            pNameLbl.setText(currentUser.getPreferredName());
            pronounsLbl.setText(currentUser.getPronouns());
            userNameLbl.setText(currentUser.getUsername());
            System.out.print(currentUser.getBio() + "BIOOOOOOOOO");
            bioTextArea.setText(currentUser.getBio());
            if (currentUser.getPreferredName() == null || currentUser.getPreferredName().trim().isEmpty()) {
                pNameHbox.setVisible(false);
                pNameHbox.setManaged(false);
            }
        }
        originalImg = new Image(getClass().getResource("/imagesIcon/floppy-disk-star-arrow-right.195x256.png").toExternalForm());
        loadingDotsImg = new Image(getClass().getResource("/imagesIcon/floppy-disk-three-dots.214x256.png").toExternalForm());
        checkmarkImg = new Image(getClass().getResource("/imagesIcon/floppy-disk-checkmark.199x256.png").toExternalForm());

    }
    @FXML
    void homeBtnClicked(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/mainScreen.fxml")); // Update the path
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    void saveBtnClicked(ActionEvent event) {
        if (bioTextArea == null) {
            System.err.println("bioTextArea is null — FXML may not be wired correctly.");
            return;
        }
        String bioText = bioTextArea.getText();
        if (bioText != null && !bioText.isBlank()) {
            saveImageAnimation();
            currentUser.setBio(bioText.trim());
            db.insertBio(currentUser);
        } else {
            bioTextArea.setText("There is nothing to save here due to the text area being empty");
        }
    }
    private void saveImageAnimation(){
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(0), e -> {
                    saveImgView.setImage(loadingDotsImg);
                    saveBioLbl.setText("saving");
                }),

                new KeyFrame(Duration.seconds(0.5), e -> {
                    saveImgView.setImage(checkmarkImg);
                    saveBioLbl.setText("saved");
                }),

                new KeyFrame(Duration.seconds(1.5), e -> {
                    saveImgView.setImage(originalImg);
                    saveBioLbl.setText("save");
                })
        );
        timeline.setCycleCount(1);
        timeline.play();
    }
}