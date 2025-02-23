package controllers;

import entities.Produit;
import entities.Commande;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.File;

import services.ServiceProduit;
import services.ServiceCommande;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class listProduit implements Initializable {

    @FXML
    private ListView<Produit> listProduits;

    @FXML
    private Button MAFButton;

    @FXML
    private ImageView productImageView;

    private final ServiceProduit serviceProduit = new ServiceProduit();
    private final ServiceCommande serviceCommande = new ServiceCommande();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Set custom cell factory
        listProduits.setCellFactory(param -> new ProductListCell());
        
        // Load products
        loadProduits();
    }

    private void loadProduits() {
        try {
            List<Produit> produits = serviceProduit.afficher();
            ObservableList<Produit> observableList = FXCollections.observableArrayList(produits);
            listProduits.setItems(observableList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void retourAjouterProduit() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterProduit.fxml"));
            Parent parent = loader.load();
            Stage stage = (Stage) MAFButton.getScene().getWindow();
            stage.setScene(new Scene(parent));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void ajouterCommandeDepuisProduit() {
        Produit produitSelectionne = listProduits.getSelectionModel().getSelectedItem();

        if (produitSelectionne == null) {
            System.out.println("Sélectionnez un produit !");
            return;
        }

        // Vérification du stock
        if (produitSelectionne.getStock_p() <= 0) {
            System.out.println("Le produit sélectionné est en rupture de stock !");
            return;
        }

        // Demander la quantité
        TextInputDialog dialog = new TextInputDialog("1"); // Valeur par défaut
        dialog.setTitle("Quantité");
        dialog.setHeaderText("Entrez la quantité de produit à commander");
        dialog.setContentText("Quantité:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(quantiteStr -> {
            try {
                int quantite = Integer.parseInt(quantiteStr);

                if (quantite <= 0) {
                    System.out.println("Quantité invalide !");
                    return;
                }

                // Vérification de la quantité par rapport au stock
                if (quantite > produitSelectionne.getStock_p()) {
                    System.out.println("Quantité demandée supérieure au stock disponible !");
                    return;
                }

                // Créer la commande
                Commande nouvelleCommande = new Commande(produitSelectionne.getPrix_p() * quantite, "En attente");
                int commandeId = serviceCommande.ajouter(nouvelleCommande);
                serviceCommande.ajouterCommandeProduit(commandeId, produitSelectionne.getId_p(), quantite);

                // Mettre à jour le stock
                produitSelectionne.setStock_p(produitSelectionne.getStock_p() - quantite);
                serviceProduit.modifier(produitSelectionne);

                // Rafraîchir la table des produits
                loadProduits();

                // Charger l'interface listCommande
                afficherListeCommandesAvecNouvelleCommande(nouvelleCommande);

                System.out.println("Commande créée avec succès pour le produit : " + produitSelectionne.getNom_p());

            } catch (NumberFormatException e) {
                System.out.println("La quantité saisie est invalide.");
            } catch (SQLException e) {
                System.out.println("Erreur lors de la création de la commande : " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    /**
     * Méthode pour charger l'interface de la liste des commandes et ajouter la nouvelle commande.
     */
    private void afficherListeCommandesAvecNouvelleCommande(Commande nouvelleCommande) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/listCommande.fxml"));
            Parent parent = loader.load();

            // Afficher l'interface
            Stage stage = (Stage) MAFButton.getScene().getWindow();
            stage.setScene(new Scene(parent));

        } catch (IOException e) {
            System.out.println("Erreur lors du chargement de l'interface listCommande : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void modifierProduit() {
        Produit produitSelectionne = listProduits.getSelectionModel().getSelectedItem();
        if (produitSelectionne == null) {
            afficherErreur("Erreur", "Veuillez sélectionner un produit à modifier.", "");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/modifierProduit.fxml"));
            Parent parent = loader.load();
            ModifierProduit controller = loader.getController();
            controller.setProduit(produitSelectionne);

            Stage stage = (Stage) MAFButton.getScene().getWindow();
            stage.setScene(new Scene(parent));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void afficherListeCommande() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/listCommande.fxml"));
            Parent parent = loader.load();
            Stage stage = (Stage) MAFButton.getScene().getWindow();
            stage.setScene(new Scene(parent));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void supprimerProduit() {
        Produit produitSelectionne = listProduits.getSelectionModel().getSelectedItem();

        if (produitSelectionne == null) {
            afficherErreur("Erreur", "Veuillez sélectionner un produit à supprimer.", "");
            return;
        }

        // Ask for confirmation before deleting
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation de suppression");
        confirmation.setHeaderText("Supprimer le produit");
        confirmation.setContentText("Voulez-vous vraiment supprimer ce produit ?");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                serviceProduit.supprimer(produitSelectionne.getId_p());
                loadProduits(); // Refresh the list
                afficherInformation("Succès", "Produit supprimé avec succès !");
            } catch (SQLException e) {
                afficherErreur("Erreur", "Impossible de supprimer le produit.", e.getMessage());
            }
        }
    }

    private void afficherInformation(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void afficherErreur(String titre, String enTete, String contenu) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(enTete);
        alert.setContentText(contenu);
        alert.showAndWait();
    }
}