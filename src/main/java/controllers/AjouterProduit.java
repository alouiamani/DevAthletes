package controllers;

import entities.Produit;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import services.ServiceProduit;
import javafx.scene.control.ComboBox;
import javafx.collections.FXCollections;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class AjouterProduit {

    @FXML
    private TextField nomP, prixP, stockP;
    
    @FXML
    private ComboBox<String> catP;

    @FXML
    private Button ajouterBtn;

    @FXML
    private TextField imagePathField;

    private final ServiceProduit serviceProduit = new ServiceProduit();

    @FXML
    private void chooseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Product Image");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif")
        );
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            imagePathField.setText(selectedFile.getPath());
        }
    }

    @FXML
    private void ajouterProduit() {
        if (!validateInputs()) {
            return;
        }

        try {
            float prix = Float.parseFloat(prixP.getText().trim());
            int stock = Integer.parseInt(stockP.getText().trim());

            Produit produit = new Produit(
                nomP.getText().trim(), 
                prix, 
                stock, 
                catP.getValue(),
                imagePathField.getText().trim()
            );
            
            serviceProduit.ajouter(produit);
            showAlert("Succès", "Produit ajouté avec succès !");
            clearFields();
        } catch (SQLException e) {
            showAlert("Erreur", "Problème de base de données : " + e.getMessage());
        }
    }

    @FXML
    private void afficherListeProduits() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/listProduit.fxml"));
            Parent parent = loader.load();
            Stage stage = (Stage) ajouterBtn.getScene().getWindow();
            stage.setScene(new Scene(parent));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        // Update the categories list to remove 'clothes'
        catP.setItems(FXCollections.observableArrayList(
            "complement",
            "accessoire"
        ));
        
        // Set default value
        catP.setValue("complement");
        
        // Add other existing listeners
        nomP.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("^[a-zA-Z0-9\\s]*$")) {
                nomP.setText(oldValue);
            }
        });

        prixP.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("^\\d*\\.?\\d*$")) {
                prixP.setText(oldValue);
            }
        });

        stockP.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                stockP.setText(oldValue);
            }
        });
    }

    private boolean validateInputs() {
        // Validate Product Name
        String nom = nomP.getText().trim();
        if (nom.isEmpty()) {
            showAlert("Erreur", "Le nom du produit ne peut pas être vide.");
            return false;
        }
        if (!nom.matches("^[a-zA-Z0-9\\s]{3,50}$")) {
            showAlert("Erreur", "Le nom du produit doit contenir entre 3 et 50 caractères alphanumériques.");
            return false;
        }

        // Check if product name already exists
        try {
            List<Produit> existingProducts = serviceProduit.afficher();
            boolean nameExists = existingProducts.stream()
                    .anyMatch(p -> p.getNom_p().equalsIgnoreCase(nom));
            if (nameExists) {
                showAlert("Erreur", "Un produit avec ce nom existe déjà.");
                return false;
            }
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors de la vérification du nom: " + e.getMessage());
            return false;
        }

        // Validate Price
        String prix = prixP.getText().trim();
        try {
            float prixValue = Float.parseFloat(prix);
            if (prixValue <= 0) {
                showAlert("Erreur", "Le prix doit être supérieur à 0.");
                return false;
            }
            if (prixValue > 1000000) {
                showAlert("Erreur", "Le prix ne peut pas dépasser 1,000,000.");
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Le prix doit être un nombre valide.");
            return false;
        }

        // Validate Stock
        String stock = stockP.getText().trim();
        try {
            int stockValue = Integer.parseInt(stock);
            if (stockValue < 0) {
                showAlert("Erreur", "Le stock ne peut pas être négatif.");
                return false;
            }
            if (stockValue > 1000000) {
                showAlert("Erreur", "Le stock ne peut pas dépasser 1,000,000 unités.");
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Le stock doit être un nombre entier valide.");
            return false;
        }

        // Validate Category
        if (catP.getValue() == null) {
            showAlert("Erreur", "Veuillez sélectionner une catégorie.");
            return false;
        }

        return true;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearFields() {
        nomP.clear();
        prixP.clear();
        stockP.clear();
        catP.setValue("complement"); // Reset to default value
        imagePathField.clear();
    }
}