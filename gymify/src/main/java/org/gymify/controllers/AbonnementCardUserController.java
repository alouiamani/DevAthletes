package org.gymify.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.gymify.entities.Abonnement;
import org.gymify.entities.User;


import java.io.IOException;

public class AbonnementCardUserController {

    @FXML private Label typeLabel;
    @FXML private Label dateDebutLabel;
    @FXML private Label dateFinLabel;
    @FXML private Label tarifLabel;
    @FXML private Button payerButton;
    @FXML private Label salleLabel;
    private Abonnement abonnement; // L'abonnement sélectionné
    // Ajoutez cette variable de classe
    private User sportif;

    // Cette méthode doit absolument exister
    public void setSportif(User sportif) {
        if (sportif == null) {
            throw new IllegalArgumentException("L'utilisateur ne peut pas être null");
        }

        if (!"sportif".equals(sportif.getRole())) {
            throw new IllegalArgumentException("Seuls les sportifs peuvent souscrire à un abonnement");
        }

        this.sportif = sportif;
        System.out.println("Utilisateur défini: " + sportif.getNom()); // Log de vérification

        // Optionnel: activer/désactiver le bouton en fonction du rôle
        payerButton.setDisable(!"sportif".equals(sportif.getRole()));
    }

    // Modifiez handlePayerButtonClick() pour utiliser this.sportif
    @FXML

    // Méthode pour initialiser les données des abonnements
    public void setAbonnementData(Abonnement abonnement) {
        this.abonnement = abonnement;

        typeLabel.setText("Type: " + abonnement.getType_Abonnement());
        tarifLabel.setText("Tarif: " + abonnement.getTarif() + " DT");

        // Afficher le nom de la salle si disponible
        if (abonnement.getSalle() != null) {
            salleLabel.setText("Salle: " + abonnement.getSalle().getNom());
        }

        payerButton.setOnAction(e -> handlePayerButtonClick());
    }
    @FXML
    private void handlePayerButtonClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/PaymentForm.fxml"));
            Parent root = loader.load();

            PaymentFormController controller = loader.getController();
            controller.setPaymentData(abonnement, sportif);

            Stage stage = new Stage();
            stage.setTitle("Paiement - " + abonnement.getType_Abonnement());
            stage.setScene(new Scene(root, 500, 400));
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'ouvrir le formulaire de paiement");
        }
    }
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

}