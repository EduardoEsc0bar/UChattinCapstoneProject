package org.example.uchattincapstoneproject.model;

import java.util.HashMap;
import java.util.Map;

public class Avatar {
    private String style;
    private Map<String, String> options;

    public Avatar(String style){
        this.style = style;
        this.options = new HashMap<>();
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public Map<String, String> getOptions() {
        return options;
    }

    public void setOptions(String category, String value) {
        options.put(category, value);
    }

    public void removeOptions(String category) {
        options.remove(category);
    }

    public String getAvatarURL(DiceBearAPI diceBearAPI) throws Exception {
        return diceBearAPI.generateAvatarURL(style, options);
    }

    @Override
    public String toString() {
        return "avatar [style=" + style + ", options=" + options + "]";
    }
}
