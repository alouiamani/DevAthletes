package org.gymify.controllers;

/*import javafx.event.ActionEvent;
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

        // Populate the fields with event and team data
        eventName.setText("Nom : " + (event.getNom() != null ? event.getNom() : "Non défini"));
        eventLocation.setText(event.getLieu() != null ? event.getLieu() : "Non défini");
        eventDate.setText("Date : " + (event.getDate() != null ? event.getDate().toString() : "Non défini"));
        eventTimeDebut.setText("Heure début : " + (event.getHeureDebut() != null ? event.getHeureDebut().toString() : "Non défini"));
        eventTimeFin.setText("Heure fin : " + (event.getHeureFin() != null ? event.getHeureFin().toString() : "Non défini"));
        eventDescription.setText("Description : " + (event.getDescription() != null ? event.getDescription() : "Aucune description"));
        equipeName.setText(equipe.getNom() != null ? equipe.getNom() : "Non défini");
        eventType.setText(event.getType() != null ? event.getType().name() : "Non défini");

        // Display the number of members in the Equipe
        try {
            int currentMembers = equipeService.getNombreSportifsInEquipe(equipe.getId());
            equipeName.setText((equipe.getNom() != null ? equipe.getNom() : "Non défini") + " (Membres: " + currentMembers + "/8)");
        } catch (SQLException e) {
            LOGGER.severe("Erreur lors de la récupération du nombre de membres: " + e.getMessage());
            equipeName.setText(equipe.getNom() != null ? equipe.getNom() + " (Membres: Erreur)" : "Non défini (Membres: Erreur)");
        }

        // Set action for the Participer button
        btnParticiper.setOnAction(this::participer);
    }

    public void setParentController(ListeParticipationController parentController) {
        this.parentController = parentController;
    }

    @FXML
    private void participer(ActionEvent event) {
        User currentUser = AuthToken.getCurrentUser();
        if (currentUser == null || !"sportif".equalsIgnoreCase(currentUser.getRole())) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Vous devez être connecté en tant que sportif pour participer.");
            return;
        }

        try {
            // Check if the Sportif already belongs to an Equipe
            if (currentUser.getId_equipe() != null) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Vous appartenez déjà à une équipe (ID: " + currentUser.getId_equipe() + ").");
                return;
            }

            // Check the number of members in the Equipe
            int currentMembers = equipeService.getNombreSportifsInEquipe(equipe.getId());
            if (currentMembers >= 8) {
                showAlert(Alert.AlertType.WARNING, "Équipe Complète",
                        "Cette équipe est complète (8 membres maximum). Veuillez choisir un autre événement ou équipe.");
                return;
            }

            // Open ParticipationSportif interface to confirm participation
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ParticipationSportif.fxml"));
            Parent root = loader.load();
            ParticipationSportifController controller = loader.getController();
            controller.setSportif(currentUser);
            controller.setEquipe(equipe);
            controller.setEvent(this.event);
            Stage stage = new Stage();
            stage.setTitle("Confirmer Participation");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            // Refresh the list after participation
            parentController.loadParticipations("");

        } catch (SQLException | IOException e) {
            LOGGER.severe("Erreur lors de la participation: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur s'est produite: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}  */