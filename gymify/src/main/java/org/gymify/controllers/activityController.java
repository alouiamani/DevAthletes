package org.gymify.controllers;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.gymify.entities.ActivityType;
import org.gymify.entities.Activité;
import org.gymify.services.ActivityService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class activityController {

    @FXML
    private GridPane activityGrid;
    @FXML
    private TextField searchbar;
    @FXML
    private CheckBox personalTrainingCheckBox;
    @FXML
    private CheckBox groupActivityCheckBox;
    @FXML
    private CheckBox fitnessConsultationCheckBox;
    private List<Activité> allActivities;

    @FXML
    public void initialize() {
        ActivityService activityService = new ActivityService();
        allActivities = activityService.Display(); // Charger toutes les activités

        // Écouter les changements dans la barre de recherche
        searchbar.textProperty().addListener((observable, oldValue, newValue) -> {
            filterActivities(newValue);
        });

        // Écouter les changements dans les CheckBox
        personalTrainingCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            filterActivities(searchbar.getText());
        });
        groupActivityCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            filterActivities(searchbar.getText());
        });
        fitnessConsultationCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            filterActivities(searchbar.getText());
        });

        // Afficher toutes les activités initialement
        displayActivities(allActivities);
    }

    private void filterActivities(String searchQuery) {
        List<ActivityType> selectedTypes = new ArrayList<>();

        // Récupérer les types sélectionnés
        if (personalTrainingCheckBox.isSelected()) {
            selectedTypes.add(ActivityType.PERSONAL_TRAINING);
        }
        if (groupActivityCheckBox.isSelected()) {
            selectedTypes.add(ActivityType.GROUP_ACTIVITY);
        }
        if (fitnessConsultationCheckBox.isSelected()) {
            selectedTypes.add(ActivityType.FITNESS_CONSULTATION);
        }

        // Si aucun type n'est sélectionné, afficher toutes les activités
        if (selectedTypes.isEmpty()) {
            selectedTypes.addAll(Arrays.asList(ActivityType.values()));
        }

        // Filtrer les activités
        List<Activité> filteredActivities = allActivities.stream()
                .filter(activity -> activity.getNom().toLowerCase().contains(searchQuery.toLowerCase()) ||
                        activity.getDescription().toLowerCase().contains(searchQuery.toLowerCase()))
                .filter(activity -> selectedTypes.contains(activity.getType())) // Filtrer par type
                .collect(Collectors.toList());

        // Afficher les activités filtrées
        displayActivities(filteredActivities);
    }

    private void displayActivities(List<Activité> activities) {
        activityGrid.getChildren().clear(); // Vider la grille

        int row = 0;
        int col = 0;

        for (Activité activity : activities) {
            VBox vbox = new VBox(10);
            vbox.setPadding(new Insets(15));
            vbox.setSpacing(10);
            vbox.setAlignment(Pos.CENTER);
            vbox.setStyle("-fx-border-color: #cfd8dc; -fx-border-radius: 10px; -fx-background-color: white; -fx-background-radius: 10px;");
            if (activity.getUrl() != null && !activity.getUrl().isEmpty()) {
                Task<Image> imageLoadTask = new Task<>() {
                    @Override
                    protected Image call() {
                        return new Image(activity.getUrl());
                    }
                };

                imageLoadTask.setOnSucceeded(event -> {
                    ImageView imageView = new ImageView(imageLoadTask.getValue());
                    imageView.setFitWidth(100); // Ajuster la largeur de l'image
                    imageView.setPreserveRatio(true);
                    imageView.setStyle("-fx-border-radius: 10px; -fx-background-radius: 10px;");
                    vbox.getChildren().add(0, imageView); // Ajouter l'image en premier
                });

                new Thread(imageLoadTask).start();
            }



            // Ajouter les détails de l'activité
            Label nameLabel = new Label("Nom : " + activity.getNom());
            Label typeLabel = new Label("Type : " + activity.getType().getLabel());
            Label descriptionLabel = new Label("Description : " + activity.getDescription());

            nameLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #37474f;");
            typeLabel.setStyle("-fx-text-fill: #546e7a;");
            descriptionLabel.setWrapText(true);
            descriptionLabel.setMaxWidth(180);
            descriptionLabel.setStyle("-fx-text-fill: #546e7a;");

            vbox.getChildren().addAll(nameLabel, typeLabel, descriptionLabel);
            // Boutons Supprimer et Modifier
            HBox buttonBox = new HBox(10);
            buttonBox.setAlignment(Pos.CENTER);

            Button deleteButton = new Button("Supprimer");
            deleteButton.setStyle("-fx-background-color: #78909c; -fx-text-fill: white; -fx-border-radius: 5px; -fx-background-radius: 5px;");
            deleteButton.setOnAction(event -> {
                ActivityService activityService = new ActivityService();
                activityService.Delete(activity);
                allActivities.remove(activity); // Retirer l'activité de la liste en cache
                filterActivities(searchbar.getText()); // Refiltrer les activités
            });
            Button editButton = new Button("Modifier");
            editButton.setStyle("-fx-background-color: #78909c; -fx-text-fill: white; -fx-border-radius: 5px; -fx-background-radius: 5px;");
            editButton.setOnAction(event -> openEditActivityPopup(activity));

            buttonBox.getChildren().addAll(deleteButton, editButton);
            vbox.getChildren().add(buttonBox);

            // Ajouter à la grille
            activityGrid.add(vbox, col, row);
            col++;
            if (col == 3) {
                col = 0;
                row++;
            }
        }
    }


    @FXML
    private void handleAddActivity(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/addActivityForm.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage popupStage = new Stage();
            popupStage.setTitle("Add Activity");
            popupStage.setScene(scene);
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    @FXML
    private void openEditActivityPopup(Activité activité) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/editActivity.fxml"));
            Parent root = loader.load();

            EditActivityController controller = loader.getController();
            controller.setActivityData(activité);

            Scene scene = new Scene(root);
            Stage popupStage = new Stage();
            popupStage.setTitle("Edit Activity");
            popupStage.setScene(scene);
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void backToPage(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AdminDash.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showUserDashboard(ActionEvent actionEvent) {
    }

    public void showActivityDashboard(ActionEvent actionEvent) {
    }

    public void showGymDashboard(ActionEvent actionEvent) {
    }

    public void subscription(ActionEvent actionEvent) {
    }

    public void statistique(ActionEvent actionEvent) {
    }

    public void logout(ActionEvent actionEvent) {

    }
}