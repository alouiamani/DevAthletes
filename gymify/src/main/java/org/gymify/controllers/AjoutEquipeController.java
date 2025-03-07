/*package org.gymify.controllers;

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

/**
 * Controller class for managing the addition or modification of teams (equipes) in the UI.

public class AjoutEquipeController {

    @FXML
    private TextField nameField, imageurl;
    @FXML
    private Spinner<Integer> membreSpinner;
    @FXML
    private ComboBox<EventType.Niveau> niveauequipe;
    @FXML
    private ImageView imagetf;
    @FXML
    private Label ErrorNom, ErrorNiveau, ErrorNombre, ErrorImage;

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
        membreSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 6));
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

        if (hasError) return;

        try {
            // Si on est en mode ajout et que le nom existe déjà
            if (equipeAModifier == null && equipeService.isNomEquipeExist(nom)) {
                ErrorNom.setText("Le nom de l'équipe existe déjà !");
                ErrorNom.setVisible(true);
                return;
            }

            Equipe equipe = new Equipe(nom, imagePath, niveau, nombreMembres);
            if (equipeAModifier == null) {
                equipeService.ajouter(equipe); // Ajouter l'équipe à la base de données
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Équipe ajoutée avec succès !");

                // Notify AjoutEvennementController if it exists
                if (ajoutEvennementController != null) {
                    ajoutEvennementController.addEquipeToEvent(equipe);
                }

                // Close the window
                Stage stage = (Stage) nameField.getScene().getWindow();
                stage.close();
            } else {
                // Mode modification: conserver l'identifiant
                equipe.setId(equipeAModifier.getId());
                equipeService.modifier(equipe); // Modifier l'équipe dans la base de données
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Équipe modifiée avec succès !");

                // Update AjoutEvennementController if necessary
                if (ajoutEvennementController != null) {
                    ajoutEvennementController.updateEquipeInList(equipeAModifier, equipe);
                }

                // Close the window
                Stage stage = (Stage) nameField.getScene().getWindow();
                stage.close();
            }

            updateListeDesEquipes(); // Rafraîchir la liste si possible
            clearFields();

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Problème lors de l'ajout/modification : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateListeDesEquipes() {
        if (listeDesEquipesController != null) {
            listeDesEquipesController.rafraichirListe();
        }
    }

    // Méthode pour permettre d'injecter le contrôleur ListeDesEquipesController
    public void setListeDesEquipesController(ListeDesEquipesController controller) {
        this.listeDesEquipesController = controller;
    }

    // Méthode pour permettre d'injecter le contrôleur AjoutEvennementController
    public void setAjoutEvennementController(AjoutEvennementController controller) {
        this.ajoutEvennementController = controller;
    }

    private void resetErrors() {
        ErrorNom.setVisible(false);
        ErrorNiveau.setVisible(false);
        ErrorImage.setVisible(false);
    }

    private void clearFields() {
        nameField.clear();
        niveauequipe.setValue(null);
        membreSpinner.getValueFactory().setValue(6);
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
        if (type == Alert.AlertType.ERROR) {
            System.err.println(content);
        }
    }

 /*   // Méthode appelée pour passer l'équipe à modifier
    public void handleModifier(Equipe equipe) {
        this.equipeAModifier = equipe;
        nameField.setText(equipe.getNom());
        niveauequipe.setValue(equipe.getNiveau());
        membreSpinner.getValueFactory().setValue(equipe.getNombreMembres());
        imagePath = equipe.getImageUrl();
        imageurl.setText(imagePath);
        if (imagePath != null && !imagePath.isEmpty()) {
            try {
                imagetf.setImage(new Image(new File(imagePath).toURI().toString()));
            } catch (Exception e) {
                imagetf.setImage(null);
            }
        }
   }
}
*/