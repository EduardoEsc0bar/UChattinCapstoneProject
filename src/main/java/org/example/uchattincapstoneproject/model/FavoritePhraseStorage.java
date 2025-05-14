package org.example.uchattincapstoneproject.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FavoritePhraseStorage {
    private static FavoritePhraseStorage instance;

    //key:userID, value:list of favs
    private Map<Integer, List<FavoritePhrase>> storage;

    private FavoritePhraseStorage() {
        storage = new HashMap<>();
    }

    public static FavoritePhraseStorage getInstance() {
        if (instance == null) {
            instance = new FavoritePhraseStorage();
        }
        return instance;
    }

    public void addFavoritePhrase(int userID, FavoritePhrase favoritePhrase) {
        storage.computeIfAbsent(userID, k -> new ArrayList<>()).add(favoritePhrase);
    }

    public List<FavoritePhrase> getFavoritePhrases(int userID){
        return storage.getOrDefault(userID, new ArrayList<>());
    }
}
