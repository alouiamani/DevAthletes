
        package org.gymify.controllers;

import javafx.event.ActionEvent;
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
import org.gymify.entities.User;
import org.gymify.utils.AuthToken;
import org.gymify.utils.ImageUtils;

import java.io.IOException;

public class SalleCardUserController {
    @FXML private Label salleName;
    @FXML private Label salleLocation;
    @FXML private Label salleDetails;
    @FXML private Label salleTel;
    @FXML private Label salleEmail;
    @FXML private ImageView salleImageView;
    @FXML private Button savoirPlusButton;

    private Salle salle;
    private ProfileMembreController profileMembreController;

    public void setSalleData(Salle salle, ProfileMembreController profileMembreController) {
        this.salle = salle;
        this.profileMembreController = profileMembreController;

        salleName.setText(salle.getNom());
        salleLocation.setText("Adresse: " + salle.getAdresse());
        salleDetails.setText("Détails: " + salle.getDetails());
        salleTel.setText("Téléphone: " + salle.getNum_tel());
        salleEmail.setText("Email: " + salle.getEmail());

        // Use ImageUtils to load image safely
        salleImageView.setImage(ImageUtils.loadImage(salle.getUrl_photo(), "/images/default-image.png"));

        savoirPlusButton.setOnAction(event -> afficherAbonnement());
    }

    @FXML
    private void afficherAbonnement() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AbonnementsSalle.fxml"));
            Parent root = loader.load();

            AbonnementsSalleUserController controller = loader.getController();
            controller.initData(salle.getId(), AuthToken.getCurrentUser());

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Abonnements de " + salle.getNom());
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
