package tn.gimify.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import tn.gimify.entities.Abonnement;
import tn.gimify.services.AbonnementService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class ListeAbonnementRSController {

    @FXML
    private FlowPane abonnementContainer; // Référence au FlowPane dans le fichier FXML

    private AbonnementService abonnementService = new AbonnementService();

    @FXML
    public void initialize() {
        try {
            loadAbonnements(); // Charger les abonnements lors de l'initialisation
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadAbonnements() throws SQLException {
        abonnementContainer.getChildren().clear(); // Nettoyer l'affichage avant de recharger (supprission)

        List<Abonnement> abonnements = abonnementService.afficher();

        for (Abonnement abonnement : abonnements) {
            Pane card = createAbonnementCard(abonnement);
            abonnementContainer.getChildren().add(card);
        }
    }

    private Pane createAbonnementCard(Abonnement abonnement) {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: #dcd9d9; -fx-padding: 15px; -fx-border-radius: 10px; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.5), 10, 0.5, 0, 0); -fx-alignment: center;");

        Label title = new Label("Abonnement: " + abonnement.getType_Abonnement());
        title.setFont(new Font("Arial", 18));
        title.setStyle("-fx-text-fill: #184311; -fx-font-weight: bold;");

        Label dateDebut = new Label("Début: " + abonnement.getDate_Début());
        Label dateFin = new Label("Fin: " + abonnement.getDate_Fin());
        Label tarif = new Label("Tarif: " + abonnement.getTarif() + " DT");

        Button editButton = new Button("Modifier");
        editButton.setOnAction(e -> handleEditAbonnement(abonnement));

        Button deleteButton = new Button("Supprimer");
        deleteButton.setStyle("-fx-background-color: #b80d1b; -fx-text-fill: white;");
        deleteButton.setOnAction(e -> handleDeleteAbonnement(abonnement.getId_Abonnement()));

        HBox buttonBox = new HBox(10, editButton, deleteButton);
        buttonBox.setStyle("-fx-alignment: center;");

        card.getChildren().addAll(title, dateDebut, dateFin, tarif, buttonBox);
        return card;
    }
    @FXML
    private void handleEditAbonnement(Abonnement abonnement) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AbonnementFormRS.fxml"));
            Parent root = loader.load();
            AbonnementFormRSController controller = loader.getController();
            controller.initData(abonnement);
            Stage stage = new Stage();
            stage.setTitle("Modifier un Abonnement");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir le formulaire de modification !");
        }
    }
    @FXML
    private void handleAddAbonnement() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AbonnementFormRS.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Ajouter une abonnement");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }}

    private void handleDeleteAbonnement(int id) {
        try {
            abonnementService.supprimer(id);
            loadAbonnements(); // Rafraîchir l'affichage après suppression
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
