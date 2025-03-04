package org.gymify.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.gymify.entities.Equipe;
import org.gymify.entities.Event;

import java.io.IOException;

/**
 * Controller class for displaying an individual participation card in the UI.
 */
public class CardParticipationController {

    @FXML
    private Button btnParticiper;

    @FXML
    private Label equipeName;

    @FXML
    private Label eventDate;

    @FXML
    private Label eventDescription;

    @FXML
    private Label eventLocation;

    @FXML
    private Label eventName;

    @FXML
    private Label eventTimeDebut;

    @FXML
    private Label eventTimeFin;

    @FXML
    private Label eventType; // Added field for event type

    private Event event;
    private Equipe equipe;

    // Method to set the data for the participation card
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
        eventType.setText(event.getType() != null ? event.getType().name() : "Non défini"); // Set the event type

        // Set action for the Participer button
        btnParticiper.setOnAction(this::openParticipationSportif);
    }

    @FXML
    private void openParticipationSportif(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ParticipationSportif.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Participation Sportif");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}