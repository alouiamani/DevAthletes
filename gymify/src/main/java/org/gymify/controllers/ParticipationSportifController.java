package org.gymify.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.gymify.entities.Equipe;
import org.gymify.entities.Event;
import org.gymify.entities.User;
import org.gymify.services.EquipeService;
import org.gymify.services.ServiceUser;

import java.sql.SQLException;
import java.util.logging.Logger;

public class ParticipationSportifController {

    @FXML private Button btnAnnuler;
    @FXML private Button btnConfirmer;
    @FXML private TextField emailField;
    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private Label ErrorNom;
    @FXML private Label ErrorPrenom;
    @FXML private Label ErrorEmail;

    private User sportif;
    private Equipe equipe;
    private Event event;
    private ServiceUser userService;
    private EquipeService equipeService;
    private static final Logger LOGGER = Logger.getLogger(ParticipationSportifController.class.getName());

    public ParticipationSportifController() {
        userService = new ServiceUser();
        try {
            equipeService = new EquipeService();
        } catch (SQLException e) {
            LOGGER.severe("Erreur lors de l'initialisation d'EquipeService: " + e.getMessage());
        }
    }

    public void setSportif(User sportif) {
        this.sportif = sportif;
        nomField.setText(sportif.getNom());
        prenomField.setText(sportif.getPrenom());
        emailField.setText(sportif.getEmail());
    }

    public void setEquipe(Equipe equipe) {
        this.equipe = equipe;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    @FXML
    void confirmerParticipation(ActionEvent event) {
        String nom = nomField.getText().trim();
        String prenom = prenomField.getText().trim();
        String email = emailField.getText().trim();

        // Reset error visibility
        ErrorNom.setVisible(false);
        ErrorPrenom.setVisible(false);
        ErrorEmail.setVisible(false);

        // Validate fields
        boolean hasError = false;
        if (nom.isEmpty()) {
            ErrorNom.setVisible(true);
            hasError = true;
        }
        if (prenom.isEmpty()) {
            ErrorPrenom.setVisible(true);
            hasError = true;
        }
        if (email.isEmpty()) {
            ErrorEmail.setVisible(true);
            hasError = true;
        }
        if (hasError) {
            return;
        }

        try {
            // Update the Sportif's details
            sportif.setNom(nom);
            sportif.setPrenom(prenom);
            sportif.setEmail(email);

            // Add the Sportif to the Equipe
            equipeService.addSportifToEquipe(sportif, equipe.getId());

            // Show success message
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Participation réussie pour " + nom + " " + prenom + " dans l'équipe " + equipe.getNom() + "!", ButtonType.OK);
            alert.showAndWait();

            // Close the window
            Stage stage = (Stage) btnConfirmer.getScene().getWindow();
            stage.close();

        } catch (SQLException e) {
            LOGGER.severe("Erreur lors de la confirmation de la participation: " + e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur: " + e.getMessage(), ButtonType.OK);
            alert.showAndWait();
        }
    }

    @FXML
    void annuler(ActionEvent event) {
        nomField.clear();
        prenomField.clear();
        emailField.clear();
        ErrorNom.setVisible(false);
        ErrorPrenom.setVisible(false);
        ErrorEmail.setVisible(false);
        Stage stage = (Stage) btnAnnuler.getScene().getWindow();
        stage.close();
    }
}