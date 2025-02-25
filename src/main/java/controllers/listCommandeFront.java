package controllers;

import entities.Commande;
import entities.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import services.ServiceCommande;
import services.PDFService;
import utils.AuthToken;
import com.itextpdf.text.DocumentException;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.control.Dialog;
import javafx.scene.control.ButtonType;

public class listCommandeFront {

    @FXML
    private ListView<Commande> listCommandes;
    
    @FXML
    private Label labelTotal;
    
    @FXML
    private Button btnSupprimerCommande;

    @FXML
    private Button exportPdfBtn;

    private final ServiceCommande serviceCommande = new ServiceCommande();
    private final ObservableList<Commande> observableCommandes = FXCollections.observableArrayList();
    private final PDFService pdfService = new PDFService();

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
        labelTotal.setText("Total des Commandes : " + total + " dt");
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

    @FXML
    private void exportToPDF() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer le PDF");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
        );
        fileChooser.setInitialFileName("commandes.pdf");
        
        File file = fileChooser.showSaveDialog(null);
        
        if (file != null) {
            try {
                List<Commande> commandes = serviceCommande.afficher();
                pdfService.generateCommandePDF(commandes, file.getAbsolutePath());
                
                afficherInformation("Succès", "Le PDF a été généré avec succès!");
                
            } catch (SQLException e) {
                afficherErreur("Erreur", "Erreur lors de la récupération des commandes", e.getMessage());
            } catch (DocumentException | IOException e) {
                afficherErreur("Erreur", "Erreur lors de la génération du PDF", e.getMessage());
            }
        }
    }

    @FXML
    private void afficherCommande() {
        Commande selectedCommande = listCommandes.getSelectionModel().getSelectedItem();
        
        if (selectedCommande == null) {
            afficherErreur("Erreur", "Veuillez sélectionner une commande", "");
            return;
        }

        // Get current user
        User currentUser = AuthToken.getCurrentUser();
        if (currentUser == null) {
            afficherErreur("Erreur", "Utilisateur non connecté", "");
            return;
        }
        
        // Create a custom dialog
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Détails de la Commande");
        dialog.setHeaderText("Commande #" + selectedCommande.getId_c());

        // Create the content
        VBox content = new VBox(10);
        content.getChildren().addAll(
            new Label("Client: " + currentUser.getNom() + " " + currentUser.getPrenom()),
            new Label("Total: " + String.format("%.2f dt", selectedCommande.getTotal_c())),
            new Label("Statut: " + selectedCommande.getStatut_c()),
            new Label("Date: " + selectedCommande.getDateC().toString())
        );

        // Style the labels
        content.getChildren().forEach(node -> {
            if (node instanceof Label) {
                Label label = (Label) node;
                label.setStyle("-fx-font-size: 14px; -fx-padding: 5px;");
            }
        });

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);
        dialog.getDialogPane().setStyle("-fx-background-color: white; -fx-padding: 10px;");

        // Show the dialog
        dialog.showAndWait();
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