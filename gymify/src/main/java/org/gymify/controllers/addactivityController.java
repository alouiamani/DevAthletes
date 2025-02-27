package org.gymify.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.gymify.entities.ActivityType;
import org.gymify.entities.Activité;
import org.gymify.services.ActivityService;
import java.io.File;

public class addactivityController {
    @FXML
    private TextField titleActivity;
    @FXML
    private TextArea description;
    @FXML
    private TextField urlPhoto;
    @FXML
    private StackPane contentArea;
    @FXML
    private RadioButton radio1;
    @FXML
    private RadioButton radio2;
    @FXML
    private RadioButton radio3;
    @FXML
    private ImageView imageView;
    @FXML
    private Label urlPhotoError;
    @FXML
    private Label titleActivityError;
    @FXML
    private Label descriptionError;
    @FXML
    private Label typeError;
    @FXML
    private Button uploadButton;

    private ToggleGroup activityTypeGroup;
    private File selectedFile; // Fichier image sélectionné

    @FXML
    public void initialize() {
        // Initialisation du ToggleGroup pour assurer une seule sélection
        activityTypeGroup = new ToggleGroup();
        radio1.setToggleGroup(activityTypeGroup);
        radio2.setToggleGroup(activityTypeGroup);
        radio3.setToggleGroup(activityTypeGroup);
    }

    @FXML
    public void addActivity(ActionEvent actionEvent) {
        // Réinitialiser les messages d'erreur
        urlPhotoError.setText("");
        titleActivityError.setText("");
        descriptionError.setText("");
        typeError.setText("");

        boolean isValid = true;
        String imagePath = null;

        if ((urlPhoto.getText() == null || urlPhoto.getText().isEmpty()) && selectedFile == null) {
            urlPhotoError.setText("Please provide an image (URL or upload).");
            isValid = false;
        } else {
            // Déterminer le chemin de l'image
            if (selectedFile != null) {
                imagePath = selectedFile.toURI().toString(); // Convertir le fichier en URI
            } else {
                imagePath = urlPhoto.getText();
            }
        }

        if (titleActivity.getText() == null || titleActivity.getText().isEmpty()) {
            titleActivityError.setText("Title is required.");
            isValid = false;
        }

        if (description.getText() == null || description.getText().isEmpty()) {
            descriptionError.setText("Description is required.");
            isValid = false;
        }

        RadioButton selectedRadio = (RadioButton) activityTypeGroup.getSelectedToggle();
        ActivityType type = null;

        if (selectedRadio != null) {
            if (selectedRadio == radio1) {
                type = ActivityType.PERSONAL_TRAINING;
            } else if (selectedRadio == radio2) {
                type = ActivityType.GROUP_ACTIVITY;
            } else if (selectedRadio == radio3) {
                type = ActivityType.FITNESS_CONSULTATION;
            }
        } else {
            typeError.setText("Please select an activity type.");
            isValid = false;
        }

        if (isValid) {
            String nom = titleActivity.getText();
            String descriptionText = description.getText();

            // Créer l'objet Activité
            Activité activité = new Activité(nom, type, descriptionText, imagePath);

            // Appeler le service pour ajouter l'activité
            ActivityService activityService = new ActivityService();
            activityService.Add(activité);

            // Fermer la fenêtre après l'ajout
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.close();
        } else {
            System.out.println("Validation failed. Please fill all required fields.");
        }
    }

    @FXML
    private void loadImage() {
        String imageUrl = urlPhoto.getText();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            try {
                Image image = new Image(imageUrl);
                imageView.setImage(image);
            } catch (Exception e) {
                System.err.println("Error loading image: " + e.getMessage());
                imageView.setImage(null);
            }
        } else {
            System.err.println("Please enter a valid image URL.");
            imageView.setImage(null);
        }
    }

    @FXML
    private void uploadImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"));

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            imageView.setImage(new Image(selectedFile.toURI().toString()));
            urlPhoto.setText(""); // Effacer l'URL si une image locale est choisie
        }
    }
}
