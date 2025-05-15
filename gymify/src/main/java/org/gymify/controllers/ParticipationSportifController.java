package org.gymify.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
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
    private ListeParticipationController parentController;
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
        nomField.setText(sportif.getNom() != null ? sportif.getNom() : "");
        prenomField.setText(sportif.getPrenom() != null ? sportif.getPrenom() : "");
        emailField.setText(sportif.getEmail() != null ? sportif.getEmail() : "");
    }

    public void setEquipe(Equipe equipe) {
        this.equipe = equipe;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public void setParentController(ListeParticipationController parentController) {
        this.parentController = parentController;
    }

    @FXML
    void confirmerParticipation(ActionEvent event) {
        String nom = nomField.getText().trim();
        String prenom = prenomField.getText().trim();
        String email = emailField.getText().trim();

        ErrorNom.setVisible(false);
        ErrorPrenom.setVisible(false);
        ErrorEmail.setVisible(false);

        if (nom.isEmpty()) {
            ErrorNom.setVisible(true);
            return;
        }
        if (prenom.isEmpty()) {
            ErrorPrenom.setVisible(true);
            return;
        }
        if (email.isEmpty()) {
            ErrorEmail.setVisible(true);
            return;
        }

        try {
            if (equipeService == null) {
                LOGGER.severe("EquipeService non initialisé.");
                showAlert(Alert.AlertType.ERROR, "Erreur", "Service d'équipe non disponible.");
                return;
            }

            User existingUser = userService.getUserByEmail(email);
            if (existingUser != null && !"sportif".equalsIgnoreCase(existingUser.getRole())) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Cet email est associé à un rôle autre que sportif.");
                return;
            }

            if (existingUser != null) {
                existingUser.setNom(nom);
                existingUser.setPrenom(prenom);
                existingUser.setEmail(email);
                sportif = existingUser;
            } else {
                sportif = new User(nom, prenom, email, "default_password", "sportif");
                userService.ajouter(sportif);
                sportif = userService.getUserByEmail(email);
            }

            if (equipeService.isUserInTeamForEvent(sportif.getEmail(), this.event.getId())) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Vous participez déjà à une équipe pour cet événement.");
                return;
            }

            LOGGER.info("Adding sportif " + sportif.getEmail() + " to equipe ID " + equipe.getId());
            equipeService.addSportifToEquipe(sportif, equipe.getId());

            showAlert(Alert.AlertType.INFORMATION, "Succès",
                    "Le sportif " + sportif.getEmail() + " a été ajouté à l'équipe " +
                            equipe.getNom() + " (ID: " + equipe.getId() + ") avec succès !");
            Stage stage = (Stage) btnConfirmer.getScene().getWindow();
            stage.close();

            if (parentController != null) {
                LOGGER.info("Refreshing participation list.");
                parentController.loadParticipations("");
            }

        } catch (SQLException e) {
            LOGGER.severe("Erreur lors de la confirmation: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ajout du sportif: " + e.getMessage());
        }
    }

    @FXML
    void annuler(ActionEvent event) {
        Stage stage = (Stage) btnAnnuler.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}