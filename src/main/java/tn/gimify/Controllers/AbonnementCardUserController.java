package tn.gimify.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tn.gimify.entities.type_Abonnement;

import java.io.IOException;

public class AbonnementCardUserController {

    @FXML private Label typeLabel;
    @FXML private Label dateDebutLabel;
    @FXML private Label dateFinLabel;
    @FXML private Label tarifLabel;
    @FXML private Button inscrireButton;
    private int idAbonnement;
    private double montant;

    // Méthode pour initialiser les données des abonnements
    public void setAbonnementData(type_Abonnement typeAbonnement, String dateDebut, String dateFin, Double tarif) {
        typeLabel.setText("Type: " + typeAbonnement);
        dateDebutLabel.setText("Début: " + dateDebut);
        dateFinLabel.setText("Fin: " + dateFin);
        tarifLabel.setText("Tarif: " + tarif + " DT");

        // L'action d'inscription
        inscrireButton.setOnAction(e -> ouvrirPaiementFictif());

    }
    @FXML
    private void ouvrirPaiementFictif() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/PaiementUser.fxml"));
            Parent root = loader.load();

            PaiementController paiementController = loader.getController();
            
            paiementController.setPaiementData(idAbonnement, 1, montant); // 1 = ID user (remplace par l'ID réel)

            Stage stage = new Stage();
            stage.setTitle("Paiement");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
