package tn.esprit.controllers;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import tn.esprit.entities.Event;
import tn.esprit.services.EventService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CardsController {

    @FXML private Label nomtf, lieutf, typetf, datetf, heuredebuttf, heurefintf, rewardtf, descriptiontf;
    @FXML private ImageView imagetf;
    @FXML private Button modifierBtn, supprimerBtn, detailsBtn;

    private Event event;
    private Event1Controller parentController;
    private final EventService eventService = new EventService();
    private static final Logger logger = Logger.getLogger(CardsController.class.getName());

    @FXML
    public void setEvent(Event event, Event1Controller parentController) {
        this.event = event;
        this.parentController = parentController;

        nomtf.setText(event.getNom() != null ? event.getNom() : "N/A");
        lieutf.setText(event.getLieu() != null ? event.getLieu() : "N/A");
        typetf.setText(event.getType() != null ? event.getType().toString() : "N/A");
        datetf.setText(event.getDate() != null ? event.getDate().toString() : "N/A");
        heuredebuttf.setText(event.getHeureDebut() != null ? event.getHeureDebut().toString() : "N/A");
        heurefintf.setText(event.getHeureFin() != null ? event.getHeureFin().toString() : "N/A");
        rewardtf.setText(event.getReward() != null ? event.getReward() : "N/A");
        descriptiontf.setText(event.getDescription() != null ? event.getDescription() : "N/A");

        loadImage(event.getImageUrl());

        modifierBtn.setOnAction(this::modifierevent);
        supprimerBtn.setOnAction(this::supprimerevent);
        detailsBtn.setOnAction(this::detailsevent);
    }

    private void loadImage(String imageUrl) {
        try {
            if (imageUrl != null && !imageUrl.trim().isEmpty()) {
                imagetf.setImage(new Image(imageUrl, true));
            } else {
                imagetf.setImage(new Image(getClass().getResource("/assets/default-image.png").toExternalForm()));
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Erreur lors du chargement de l'image", e);
            imagetf.setImage(new Image(getClass().getResource("/assets/default-image.png").toExternalForm()));
        }
    }

    @FXML
    private void modifierevent(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/views/ServiceEvent.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Modifier un événement");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            parentController.refreshList();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erreur lors de l'ouverture de la fenêtre de modification", e);
        }
    }

    @FXML
    private void supprimerevent(ActionEvent event) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmation");
        confirmAlert.setHeaderText("Supprimer l'événement ?");
        confirmAlert.setContentText("Voulez-vous vraiment supprimer cet événement ?");
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    eventService.supprimer(this.event.getId());
                    parentController.refreshList();
                } catch (SQLException e) {
                    logger.log(Level.SEVERE, "Erreur lors de la suppression de l'événement", e);
                }
            }
        });
    }

    @FXML
    private void detailsevent(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Détails");
        alert.setHeaderText(null);
        alert.setContentText("Détails de l'événement : " + this.event.getNom());
        alert.showAndWait();
    }
}