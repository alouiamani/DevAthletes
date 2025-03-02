package org.gymify.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.web.WebView;
import org.gymify.entities.AbonnementData;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class PaiementController {

    @FXML private WebView webView;
    @FXML private Label abonnementLabel;
    @FXML private Label sportifLabel;
    @FXML private Label salleLabel;
    @FXML private Label montantLabel;

    private AbonnementData abonnementData;

    public void setAbonnementData(AbonnementData abonnementData) {
        this.abonnementData = abonnementData;

        // Affiche les données dans l'interface
        abonnementLabel.setText("Abonnement ID: " + abonnementData.getAbonnementId());
        sportifLabel.setText("Sportif ID: " + abonnementData.getSportifId());
        salleLabel.setText("Salle ID: " + abonnementData.getSalleId());
        montantLabel.setText("Montant: " + abonnementData.getMontant() + " USD");
    }

    @FXML
    private void creerIntentionPaiement() {
        if (abonnementData != null) {
            Long sportifId = abonnementData.getSportifId();
            Long abonnementId = abonnementData.getAbonnementId();
            Double montant = abonnementData.getMontant();
            String devise = "USD"; // Devise par défaut

            try {
                // URL de l'API de paiement
                URL url = new URL("http://localhost:8081/payments");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json; utf-8");
                connection.setRequestProperty("Accept", "application/json");
                connection.setDoOutput(true);

                // Corps de la requête JSON
                String jsonInputString = String.format(
                        "{\"sportifId\": %d, \"abonnementId\": %d, \"montant\": %.2f, \"devise\": \"%s\"}",
                        sportifId, abonnementId, montant, devise
                );

                // Envoyer la requête
                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                // Récupérer la réponse
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Paiement réussi
                    showAlert("Succès", "Paiement effectué avec succès !");
                } else {
                    // Paiement échoué
                    showAlert("Erreur", "Le paiement a échoué. Code de réponse : " + responseCode);
                }
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Erreur", "Une erreur est survenue lors du paiement : " + e.getMessage());
            }
        } else {
            showAlert("Erreur", "Aucune donnée d'abonnement reçue.");
        }
    }

    /**
     * Affiche une alerte à l'utilisateur.
     *
     * @param title   Le titre de l'alerte.
     * @param message Le message à afficher.
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}