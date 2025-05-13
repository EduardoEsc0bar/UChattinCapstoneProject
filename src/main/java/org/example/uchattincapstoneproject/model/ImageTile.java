package org.example.uchattincapstoneproject.model;

import javafx.scene.image.Image;

public class ImageTile {
    private transient Image image;
    private String word;

    public ImageTile(Image image, String word) {
        this.image = image;
        this.word = word;
    }

    public Image getImage() {
        return image;
    }

    public String getWord() {
        return word;
    }
}
