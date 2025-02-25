package controllers;

import entities.Commande;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
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

public class listCommande {

    @FXML
    private ListView<Commande> listCommandes;
    @FXML
    private Label labelTotal; // Label pour afficher le total des commandes

    @FXML
    private Button btnSupprimerCommande;
    @FXML
    private Button btnRetour; // Bouton pour retourner à la page précédente

    private final ServiceCommande serviceCommande = new ServiceCommande();
    private final ObservableList<Commande> observableCommandes = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Set custom cell factory
        listCommandes.setCellFactory(param -> new CommandeListCell());
        listCommandes.setItems(observableCommandes);
        chargerCommandes();
    }

    private void chargerCommandes() {
        try {
            observableCommandes.setAll(serviceCommande.afficher());
            calculerTotal(); // Calculer et afficher le total des commandes
        } catch (SQLException e) {
            afficherErreur("Erreur", "Impossible de charger les commandes.", e.getMessage());
        }
    }

    /**
     * Méthode pour ajouter une commande dans la ListView et mettre à jour le total.
     */
    public void ajouterCommandeDansListView(Commande commande) {
        observableCommandes.add(commande);
        calculerTotal(); // Mettre à jour le total après ajout
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
            calculerTotal(); // Mettre à jour le total après suppression
            afficherInformation("Succès", "Commande supprimée !");
        } catch (SQLException e) {
            afficherErreur("Erreur", "Impossible de supprimer la commande.", e.getMessage());
        }
    }

    /**
     * Méthode pour calculer et afficher le total des commandes.
     */
    private void calculerTotal() {
        float total = 0;
        for (Commande commande : observableCommandes) {
            total += commande.getTotal_c();
        }
        labelTotal.setText("Total des Commandes : " + total + " dt");
    }

    /**
     * Méthode pour retourner à la page précédente (par exemple, listProduit.fxml).
     */
    @FXML
    private void retourPagePrecedente() {
        try {
            // Charger la vue de la page précédente (listProduit.fxml)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/listProduit.fxml"));
            Parent parent = loader.load();

            // Obtenir la scène actuelle et définir le nouveau contenu
            Scene scene = listCommandes.getScene(); // Obtenir la scène actuelle
            scene.setRoot(parent); // Définir le nouveau contenu

            System.out.println("Retour à la page précédente !");
        } catch (IOException e) {
            e.printStackTrace();
            afficherErreur("Erreur", "Impossible de retourner à la page précédente.", e.getMessage());
        }
    }

    @FXML
    private void modifierStatutCommande() {
        Commande selectedCommande = listCommandes.getSelectionModel().getSelectedItem();

        if (selectedCommande == null) {
            afficherErreur("Erreur", "Veuillez sélectionner une commande à modifier.", "");
            return;
        }

        // Create a dialog to choose the new status
        ChoiceDialog<String> dialog = new ChoiceDialog<>("En attente", 
            "En attente", "En cours", "Terminée", "Annulée");
        dialog.setTitle("Modifier le statut");
        dialog.setHeaderText("Choisir le nouveau statut");
        dialog.setContentText("Statut :");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(nouveauStatut -> {
            try {
                selectedCommande.setStatu_c(nouveauStatut);
                serviceCommande.modifierStatut(selectedCommande.getId_c(), nouveauStatut);
                chargerCommandes(); // Refresh the list
                afficherInformation("Succès", "Statut de la commande modifié avec succès !");
            } catch (SQLException e) {
                afficherErreur("Erreur", "Impossible de modifier le statut de la commande.", e.getMessage());
            }
        });
    }

    // Méthode pour afficher une alerte d'erreur
    private void afficherErreur(String titre, String enTete, String contenu) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(enTete);
        alert.setContentText(contenu);
        alert.showAndWait();
    }

    // Méthode pour afficher une alerte d'information
    private void afficherInformation(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}