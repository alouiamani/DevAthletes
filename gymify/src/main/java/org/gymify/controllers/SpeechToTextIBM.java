package org.gymify.controllers;

import com.ibm.watson.speech_to_text.v1.SpeechToText;
import com.ibm.watson.speech_to_text.v1.model.RecognizeOptions;
import com.ibm.watson.speech_to_text.v1.model.SpeechRecognitionResults;
import com.ibm.cloud.sdk.core.security.IamAuthenticator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class SpeechToTextIBM {

    private static final String API_KEY = "TA_CLE_API"; // Remplace par ta clé API
    private static final String SERVICE_URL = "TON_URL_SERVICE"; // Remplace par ton URL

    public static String transcribeAudio(File audioFile) throws IOException {
        IamAuthenticator authenticator = new IamAuthenticator(API_KEY);
        SpeechToText speechToText = new SpeechToText(authenticator);
        speechToText.setServiceUrl(SERVICE_URL);

        RecognizeOptions options = new RecognizeOptions.Builder()
                .audio(new FileInputStream(audioFile))
                .contentType("audio/mp3") // Adaptable selon le format audio
                .model("fr-FR_BroadbandModel") // Modèle pour le français
                .build();

        SpeechRecognitionResults transcript = speechToText.recognize(options).execute().getResult();
        return transcript.getResults().get(0).getAlternatives().get(0).getTranscript();
    }
}
