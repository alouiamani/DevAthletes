package org.gymify.controllers;

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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.gymify.entities.Planning;
import org.gymify.services.PlanningService;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

public class planningController {
    private Planning selectedPlanning;
    @FXML
    public StackPane contentArea;
    @FXML
    private TextField titlePlanning;
    @FXML
    private TextArea description;
    @FXML
    private DatePicker dateDebut;
    @FXML
    private DatePicker dateFin;
    @FXML
    private Label titlePlanningError;
    @FXML
    private Label descriptionPlanningError;
    @FXML
    private Label dateError;
    @FXML
    private GridPane planningGrid;


    @FXML
    private PlanningService planningService;

    public planningController() {
        planningService = new PlanningService();  // Assurez-vous que le service est initialisé
    }



    @FXML
    public void showAddPlanningPage(ActionEvent actionEvent) {
        try {
            // Charger le fichier FXML de la popup
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/addPlanning.fxml"));
            Parent root = loader.load();

            // Créer une nouvelle scène avec la popup
            Scene scene = new Scene(root);

            // Créer un nouveau Stage pour la popup
            Stage popupStage = new Stage();
            popupStage.setTitle("Add Planning");
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
    private void addPlanning(ActionEvent actionEvent) {
        // Récupérer les données depuis les champs de texte
        String title = titlePlanning.getText();
        String desc = description.getText();
        LocalDate startDate = dateDebut.getValue();
        LocalDate endDate = dateFin.getValue();

        // Réinitialiser les erreurs avant de valider
        titlePlanningError.setText("");
        descriptionPlanningError.setText("");
        dateError.setText("");

        // Validation des champs (si nécessaire)
        boolean isValid = true;

        if (title.isEmpty()) {
            titlePlanningError.setText("Title is required");
            isValid = false;
        }
        if (desc.isEmpty()) {
            descriptionPlanningError.setText("Description is required");
            isValid = false;
        }
        if (startDate == null || endDate == null) {
            dateError.setText("Dates are required");
            isValid = false;
        }

        // Vérifier que la date de début est avant la date de fin
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            dateError.setText("Start date cannot be after end date");
            isValid = false;
        }

        // Si tous les champs sont valides, procéder à l'ajout
        if (isValid) {
            // Créer un objet Planning
            Planning planning = new Planning();
            planning.setTitle(title);
            planning.setDescription(desc);
            planning.setdateDebut(Date.valueOf(startDate));
            planning.setDateFin(Date.valueOf(endDate));

            // Ajouter le planning dans la base de données
            planningService.Add(planning);

            // Fermer la fenêtre (popup) après ajout
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.close();
            displayPlanning();

            // Afficher un message de succès
            System.out.println("Planning ajouté avec succès.");
        }
    }

    @FXML
    public void initialize() {
        if (planningGrid == null) {
            System.err.println("planningGrid is null!");
        } else {
            System.out.println("planningGrid is initialized.");
            displayPlanning();
        }
         // Appel de la méthode pour afficher les activités
    }
    private void displayPlanning() {
        PlanningService planningService = new PlanningService();
        List<Planning> plannings = planningService.Display();
        planningGrid.getChildren().clear();

        int row = 0;
        int col = 0;

        for (Planning planning : plannings) {
            VBox vbox = new VBox(10);
            vbox.setPadding(new Insets(10));
            vbox.setStyle("-fx-border-color: #9fb1c5; -fx-border-radius: 5px; -fx-background-color: #f9f9f9;");
            vbox.setAlignment(Pos.CENTER);
            GridPane.setMargin(vbox, new Insets(10));
            ImageView planningImage = new ImageView(new Image(getClass().getResource("/images/calendar.png").toExternalForm()));
            planningImage.setFitWidth(30);
            planningImage.setFitHeight(30);
            vbox.getChildren().add(planningImage);

            vbox.getChildren().addAll(
                    new Label( planning.getTitle())



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
                planningService.Delete(planning);  // Supprimer l'activité
                displayPlanning();  // Rafraîchir l'affichage
            });
            editButton.setOnAction(event -> {
                openEditActivityPopup(planning); // Ouvrir la popup d'édition avec les informations de l'activité
                displayPlanning(); // Rafraîchir l'affichage après la modification
            });

            buttonBox.getChildren().addAll(deleteButton, editButton);
            vbox.getChildren().add(buttonBox);
            vbox.setOnMouseClicked(event -> openCoursesPage(planning));
            planningGrid.add(vbox, col, row);

            col++;
            if (col == 3) {
                col = 0;
                row++;
            }
        }

    }
    @FXML
    private void openEditActivityPopup(Planning planning) {
        try {
            // Charger le fichier FXML de la popup
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/editPlanning.fxml"));
            Parent root = loader.load();

            // Obtenir le contrôleur de la popup
            planningController controller = loader.getController();
            controller.setPlanningData(planning); // Pré-remplir les champs

            // Créer une nouvelle scène avec la popup
            Scene scene = new Scene(root);

            // Créer un nouveau Stage pour la popup
            Stage popupStage = new Stage();
            popupStage.setTitle("Edit Planning");
            popupStage.setScene(scene);

            // Rendre la fenêtre modale (bloque l'accès à la fenêtre principale jusqu'à fermeture)
            popupStage.initModality(Modality.APPLICATION_MODAL);

            // Afficher la popup
            popupStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void setPlanningData(Planning planning) {
        titlePlanning.setText(planning.getTitle());
        description.setText(planning.getDescription());
        selectedPlanning = planning;




    }
    @FXML
    private void editPlanning(ActionEvent actionEvent) {
        // Vérifier si une activité a été sélectionnée
        if (selectedPlanning == null) {
            System.err.println("Aucune activité sélectionnée.");
            return;
        }

        // Récupérer les valeurs modifiées dans les champs
        String nom = titlePlanning.getText();
        String descriptionText = description.getText();



        // Créer une nouvelle instance de l'activité avec les nouvelles données
        Planning planning = new Planning( descriptionText,nom,selectedPlanning.getId());  // Utiliser l'ID de l'activité existante

        // Appeler le service pour mettre à jour l'activité
        PlanningService planningService = new PlanningService();
        planningService.Update(planning);

        // Fermer la fenêtre après la mise à jour
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.close();

        // Rafraîchir l'affichage des activités après la modification
        displayPlanning();
    }
    private void openCoursesPage(Planning planning) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/courses.fxml"));
            Parent root = loader.load();

            // Obtenir le contrôleur de la page des cours
            showController controller = loader.getController();
            controller.setPlanningData(planning); // Passer les infos du planning sélectionné

            // Remplacer la scène actuelle par la nouvelle
            Stage stage = (Stage) planningGrid.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
}


}
