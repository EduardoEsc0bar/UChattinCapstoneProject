package org.example.uchattincapstoneproject.model;

import com.microsoft.cognitiveservices.speech.*;
import com.microsoft.cognitiveservices.speech.audio.AudioConfig;

public class SpeechService {

    // Getting API Key from Azure Key Vault
    private static final String API_KEY = KeyVaultClient.getSecret("Speech-API-KEY"); // Replace with actual logic to retrieve API Key
    private static final String REGION = "eastus";

    private SpeechSynthesizer synthesizer;
    private SpeechConfig speechConfig;

    public SpeechService() throws Exception {
        // Initialize SpeechConfig
        SpeechConfig speechConfig = SpeechConfig.fromSubscription(API_KEY, REGION);
        speechConfig.setSpeechSynthesisVoiceName("en-US-JessaNeural"); // Optional: Change voice

        // Create SpeechSynthesizer
        synthesizer = new SpeechSynthesizer(speechConfig);
    }


    public void setVoice(String voiceName) {
        // Change the voice based on user selection
        speechConfig.setSpeechSynthesisVoiceName(voiceName);
        synthesizer = new SpeechSynthesizer(speechConfig);  // Re-initialize synthesizer with new voice
    }
    public void synthesizeText(String text) {
        if (text == null || text.isEmpty()) {
            System.out.println("Input text is empty.");
            return;
        }

        try {
            SpeechSynthesisResult result = synthesizer.SpeakText(text);
            if (result.getReason() == ResultReason.SynthesizingAudioCompleted) {
                System.out.println("Successfully synthesized speech.");
            } else {
                System.err.println("Error: ");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
