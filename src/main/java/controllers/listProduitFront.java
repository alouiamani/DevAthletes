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
import utils.AuthToken;
import services.CartService;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.Optional;

public class listProduitFront implements Initializable {

    @FXML
    private ListView<Produit> listProduits;
    
    @FXML
    private Button addOrderBtn;
    
    @FXML
    private Button ordersListBtn;

    @FXML
    private ComboBox<String> categoryFilter;

    @FXML
    private Button viewCartButton;

    private final ServiceProduit serviceProduit = new ServiceProduit();
    private final ServiceCommande serviceCommande = new ServiceCommande();
    private final CartService cartService = new CartService();

    private List<Produit> allProducts = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Set this controller as the scene's user data
        Platform.runLater(() -> {
            if (listProduits.getScene() != null) {
                listProduits.getScene().setUserData(this);
            }
        });
        
        // Set custom cell factory
        listProduits.setCellFactory(param -> new ProductListCell());
        
        // Set default value and items for category filter
        categoryFilter.setItems(FXCollections.observableArrayList("Tous", "complement", "accessoire"));
        categoryFilter.setValue("Tous");
        
        // Add listener for category filter
        categoryFilter.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                filterProducts();
            }
        });
        
        // Set cart icon for view cart button
        try {
            Image cartImage = new Image(getClass().getResourceAsStream("/icons/cart2.png"));
            ImageView cartIcon = new ImageView(cartImage);
            cartIcon.setFitHeight(20);
            cartIcon.setFitWidth(20);
            viewCartButton.setGraphic(cartIcon);
            viewCartButton.setText(""); // Remove text, show only icon
            
            // Add some styling
            viewCartButton.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-padding: 5px;" +
                "-fx-cursor: hand;"
            );
            
            // Add hover effect
            viewCartButton.setOnMouseEntered(e -> 
                viewCartButton.setStyle(
                    "-fx-background-color: #f0f0f0;" +
                    "-fx-padding: 5px;" +
                    "-fx-cursor: hand;"
                )
            );
            viewCartButton.setOnMouseExited(e -> 
                viewCartButton.setStyle(
                    "-fx-background-color: transparent;" +
                    "-fx-padding: 5px;" +
                    "-fx-cursor: hand;"
                )
            );
        } catch (Exception e) {
            // If icon fails to load, keep text button
            viewCartButton.setText("Panier");
        }
        
        // Load products
        loadProduits();
    }

    private void loadProduits() {
        try {
            allProducts = serviceProduit.afficher();
            filterProducts(); // Apply initial filter
        } catch (SQLException e) {
            e.printStackTrace();
            afficherErreur("Erreur", "Impossible de charger les produits.", e.getMessage());
        }
    }

    private void filterProducts() {
        String category = categoryFilter.getValue();
        if (category == null || category.equals("Tous")) {
            listProduits.setItems(FXCollections.observableArrayList(allProducts));
        } else {
            List<Produit> filteredProducts = allProducts.stream()
                .filter(p -> p.getCategorie_p().equals(category))
                .collect(Collectors.toList());
            listProduits.setItems(FXCollections.observableArrayList(filteredProducts));
        }
    }

    @FXML
    private void retourAjouterProduit() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterProduit.fxml"));
            Parent parent = loader.load();
            Stage stage = (Stage) addOrderBtn.getScene().getWindow();
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
                nouvelleCommande.setUser_id(AuthToken.getCurrentUser().getId_User());
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
            Scene scene = new Scene(parent);
            stage.setScene(scene);
            stage.setMinWidth(1200);
            stage.setMinHeight(800);
        } catch (IOException e) {
            e.printStackTrace();
            afficherErreur("Erreur", "Impossible de charger la page", e.getMessage());
        }
    }

    @FXML
    public void ajouterAuPanier() {
        Produit produitSelectionne = listProduits.getSelectionModel().getSelectedItem();
        ajouterAuPanier(produitSelectionne);
    }

    public void ajouterAuPanier(Produit produit) {
        if (produit == null) {
            afficherErreur("Erreur", "Veuillez sélectionner un produit.", "");
            return;
        }
        
        // Select the product in the ListView
        listProduits.getSelectionModel().select(produit);
        
        // Create an icon button for the quantity dialog
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Quantité");
        dialog.setHeaderText("Entrez la quantité souhaitée pour: " + produit.getNom_p());

        // Set the button types
        ButtonType confirmButtonType = new ButtonType("Ajouter", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmButtonType, ButtonType.CANCEL);

        // Create the quantity input field
        TextField quantityField = new TextField("1");
        quantityField.setTextFormatter(new TextFormatter<>(change -> 
            change.getControlNewText().matches("\\d*") ? change : null));
        dialog.getDialogPane().setContent(quantityField);

        // Convert the result to the quantity when the confirm button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == confirmButtonType) {
                return quantityField.getText();
            }
            return null;
        });

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String quantiteStr = result.get();
            try {
                int quantite = Integer.parseInt(quantiteStr);
                cartService.addToCart(produit, quantite);
                afficherInformation("Succès", "Produit ajouté au panier!");
            } catch (NumberFormatException e) {
                afficherErreur("Erreur", "Quantité invalide", "");
            } catch (IllegalArgumentException e) {
                afficherErreur("Erreur", e.getMessage(), "");
            }
        }
    }

    @FXML
    private void afficherPanier() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Cart.fxml"));
            Parent parent = loader.load();
            Stage stage = (Stage) viewCartButton.getScene().getWindow();
            Scene scene = new Scene(parent);
            stage.setScene(scene);
            stage.setMinWidth(1200);
            stage.setMinHeight(800);
        } catch (IOException e) {
            e.printStackTrace();
            afficherErreur("Erreur", "Impossible d'afficher le panier", e.getMessage());
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