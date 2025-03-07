package org.gymify.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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

    public void setParentController(ListeDesSalleController parentController) {
        this.parentController = parentController;
    }

    public void setSalleData(Salle salle, ListeDesSalleController parentController) {
        this.salle = salle;
        this.parentController = parentController;

        // Mise à jour des labels
        nomLabel.setText(salle.getNom());
        adresseLabel.setText(salle.getAdresse());
        detailsLabel.setText(salle.getDetails());
        numTelLabel.setText(salle.getNum_tel());
        emailLabel.setText(salle.getEmail());

        if (salle.getUrl_photo() != null && !salle.getUrl_photo().isEmpty()) {
            salleImageView.setImage(new Image(salle.getUrl_photo(), true));
        } else {
            salleImageView.setImage(new Image("/images/default-image.png", true)); // Image par défaut
        }

        modifierBtn.setOnAction(e -> modifierSalle());
        supprimerBtn.setOnAction(e -> supprimerSalle(e));
    }

    @FXML
    private void modifierSalle() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SalleFormAdmin.fxml"));
            Parent root = loader.load();

            // Passer la salle et le contrôleur parent au formulaire de modification
            SalleFormAdminController controller = loader.getController();
            controller.chargerSalle(salle, parentController);

            // Ouvrir le formulaire de modification dans une nouvelle fenêtre
            Stage stage = (Stage)modifierBtn .getScene().getWindow();
            stage.setTitle("Modifier une salle");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @FXML
    private void supprimerSalle(ActionEvent actionEvent) {
        if (salle == null) {
            System.out.println("Aucune salle sélectionnée !");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Voulez-vous vraiment supprimer cette salle ?");
        alert.setContentText("Cette action est irréversible.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                salleService.supprimer(salle.getId_Salle());
                System.out.println("Salle supprimée avec succès !");

                // Actualiser la liste des salles
                if (parentController != null) {
                    parentController.chargerSalles();
                }
            } catch (SQLException e) {
                System.out.println("Erreur lors de la suppression de la salle : " + e.getMessage());
            }
        }
    }
}