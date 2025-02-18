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
    @FXML
    private Button modifierBtn;
    @FXML
    private Button supprimerBtn;
    @FXML
    private Button detailsBtn;
    @FXML private ImageView salleImageView;

    private Salle salle;
    private SalleService salleService = new SalleService();
    private ListeDesSalleController parentController;

    public void setSalleData(Salle salle, ListeDesSalleController parentController) {
        this.salle = salle;
        this.parentController = parentController;


        // Mettre à jour les labels
        nomLabel.setText(salle.getNom() );
        adresseLabel.setText(salle.getAdresse());
        detailsLabel.setText(salle.getDetails() );
        numTelLabel.setText(salle.getNum_tel() );
        emailLabel.setText(salle.getEmail() );

        modifierBtn.setOnAction(e -> ouvrirModificationSalle());
        supprimerBtn.setOnAction(e -> supprimerSalle());
    }

    private void ouvrirModificationSalle() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/SalleFormAdmin.fxml"));
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        SalleFormAdminController controller = loader.getController();
        controller.chargerSalle(salle);

        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Modifier Salle");
        stage.show();
    }

    private void supprimerSalle() {
        try {
            salleService.supprimer(salle.getId_Salle());
            parentController.refreshList();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
