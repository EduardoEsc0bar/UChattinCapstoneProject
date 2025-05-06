package org.example.uchattincapstoneproject.model;

import com.microsoft.cognitiveservices.speech.*;
import com.microsoft.cognitiveservices.speech.audio.AudioConfig;
    /**
     * SpeechService is responsible for handling speech synthesis using Azure Cognitive Services.
     * It retrieves the API key securely from Azure Key Vault, initializes the speech synthesizer,
     * and provides methods to change voice and synthesize speech from text.
     */
public class SpeechService {

    // Getting API Key from Azure Key Vault
    private static final String API_KEY = KeyVaultClient.getSecret("Speech-API-KEY"); // Replace with actual logic to retrieve API Key
    private static final String REGION = "eastus";

    private SpeechSynthesizer synthesizer;
    private SpeechConfig speechConfig;
    /**
     * Constructs a new SpeechService and initializes the Azure Speech synthesizer.
     *
     * @throws Exception if the SpeechConfig or SpeechSynthesizer initialization fails
     */
    public SpeechService() throws Exception {
        // Initialize SpeechConfig
        SpeechConfig speechConfig = SpeechConfig.fromSubscription(API_KEY, REGION);
        speechConfig.setSpeechSynthesisVoiceName("en-US-JessaNeural"); // Optional: Change voice

        // Create SpeechSynthesizer
        synthesizer = new SpeechSynthesizer(speechConfig);
    }
    /**
    * Sets the voice to be used for speech synthesis.
    *
    * @param voiceName the name of the Azure voice (e.g., "en-US-JessaNeural")
    */
    public void setVoice(String voiceName) {
        // Change the voice based on user selection
        speechConfig.setSpeechSynthesisVoiceName(voiceName);
        synthesizer = new SpeechSynthesizer(speechConfig);  // Re-initialize synthesizer with new voice
    }
    /**
    * Synthesizes speech from the given text.
    *
    * @param text the input text to convert to speech
    */
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
