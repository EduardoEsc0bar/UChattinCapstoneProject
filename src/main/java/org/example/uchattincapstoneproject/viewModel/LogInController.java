package org.example.uchattincapstoneproject.viewModel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.example.uchattincapstoneproject.model.Avatar;
import org.example.uchattincapstoneproject.model.DiceBearAPI;
import org.mindrot.jbcrypt.BCrypt;

public class LogInController {
    //----------------- Panes --------------------------------------------------------------------------------------------------
    @FXML
    StackPane LoginScreenStackPane, customizationStackPane;
    @FXML
    Pane LoginScreenPane, createAvatarPane, createAccountPane, forgotPasswordPane;
    @FXML
    TilePane avatarStyleTP, hairTP, clothingTP, facialFeaturesTP, skinTP, accessoriesTP, backgroundTP;
    @FXML
    AnchorPane displayAvatarPane, customizationPane;
    //------------Login Screen FXMLs -------------------------------------------------------------------------------------
    @FXML
    TextField usernameTextField, passwordTextField;
    @FXML
    Button logInButton, forgotPasswordButton, exitButton, createAccountButton;

    //----------------------- Create Account FXMLs --------------------------------------------------
    @FXML
    TextField firstNameTF, lastNameTF, dobTF, preferredNameTF, phoneNumberTF,createUsernameTF, createPasswordTF, specifyGenderTF, specifyPronounsTF, emailTF;
    @FXML
    ComboBox<String> genderCB, pronounsCB;
    @FXML
    Button toCreateAvatarButton;

    //--------------------------- create avatar FXMLs ---------------------------------------------
    @FXML
    ImageView avatarImageView;
    @FXML
    Button avatarStyleButton, hairButton, skinButton, clothingButton, accessoriesButton, facialFeaturesButton, backgroundButton, saveAvatarButton, resetAvatarButton;


    //--------------------------- Forgot Password FXMLs ----------------------------------------------
    @FXML
    TextField enterNewPasswordTF, confirmPasswordTF;
    @FXML
    Button confirmPasswordButton;

    //------------------------------------------------- initialize ------------------------------------------------
    /*private void initialize() {
        specifyGenderTF.setVisible(false);
        specifyPronounsTF.setVisible(false);
        populateComboBoxes();

    }*/
    //------------- Log In Screen Controller ------------------------------------

    private void handleLogin(){
        String username = usernameTextField.getText();
        String password = passwordTextField.getText();

        if(validateCredentials(username, password)){
            System.out.println("Login Successful");
            //transition to next pane
        }else{
            System.out.println("Login Failed. Please try again");
        }

    }

    public void goToAvatarPane(){
        createAvatarPane.setVisible(false);
        createAvatarPane.setVisible(true);
    }

    //------------------------------- registration screen controller --------------------------------------------------------

    private void populateComboBoxes(){

        ObservableList<String> genderOptions = FXCollections.observableArrayList("Male", "Female", "Non-Binary", "Other");
        genderCB.setItems(genderOptions);

        ObservableList<String> pronounOptions = FXCollections.observableArrayList("She/Her/Hers", "He/Him/His", "They/Them/Their", "Other");
        pronounsCB.setItems(pronounOptions);

        genderCB.setOnAction(event -> {
            String gender = genderCB.getValue();
            specifyGenderTF.setVisible("Other".equals(gender));
        });

        pronounsCB.setOnAction(event -> {
            String pronoun = pronounsCB.getValue();
            specifyPronounsTF.setVisible("Other".equals(pronoun));
        });
    }

    public void handleRegistration(ActionEvent event){
        String username = createUsernameTF.getText().trim();
        String password = createPasswordTF.getText().trim();
        String firstName = firstNameTF.getText().trim();
        String lastName = lastNameTF.getText().trim();
        String dob = dobTF.getText().trim();
        String preferredName = preferredNameTF.getText().trim();
        String phoneNumber = phoneNumberTF.getText().trim();
        String gender = genderCB.getValue();
        String email = emailTF.getText().trim();
        String pronouns = pronounsCB.getValue();

        if("Other".equals(gender)){
            gender = specifyGenderTF.getText().trim();
        }
        if("Other".equals(pronouns)){
            pronouns = specifyPronounsTF.getText().trim();
        }

        //validate required fields
        if (username.isEmpty() || password.isEmpty() || email.isEmpty() || firstName.isEmpty() || lastName.isEmpty()){
            System.out.println("All required fields must be filled in");
            return;
        }

        //call register method
        //User user = new User();
        //boolean isRegistered = user.register(username, password, firstName, lastName, dob, phoneNumber, gender, pronouns, email);

        //if(isRegistered){
           //System.out.println();
        //}
    }

    //------------------------------- Create avatar Controller ----------------------------------------------------------
    private DiceBearAPI diceBearAPI = new DiceBearAPI();
    private Map<String, String> selectedOptions = new HashMap<>();
    private Avatar avatar = new Avatar("micah");

    //tester
    public void initialize(){
        avatarStyleButton.setOnAction(event -> showCategory("avatarStyle", avatarStyleTP));
        hairButton.setOnAction(event -> showCategory("hair", hairTP));
        skinButton.setOnAction(event -> showCategory("skin", skinTP));
        clothingButton.setOnAction(event -> showCategory("clothing", clothingTP));
        accessoriesButton.setOnAction(event -> showCategory("accessories", accessoriesTP));
        facialFeaturesButton.setOnAction(event -> showCategory("facialFeatures", facialFeaturesTP));
        backgroundButton.setOnAction(event -> showCategory("background", backgroundTP));
    }

    public void showCategory(String category,TilePane tilePane){
        customizationStackPane.getChildren().setAll(tilePane);

        try{
            populateTilePane(category, tilePane);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void populateTilePane(String category, TilePane tilePane) throws Exception{
        //String style = selectedOptions.getOrDefault("style", "micah");
        String schema = diceBearAPI.fetchOptions(avatar.getStyle());
        Map<String, List<String>> allOptions = diceBearAPI.parseOptions(schema);
        List<String> options = allOptions.get(category);

        tilePane.getChildren().clear();

        if(options == null || options.isEmpty()){
            System.out.println("No options found");
            return;
        }

        for(String option : options){
            String previewURL = diceBearAPI.generatePreviewURL(avatar.getStyle(), category, option);

            ImageView imageView = new ImageView(new Image(previewURL));
            imageView.setFitHeight(100);
            imageView.setFitWidth(100);

            Button button = new Button(option);
            button.setGraphic(imageView);
            button.setOnAction(event -> {
                try {
                    handleOptionSelection(category, option);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });

            tilePane.getChildren().add(button);

        }
    }

    private void handleOptionSelection(String category, String option) throws Exception {
        avatar.setOptions(category, option);
        updateAvatarPreview();
    }

    private void updateAvatarPreview() throws Exception {
        String avatarURL = avatar.getAvatarURL(diceBearAPI);
        avatarImageView.setImage(new Image(avatarURL));
    }

    public void saveAvatar() throws Exception {
        String avatarURL = avatar.getAvatarURL(diceBearAPI);
        avatarImageView.setImage(new Image(avatarURL));
        System.out.println("Avatar saved with URL: " + avatarURL);
    }

    public void resetAvatar() throws Exception {
        avatar = new Avatar(avatar.getStyle());
        updateAvatarPreview();
    }


    //--------------------------- Forgot Password Screen Controller-------------------------------------------------------------







    private boolean validateCredentials(String username, String password) {
        String dbUrl = "jdbc:mysql://uchattin-csc311.mysql.database.azure.com:3306/uchattin-userinfo";
        String dbUsername = "nadeige628_";
        String dbPassword = "Farm123$";

        String query = "select password from user where username = ?";

        try (Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)){
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()){
                String storedHashedPassword = resultSet.getString("password");
                return org.mindrot.jbcrypt.BCrypt.checkpw(password, storedHashedPassword);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public void exitButtonClicked(ActionEvent event) {
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }
}
