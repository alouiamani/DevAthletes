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
    @FXML private Label emptyLabel;

    private AbonnementService abonnementService = new AbonnementService();
    private SalleService salleService = new SalleService();
    private int idSalle;
    private User loggedInUser;

    public void initData(int idSalle, User loggedInUser) {
        this.idSalle = idSalle;
        this.loggedInUser = loggedInUser;

        try {
            Salle salle = salleService.getSalleById(idSalle);
            salleLabel.setText("Available Subscriptions - " + salle.getNom());
        } catch (SQLException e) {
            salleLabel.setText("Available Subscriptions");
        }

        loadSubscriptions();
    }

    private void loadSubscriptions() {
        try {
            List<Abonnement> abonnements = abonnementService.getAbonnementsParSalle(idSalle);
            abonnementContainer.getChildren().clear();

            if (abonnements.isEmpty()) {
                emptyLabel.setVisible(true);
                emptyLabel.setManaged(true);
                return;
            }

            emptyLabel.setVisible(false);
            emptyLabel.setManaged(false);

            for (Abonnement abonnement : abonnements) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/AbonnementCardUser.fxml"));
                Parent card = loader.load();

                AbonnementCardUserController controller = loader.getController();
                controller.setAbonnementData(abonnement);

                if (loggedInUser != null) {
                    controller.setSportif(loggedInUser);
                } else {
                    System.err.println("No user logged in!");
                }

                abonnementContainer.getChildren().add(card);
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
            Label errorLabel = new Label("Error loading subscriptions");
            abonnementContainer.getChildren().add(errorLabel);
        }
    }

    @FXML
    private void fermerFenetre() {
        Stage stage = (Stage) abonnementContainer.getScene().getWindow();
        stage.close();
    }
}
