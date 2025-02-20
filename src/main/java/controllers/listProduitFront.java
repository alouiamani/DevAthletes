package controllers;

import entities.Commande;
import entities.Produit;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import services.ServiceCommande;
import services.ServiceProduit;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class listProduitFront implements Initializable {

    @FXML
    private ListView<Produit> listProduits;
    
    @FXML
    private Button addProductBtn;
    
    @FXML
    private Button addOrderBtn;
    
    @FXML
    private Button editProductBtn;
    
    @FXML
    private Button deleteProductBtn;
    
    @FXML
    private Button ordersListBtn;

    private final ServiceProduit serviceProduit = new ServiceProduit();
    private final ServiceCommande serviceCommande = new ServiceCommande();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Customize the ListView cell factory to control how products are displayed
        listProduits.setCellFactory(param -> new ListCell<Produit>() {
            @Override
            protected void updateItem(Produit produit, boolean empty) {
                super.updateItem(produit, empty);
                if (empty || produit == null) {
                    setText(null);
                } else {
                    // Customize the display format here
                    setText(String.format("Nom: %s | Prix: %.2f€ | Stock: %d | Catégorie: %s",
                            produit.getNom_p(),
                            produit.getPrix_p(),
                            produit.getStock_p(),
                            produit.getCategorie_p()));
                }
            }
        });
        
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
            Stage stage = (Stage) addProductBtn.getScene().getWindow();
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

                // Charger l'interface listCommandeFront
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/listCommandeFront.fxml"));
            Parent parent = loader.load();

            // Get the scene from the ListView since it's always present
            Stage stage = (Stage) listProduits.getScene().getWindow();
            stage.setScene(new Scene(parent));

        } catch (IOException e) {
            System.out.println("Erreur lors du chargement de l'interface listCommandeFront : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void afficherListeCommande() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/listCommandeFront.fxml"));
            Parent parent = loader.load();
            Stage stage = (Stage) ordersListBtn.getScene().getWindow();
            stage.setScene(new Scene(parent));
        } catch (IOException e) {
            e.printStackTrace();
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