package org.gymify.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.gymify.entities.Salle;
import org.gymify.services.SalleService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

public class SalleCardController {

    @FXML private Label nomLabel;
    @FXML private Label adresseLabel;
    @FXML private Label numTelLabel;
    @FXML private Label emailLabel;
    @FXML private Label detailsLabel;
    @FXML private Button modifierBtn;
    @FXML private Button supprimerBtn;
    @FXML private ImageView salleImageView;

    private Salle salle;
    private SalleService salleService = new SalleService();
    private ListeDesSalleController parentController;

    public void setSalleData(Salle salle, ListeDesSalleController parentController) {
        this.salle = salle;
        this.parentController = parentController;

        // Mettre à jour les labels
        nomLabel.setText(salle.getNom());
        adresseLabel.setText(salle.getAdresse());
        detailsLabel.setText(salle.getDetails());
        numTelLabel.setText(salle.getNum_tel());
        emailLabel.setText(salle.getEmail());

        System.out.println("Chargement de l'image pour : " + salle.getNom());

        if (salle.getUrl_photo() != null && !salle.getUrl_photo().isEmpty()) {
            try {
                salleImageView.setImage(new Image(salle.getUrl_photo(), true));
            } catch (Exception e) {
                System.out.println("Erreur de chargement de l'image, utilisation de l'image par défaut.");
                salleImageView.setImage(new Image("/images/default-image.png", true));
            }
        } else {
            salleImageView.setImage(new Image("/images/default-image.png", true)); // Image par défaut
        }

        modifierBtn.setOnAction(event -> modifierSalle(event));
        supprimerBtn.setOnAction(e -> supprimerSalle());
    }

    @FXML
    private void modifierSalle(ActionEvent event) {
        try {
            // Charger le fichier FXML du formulaire d'ajout
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SalleFormAdmin.fxml"));
            Parent root = loader.load();
            SalleFormAdminController controller = loader.getController();
            // Pass the salleId to the form controller
            controller.chargerSalle(salle);
            // Obtenir la scène actuelle (celle de la fenêtre ouverte)
            Scene currentScene = ((Node) event.getSource()).getScene();

            // Définir la nouvelle scène dans le même Stage
            Stage currentStage = (Stage) currentScene.getWindow();
            currentStage.setScene(new Scene(root));

            // Optionnel : définir un titre pour la scène si nécessaire
            currentStage.setTitle("Ajouter une salle");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void supprimerSalle() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Supprimer cette salle");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer cette salle ?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                salleService.supprimer(salle.getId_Salle());
                System.out.println("Salle supprimée : " + salle.getNom());
                parentController.refreshList(); // Rafraîchir la liste après suppression
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
