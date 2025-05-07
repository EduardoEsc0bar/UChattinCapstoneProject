package org.example.uchattincapstoneproject.model;

import java.awt.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class ArasaacService {

    //private static final String LANGUAGE = "en"; //Change to preferred language

    public String fetchPictograms(String category) {
        try {
            String BASE_URL = "https://api.arasaac.org/api/pictograms/en/search/";
            String urlString = BASE_URL + category;
            URL url = new URL(urlString);
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

            System.out.println("Full API Response: " + responseBuilder.toString());
            return parsePictogramResponse(responseBuilder.toString());

        } catch (Exception e) {
            e.printStackTrace();
            return "Error fetching pictogram.";
        }
    }

    private String parsePictogramResponse(String jsonResponse) {
        try {
            JSONArray jsonArray = new JSONArray(jsonResponse);
            StringBuilder pictogramBuilder = new StringBuilder();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject pictogram = jsonArray.getJSONObject(i);

                if (!pictogram.has("_id") || !pictogram.has("keywords") || pictogram.getJSONArray("keywords").length() == 0) {
                    System.out.println("Skipping pictogram due to missing fields: " + pictogram.toString());
                    continue;
                }

                int pictogramId = pictogram.getInt("_id");  // Always an integer
                String imageUrl = "https://static.arasaac.org/pictograms/" + pictogramId + "/" + pictogramId + "_500.png";


                String phrase = pictogram.getJSONArray("keywords").getJSONObject(0).getString("keyword");

                System.out.println("extracted id: " + pictogramId);
                System.out.println("generated pictogram url: " + imageUrl);
                pictogramBuilder.append(phrase).append(": ").append(imageUrl).append("\n");
            }

            return pictogramBuilder.toString();

        } catch (Exception e) {
            System.err.println("Error processing pictogram data: " + e.getMessage());
            e.printStackTrace();
            return "Error processing pictogram data.";
        }
    }

}

