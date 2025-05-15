package org.gymify.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.gymify.entities.Equipe;
import org.gymify.entities.EventType;
import org.gymify.services.EquipeService;

import java.io.File;
import java.sql.SQLException;

public class AjoutEquipeController {

    @FXML private TextField nameField, imageurl;
    @FXML private Spinner<Integer> membreSpinner;
    @FXML private ComboBox<EventType.Niveau> niveauequipe;
    @FXML private ImageView imagetf;
    @FXML private Label ErrorNom, ErrorNiveau, ErrorNombre, ErrorImage;

    private String imagePath = "";
    private final EquipeService equipeService;
    private Equipe equipeAModifier = null;
    private ListeDesEquipesController listeDesEquipesController;
    private AjoutEvennementController ajoutEvennementController;

    public AjoutEquipeController() throws SQLException {
        this.equipeService = new EquipeService();
    }

    @FXML
    public void initialize() {
        niveauequipe.getItems().setAll(EventType.Niveau.values());
        membreSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 8, 0));
        resetErrors();
    }

    @FXML
    void handleUploadImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"));
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            imagePath = file.getAbsolutePath();
            imageurl.setText(imagePath);
            imagetf.setImage(new Image(file.toURI().toString()));
            ErrorImage.setVisible(false);
        }
    }

    @FXML
    void handleSaveButton() {
        resetErrors();
        String nom = nameField.getText().trim();
        EventType.Niveau niveau = niveauequipe.getValue();
        Integer nombreMembres = membreSpinner.getValue();
        boolean hasError = false;

        if (nom.isEmpty()) {
            ErrorNom.setText("Le nom ne peut pas être vide !");
            ErrorNom.setVisible(true);
            hasError = true;
        }
        if (niveau == null) {
            ErrorNiveau.setText("Veuillez sélectionner un niveau !");
            ErrorNiveau.setVisible(true);
            hasError = true;
        }
        if (imageurl.getText().trim().isEmpty()) {
            ErrorImage.setText("Veuillez sélectionner une image !");
            ErrorImage.setVisible(true);
            hasError = true;
        }
        if (nombreMembres > 8) {
            ErrorNombre.setText("Le nombre de membres ne peut pas dépasser 8 !");
            ErrorNombre.setVisible(true);
            hasError = true;
        }

        if (hasError) return;

        try {
            if (equipeAModifier == null && equipeService.isNomEquipeExist(nom)) {
                ErrorNom.setText("Le nom de l'équipe existe déjà !");
                ErrorNom.setVisible(true);
                return;
            }

            Equipe equipe = new Equipe(nom, imagePath, niveau, nombreMembres);
            if (equipeAModifier == null) {
                equipeService.ajouter(equipe);
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Équipe ajoutée avec succès !");

                if (ajoutEvennementController != null) {
                    ajoutEvennementController.addEquipeToEvent(equipe);
                }

                Stage stage = (Stage) nameField.getScene().getWindow();
                stage.close();
            } else {
                equipe.setId(equipeAModifier.getId());
                equipeService.modifier(equipe);
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Équipe modifiée avec succès !");

                if (ajoutEvennementController != null) {
                    ajoutEvennementController.updateEquipeInList(equipeAModifier, equipe);
                }

                Stage stage = (Stage) nameField.getScene().getWindow();
                stage.close();
            }

            updateListeDesEquipes();
            clearFields();

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Problème lors de l'ajout/modification : " + e.getMessage());
        }
    }

    private void updateListeDesEquipes() {
        if (listeDesEquipesController != null) {
            listeDesEquipesController.rafraichirListe();
        }
    }

    public void setListeDesEquipesController(ListeDesEquipesController controller) {
        this.listeDesEquipesController = controller;
    }

    public void setAjoutEvennementController(AjoutEvennementController controller) {
        this.ajoutEvennementController = controller;
    }

    private void resetErrors() {
        ErrorNom.setVisible(false);
        ErrorNiveau.setVisible(false);
        ErrorNombre.setVisible(false);
        ErrorImage.setVisible(false);
    }

    private void clearFields() {
        nameField.clear();
        niveauequipe.setValue(null);
        membreSpinner.getValueFactory().setValue(0);
        imageurl.clear();
        imagetf.setImage(null);
        equipeAModifier = null;
        resetErrors();
    }

    @FXML
    void handleCancelButton() {
        clearFields();
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void handleModifier(Equipe equipe) {
        this.equipeAModifier = equipe;
        nameField.setText(equipe.getNom());
        niveauequipe.setValue(equipe.getNiveau());
        membreSpinner.getValueFactory().setValue(equipe.getNombreMembres());
        imagePath = equipe.getImageUrl();
        imageurl.setText(imagePath);
        if (imagePath != null && !imagePath.isEmpty()) {
            imagetf.setImage(new Image(new File(imagePath).toURI().toString()));
        }
    }
}