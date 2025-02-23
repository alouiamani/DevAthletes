package controllers;

import entities.Commande;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import services.ServiceCommande;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

public class listCommandeFront {

    @FXML
    private ListView<Commande> listCommandes;
    
    @FXML
    private Label labelTotal;
    
    @FXML
    private Button btnSupprimerCommande;

    private final ServiceCommande serviceCommande = new ServiceCommande();
    private final ObservableList<Commande> observableCommandes = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        listCommandes.setItems(observableCommandes);
        listCommandes.setCellFactory(param -> new ListCell<Commande>() {
            @Override
            protected void updateItem(Commande commande, boolean empty) {
                super.updateItem(commande, empty);
                if (empty || commande == null) {
                    setText(null);
                } else {
                    setText(String.format("Total: %.2f€ | Statut: %s | Date: %s",
                            commande.getTotal_c(),
                            commande.getStatut_c(),
                            commande.getDateC().toString()));
                }
            }
        });
        chargerCommandes();
    }

    private void chargerCommandes() {
        try {
            observableCommandes.setAll(serviceCommande.afficher());
            calculerTotal();
        } catch (SQLException e) {
            afficherErreur("Erreur", "Impossible de charger les commandes.", e.getMessage());
        }
    }

    @FXML
    private void supprimerCommande() {
        Commande selectedCommande = listCommandes.getSelectionModel().getSelectedItem();
        if (selectedCommande == null) {
            afficherErreur("Erreur", "Veuillez sélectionner une commande à supprimer.", "");
            return;
        }

        try {
            serviceCommande.supprimer(selectedCommande.getId_c());
            observableCommandes.remove(selectedCommande);
            calculerTotal();
            afficherInformation("Succès", "Commande supprimée !");
        } catch (SQLException e) {
            afficherErreur("Erreur", "Impossible de supprimer la commande.", e.getMessage());
        }
    }

    private void calculerTotal() {
        float total = 0;
        for (Commande commande : observableCommandes) {
            total += commande.getTotal_c();
        }
        labelTotal.setText("Total des Commandes : " + total + " €");
    }

    @FXML
    private void retourPagePrecedente() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/listProduitFront.fxml"));
            Parent parent = loader.load();
            Stage stage = (Stage) listCommandes.getScene().getWindow();
            stage.setScene(new Scene(parent));
        } catch (IOException e) {
            e.printStackTrace();
            afficherErreur("Erreur", "Impossible de retourner à la page précédente.", e.getMessage());
        }
    }

    private void afficherErreur(String titre, String enTete, String contenu) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(enTete);
        alert.setContentText(contenu);
        alert.showAndWait();
    }

    private void afficherInformation(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}