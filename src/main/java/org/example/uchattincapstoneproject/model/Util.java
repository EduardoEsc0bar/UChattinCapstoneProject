package org.example.uchattincapstoneproject.model;

public class Util {
    private static volatile Util instance;
    private User currentUser;
    private Util() {}
    public static Util getInstance() {
        if (instance == null) {
            synchronized (Util.class) {
                if (instance == null) {
                    instance = new Util();
                }
            }
        }
        return instance;
    }
    public static Util ensureInitialized() {
        return getInstance();
    }
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }
    public User getCurrentUser() {
        return currentUser;
    }
    public boolean isUserSignedIn() {
        return currentUser != null;
    }
    public void signOut() {
        this.currentUser = null;
    }
    public String formatEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase();
    }
}