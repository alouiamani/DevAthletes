package org.gymify.controllers;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.gymify.entities.Salle;
import org.gymify.services.SalleService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class ListeDesSalleController {

    @FXML private VBox sallesContainer;
    @FXML private TextField searchField;

    @FXML private Button ajoutBtn;

    private SalleService salleService = new SalleService();
    private PauseTransition pause = new PauseTransition(Duration.millis(300));

    @FXML
    public void initialize() throws SQLException {
        // Ajouter un listener sur le champ de recherche
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            pause.setOnFinished(event -> {
                try {
                    searchSalles();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
            pause.playFromStart();
        });

        ajoutBtn.setOnAction(event -> ouvrirFormulaireAjout());
        searchSalles(); // Charger toutes les salles au démarrage
    }

    private void afficherSalles(List<Salle> salles) {
        sallesContainer.getChildren().clear(); // Effacer les salles existantes

        for (Salle salle : salles) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/SalleCard.fxml"));
                Parent salleCard = loader.load();

                SalleCardController controller = loader.getController();
                controller.setSalleData(salle, this);

                sallesContainer.getChildren().add(salleCard); // Ajouter la carte de la salle à la liste
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Recherche des salles par texte (via le service)
    private void searchSalles() throws SQLException {
        String search = searchField.getText().toLowerCase();
        List<Salle> salles;

        // Vérifiez si le champ de recherche est vide
        if (search.isEmpty()) {
            salles = salleService.afficher(); // Si vide, chargez toutes les salles
        } else {
            salles = salleService.searchSalles(search); // Sinon, utilisez la méthode de recherche
        }

        afficherSalles(salles); // Affichez les salles filtrées
    }

    // Ouvrir l'interface d'ajout/modification
    private void ouvrirFormulaireAjout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SalleFormAdmin.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Ajouter une salle");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Rafraîchir la liste après modification/suppression
    public void refreshList() throws SQLException {
        searchSalles();
    }

    public void retourDashboard(ActionEvent actionEvent) {
        try {
            // Charger le fichier FXML de la liste des salles
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AdminDash.fxml"));
            Parent root = loader.load();

            // Obtenir le contrôleur de la nouvelle scène
            ListeDesSalleController controller = loader.getController();

            // Créer une nouvelle scène avec la liste des salles
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Liste des Salles");

            // Vous pouvez également appeler une méthode pour actualiser la liste des salles si nécessaire
            try {
                controller.refreshList();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            // Afficher la nouvelle scène
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

