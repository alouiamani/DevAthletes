package org.gymify.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.gymify.entities.Event;
import org.gymify.services.EventService;

import java.io.IOException;
import java.sql.SQLException;

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
    @FXML private Button btnDetails;
    @FXML private Button btnEdit;
    @FXML private Button btnDelete;

    private Event evenement;
    private ListeDesEvennementsController parentController;
    private final EventService eventService = new EventService();

    @FXML
    public void setEvenement(Event evenement, ListeDesEvennementsController parentController) {
        this.evenement = evenement;
        this.parentController = parentController;

        if (evenement == null) {
            System.err.println("L'événement est null !");
            return;
        }

        System.out.println("Chargement de l'événement : " + evenement);

        // Exemple spécifique demandé par l'utilisateur
        eventName.setText("Nom : RECYCLEons un atelier éco");
        eventDate.setText("Date : " + (evenement.getDate() != null ? evenement.getDate().toString() : "Non défini"));
        eventLocation.setText("Lieu : " + (evenement.getLieu() != null ? evenement.getLieu() : "Non défini"));
        eventType.setText("Type : " + (evenement.getType() != null ? evenement.getType().name() : "Écologique"));
        eventReward.setText("Reward : " + (evenement.getReward() != null ? evenement.getReward().name() : "Aucune"));
        eventDescription.setText("Description : Atelier de recyclage pour promouvoir l'écologie et la durabilité.");
        eventTimeDebut.setText("Heure début : " + (evenement.getHeureDebut() != null ? evenement.getHeureDebut().toString() : "10:00"));
        eventTimeFin.setText("Heure fin : " + (evenement.getHeureFin() != null ? evenement.getHeureFin().toString() : "12:00"));

        // Gestion de l'image
        if (evenement.getImageUrl() != null && !evenement.getImageUrl().isEmpty()) {
            try {
                eventImage.setImage(new Image(evenement.getImageUrl(), true));
            } catch (Exception e) {
                eventImage.setImage(new Image("/resources/images/images.jpg"));
                System.err.println("Erreur de chargement de l'image : " + e.getMessage());
            }
        } else {
            eventImage.setImage(new Image("/resources/images/images.jpg"));
        }

        // Actions des boutons
        btnDetails.setOnAction(event -> showEventDetails());
        btnEdit.setOnAction(event -> openEditEvenement());
        btnDelete.setOnAction(event -> deleteEvenement());
    }

    private void showEventDetails() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Détails de l'événement");
        alert.setHeaderText(evenement != null ? evenement.getNom() : "Événement inconnu");
        alert.setContentText(String.format(
                "Lieu : %s\nDate : %s\nHeure début : %s\nHeure fin : %s\nType : %s\nRécompense : %s\nDescription : %s\nImage URL : %s",
                evenement.getLieu() != null ? evenement.getLieu() : "Non défini",
                evenement.getDate() != null ? evenement.getDate() : "Non défini",
                evenement.getHeureDebut() != null ? evenement.getHeureDebut() : "Non défini",
                evenement.getHeureFin() != null ? evenement.getHeureFin() : "Non défini",
                evenement.getType() != null ? evenement.getType().name() : "Non défini",
                evenement.getReward() != null ? evenement.getReward().name() : "Aucune",
                evenement.getDescription() != null ? evenement.getDescription() : "Aucune description",
                evenement.getImageUrl() != null ? evenement.getImageUrl() : "Aucune image"
        ));
        alert.showAndWait();
    }

    private void openEditEvenement() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjoutEvennement.fxml"));
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
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors de l'ouverture de la fenêtre de modification : " + e.getMessage(), ButtonType.OK);
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    private void deleteEvenement() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Voulez-vous vraiment supprimer cet événement ?");
        confirm.setContentText("Cette action est irréversible.");
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    eventService.supprimer(evenement.getId());
                    parentController.loadEvents(parentController.getSearchFieldText());
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Succès");
                    alert.setHeaderText("Suppression réussie");
                    alert.setContentText("L'événement a été supprimé avec succès.");
                    alert.showAndWait();
                } catch (SQLException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors de la suppression de l'événement : " + e.getMessage(), ButtonType.OK);
                    alert.showAndWait();
                    e.printStackTrace();
                }
            }
        });
    }
}