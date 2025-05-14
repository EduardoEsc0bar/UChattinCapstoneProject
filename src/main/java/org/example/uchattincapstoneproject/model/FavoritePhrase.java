package org.example.uchattincapstoneproject.model;

public class FavoritePhrase {
    private String phrase;
    private String imageURL;

    public FavoritePhrase(String phrase, String imageURL) {
        this.phrase = phrase;
        this.imageURL = imageURL;
    }

    public String getPhrase() {
        return phrase;
    }

    public void setPhrase(String phrase) {
        this.phrase = phrase;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }


}
