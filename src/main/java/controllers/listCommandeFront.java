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
import javafx.fxml.Initializable;
import java.net.URL;
import java.util.ResourceBundle;
import java.awt.Desktop;

public class listCommandeFront implements Initializable {

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Set custom cell factory
        listCommandes.setCellFactory(param -> new CommandeListCell());
        listCommandes.setItems(observableCommandes);
        loadCommandes();
    }

    private void loadCommandes() {
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
        Commande selectedCommande = listCommandes.getSelectionModel().getSelectedItem();
        if (selectedCommande == null) {
            afficherErreur("Erreur", "Veuillez sélectionner une commande à exporter.", "");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer la facture");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
        );
        fileChooser.setInitialFileName("Facture_" + selectedCommande.getId_c() + ".pdf");
        
        File file = fileChooser.showSaveDialog(exportPdfBtn.getScene().getWindow());
        if (file != null) {
            try {
                pdfService.exportCommandeToPDF(selectedCommande, file.getAbsolutePath());
                afficherInformation("Succès", "La facture a été exportée avec succès!");
                
                // Ask if user wants to open the PDF
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Ouvrir le PDF");
                alert.setHeaderText("La facture a été créée avec succès");
                alert.setContentText("Voulez-vous ouvrir le fichier maintenant?");
                
                if (alert.showAndWait().get() == ButtonType.OK) {
                    if (Desktop.isDesktopSupported()) {
                        Desktop.getDesktop().open(file);
                    }
                }
            } catch (Exception e) {
                afficherErreur("Erreur", "Impossible d'exporter la facture.", e.getMessage());
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
        dialog.setTitle("Commande Confirmee");
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

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/listProduitFront.fxml"));
            Parent parent = loader.load();
            Stage stage = (Stage) listCommandes.getScene().getWindow();
            stage.setScene(new Scene(parent));
        } catch (IOException e) {
            e.printStackTrace();
            afficherErreur("Erreur", "Could not return to products page", e.getMessage());
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