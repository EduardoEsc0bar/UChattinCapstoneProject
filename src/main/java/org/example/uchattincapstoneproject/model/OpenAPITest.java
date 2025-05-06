package org.example.uchattincapstoneproject.model;

import java.io.*;
import java.net.*;

public class OpenAPITest {
    public static void main(String[] args) throws Exception {
        String apiKey = "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"; // replace with your key

        URL url = new URL("https://api.openai.com/v1/models");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Authorization", "Bearer " + apiKey);

        int status = con.getResponseCode();
        System.out.println("HTTP Status: " + status);

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer content = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }

        in.close();
        con.disconnect();

        System.out.println(content.toString());
    }
}

