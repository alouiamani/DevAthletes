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
import org.gymify.entities.AbonnementData;
import org.gymify.entities.User;
import org.gymify.entities.type_Abonnement;

import java.io.IOException;

public class AbonnementCardUserController {

    @FXML private Label typeLabel;
    @FXML private Label dateDebutLabel;
    @FXML private Label dateFinLabel;
    @FXML private Label tarifLabel;
    @FXML private Button payerButton;

    private Abonnement abonnement; // L'abonnement sélectionné
    private User sportif;          // L'utilisateur connecté (sportif)

    // Méthode pour initialiser les données des abonnements
    public void setAbonnementData(Abonnement abonnement) {
        this.abonnement = abonnement; // Stocker l'abonnement sélectionné

        typeLabel.setText("Type: " + abonnement.getType_Abonnement());
        dateDebutLabel.setText("Début: " + abonnement.getDate_Début().toString());
        dateFinLabel.setText("Fin: " + abonnement.getDate_Fin().toString());
        tarifLabel.setText("Tarif: " + abonnement.getTarif() + " DT");

        // L'action d'inscription
        payerButton.setOnAction(e -> handlePayerButtonClick());
    }

    // Setter pour l'utilisateur connecté (sportif)
    public void setSportif(User sportif) {
        this.sportif = sportif;
    }

    @FXML
    private void handlePayerButtonClick() {
        // Vérifie que l'utilisateur est connecté
        if (sportif == null) {
            System.err.println("Aucun utilisateur connecté !");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Utilisateur non connecté");
            alert.setContentText("Veuillez vous connecter pour effectuer un paiement.");
            alert.showAndWait();
            return;
        }

        // Vérifie que l'utilisateur est un sportif
        if (!sportif.getRole().equals("Sportif")) {
            System.out.println("Seuls les utilisateurs avec le rôle Sportif peuvent effectuer un paiement.");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de rôle");
            alert.setHeaderText("Accès refusé");
            alert.setContentText("Seuls les utilisateurs avec le rôle Sportif peuvent effectuer un paiement.");
            alert.showAndWait();
            return;
        }

        // Vérifie que l'abonnement est défini
        if (abonnement == null) {
            System.err.println("Aucun abonnement sélectionné !");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Abonnement non sélectionné");
            alert.setContentText("Veuillez sélectionner un abonnement valide.");
            alert.showAndWait();
            return;
        }

        // Vérifie que la salle est définie
        if (abonnement.getSalle() == null) {
            System.err.println("Aucune salle associée à cet abonnement !");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Salle non définie");
            alert.setContentText("Veuillez contacter l'administrateur pour résoudre ce problème.");
            alert.showAndWait();
            return;
        }

        // Récupère les données nécessaires
        Long Id_Abonnement = (long) abonnement.getId_Abonnement();
        Long sportifId = (long) sportif.getId_User();
        Long salle_Id = (long) abonnement.getSalle().getId_Salle();
        Double montant = abonnement.getTarif();

        // Crée un objet AbonnementData
        AbonnementData abonnementData = new AbonnementData(Id_Abonnement, sportifId, salle_Id, montant);

        // Charge l'interface de paiement
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/PaiementUser.fxml"));
            Parent root = loader.load();

            // Passe les données au contrôleur de paiement
            PaiementController paiementController = loader.getController();
            paiementController.setAbonnementData(abonnementData);

            // Affiche la nouvelle interface
            Stage stage = new Stage();
            stage.setTitle("Paiement");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Erreur de chargement");
            alert.setContentText("Impossible de charger l'interface de paiement.");
            alert.showAndWait();
        }
    }
}