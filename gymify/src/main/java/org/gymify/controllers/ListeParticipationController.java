package org.gymify.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.gymify.entities.Equipe;
import org.gymify.entities.EquipeEvent;
import org.gymify.entities.Event;
import org.gymify.entities.User;
import org.gymify.services.EquipeEventService;
import org.gymify.services.EquipeService;
import org.gymify.services.EventService;
import org.gymify.utils.AuthToken;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class ListeParticipationController {

    @FXML private Button btnRetour;
    @FXML private ScrollPane scrollPaneParticipations;
    @FXML private TextField searchField;
    @FXML private VBox vboxParticipations;

    private EquipeEventService equipeEventService;
    private EventService eventService;
    private EquipeService equipeService;
    private static final Logger LOGGER = Logger.getLogger(ListeParticipationController.class.getName());

    @FXML
    public void initialize() {
        try {
            equipeEventService = new EquipeEventService();
            eventService = new EventService();
            equipeService = new EquipeService();
        } catch (SQLException e) {
            LOGGER.severe("Erreur lors de l'initialisation des services: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Erreur d'initialisation", "Impossible d'initialiser les services: " + e.getMessage());
            return;
        }

        if (vboxParticipations == null || searchField == null || scrollPaneParticipations == null || btnRetour == null) {
            LOGGER.severe("FXML components not initialized properly.");
            showAlert(Alert.AlertType.ERROR, "Erreur", "Composants de l'interface non initialisés.");
            return;
        }
        loadParticipations("");
        searchField.textProperty().addListener((observable, oldValue, newValue) -> loadParticipations(newValue));
    }

    public void loadParticipations(String searchQuery) {
        vboxParticipations.getChildren().clear();
        try {
            List<EquipeEvent> participationList = equipeEventService.afficher();
            LOGGER.info("Loaded " + participationList.size() + " participations from database.");
            for (EquipeEvent participation : participationList) {
                Event event = eventService.recupererParId(participation.getIdEvent());
                if (event == null) {
                    LOGGER.warning("Event not found for ID: " + participation.getIdEvent());
                    continue;
                }

                List<Equipe> equipes = equipeEventService.getEquipesForEvent(participation.getIdEvent());
                if (equipes.isEmpty()) {
                    LOGGER.warning("No equipes found for event ID: " + participation.getIdEvent());
                    continue;
                }

                boolean matchesSearch = searchQuery == null || searchQuery.trim().isEmpty() ||
                        (event.getNom() != null && event.getNom().toLowerCase().contains(searchQuery.toLowerCase())) ||
                        (event.getType() != null && event.getType().name().toLowerCase().contains(searchQuery.toLowerCase()));

                if (matchesSearch) {
                    for (Equipe equipe : equipes) {
                        Equipe updatedEquipe = equipeService.getEquipeById(equipe.getId());
                        if (updatedEquipe != null) {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CardParticipation.fxml"));
                            if (loader.getLocation() == null) {
                                LOGGER.severe("Cannot find CardParticipation.fxml");
                                continue;
                            }
                            Parent card = loader.load();
                            CardParticipationController controller = loader.getController();
                            controller.setParticipation(event, updatedEquipe);
                            controller.setParentController(this);
                            vboxParticipations.getChildren().add(card);
                        }
                    }
                }
            }
        } catch (SQLException | IOException e) {
            LOGGER.severe("Erreur lors du chargement des participations: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement des participations: " + e.getMessage());
        }
    }

    @FXML
    void goBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ProfileMembre.fxml"));
            if (loader.getLocation() == null) {
                LOGGER.severe("Cannot find ProfileMembre.fxml");
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger le profil.");
                return;
            }
            Parent root = loader.load();
            Stage stage = (Stage) btnRetour.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Profil Membre");
            stage.show();
        } catch (IOException e) {
            LOGGER.severe("Erreur lors du retour: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du retour: " + e.getMessage());
        }
    }

    public void joinEvent(Event event) throws SQLException, IOException {
        User currentUser = AuthToken.getCurrentUser();
        if (currentUser == null) {
            LOGGER.severe("No user logged in, should not happen after login.");
            showAlert(Alert.AlertType.ERROR, "Erreur", "Utilisateur non connecté.");
            return;
        }

        if (equipeService.isUserInTeamForEvent(currentUser.getEmail(), event.getId())) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Vous participez déjà à une équipe pour cet événement.");
            return;
        }

        List<Equipe> equipes = equipeEventService.getEquipesForEvent(event.getId());
        Equipe availableEquipe = null;
        for (Equipe eq : equipes) {
            int members = eq.getNombreMembres();
            LOGGER.info("Equipe " + eq.getNom() + " has " + members + " members.");
            if (members < 8) {
                availableEquipe = eq;
                break;
            }
        }

        if (availableEquipe == null) {
            showAlert(Alert.AlertType.WARNING, "Événement Complet", "Toutes les équipes de cet événement sont complètes (8/8).");
            return;
        }

        LOGGER.info("Opening ParticipationSportif for equipe: " + availableEquipe.getNom());
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ParticipationSportif.fxml"));
        if (loader.getLocation() == null) {
            LOGGER.severe("Cannot find ParticipationSportif.fxml");
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger le formulaire de participation.");
            return;
        }
        Parent root = loader.load();
        ParticipationSportifController controller = loader.getController();
        controller.setSportif(currentUser);
        controller.setEquipe(availableEquipe);
        controller.setEvent(event);
        controller.setParentController(this);
        Stage stage = new Stage();
        stage.setTitle("Confirmer Participation");
        stage.setScene(new Scene(root));
        stage.showAndWait();
        loadParticipations("");
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}