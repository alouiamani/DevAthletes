/*package org.gymify.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import org.gymify.entities.Equipe;
import org.gymify.services.EquipeService;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CardEquipeController {

    @FXML
    private Label nomEquipe, niveauEquipe, nombreMembreEquipe;
    @FXML
    private Button btnModifier, btnSupprimer, btnDetails;

    private Equipe equipe;
    private ListeDesEquipesController listeDesEquipesController;
    private final EquipeService equipeService = new EquipeService();

    public CardEquipeController() throws SQLException {}

    public void setEquipe(Equipe equipe) {
        this.equipe = equipe;
        if (equipe != null) {
            nomEquipe.setText("Nom : " + equipe.getNom());
            niveauEquipe.setText("Niveau : " + equipe.getNiveau());
            nombreMembreEquipe.setText("Membres : " + equipe.getNombreMembres());
        }
    }

    public void setListeDesEquipesController(ListeDesEquipesController controller) {
        this.listeDesEquipesController = controller;
    }

    @FXML
    private void handleSupprimer() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText(null);
        alert.setContentText("Voulez-vous vraiment supprimer cette équipe ?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                equipeService.supprimer(equipe.getId());  // Supprimer l'équipe de la base de données
                if (listeDesEquipesController != null) {
                    listeDesEquipesController.rafraichirListe();  // Rafraîchir la liste après suppression
                }
                afficherMessage("Succès", "L'équipe a été supprimée avec succès.");
            } catch (SQLException e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Erreur lors de la suppression", e);
                afficherMessage("Erreur", "Échec de la suppression de l'équipe.");
            }
        }
    }

    @FXML
    private void handleDetails() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Détails de l'équipe");
        alert.setHeaderText("Informations de l'équipe");
        alert.setContentText(
                "Nom : " + equipe.getNom() + "\n" +
                        "Niveau : " + equipe.getNiveau() + "\n" +
                        "Membres : " + equipe.getNombreMembres()
        );
        alert.showAndWait();
    }

    @FXML
    private void handleModifier() {
        try {
            URL fxmlURL = getClass().getResource("/AjoutEquipe.fxml");
            if (fxmlURL == null) {
                throw new IOException("Le fichier FXML '/AjoutEquipe.fxml' est introuvable");
            }
            FXMLLoader loader = new FXMLLoader(fxmlURL);
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));

            // Récupérer le contrôleur du formulaire et lui passer l'équipe à modifier
            AjoutEquipeController controller = loader.getController();
            // (Optionnel) si vous souhaitez que le formulaire puisse lui-même rafraîchir la liste :
            controller.setListeDesEquipesController(listeDesEquipesController);
            controller.handleModifier(equipe); // Pré-remplir les champs
            stage.showAndWait();

            if (listeDesEquipesController != null) {
                listeDesEquipesController.rafraichirListe(); // Rafraîchir la liste après modification
            }

        } catch (IOException e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Erreur lors de l'ouverture du formulaire", e);
            afficherMessage("Erreur", "Impossible d'ouvrir le formulaire de modification.");
        }
    }

    private void afficherMessage(String titre, String contenu) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(contenu);
        alert.showAndWait();
    }
}*/
