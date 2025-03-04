package org.gymify.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Controller class for handling the ParticipationSportif form UI.
 */
public class ParticipationSportifController {

    @FXML
    private Button btnAnnuler;

    @FXML
    private Button btnConfirmer;

    @FXML
    private TextField emailField;

    @FXML
    private TextField nomField;

    @FXML
    private TextField prenomField;

    @FXML
    private Label ErrorNom;

    @FXML
    private Label ErrorPrenom;

    @FXML
    private Label ErrorEmail;

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

        // If there are errors, stop processing
        if (hasError) {
            return;
        }

        // Show success message
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Participation réussie pour " + nom + " " + prenom + "!", ButtonType.OK);
        alert.showAndWait();

        // Close the window
        Stage stage = (Stage) btnConfirmer.getScene().getWindow();
        stage.close();
    }

    @FXML
    void annuler(ActionEvent event) {
        // Clear the form fields
        nomField.clear();
        prenomField.clear();
        emailField.clear();

        // Hide error messages
        ErrorNom.setVisible(false);
        ErrorPrenom.setVisible(false);
        ErrorEmail.setVisible(false);

        // Close the window to return to the previous interface
        Stage stage = (Stage) btnAnnuler.getScene().getWindow();
        stage.close();
    }
}