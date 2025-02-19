package tn.gimify.Controllers;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tn.gimify.entities.type_Abonnement;

public class AbonnementCardUserController {

    @FXML private Label typeLabel;
    @FXML private Label dateDebutLabel;
    @FXML private Label dateFinLabel;
    @FXML private Label tarifLabel;
    @FXML private Button inscrireButton;

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
        Stage paiementStage = new Stage();
        paiementStage.setTitle("Paiement Fictif");
        Scene scene = new Scene(new VBox(20, new javafx.scene.control.Label("Simulation de paiement en cours...")), 300, 150);
        paiementStage.setScene(scene);
        paiementStage.show();
    }
}
