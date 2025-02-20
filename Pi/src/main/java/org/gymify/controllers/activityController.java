package org.gymify.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.gymify.entities.Activité;
import org.gymify.services.ActivityService;

import java.io.IOException;
import java.util.List;

public class activityController {

    @FXML
    private TextField titleActivity;
    @FXML
    private TextArea description;
    @FXML
    private TextField urlPhoto;
    @FXML
    private RadioButton radioButtonType;
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
    public void initialize() {
        // Initialisation (optionnelle)
        System.out.println("Initializing activityController...");
        if (activityGrid != null) {
            displayActivities();
        } else {
            System.err.println("activityGrid est null. Vérifiez le fichier FXML.");
        }

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
        } else {
            System.err.println("Veuillez saisir une URL valide.");
            imageView.setImage(null); // Efface l'image si l'URL est vide
        }
    }


    @FXML
    private void showUserDashboard() {
        loadPage("/userDashboard.fxml");
    }

    @FXML
    private void showActivityDashboard() {
        loadPage("/addActivity.fxml");
    }

    @FXML
    private void showGymDashboard() {
        loadPage("/GymDashboard.fxml");
    }

    @FXML
    private void subscription(ActionEvent actionEvent) {
    }



    @FXML
    private void logout() {
        System.out.println("Logout clicked!");
    }

    private void loadPage(String page) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(page));
            Pane pane = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(pane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void statistique(ActionEvent actionEvent) {
    }
    @FXML
    private void handleAddActivity(ActionEvent actionEvent) {
        try {
            // Charger le fichier FXML de la popup
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/addActivityForm.fxml"));
            Parent root = loader.load();

            // Créer une nouvelle scène avec la popup
            Scene scene = new Scene(root);

            // Créer un nouveau Stage pour la popup
            Stage popupStage = new Stage();
            popupStage.setTitle("Add Activity");
            popupStage.setScene(scene);

            // Rendre la fenêtre modale (bloque l'accès à la fenêtre principale jusqu'à fermeture)
            popupStage.initModality(Modality.APPLICATION_MODAL);

            // Afficher la popup
            popupStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void addActivity(ActionEvent actionEvent) {
        // Réinitialiser les messages d'erreur
        urlPhotoError.setText("");
        titleActivityError.setText("");
        descriptionError.setText("");
        typeError.setText("");

        // Valider les champs
        boolean isValid = true;

        if (urlPhoto.getText() == null || urlPhoto.getText().isEmpty()) {
            urlPhotoError.setText("URL Photo is required.");
            isValid = false;
        }

        if (titleActivity.getText() == null || titleActivity.getText().isEmpty()) {
            titleActivityError.setText("Title is required.");
            isValid = false;
        }

        if (description.getText() == null || description.getText().isEmpty()) {
            descriptionError.setText("Description is required.");
            isValid = false;
        }

        if (!radio1.isSelected() && !radio2.isSelected() && !radio3.isSelected()) {
            typeError.setText("Please select an activity type.");
            isValid = false;
        }

        // Si tous les champs sont valides, procéder à l'ajout de l'activité
        if (isValid) {
            // Récupérer les valeurs des champs
            String nom = titleActivity.getText();
            String descriptionText = description.getText();
            String url = urlPhoto.getText();

            // Obtenir le type d'activité sélectionné
            String type = "";
            if (radio1.isSelected()) {
                type = "Personal Training";
            } else if (radio2.isSelected()) {
                type = "Group Activity";
            } else if (radio3.isSelected()) {
                type = "Fitness Consultation";
            }

            // Créer l'objet Activité
            Activité activité = new Activité(nom, type, descriptionText, url);

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
    private void displayActivities() {
        ActivityService activityService = new ActivityService();
        List<Activité> activités = activityService.Display();
        activityGrid.getChildren().clear();

        int row = 0;
        int col = 0;

        for (Activité activité : activités) {
            VBox vbox = new VBox(10);
            vbox.setPadding(new Insets(10));
            vbox.setStyle("-fx-border-color: #9fb1c5; -fx-border-radius: 5px; -fx-background-color: #f9f9f9;");

            if (activité.getUrl() != null && !activité.getUrl().isEmpty()) {
                try {
                    Image image = new Image(activité.getUrl());
                    ImageView imageView = new ImageView(image);
                    imageView.setFitWidth(100);
                    imageView.setPreserveRatio(true);
                    vbox.getChildren().add(imageView);
                } catch (Exception e) {
                    System.err.println("Erreur lors du chargement de l'image : " + e.getMessage());
                }
            }

            vbox.getChildren().addAll(
                    new Label("Nom: " + activité.getNom()),
                    new Label("Type: " + activité.getType()),
                    new Label("Description: " + activité.getDescription())
            );

            HBox buttonBox = new HBox(10);

            // Ajouter une icône pour le bouton "Supprimer"
            ImageView deleteIcon = new ImageView(new Image(getClass().getResourceAsStream("/images/supprimer.png")));
            deleteIcon.setFitWidth(16);
            deleteIcon.setFitHeight(16);
            Button deleteButton = new Button("", deleteIcon);
            deleteButton.setStyle("-fx-background-color: #eceff1; -fx-text-fill: white;");

            // Ajouter une icône pour le bouton "Modifier"
            ImageView editIcon = new ImageView(new Image(getClass().getResourceAsStream("/images/editer.png")));
            editIcon.setFitWidth(16);
            editIcon.setFitHeight(16);
            Button editButton = new Button("", editIcon);
            editButton.setStyle("-fx-background-color: #eceff1; -fx-text-fill: white;");
            deleteButton.setOnAction(event -> {
                activityService.Delete(activité);  // Supprimer l'activité
                displayActivities();  // Rafraîchir l'affichage
            });
            editButton.setOnAction(event -> {
                openEditActivityPopup(activité); // Ouvrir la popup d'édition avec les informations de l'activité
                displayActivities(); // Rafraîchir l'affichage après la modification
            });



            buttonBox.getChildren().addAll(deleteButton, editButton);
            vbox.getChildren().add(buttonBox);
            activityGrid.add(vbox, col, row);

            col++;
            if (col == 3) {
                col = 0;
                row++;
            }
        }
    }
    @FXML
    private void openEditActivityPopup(Activité activité) {
        try {
            // Charger le fichier FXML de la popup
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/editActivity.fxml"));
            Parent root = loader.load();

            // Obtenir le contrôleur de la popup
            activityController controller = loader.getController();
            controller.setActivityData(activité); // Pré-remplir les champs

            // Créer une nouvelle scène avec la popup
            Scene scene = new Scene(root);

            // Créer un nouveau Stage pour la popup
            Stage popupStage = new Stage();
            popupStage.setTitle("Edit Activity");
            popupStage.setScene(scene);

            // Rendre la fenêtre modale (bloque l'accès à la fenêtre principale jusqu'à fermeture)
            popupStage.initModality(Modality.APPLICATION_MODAL);

            // Afficher la popup
            popupStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
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
            case "Personal Training":
                radio1.setSelected(true);
                break;
            case "Group Activity":
                radio2.setSelected(true);
                break;
            case "Fitness Consultation":
                radio3.setSelected(true);
                break;
        }

        // Charger l'image si l'URL est valide
        loadImage();
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

        // Vérifier si un type a été sélectionné
        String type = "";
        if (radio1.isSelected()) {
            type = "Personal Training";
        } else if (radio2.isSelected()) {
            type = "Group Activity";
        } else if (radio3.isSelected()) {
            type = "Fitness Consultation";
        }

        // Créer une nouvelle instance de l'activité avec les nouvelles données
        Activité activité = new Activité(selectedActivity.getId(), nom, type, descriptionText, url);  // Utiliser l'ID de l'activité existante

        // Appeler le service pour mettre à jour l'activité
        ActivityService activityService = new ActivityService();
        activityService.Update(activité);

        // Fermer la fenêtre après la mise à jour
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.close();

        // Rafraîchir l'affichage des activités après la modification
        displayActivities();
    }


}
