package org.gymify.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.control.ToggleGroup;
import org.gymify.entities.ActivityType;
import org.gymify.entities.Activité;
import org.gymify.services.ActivityService;

import java.io.File;
import java.util.List;

public class EditActivityController {
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
    private GridPane activityGrid;
    private Activité selectedActivity;
    @FXML
    private TextField searchbar;
    private List<Activité> allActivities;

    private File selectedFile; // Variable pour stocker l'image sélectionnée localement

    // Création du ToggleGroup pour gérer la sélection unique des boutons radio
    private ToggleGroup activityTypeGroup;

    @FXML
    private void initialize() {
        // Initialisation du ToggleGroup pour les boutons radio
        activityTypeGroup = new ToggleGroup();
        radio1.setToggleGroup(activityTypeGroup);
        radio2.setToggleGroup(activityTypeGroup);
        radio3.setToggleGroup(activityTypeGroup);
    }

    @FXML
    private void editActivity(ActionEvent actionEvent) {
        // Vérifier si une activité a été sélectionnée
        if (selectedActivity == null) {
            System.err.println("Aucune activité sélectionnée.");
            return;
        }

        // Récupérer les valeurs modifiées dans les champs
        String nom = titleActivity.getText();
        String descriptionText = description.getText();
        String url = urlPhoto.getText();

        // Vérifier si un type a été sélectionné et récupérer le type
        ActivityType type = null;
        if (radio1.isSelected()) {
            type = ActivityType.PERSONAL_TRAINING;
        } else if (radio2.isSelected()) {
            type = ActivityType.GROUP_ACTIVITY;
        } else if (radio3.isSelected()) {
            type = ActivityType.FITNESS_CONSULTATION;
        }

        // Si un fichier a été sélectionné, utiliser le chemin local du fichier
        if (selectedFile != null) {
            url = selectedFile.toURI().toString(); // Mettre à jour l'URL avec le fichier sélectionné
        }

        // Créer une nouvelle instance de l'activité avec les nouvelles données
        Activité activité = new Activité(selectedActivity.getId(), nom, type, descriptionText, url); // Utiliser l'ID de l'activité existante

        // Appeler le service pour mettre à jour l'activité
        ActivityService activityService = new ActivityService();
        activityService.Update(activité);

        // Fermer la fenêtre après la mise à jour
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    private void loadImage() {
        String imageUrl = urlPhoto.getText(); // Récupère l'URL saisie par l'utilisateur
        if (imageUrl != null && !imageUrl.isEmpty()) {
            try {
                // Charge l'image depuis l'URL
                Image image = new Image(imageUrl);
                imageView.setImage(image); // Affiche l'image dans l'ImageView
            } catch (Exception e) {
                System.err.println("Erreur lors du chargement de l'image : " + e.getMessage());
                imageView.setImage(null); // Efface l'image en cas d'erreur
            }
        } else if (selectedFile != null) {
            // Si un fichier a été sélectionné, l'afficher dans l'ImageView
            imageView.setImage(new Image(selectedFile.toURI().toString()));
        } else {
            System.err.println("Veuillez saisir une URL valide ou importer une image.");
            imageView.setImage(null); // Efface l'image si aucune URL ou fichier n'est fourni
        }
    }

    @FXML
    private void uploadImage(ActionEvent event) {
        // Utiliser un FileChooser pour sélectionner une image depuis le PC
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"));

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            // Si un fichier a été sélectionné, afficher l'image dans l'ImageView et mettre à jour l'URL
            imageView.setImage(new Image(selectedFile.toURI().toString()));
            urlPhoto.setText(""); // Effacer l'URL si une image locale est choisie
        }
    }

    public void setActivityData(Activité activité) {
        selectedActivity = activité;
        titleActivity.setText(activité.getNom());
        description.setText(activité.getDescription());
        urlPhoto.setText(activité.getUrl());
        selectedActivity = activité;

        // Sélectionner le bon RadioButton en fonction du type d'activité
        switch (activité.getType()) {
            case PERSONAL_TRAINING:
                radio1.setSelected(true);
                break;
            case GROUP_ACTIVITY:
                radio2.setSelected(true);
                break;
            case FITNESS_CONSULTATION:
                radio3.setSelected(true);
                break;
        }

        // Charger l'image si l'URL est valide
        loadImage();
    }
}
