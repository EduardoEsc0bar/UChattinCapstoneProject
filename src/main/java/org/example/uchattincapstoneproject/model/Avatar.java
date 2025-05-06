package org.example.uchattincapstoneproject.model;

import com.fasterxml.jackson.annotation.JsonIgnoreType;

import java.util.HashMap;
import java.util.Map;
    /**
     * The {@code Avatar} class represents a user avatar with a specific style,
     * generated using the DiceBear API.
     */
public class Avatar {
    private String style;
    private DiceBearAPI diceBearAPI;

    /**
     * Constructs a new {@code Avatar} with the given style and API reference.
     *
     * @param style the style to be used for generating the avatar
     * @param diceBearAPI an instance of {@code DiceBearAPI} used to generate avatar URLs
     */
    public Avatar(String style, DiceBearAPI diceBearAPI) {
        this.style = style;
        this.diceBearAPI = diceBearAPI;
    }
    /**
     * Returns the current style of the avatar.
     *
     * @return the avatar style
     */
    public String getStyle() {
        return style;
    }
    /**
     * Sets the style of the avatar.
     *
     * @param style the new avatar style
     */
    public void setStyle(String style) {
        this.style = style;
    }

    /**
     * Generates and returns the avatar URL based on the current style.
     * If the URL generation fails, logs the error and returns {@code null}.
     *
     * @return the generated avatar URL, or {@code null} if an error occurs
     */
    public String getAvatarURL(){
       try{
           return diceBearAPI.generateAvatarURL(style, new HashMap<>());
       }catch(Exception e){
           System.err.println("Error generating avatar URL: "+ e.getMessage());
           return null;
       }
    }

}
