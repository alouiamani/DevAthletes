package tn.gimify.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import tn.gimify.entities.Salle;
import tn.gimify.services.SalleService;

import java.io.IOException;
import java.sql.SQLException;

public class SalleFormAdminController {

    @FXML private TextField nomFX, adresseFX, numtelFX, emailFX,url_photoFX;
    @FXML private TextArea detailsFX;

    private Salle salleAModifier;
    private SalleService salleService = new SalleService();

    public void chargerSalle(Salle salle) {
        salleAModifier = salle;
        nomFX.setText(salle.getNom());
        adresseFX.setText(salle.getAdresse());
        detailsFX.setText(salle.getDetails());
        numtelFX.setText(salle.getNum_tel());
        emailFX.setText(salle.getEmail());
        url_photoFX.setText(salle.getUrl_photo());
    }
    @FXML
    private void btnEnregistrer(ActionEvent actionEvent) {
        if (nomFX.getText().isEmpty() || adresseFX.getText().isEmpty() || emailFX.getText().isEmpty()
                || detailsFX.getText().isEmpty() || numtelFX.getText().isEmpty() || url_photoFX.getText().isEmpty()) {
            afficherAlerte("Erreur", "Veuillez remplir tous les champs obligatoires.");
            return;
        }

        Salle salle = new Salle(nomFX.getText(), adresseFX.getText(), detailsFX.getText(),
                numtelFX.getText(), emailFX.getText(), url_photoFX.getText());

        try {
            String message;

            if (salleAModifier == null) {
                salleService.ajouter(salle);
                message = "La salle a été ajoutée avec succès !";
            } else {
                salle.setId_Salle(salleAModifier.getId_Salle());
                salleService.modifier(salle);
                message = "La salle a été modifiée avec succès !";
            }

            afficherAlerte("Succès", message);

            // Rediriger vers la page de liste des salles après l'enregistrement
            chargerListeSalles();

        } catch (SQLException e) {
            e.printStackTrace();
            afficherAlerte("Erreur", "Une erreur est survenue lors de l'enregistrement.");
        }
    }

    // Méthode pour charger la liste des salles
    private void chargerListeSalles() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListeDesSalleAdmin.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) nomFX.getScene().getWindow(); // Récupérer la fenêtre actuelle
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            afficherAlerte("Erreur", "Impossible de charger la liste des salles.");
        }
    }

    // Méthode pour afficher une alerte
    private void afficherAlerte(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void btnAnnuler(ActionEvent actionEvent) {
        viderChamps(); // Effacer les champs
    }

    // Méthode pour vider tous les champs
    private void viderChamps() {
        nomFX.clear();
        adresseFX.clear();
        emailFX.clear();
        detailsFX.clear();
        numtelFX.clear();
        url_photoFX.clear();
    }

    @FXML
    private void retournerEnArriere(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListeDesSalleAdmin.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            afficherAlerte("Erreur", "Impossible de retourner à l'interface précédente.");
        }
    }
}
