package org.example.uchattincapstoneproject.model;

import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.security.keyvault.secrets.SecretClient;
import com.azure.security.keyvault.secrets.SecretClientBuilder;
import com.microsoft.cognitiveservices.speech.*;
import com.microsoft.cognitiveservices.speech.audio.AudioConfig;

public class SpeechService {

    // Getting API Key from Azure Key Vault
    private static final String API_KEY = "8rvZvwIhkqJJeFuPeQR5XNdGJDCnw5y7HOkZE1U0y2dvvsL54VojJQQJ99BDACYeBjFXJ3w3AAAYACOGaMA1"; // Replace with actual logic to retrieve API Key

    private static final String REGION = "eastus";
    private SpeechSynthesizer synthesizer;
    private SpeechConfig speechConfig;

    public SpeechService() throws Exception {

        //initialize speechConfig with retrieved API Key
        this.speechConfig = SpeechConfig.fromSubscription(API_KEY, REGION);
        this.speechConfig.setSpeechSynthesisVoiceName("en-US-JessaNeural");

        //create speechSynthesizer instance
        this.synthesizer = new SpeechSynthesizer(this.speechConfig);
    }


    public void setVoice(String voiceName) {
        // Change the voice based on user selection
        this.speechConfig.setSpeechSynthesisVoiceName(voiceName);
        this.synthesizer = new SpeechSynthesizer(speechConfig);  // Re-initialize synthesizer with new voice
    }
    public void synthesizeText(String text) {
        if (text == null || text.isEmpty()) {
            System.out.println("Input text is empty.");
            return;
        }

        try {
            SpeechSynthesisResult result = this.synthesizer.SpeakText(text);
            if (result.getReason() == ResultReason.SynthesizingAudioCompleted) {
                System.out.println("Successfully synthesized speech.");
            } else {
                System.err.println("speech synthesization failed.");
            }
        } catch (Exception e) {
            System.err.println("error in speech synthesizer: " + e.getMessage());
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        try{
            SpeechService speechService = new SpeechService();
            System.out.println("api key retrieved");

            String testText = "Hello this is a test.";
            System.out.println("speaking: " + testText);
            speechService.synthesizeText(testText);
        }catch(Exception e){
            System.err.println("error in speech synthesizer: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
