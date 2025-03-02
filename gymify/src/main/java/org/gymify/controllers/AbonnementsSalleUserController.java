package org.gymify.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.gymify.entities.Abonnement;
import org.gymify.entities.User;
import org.gymify.services.AbonnementService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class AbonnementsSalleUserController {

    @FXML private VBox abonnementContainer;

    private AbonnementService abonnementService = new AbonnementService();
    private int idSalle;
    private User loggedInUser;

    // Initialiser avec l'ID de la salle et l'utilisateur connecté
    public void initData(int idSalle, User loggedInUser) {
        this.idSalle = idSalle;
        this.loggedInUser = loggedInUser;
        chargerAbonnements();
    }

    private void chargerAbonnements() {
        try {
            List<Abonnement> abonnements = abonnementService.getAbonnementsParSalle(idSalle);
            abonnementContainer.getChildren().clear();

            for (Abonnement abonnement : abonnements) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/AbonnementCardUser.fxml"));
                VBox card = loader.load();

                AbonnementCardUserController controller = loader.getController();
                controller.setAbonnementData(abonnement); // Passer l'abonnement sélectionné
                controller.setSportif(loggedInUser); // Passer l'utilisateur connecté

                abonnementContainer.getChildren().add(card);
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void fermerFenetre() {
        Stage stage = (Stage) abonnementContainer.getScene().getWindow();
        stage.close();
    }
}