package org.gymify.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.gymify.entities.Equipe;
import org.gymify.entities.EquipeEvent;
import org.gymify.entities.Event;
import org.gymify.services.EquipeEventService;
import org.gymify.services.EventService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

/**
 * Controller class for managing the list of participations (equipe-event associations) in the UI.
 */
public class ListeParticipationController {

    @FXML
    private Button btnRetour;

    @FXML
    private ScrollPane scrollPaneParticipations;

    @FXML
    private TextField searchField;

    @FXML
    private VBox vboxParticipations;

    private final EquipeEventService equipeEventService = new EquipeEventService();
    private final EventService eventService = new EventService();
    private static final Logger LOGGER = Logger.getLogger(ListeParticipationController.class.getName());

    @FXML
    public void initialize() {
        loadParticipations("");

        // Add listener for search field to filter participations
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            loadParticipations(newValue);
        });
    }

    public void loadParticipations(String searchQuery) {
        if (vboxParticipations == null) {
            LOGGER.severe("vboxParticipations is null. Cannot load participations.");
            return;
        }

        vboxParticipations.getChildren().clear();
        try {
            List<EquipeEvent> participationList = equipeEventService.afficher();
            for (EquipeEvent participation : participationList) {
                // Fetch the event details
                Event event = eventService.recupererParId(participation.getIdEvent());
                if (event == null) continue;

                // Fetch the team details
                List<Equipe> equipes = equipeEventService.getEquipesForEvent(participation.getIdEvent());
                if (equipes.isEmpty()) continue;
                Equipe equipe = equipes.get(0); // Assuming one team per event for simplicity

                // Apply search filter by event name or type
                boolean matchesSearch = searchQuery == null || searchQuery.trim().isEmpty() ||
                        (event.getNom() != null && event.getNom().toLowerCase().contains(searchQuery.toLowerCase())) ||
                        (event.getType() != null && event.getType().name().toLowerCase().contains(searchQuery.toLowerCase()));

                if (matchesSearch) {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/CardParticipation.fxml"));
                    Parent card = loader.load();
                    tn.esprit.controllers.CardParticipationController controller = loader.getController();
                    controller.setParticipation(event, equipe);
                    vboxParticipations.getChildren().add(card);
                }
            }
        } catch (SQLException e) {
            LOGGER.severe("Erreur lors du chargement des participations: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            LOGGER.severe("Erreur lors du chargement de la carte FXML: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void goBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListeDesEvennements.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnRetour.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Liste des Événements");
            stage.show();
        } catch (IOException e) {
            LOGGER.severe("Erreur lors du retour à l'interface ListeDesEvennements: " + e.getMessage());
            e.printStackTrace();
        }
    }
}