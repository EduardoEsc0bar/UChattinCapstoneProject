package org.example.uchattincapstoneproject.model;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import javafx.scene.image.Image;

import java.util.*;
/**
 * The {@code DiceBearAPI} class provides functionality for interacting with the DiceBear Avatars API.
 * It allows users to retrieve avatar styles, generate image URLs with customizable options,
 * fetch avatar images, parse JSON schema for option categories, and build preview URLs.
 *
 * <p>Example usage includes dynamically generating avatars in JavaFX applications or retrieving
 * customization metadata from the API to build user interfaces.
 */
public class DiceBearAPI {
    private final HttpClient httpClient = HttpClient.newHttpClient();
    /**
     * Returns a list of available avatar styles supported by the DiceBear API.
     *
     * @return a list of avatar style strings
     */
    //get available styles
    public List<String> getAvailableStyles(){
        return List.of("adventurer", "bottts", "adventurer-neutral", "avataaars", "avataaars-neutral", "big-ears",
                "big-ears-neutral", "big-smile", "bottts-neutral", "croodles", "croodles-neutral", "dylan", "fun-emoji", "glass",
                "icons", "identicon", "initials", "lorelei", "lorelei-neutral", "micah", "miniavs", "notionists",
                "notionists-neutral", "open-peeps", "personas", "pixel-art", "pixel-art-neutral", "rings",
                "shapes", "thumbs");
    }
    /**
     * Generates a URL to request a PNG avatar image using a specified style and customization options.
     *
     * @param style the avatar style (e.g., "adventurer", "micah")
     * @param options a map of customization options (e.g., hair, eyes)
     * @return the complete URL string for the avatar image
     */
    //generate style url
    public String generateAvatarURL(String style, Map<String, String> options) {
        try{
            StringBuilder url = new StringBuilder("https://api.dicebear.com/9.x/" + style + "/png?seed=randomname&size=128");
            for(Map.Entry<String, String> entry : options.entrySet()) {
                url.append("&").append(URLEncoder.encode("options[" + entry.getKey() + "]", "UTF-8"))
                        .append("=").append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            }
            System.out.println("generated avatar URL: " + url);
            return url.toString();
        }catch(Exception e){
            System.err.println("error generating avatar URL: " + e.getMessage());
            return null;
        }
    }

    /**
     * Fetches the avatar image from the DiceBear API using a specified style.
     *
     * @param style the avatar style to fetch (e.g., "bottts", "avataaars")
     * @return a JavaFX {@code Image} object of the avatar, or {@code null} if an error occurs
     */
    // fetch schema for options
    public Image fetchAvatar(String style){
        try{
            String avatarurl = "https://api.dicebear.com/9.x/" + style + "/png?seed=randomname&size=128"; //check if its schema
            System.out.println("fetching options from avatarurl: " + avatarurl);

            URL url = new URL(avatarurl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            connection.setRequestProperty("Accept", "image/png");
            connection.setDoInput(true);
            connection.connect();

            InputStream stream = connection.getInputStream();
            return new Image(stream);
        }catch(Exception e){
            System.err.println("API request failed: " + e);
            return null;
        }
    }
    /**
     * Parses a JSON schema string to extract customization option categories and their possible values.
     *
     * @param jsonSchema the JSON schema string returned from the DiceBear API
     * @return a map of categories to lists of valid option values
     */
    //parse schema for options
    public Map<String, List<String>> parseOptions(String jsonSchema){
        Map<String, List<String>> categoryOptions = new HashMap<>();
        try{
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(jsonSchema);
            JsonNode properties = root.path("extra");

            if(properties.isMissingNode()){
                System.out.println("Error, no custom options found");
                return categoryOptions;
            }
            Iterator<Map.Entry<String, JsonNode>> fields = properties.fields();

            while(fields.hasNext()){
                Map.Entry<String, JsonNode> field = fields.next();
                String category = field.getKey();

                List<String> options = new ArrayList<>();
                JsonNode enumVals = field.getValue().path("options");
                if(enumVals.isArray()){
                    for(JsonNode val : enumVals){
                        options.add(val.asText());
                    }
                }
                categoryOptions.put(category, options); //add category and its options
            }
            System.out.println("parsed categoryOptions: " + categoryOptions);
        }catch(Exception e){
            System.err.println("API request failed: " + e.getMessage());
        }
        return categoryOptions; //return all categories and options

    }

    /**
     * Generates a preview URL to visualize a specific category option of an avatar style in SVG format.
     *
     * @param style the avatar style
     * @param category the customization category (e.g., "eyes", "mouth")
     * @param options the specific option to preview within the category
     * @return the preview URL string
     */
    //generate preview of avatar
    public String generatePreviewURL(String style, String category, String options){
        String previewURL = "https://api.dicebear.com/9.x/" + style + "/svg?options[" + category + "]=" + options;
        System.out.println("generated preview URL: " + previewURL);
        return previewURL;
    }

   /*
    public boolean isCustomizable(String style){
        try{
            String schema = fetchAvatar(style);
            Map<String, List<String>> validOptions = parseOptions(schema);
            return !validOptions.isEmpty();
        }catch(Exception e){
            System.err.println("API request failed: " + e.getMessage());
            return false;
        }

    }

    */


}
