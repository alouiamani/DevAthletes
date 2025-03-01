package org.gymify.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.gymify.entities.Salle;
import org.gymify.entities.User;
import org.gymify.services.SalleService;
import org.gymify.services.ServiceUser;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class SalleFormAdminController {

    @FXML private TextField nomFX, adresseFX, numtelFX, emailFX, url_photoFX;
    @FXML private TextArea detailsFX;
    @FXML private Label errorNom, errorAdresse, errorNumTel, errorEmail, errorDetails, errorUrlPhoto;
    @FXML private ChoiceBox<String> responsableChoiceBox;

    private Salle salleAModifier;
    private SalleService salleService = new SalleService();
    private ServiceUser userService = new ServiceUser();

    public void chargerSalle(Salle salle) {
        salleAModifier = salle;
        nomFX.setText(salle.getNom());
        adresseFX.setText(salle.getAdresse());
        detailsFX.setText(salle.getDetails());
        numtelFX.setText(salle.getNum_tel());
        emailFX.setText(salle.getEmail());
        url_photoFX.setText(salle.getUrl_photo());
    }

    public void initialize() throws SQLException {
        List<User> responsables = userService.afficher(); // Récupérer la liste des responsables
        for (User responsable : responsables) {
            responsableChoiceBox.getItems().add(responsable.getId_User() + " - " + responsable.getNom());
        }
    }

    @FXML
    private void btnEnregistrer(ActionEvent actionEvent) {
        boolean hasErrors = false;

        // Vérification des champs et gestion des erreurs
        if (nomFX.getText().isEmpty()) {
            errorNom.setText("Nom obligatoire");
            hasErrors = true;
        } else {
            errorNom.setText("");
        }

        if (adresseFX.getText().isEmpty()) {
            errorAdresse.setText("Adresse obligatoire");
            hasErrors = true;
        } else {
            errorAdresse.setText("");
        }

        if (numtelFX.getText().isEmpty()) {
            errorNumTel.setText("Numéro de téléphone obligatoire");
            hasErrors = true;
        } else {
            errorNumTel.setText("");
        }

        if (emailFX.getText().isEmpty()) {
            errorEmail.setText("Email obligatoire");
            hasErrors = true;
        } else {
            errorEmail.setText("");
        }

        if (detailsFX.getText().isEmpty()) {
            errorDetails.setText("Détails obligatoires");
            hasErrors = true;
        } else {
            errorDetails.setText("");
        }

        if (url_photoFX.getText().isEmpty()) {
            errorUrlPhoto.setText("URL photo obligatoire");
            hasErrors = true;
        } else {
            errorUrlPhoto.setText("");
        }

        if (hasErrors) return;

        Salle salle = new Salle(nomFX.getText(), adresseFX.getText(), detailsFX.getText(),
                numtelFX.getText(), emailFX.getText(), url_photoFX.getText());

        try {
            String message;

            if (salleAModifier == null) {
                salleService.ajouter(salle);
                message = "La salle a été ajoutée avec succès !";
            } else {
                salle.setId_Salle(salleAModifier.getId_Salle());
                salleService.modifier(salle);
                message = "La salle a été modifiée avec succès !";
            }

            afficherAlerte("Succès", message);
            chargerListeSalles();

        } catch (SQLException e) {
            e.printStackTrace();
            afficherAlerte("Erreur", "Une erreur est survenue lors de l'enregistrement.");
        }
    }

    private void chargerListeSalles() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListeDesSalleAdmin.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) nomFX.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            afficherAlerte("Erreur", "Impossible de charger la liste des salles.");
        }
    }

    private void afficherAlerte(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void btnAnnuler(ActionEvent actionEvent) {
        viderChamps();
    }

    private void viderChamps() {
        nomFX.clear();
        adresseFX.clear();
        emailFX.clear();
        detailsFX.clear();
        numtelFX.clear();
        url_photoFX.clear();
    }

    @FXML
    private void retournerEnArriere(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListeDesSalleAdmin.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            afficherAlerte("Erreur", "Impossible de retourner à l'interface précédente.");
        }
    }
}
