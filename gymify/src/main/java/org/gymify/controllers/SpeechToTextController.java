package org.gymify.controllers;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.util.Scanner;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class SpeechToTextController {

    private static final String API_KEY = "514fa1533eb6428bbca7272bf52414ff";  // Remplace par ta clé API AssemblyAI
    private String uploadUrl;

    @FXML
    private TextArea transcribedTextArea;

    // Méthode pour ouvrir un fichier audio et l'envoyer à AssemblyAI
    @FXML
    public void openFileDialog() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new ExtensionFilter("Audio Files", "*.mp3", "*.wav", "*.flac"));
        File selectedFile = fileChooser.showOpenDialog(new Stage());

        if (selectedFile != null) {
            try {
                uploadUrl = uploadAudio(selectedFile);
                if (uploadUrl != null) {
                    startTranscription(uploadUrl);
                }
            } catch (IOException e) {
                showAlert(AlertType.ERROR, "Erreur", "Impossible de télécharger ou transcrire le fichier.");
            }
        }
    }

    private String uploadAudio(File audioFile) throws IOException {
        URL url = new URL("https://api.assemblyai.com/v2/upload");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("authorization", API_KEY);
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) {
            Files.copy(audioFile.toPath(), os);
            os.flush();
        }

        try (InputStream is = connection.getInputStream()) {
            Scanner scanner = new Scanner(is);
            String response = scanner.useDelimiter("\\A").next();
            System.out.println("Upload response: " + response); // Afficher la réponse de l'upload pour déboguer
            if (response.contains("upload_url")) {
                String uploadUrl = response.split(":")[1].split("\"")[1];
                System.out.println("Upload URL: " + uploadUrl); // Log de l'URL pour vérification
                return uploadUrl;
            }
            return null;
        }
    }

    private void startTranscription(String uploadUrl) throws IOException {
        URL url = new URL("https://api.assemblyai.com/v2/transcript");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("authorization", API_KEY);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        String jsonRequest = "{\"audio_url\": \"" + uploadUrl + "\"}";

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonRequest.getBytes("utf-8");
            os.write(input, 0, input.length);
            os.flush();
        }

        try (InputStream is = connection.getInputStream()) {
            Scanner scanner = new Scanner(is);
            String response = scanner.useDelimiter("\\A").next();
            System.out.println("Transcription start response: " + response); // Afficher la réponse du démarrage de la transcription pour déboguer
            String transcriptId = response.split(":")[1].split("\"")[1];
            checkTranscriptionStatus(transcriptId);
        }
    }

    private void checkTranscriptionStatus(String transcriptId) throws IOException {
        URL url = new URL("https://api.assemblyai.com/v2/transcript/" + transcriptId);

        // Vérifier l'état de la transcription à intervalles réguliers
        while (true) {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("authorization", API_KEY);

            try (InputStream is = connection.getInputStream()) {
                Scanner scanner = new Scanner(is);
                String response = scanner.useDelimiter("\\A").next();
                System.out.println("Transcription status response: " + response); // Afficher la réponse du statut de la transcription pour déboguer

                if (response.contains("\"status\": \"completed\"")) {
                    String transcribedText = response.split("\"text\": \"")[1].split("\"")[0];
                    transcribedTextArea.setText(transcribedText);  // Afficher le texte transcrit
                    break;  // Sortir de la boucle une fois terminé
                } else if (response.contains("\"status\": \"error\"")) {
                    showAlert(AlertType.ERROR, "Erreur", "Une erreur est survenue pendant la transcription.");
                    break;
                } else {
                    showAlert(AlertType.WARNING, "Transcription en cours", "La transcription est en cours.");
                    Thread.sleep(5000);  // Attendre 5 secondes avant de refaire la vérification
                }
            } catch (IOException | InterruptedException e) {
                showAlert(AlertType.ERROR, "Erreur", "Impossible de vérifier l'état de la transcription.");
                break;
            }
        }
    }

    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
