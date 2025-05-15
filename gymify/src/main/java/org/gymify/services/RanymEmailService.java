package org.gymify.services;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class RanymEmailService {

    private static final String EMAILJS_SERVICE_ID = "service_6d51s0a";
    private static final String EMAILJS_TEMPLATE_ID = "template_sutsb1u"; // Assure-toi que c'est bien le bon template
    private static final String EMAILJS_USER_ID = "eh95ayxVsB8mPI6L8";

    public static void sendEmail(String recipientEmail, String subject, String message) {
        try {
            String apiUrl = "https://api.emailjs.com/api/v1.0/email/send";
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // Construire le JSON
            String jsonPayload = "{"
                    + "\"service_id\":\"" + EMAILJS_SERVICE_ID + "\","
                    + "\"template_id\":\"" + EMAILJS_TEMPLATE_ID + "\","
                    + "\"user_id\":\"" + EMAILJS_USER_ID + "\","
                    + "\"template_params\":{"
                    + "\"to_email\":\"" + recipientEmail + "\","
                    + "\"subject\":\"" + subject + "\","
                    + "\"message\":\"" + message + "\""
                    + "}}";

            // Envoyer les données
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonPayload.getBytes("UTF-8");
                os.write(input, 0, input.length);
            }

            // Lire la réponse de l'API EmailJS
            int responseCode = connection.getResponseCode();
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            br.close();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("✅ Email envoyé avec succès.");
                System.out.println("Réponse : " + response.toString());
            } else {
                System.err.println("❌ Erreur lors de l'envoi de l'email. Code HTTP : " + responseCode);
                System.err.println("Réponse : " + response.toString());
            }

        } catch (Exception e) {
            System.err.println("❌ Exception : " + e.getMessage());
            e.printStackTrace();
        }
    }
}