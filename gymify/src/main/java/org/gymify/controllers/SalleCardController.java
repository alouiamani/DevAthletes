package org.gymify.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.gymify.entities.Salle;
import org.gymify.services.SalleService;


import java.io.IOException;
import java.sql.SQLException;

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



        // Mettre à jour les labels
        nomLabel.setText(salle.getNom() );
        adresseLabel.setText(salle.getAdresse());
        detailsLabel.setText(salle.getDetails() );
        numTelLabel.setText(salle.getNum_tel() );
        emailLabel.setText(salle.getEmail() );
        if (salle.getUrl_photo() != null && !salle.getUrl_photo().isEmpty()) {
            salleImageView.setImage(new Image(salle.getUrl_photo(), true));
        } else {
            salleImageView.setImage(new Image("/images/default-image.png", true)); // Image par défaut
        }

        modifierBtn.setOnAction(e -> modifierSalle());
        supprimerBtn.setOnAction(e -> supprimerSalle());
    }

    @FXML
    private void modifierSalle() {
        try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/SalleFormAdmin.fxml"));
        Parent root = loader.load();
            SalleFormAdminController controller = loader.getController();
            controller.chargerSalle(salle);
            Stage stage = new Stage();
            stage.setTitle("Modifier un Sallle");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @FXML
    private void supprimerSalle() {
        try {
            salleService.supprimer(salle.getId_Salle());
            parentController.refreshList();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
