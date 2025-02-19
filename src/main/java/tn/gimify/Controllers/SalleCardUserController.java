package tn.gimify.Controllers;

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
import tn.gimify.entities.Salle;

import java.io.IOException;

public class SalleCardUserController {
    @FXML private Label salleName;
    @FXML private Label salleLocation;
    @FXML private Label salleDetails;
    @FXML private Label salleTel;
    @FXML private Label salleEmail;
    @FXML private ImageView salleImageView;
    @FXML private Button savoirPlusButton; // Ajout du bouton "Savoir plus"

    private Salle salle;

    // Méthode pour afficher les informations d'une salle
    public void setSalleData(Salle salle, ListeDesSallesUser listeDesSalleController) {
        this.salle = salle;
        salleName.setText(salle.getNom());
        salleLocation.setText("Adresse: " + salle.getAdresse());
        salleDetails.setText("Détails: " + salle.getDetails());
        salleTel.setText("Téléphone: " + salle.getNum_tel());
        salleEmail.setText("Email: " + salle.getEmail());

        if (salle.getUrl_photo() != null && !salle.getUrl_photo().isEmpty()) {
            salleImageView.setImage(new Image(salle.getUrl_photo(), true));
        } else {
            salleImageView.setImage(new Image("/images/default-image.png", true)); // Image par défaut
        }

        // Associer l'événement au bouton
        savoirPlusButton.setOnAction(event -> afficherAbonnement());
    }

    // Méthode pour ouvrir l'interface AbonnementsSalle.fxml
    @FXML
    private void afficherAbonnement() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AbonnementsSalle.fxml"));
            Parent root = loader.load();

            AbonnementsSalleUserController controller = loader.getController();
            controller.initData(salle.getId_Salle()); // Passer l'ID de la salle

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Abonnements de " + salle.getNom());
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setSalleData(Salle salle, ListeDesSalleController listeDesSalleController) {
    }
}
