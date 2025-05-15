package org.gymify.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.gymify.entities.Equipe;
import org.gymify.entities.Event;
import org.gymify.entities.User;
import org.gymify.services.EquipeEventService;
import org.gymify.services.EquipeService;
import org.gymify.utils.AuthToken;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Logger;

public class CardParticipationController {

    @FXML private Button btnParticiper;
    @FXML private Label equipeName;
    @FXML private Label eventDate;
    @FXML private Label eventDescription;
    @FXML private Label eventLocation;
    @FXML private Label eventName;
    @FXML private Label eventTimeDebut;
    @FXML private Label eventTimeFin;
    @FXML private Label eventType;

    private Event event;
    private Equipe equipe;
    private ListeParticipationController parentController;
    private static final Logger LOGGER = Logger.getLogger(CardParticipationController.class.getName());
    private EquipeService equipeService;
    private EquipeEventService equipeEventService;

    public CardParticipationController() {
        try {
            equipeService = new EquipeService();
            equipeEventService = new EquipeEventService();
        } catch (SQLException e) {
            LOGGER.severe("Erreur lors de l'initialisation des services: " + e.getMessage());
        }
    }

    public void setParticipation(Event event, Equipe equipe) {
        this.event = event;
        this.equipe = equipe;

        eventName.setText(event.getNom() != null ? event.getNom() : "Non défini");
        eventLocation.setText(event.getLieu() != null ? event.getLieu() : "Non défini");
        eventDate.setText(event.getDate() != null ? event.getDate().toString() : "Non défini");
        eventTimeDebut.setText(event.getHeureDebut() != null ? event.getHeureDebut().toString() : "Non défini");
        eventTimeFin.setText(event.getHeureFin() != null ? event.getHeureFin().toString() : "Non défini");
        eventDescription.setText(event.getDescription() != null ? event.getDescription() : "Aucune description");
        eventType.setText(event.getType() != null ? event.getType().name() : "Non défini");

        int currentMembers = equipe.getNombreMembres();
        LOGGER.info("Setting equipe " + equipe.getNom() + " with " + currentMembers + " members.");
        equipeName.setText(equipe.getNom() != null ? equipe.getNom() + " (" + currentMembers + "/8)" : "Non défini (" + currentMembers + "/8)");
    }

    public void setParentController(ListeParticipationController parentController) {
        this.parentController = parentController;
    }

    @FXML
    private void btnparticiper(ActionEvent event) {
        User currentUser = AuthToken.getCurrentUser();
        if (currentUser == null) {
            LOGGER.severe("No user logged in, should not happen after login.");
            showAlert(Alert.AlertType.ERROR, "Erreur", "Utilisateur non connecté.");
            return;
        }

        try {
            if (equipeService.isUserInTeamForEvent(currentUser.getEmail(), this.event.getId())) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Vous participez déjà à une équipe pour cet événement.");
                return;
            }

            int currentMembers = equipe.getNombreMembres();
            if (currentMembers >= 8) {
                showAlert(Alert.AlertType.WARNING, "Équipe Complète", "Cette équipe est complète (8/8).");
                return;
            }

            LOGGER.info("Loading ParticipationSportif.fxml for equipe: " + equipe.getNom());
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ParticipationSportif.fxml"));
            if (loader.getLocation() == null) {
                LOGGER.severe("Cannot find ParticipationSportif.fxml");
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger le formulaire de participation.");
                return;
            }
            Parent root = loader.load();
            ParticipationSportifController controller = loader.getController();
            controller.setSportif(currentUser);
            controller.setEquipe(equipe);
            controller.setEvent(this.event);
            controller.setParentController(parentController);
            Stage stage = new Stage();
            stage.setTitle("Confirmer Participation");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            if (parentController != null) {
                parentController.loadParticipations("");
            }

        } catch (IOException | SQLException e) {
            LOGGER.severe("Erreur lors de la participation: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la participation: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}