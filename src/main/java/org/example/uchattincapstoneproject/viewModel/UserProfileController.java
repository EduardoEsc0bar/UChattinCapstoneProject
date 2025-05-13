package org.example.uchattincapstoneproject.viewModel;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import org.example.uchattincapstoneproject.model.DB;
import org.example.uchattincapstoneproject.model.User;
import org.example.uchattincapstoneproject.model.Util;

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

    }

    @FXML
    void saveBtnClicked(ActionEvent event) {
        saveImageAnimation();

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

