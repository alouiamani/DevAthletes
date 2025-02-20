package controllers;

import entities.Produit;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import services.ServiceProduit;

import java.io.IOException;
import java.sql.SQLException;

public class ModifierProduit {

    @FXML
    private TextField nomP, prixP, stockP, catP;
    @FXML
    private Button modifierBtn;

    private final ServiceProduit serviceProduit = new ServiceProduit();
    private Produit produit;

    public void setProduit(Produit produit) {
        this.produit = produit;
        nomP.setText(produit.getNom_p());
        prixP.setText(String.valueOf(produit.getPrix_p()));
        stockP.setText(String.valueOf(produit.getStock_p()));
        catP.setText(produit.getCategorie_p());
    }

    @FXML
    private void modifierProduit() {
        if (!validateInputs()) {
            return;
        }

        try {
            float prix = Float.parseFloat(prixP.getText().trim());
            int stock = Integer.parseInt(stockP.getText().trim());

            // Keep the original name, only update other fields
            produit.setPrix_p(prix);
            produit.setStock_p(stock);
            produit.setCategorie_p(catP.getText().trim());

            serviceProduit.modifier(produit);
            showAlert("Succès", "Produit modifié avec succès !");
            afficherListeProduits(); // Return to product list after successful modification

        } catch (SQLException e) {
            showAlert("Erreur", "Problème de base de données : " + e.getMessage());
        }
    }

    private boolean validateInputs() {
        // Don't validate name since it won't change
        
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
        String categorie = catP.getText().trim();
        if (categorie.isEmpty()) {
            showAlert("Erreur", "La catégorie ne peut pas être vide.");
            return false;
        }
        if (!categorie.matches("^[a-zA-Z\\s]{3,30}$")) {
            showAlert("Erreur", "La catégorie doit contenir entre 3 et 30 lettres.");
            return false;
        }

        return true;
    }

    @FXML
    public void initialize() {
        // Disable the name field since it shouldn't be modified
        nomP.setDisable(true);
        
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

        catP.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("^[a-zA-Z\\s]*$")) {
                catP.setText(oldValue);
            }
        });
    }

    @FXML
    private void afficherListeProduits() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/listProduit.fxml"));
            Parent parent = loader.load();
            Stage stage = (Stage) modifierBtn.getScene().getWindow();
            stage.setScene(new Scene(parent));
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        catP.clear();
    }
}