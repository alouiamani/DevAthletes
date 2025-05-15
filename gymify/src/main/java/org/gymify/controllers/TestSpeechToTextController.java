package org.gymify.controllers;


import com.ibm.watson.speech_to_text.v1.SpeechToText;
import com.ibm.watson.speech_to_text.v1.model.RecognizeOptions;
import com.ibm.watson.speech_to_text.v1.model.SpeechRecognitionResults;
import com.ibm.cloud.sdk.core.security.IamAuthenticator;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class TestSpeechToTextController {

    @FXML
    private Button speechToTextButton;

    @FXML
    private TextArea transcriptionArea;

    private static final String API_KEY = "JR-LCWjnds3scsjrtCtn2wCNlyR2GWGAQq_hJ1D6Fhh-";
    private static final String SERVICE_URL = "https://api.au-syd.speech-to-text.watson.cloud.ibm.com/instances/760398a7-3622-4319-83f8-415080a4ee25"; // Remplace par ton URL IBM

    @FXML
    private void handleSpeechToText() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Audio Files", "*.mp3", "*.wav"));
        File audioFile = fileChooser.showOpenDialog(speechToTextButton.getScene().getWindow());

        if (audioFile != null) {
            try {
                String transcribedText = transcribeAudio(audioFile);
                transcriptionArea.setText(transcribedText); // Affiche le texte dans la zone de transcription
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'envoyer l'audio à IBM.");
                e.printStackTrace();
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Avertissement", "Aucun fichier audio sélectionné.");
        }
    }

    private String transcribeAudio(File audioFile) throws IOException {
        IamAuthenticator authenticator = new IamAuthenticator(API_KEY);
        SpeechToText speechToText = new SpeechToText(authenticator);
        speechToText.setServiceUrl(SERVICE_URL);

        RecognizeOptions options = new RecognizeOptions.Builder()
                .audio(new FileInputStream(audioFile))
                .contentType("audio/mp3") // Change selon ton format
                .model("fr-FR_BroadbandModel") // Modèle pour la langue française
                .build();

        SpeechRecognitionResults transcript = speechToText.recognize(options).execute().getResult();
        return transcript.getResults().get(0).getAlternatives().get(0).getTranscript();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }
}

