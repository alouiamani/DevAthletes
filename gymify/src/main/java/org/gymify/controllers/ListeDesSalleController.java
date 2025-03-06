package org.gymify.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import org.gymify.entities.Salle;
import org.gymify.services.SalleService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class ListeDesSalleController {

    @FXML private FlowPane sallesContainer;
    @FXML private TextField searchField;
    @FXML private Button ajoutBtn;

    private SalleService salleService = new SalleService();

    @FXML
    public void initialize() {
        try {
            System.out.println("Chargement des salles...");
            searchSalles();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Écouteur sur le champ de recherche
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                searchSalles();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        // Gestion du bouton d'ajout
        ajoutBtn.setOnAction(event -> ouvrirFormulaireAjout(event));
    }

    private void afficherSalles(List<Salle> salles) {
        sallesContainer.getChildren().clear(); // Effacer les salles existantes
        System.out.println("Nombre de salles à afficher : " + salles.size());

        for (Salle salle : salles) {
            try {
                System.out.println("Ajout de la salle : " + salle.getNom());

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/SalleCard.fxml"));
                Parent salleCard = loader.load();

                SalleCardController controller = loader.getController();
                controller.setSalleData(salle, this);

                sallesContainer.getChildren().add(salleCard);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Forcer l'affichage des cartes
        sallesContainer.requestLayout();
    }

    private void searchSalles() throws SQLException {
        String search = searchField.getText().trim().toLowerCase();
        List<Salle> salles;

        if (search.isEmpty()) {
            salles = salleService.afficher(); // Charger toutes les salles
        } else {
            salles = salleService.searchSalles(search); // Filtrer les salles
        }

        afficherSalles(salles);
    }
    @FXML
    private void ouvrirFormulaireAjout(ActionEvent event) {
        try {
            // Charger le fichier FXML du formulaire d'ajout
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SalleFormAdmin.fxml"));
            Parent root = loader.load();

            // Obtenir la scène actuelle (celle de la fenêtre ouverte)
            Scene currentScene = ((Node) event.getSource()).getScene();

            // Définir la nouvelle scène dans le même Stage
            Stage currentStage = (Stage) currentScene.getWindow();
            currentStage.setScene(new Scene(root));

            // Optionnel : définir un titre pour la scène si nécessaire
            currentStage.setTitle("Ajouter une salle");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void refreshList() {
        try {
            searchSalles();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

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
}
