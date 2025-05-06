package org.example.uchattincapstoneproject.model;

import java.net.HttpURLConnection;
import java.net.URL;
import java.io.*;
import org.json.JSONArray;
import org.json.JSONObject;


public class ArasaacService {
    private static final String BASE_URL = "https://api.arasaac.org/api/pictograms/";
    private static final String LANGUAGE = "en"; //Change to preferred language

    public String fetchPictograms(String keyword) {
        try {
            URL url = new URL(BASE_URL + "/pictograms/" + LANGUAGE + "/search/" + keyword);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder responseBuilder = new StringBuilder();
            String response;

            while ((response = in.readLine()) != null) {
                responseBuilder.append(response);
            }
            in.close();

            return parsePictogramResponse(responseBuilder.toString());

        } catch (Exception e) {
            e.printStackTrace();
            return "Error fetching pictogram.";
        }
    }

    private String parsePictogramResponse(String response) {
        StringBuilder formattedResponse = new StringBuilder();
        JSONArray jsonArray = new JSONArray(response);

        for(int i = 0; i < jsonArray.length(); i++) {
            JSONObject pictogram = jsonArray.getJSONObject(i);
            String imageURL = pictogram.getString("image");
            String label = pictogram.getString("label");

            formattedResponse.append(label).append(": ").append(imageURL).append("\n");
        }
        return formattedResponse.toString();
    }
}

