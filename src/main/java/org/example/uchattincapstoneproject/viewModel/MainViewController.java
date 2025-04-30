package org.example.uchattincapstoneproject.viewModel;

import javafx.scene.control.Label;

public class MainViewController {
    private int userID;
    public void setUsername(String username) {
        Label welcomeLabel = new Label("Welcome " + username + "!");
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }
}
