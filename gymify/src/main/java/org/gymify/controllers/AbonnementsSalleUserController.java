package org.gymify.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.gymify.entities.Abonnement;
import org.gymify.services.AbonnementService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class AbonnementsSalleUserController {

    @FXML private VBox abonnementContainer;

    private AbonnementService abonnementService = new AbonnementService();
    private int idSalle;

    // Initialiser avec l'ID de la salle
    public void initData(int idSalle) {
        this.idSalle = idSalle;
        chargerAbonnements(); // Charger les abonnements
    }

    // Charger les abonnements de la salle
    private void chargerAbonnements() {
        try {
            List<Abonnement> abonnements = abonnementService.getAbonnementsParSalle(idSalle);
            abonnementContainer.getChildren().clear(); // Effacer les abonnements existants

            for (Abonnement abonnement : abonnements) {
                // Charger le fichier FXML de la carte d'abonnement
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/AbonnementCardUser.fxml"));
                VBox card = loader.load();

                // Obtenir le contrôleur de la carte
                AbonnementCardUserController controller = loader.getController();
                controller.setAbonnementData(
                        abonnement.getType_Abonnement(),
                        abonnement.getDate_Début().toString(),
                        abonnement.getDate_Fin().toString(),
                        abonnement.getTarif());

                // Ajouter la carte au conteneur
                abonnementContainer.getChildren().add(card);
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    // Méthode pour ouvrir l'interface de paiement fictif


    // Fermer la fenêtre
    @FXML
    private void fermerFenetre() {
        Stage stage = (Stage) abonnementContainer.getScene().getWindow();
        stage.close();
    }
}
