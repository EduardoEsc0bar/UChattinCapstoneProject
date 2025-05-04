package org.example.uchattincapstoneproject.model;

import com.fasterxml.jackson.annotation.JsonIgnoreType;

import java.util.HashMap;
import java.util.Map;

public class Avatar {
    private String style;
    private DiceBearAPI diceBearAPI;

    public Avatar(String style, DiceBearAPI diceBearAPI) {
        this.style = style;
        this.diceBearAPI = diceBearAPI;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }


    public String getAvatarURL(){
       try{
           return diceBearAPI.generateAvatarURL(style, new HashMap<>());
       }catch(Exception e){
           System.err.println("Error generating avatar URL: "+ e.getMessage());
           return null;
       }
    }

}
