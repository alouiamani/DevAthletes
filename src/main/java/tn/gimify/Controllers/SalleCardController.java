package tn.gimify.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import tn.gimify.entities.Salle;
import tn.gimify.services.SalleService;

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
    @FXML private Button detailsBtn;
    @FXML private ImageView salleImageView;

    private Salle salle;
    private SalleService salleService = new SalleService();
    private ListeDesSalleController parentController;

    public void setSalleData(Salle salle, ListeDesSallesUser parentController) {
        this.salle = salle;



        // Mettre à jour les labels
        nomLabel.setText(salle.getNom() );
        adresseLabel.setText(salle.getAdresse());
        detailsLabel.setText(salle.getDetails() );
        numTelLabel.setText(salle.getNum_tel() );
        emailLabel.setText(salle.getEmail() );

        modifierBtn.setOnAction(e -> modifierSalle());
        supprimerBtn.setOnAction(e -> supprimerSalle());
    }

    @FXML
    private void modifierSalle() {
        try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/SalleFormAdmin.fxml"));
        Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Ajouter un Abonnement");
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
    @FXML
    private void DetailsSalle() {
        // Show the details of the salle
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SalleDetails.fxml"));
            Parent root = loader.load();
            // Pass data to the controller if needed
            Stage stage = new Stage();
            stage.setTitle("Details de la Salle");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
