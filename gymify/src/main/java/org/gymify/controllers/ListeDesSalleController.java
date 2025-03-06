package org.gymify.controllers;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
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

   /*@FXML private FlowPane sallesContainer;
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

    private void ouvrirFormulaireAjout() {
        try {
            // Récupérer le stage actuel et le fermer
            Stage currentStage = (Stage) searchField.getScene().getWindow(); // Ou n'importe quel autre composant de la fenêtre actuelle
            currentStage.close(); // Fermer la fenêtre actuelle

            // Charger et afficher la nouvelle fenêtre
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SalleFormAdmin.fxml"));
            Parent root = loader.load();
            Stage newStage = new Stage();
            newStage.setScene(new Scene(root));
            newStage.setTitle("Ajouter une salle");
            newStage.show(); // Afficher la nouvelle fenêtre
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

            // Créer une nouvelle scène avec la liste des salles
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Dashboard Admin");

            // Afficher la nouvelle scène
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void chargerSalles() {
        sallesContainer.getChildren().clear(); // Vider la liste avant de la recharger
        SalleService salleService = new SalleService();

        try {
            List<Salle> salles = salleService.afficher();
            for (Salle salle : salles) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/SalleCard.fxml"));
                    Parent card = loader.load();

                    SalleCardController controller = loader.getController();
                    controller.setSalleData(salle, this);

                    sallesContainer.getChildren().add(card);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }}


<<<<<<< HEAD
=======
    @FXML
    private void retourDashboard(javafx.event.ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AdminDash.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    */
}
>>>>>>> aea46390f69b2bb6a9b587a90aaa9dc13d0bcff7
