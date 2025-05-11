package org.gymify.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.gymify.entities.Abonnement;
import org.gymify.entities.Salle;
import org.gymify.entities.User;
import org.gymify.services.AbonnementService;
import org.gymify.services.SalleService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class AbonnementsSalleUserController {

    @FXML private VBox abonnementContainer;
    @FXML private Label salleLabel;

    private AbonnementService abonnementService = new AbonnementService();
    private SalleService salleService = new SalleService();
    private int idSalle;
    private User loggedInUser;

    public void initData(int idSalle, User loggedInUser) {
        this.idSalle = idSalle;
        this.loggedInUser = loggedInUser;

        try {
            Salle salle = salleService.getSalleById(idSalle);
            salleLabel.setText("Abonnements disponibles - " + salle.getNom());
        } catch (SQLException e) {
            salleLabel.setText("Abonnements disponibles");
        }

        chargerAbonnements();
    }
    private void chargerAbonnements() {
        try {
            List<Abonnement> abonnements = abonnementService.getAbonnementsParSalle(idSalle);
            abonnementContainer.getChildren().clear();

            if (abonnements.isEmpty()) {
                Label emptyLabel = new Label("Aucun abonnement disponible pour cette salle");
                abonnementContainer.getChildren().add(emptyLabel);
                return;
            }

            for (Abonnement abonnement : abonnements) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/AbonnementCardUser.fxml"));
                Parent card = loader.load(); // Changé de VBox à Parent

                AbonnementCardUserController controller = loader.getController();
                controller.setAbonnementData(abonnement);

                if (loggedInUser != null) {
                    controller.setSportif(loggedInUser);
                } else {
                    System.err.println("Aucun utilisateur connecté !");
                }

                abonnementContainer.getChildren().add(card);
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
            Label errorLabel = new Label("Erreur lors du chargement des abonnements");
            abonnementContainer.getChildren().add(errorLabel);
        }
    }

    @FXML
    private void fermerFenetre() {
        Stage stage = (Stage) abonnementContainer.getScene().getWindow();
        stage.close();
    }
}