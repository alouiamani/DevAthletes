package org.gymify.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.gymify.entities.Planning;

import java.io.IOException;
import java.net.URL;

public class showController {
    @FXML
    public StackPane contentArea;
    @FXML
    private Label planningTitle;
    private Planning selectedPlanning;


    public void setPlanningData(Planning planning) {
        if (planning != null) {
            this.selectedPlanning = planning;
            planningTitle.setText(planning.getTitle());
            System.out.println("Le planning n'est pas null");
            // Affiche le titre du planning
        } else {
            System.out.println("Le planning est null");
        }
    }

    public void showAddCoursPage(ActionEvent actionEvent) {
        try {
            // Charger le fichier FXML de la popup
            System.out.println(selectedPlanning);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/addCours.fxml"));

            Parent root = loader.load();
            coursController controller = loader.getController();
            if (controller != null) {
                controller.setPlanningSelect(this.selectedPlanning); // Vérifiez que popupController n'est pas null
            } else {
                System.out.println("Controller is null");
            }


            // Créer une nouvelle scène avec la popup
            Scene scene = new Scene(root);

            // Créer un nouveau Stage pour la popup
            Stage popupStage = new Stage();
            popupStage.setTitle("Add Course");
            popupStage.setScene(scene);

            // Rendre la fenêtre modale (bloque l'accès à la fenêtre principale jusqu'à fermeture)
            popupStage.initModality(Modality.APPLICATION_MODAL);

            // Afficher la popup
            popupStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void filterCoursesByDate(ActionEvent actionEvent) {
    }
}
