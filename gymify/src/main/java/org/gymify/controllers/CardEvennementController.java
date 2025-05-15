package org.gymify.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.gymify.entities.Equipe;
import org.gymify.entities.Event;
import org.gymify.services.EquipeEventService;
import org.gymify.services.EquipeService;
import org.gymify.services.EventService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

public class CardEvennementController {

    @FXML private ImageView eventImage;
    @FXML private Label eventName;
    @FXML private Label eventDate;
    @FXML private Label eventLocation;
    @FXML private Label eventType;
    @FXML private Label eventReward;
    @FXML private Label eventDescription;
    @FXML private Label eventTimeDebut;
    @FXML private Label eventTimeFin;
    @FXML private ListView<String> eventTeamsListView;
    @FXML private Button btnDetails;
    @FXML private Button btnEdit;
    @FXML private Button btnDelete;

    private Event evenement;
    private ListeDesEvennementsController parentController;
    private final EventService eventService = new EventService();
    private final EquipeEventService equipeEventService = new EquipeEventService();
    private final EquipeService equipeService = new EquipeService();
    private static final Logger LOGGER = Logger.getLogger(CardEvennementController.class.getName());

    public CardEvennementController() throws SQLException {
        // Constructor initializes the services
    }

    @FXML
    public void initialize() {
        if (eventImage == null || eventName == null || eventDate == null || eventLocation == null ||
                eventType == null || eventReward == null || eventDescription == null ||
                eventTimeDebut == null || eventTimeFin == null || eventTeamsListView == null ||
                btnDetails == null || btnEdit == null || btnDelete == null) {
            LOGGER.severe("One or more FXML elements are not properly initialized: " +
                    "eventImage=" + eventImage + ", eventName=" + eventName + ", eventDate=" + eventDate +
                    ", eventLocation=" + eventLocation + ", eventType=" + eventType +
                    ", eventReward=" + eventReward + ", eventDescription=" + eventDescription +
                    ", eventTimeDebut=" + eventTimeDebut + ", eventTimeFin=" + eventTimeFin +
                    ", eventTeamsListView=" + eventTeamsListView +
                    ", btnDetails=" + btnDetails + ", btnEdit=" + btnEdit + ", btnDelete=" + btnDelete);
            return;
        }
    }

    @FXML
    public void setEvenement(Event evenement, ListeDesEvennementsController parentController) {
        this.evenement = evenement;
        this.parentController = parentController;

        if (evenement == null) {
            LOGGER.severe("L'événement est null !");
            return;
        }

        LOGGER.info("Chargement de l'événement : " + evenement);

        eventName.setText("Nom : " + (evenement.getNom() != null ? evenement.getNom() : "Non défini"));
        eventDate.setText("Date : " + (evenement.getDate() != null ? evenement.getDate().toString() : "Non défini"));
        eventLocation.setText(evenement.getLieu() != null ? evenement.getLieu() : "Non défini");
        eventType.setText(evenement.getType() != null ? evenement.getType().name() : "Non défini");
        eventReward.setText(evenement.getReward() != null ? evenement.getReward().name() : "Aucune");
        eventDescription.setText("Description : " + (evenement.getDescription() != null ? evenement.getDescription() : "Aucune description"));
        eventTimeDebut.setText(evenement.getHeureDebut() != null ? evenement.getHeureDebut().toString() : "Non défini");
        eventTimeFin.setText(evenement.getHeureFin() != null ? evenement.getHeureFin().toString() : "Non défini");

        Set<Equipe> equipes = evenement.getEquipes();
        eventTeamsListView.getItems().clear();
        if (equipes != null && !equipes.isEmpty()) {
            for (Equipe equipe : equipes) {
                eventTeamsListView.getItems().add(equipe.getNom());
            }
        } else {
            eventTeamsListView.getItems().add("Aucune équipe associée");
        }

        String imagePath = resolveImagePath(evenement.getImageUrl());
        try {
            eventImage.setImage(new Image(imagePath, true));
            LOGGER.info("Image loaded for event: " + imagePath);
        } catch (Exception e) {
            LOGGER.warning("Erreur de chargement de l'image à partir de " + imagePath + ": " + e.getMessage());
            eventImage.setImage(new Image(resolveImagePath("/Uploads/events/default.jpg"), true));
        }

        btnDetails.setOnAction(event -> showEventDetails());
        btnEdit.setOnAction(event -> openEditEvenement());
        btnDelete.setOnAction(event -> deleteEvenement());
    }

    private String resolveImagePath(String imageUrl) {
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            LOGGER.warning("Image URL is null or empty, using default image");
            return getClass().getResource("/Uploads/events/default.jpg") != null ?
                    getClass().getResource("/Uploads/events/default.jpg").toExternalForm() :
                    "file:src/main/resources/Uploads/events/default.jpg";
        }

        String normalizedImageUrl = imageUrl.replace("\\", "/");
        if (!normalizedImageUrl.startsWith("/")) {
            normalizedImageUrl = "/" + normalizedImageUrl;
        }

        try {
            if (getClass().getResource(normalizedImageUrl) != null) {
                return getClass().getResource(normalizedImageUrl).toExternalForm();
            } else {
                LOGGER.warning("Image resource not found: " + normalizedImageUrl);
                String lowercasePath = normalizedImageUrl.replace("/Uploads/", "/uploads/");
                if (getClass().getResource(lowercasePath) != null) {
                    return getClass().getResource(lowercasePath).toExternalForm();
                }
                LOGGER.warning("Image resource not found with lowercase path: " + lowercasePath);

                Path filePath = Paths.get("src/main/resources" + normalizedImageUrl);
                if (Files.exists(filePath)) {
                    return filePath.toUri().toString();
                }
                LOGGER.warning("Image not found on filesystem: " + filePath);

                return getClass().getResource("/Uploads/events/default.jpg") != null ?
                        getClass().getResource("/Uploads/events/default.jpg").toExternalForm() :
                        "file:src/main/resources/Uploads/events/default.jpg";
            }
        } catch (Exception e) {
            LOGGER.warning("Erreur lors de la résolution du chemin de l'image " + normalizedImageUrl + ": " + e.getMessage());
            return getClass().getResource("/Uploads/events/default.jpg") != null ?
                    getClass().getResource("/Uploads/events/default.jpg").toExternalForm() :
                    "file:src/main/resources/Uploads/events/default.jpg";
        }
    }

    private void showEventDetails() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Détails de l'événement");
        alert.setHeaderText(evenement != null ? evenement.getNom() : "Événement inconnu");
        StringBuilder equipesList = new StringBuilder();
        if (evenement.getEquipes() != null && !evenement.getEquipes().isEmpty()) {
            for (Equipe equipe : evenement.getEquipes()) {
                equipesList.append(equipe.getNom()).append("\n");
            }
        } else {
            equipesList.append("Aucune équipe associée");
        }
        alert.setContentText(String.format(
                "Lieu : %s\nDate : %s\nHeure début : %s\nHeure fin : %s\nType : %s\nRécompense : %s\nDescription : %s\nImage URL : %s\nÉquipes :\n%s",
                evenement.getLieu() != null ? evenement.getLieu() : "Non défini",
                evenement.getDate() != null ? evenement.getDate() : "Non défini",
                evenement.getHeureDebut() != null ? evenement.getHeureDebut() : "Non défini",
                evenement.getHeureFin() != null ? evenement.getHeureFin() : "Non défini",
                evenement.getType() != null ? evenement.getType().name() : "Non défini",
                evenement.getReward() != null ? evenement.getReward().name() : "Aucune",
                evenement.getDescription() != null ? evenement.getDescription() : "Aucune description",
                evenement.getImageUrl() != null ? evenement.getImageUrl() : "Aucune image",
                equipesList.toString()
        ));
        alert.showAndWait();
    }

    private void openEditEvenement() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjoutEvennement.fxml"));
            if (loader.getLocation() == null) {
                LOGGER.severe("Cannot find AjoutEvennement.fxml. Ensure the file exists in src/main/resources/");
                Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur: Cannot find AjoutEvennement.fxml.", ButtonType.OK);
                alert.showAndWait();
                return;
            }
            Parent root = loader.load();
            AjoutEvennementController controller = loader.getController();
            controller.setParentController(parentController);
            controller.setEvenement(evenement, parentController);
            Stage stage = new Stage();
            stage.setTitle("Modifier l'événement");
            stage.setScene(new Scene(root));
            stage.setOnHidden(e -> parentController.loadEvents(parentController.getSearchFieldText()));
            stage.show();
        } catch (IOException e) {
            LOGGER.severe("Erreur lors de l'ouverture de la fenêtre de modification : " + e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors de l'ouverture de la fenêtre de modification : " + e.getMessage(), ButtonType.OK);
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    private void deleteEvenement() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Voulez-vous vraiment supprimer cet événement ?");
        confirm.setContentText("Cette action supprimera également les équipes associées et leurs associations. Cette action est irréversible.");
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    List<Equipe> associatedTeams = equipeEventService.getEquipesForEvent(evenement.getId());
                    for (Equipe equipe : associatedTeams) {
                        equipeEventService.supprimer(equipe, evenement);
                    }
                    for (Equipe equipe : associatedTeams) {
                        equipeService.supprimer(equipe.getId());
                    }
                    eventService.supprimer(evenement.getId());
                    parentController.loadEvents(parentController.getSearchFieldText());
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Succès");
                    alert.setHeaderText("Suppression réussie");
                    alert.setContentText("L'événement, ses équipes associées et leurs associations ont été supprimés avec succès.");
                    alert.showAndWait();
                } catch (SQLException e) {
                    LOGGER.severe("Erreur lors de la suppression de l'événement : " + e.getMessage());
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors de la suppression de l'événement : " + e.getMessage(), ButtonType.OK);
                    alert.showAndWait();
                    e.printStackTrace();
                }
            }
        });
    }
}