package org.example.uchattincapstoneproject.model;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.*;

public class DiceBearAPI {
    //get available styles
    public List<String> getAvailableStyles(){
        return List.of("adventurer", "bottts", "adventurer-neutral", "avataaars", "avataaars-neutral", "big-ears",
                "big-ears-neutral", "big-smile", "bottts-neutral", "croodles", "croodles-neutral", "dylan", "fun-emoji", "glass",
                "icons", "identicon", "initials", "lorelei", "lorelei-neutral", "micah", "miniavs", "notionists",
                "notionists-neutral", "open-peeps", "personas", "pixel-art", "pixel-art-neutral", "rings",
                "shapes", "thumbs");
    }

    //generate style url
    public String generateAvatarURL(String style, Map<String, String> options) throws Exception{
        StringBuilder url = new StringBuilder("https://api.dicebear.com/9.x/" + style + "/svg?");
        for(Map.Entry<String, String> entry : options.entrySet()){
            url.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }
        return url.toString();
    }

    // fetch schema for options
    public String fetchOptions(String style) throws Exception{
        String url = "https://api.dicebear.com/9.x/" + style + "/schema";

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    //parse schema for options
    public Map<String, List<String>> parseOptions(String jsonSchema) throws Exception{
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(jsonSchema);
        JsonNode properties = root.path("properties");

        Map<String, List<String>> categoryOptions = new HashMap<>();
        Iterator<Map.Entry<String, JsonNode>> fields = properties.fields();

        while(fields.hasNext()){
            Map.Entry<String, JsonNode> field = fields.next();
            String category = field.getKey();

            List<String> options = new ArrayList<>();
            JsonNode enumVals = field.getValue().path("enum");
            if(enumVals.isArray()){
                for(JsonNode val : enumVals){
                    options.add(val.asText());
                }
            }
            categoryOptions.put(category, options); //add category and its options
        }
        return categoryOptions; //return all categories and options

    }

    //make sure options are valid before building avatar
    public boolean validateOptions(String category, String option, Map<String, List<String>> categoryOptions) throws Exception{
        List<String> validOptions = categoryOptions.get(category);
        return validOptions != null && validOptions.contains(option);
    }

    //generate preview of avatar
    public String generatePreviewURL(String style, String category, String options){
        return "https://api.dicebear.com/9.x/" + style + "/svg?options[" + category + "]=" + options;
    }

    //combine all selections
    public Map<String, String> combineOptions(Map<String, String> selectedOptions){
        Map<String, String> combinedOptions = new HashMap<>();
        for(Map.Entry<String, String> enrty : selectedOptions.entrySet()){
            combinedOptions.put(enrty.getKey(), enrty.getValue());
        }
        return combinedOptions;
    }


}
