package org.gymify.controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import org.gymify.services.ServiceReclamation ;
import org.gymify.entities.Reclamation;
import org.gymify.utils.AuthToken;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReclamationSportifController {

    @FXML
    private TextField txtSujet, searchField;
    @FXML
    private TextArea txtDescription;
    @FXML
    private ListView<String> listReclamations;

    private final ServiceReclamation serviceReclamation = new ServiceReclamation();
    private final ObservableList<String> reclamationList = FXCollections.observableArrayList();
    private static final Logger LOGGER = Logger.getLogger(ReclamationSportifController.class.getName());

    @FXML
    public void initialize() {
        chargerReclamations();
    }

    @FXML
    private void envoyerReclamation(ActionEvent event) {
        if (AuthToken.getCurrentUser() == null) {
            afficherAlerte(Alert.AlertType.ERROR, "Erreur", "⚠️ Aucun utilisateur connecté. Veuillez vous reconnecter !");
            return;
        }

        String sujet = txtSujet.getText().trim();
        String description = txtDescription.getText().trim();
        int idUser = AuthToken.getCurrentUser().getId_User();

        if (sujet.isEmpty() || description.isEmpty()) {
            afficherAlerte(Alert.AlertType.WARNING, "Champs vides", "Veuillez remplir tous les champs !");
            return;
        }

        Reclamation reclamation = new Reclamation(idUser, sujet, description);
        try {
            serviceReclamation.ajouter(reclamation);
            afficherAlerte(Alert.AlertType.INFORMATION, "Succès", "Réclamation envoyée avec succès !");
            txtSujet.clear();
            txtDescription.clear();
            chargerReclamations();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'ajout de la réclamation", e);
            afficherAlerte(Alert.AlertType.ERROR, "Erreur", "Problème lors de l'ajout !");
        }
    }

    @FXML
    private void supprimerReclamation() {
        String selectedItem = listReclamations.getSelectionModel().getSelectedItem();

        if (selectedItem == null) {
            afficherAlerte(Alert.AlertType.WARNING, "Suppression impossible", "Veuillez sélectionner une réclamation !");
            return;
        }

        int idReclamation = extraireIdReclamation(selectedItem);
        if (idReclamation <= 0) {
            afficherAlerte(Alert.AlertType.ERROR, "Erreur", "Impossible d'extraire l'ID de la réclamation !");
            return;
        }

        // Confirmation avant suppression
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText(null);
        alert.setContentText("Voulez-vous vraiment supprimer cette réclamation ?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                serviceReclamation.supprimer(idReclamation);
                chargerReclamations();  // Rafraîchir la liste après suppression
                afficherAlerte(Alert.AlertType.INFORMATION, "Succès", "Réclamation supprimée avec succès !");
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Erreur lors de la suppression de la réclamation", e);
                afficherAlerte(Alert.AlertType.ERROR, "Erreur", "Problème lors de la suppression !");
            }
        }
    }

    @FXML
    private void rechercherReclamation() {
        String keyword = searchField.getText().trim().toLowerCase();
        if (!keyword.isEmpty()) {
            try {
                List<Reclamation> resultats = serviceReclamation.afficher();
                afficherReclamationsFiltrees(resultats, keyword);
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Erreur lors de la recherche des réclamations", e);
                afficherAlerte(Alert.AlertType.ERROR, "Erreur", "Impossible d'effectuer la recherche !");
            }
        } else {
            chargerReclamations();
        }
    }

    private void chargerReclamations() {
        try {
            List<Reclamation> reclamations = serviceReclamation.afficher();
            afficherReclamations(reclamations);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Problème de connexion à la base", e);
            afficherAlerte(Alert.AlertType.ERROR, "Erreur", "Problème de connexion à la base !");
        }
    }

    private void afficherReclamations(List<Reclamation> reclamations) {
        reclamationList.clear();
        for (Reclamation r : reclamations) {
            reclamationList.add("📝 Sujet: " + r.getSujet() + " | " + r.getDescription() + " | 📌 " + r.getStatut());
        }
        listReclamations.setItems(reclamationList);
    }

    private void afficherReclamationsFiltrees(List<Reclamation> reclamations, String keyword) {
        reclamationList.clear();
        for (Reclamation r : reclamations) {
            if (r.getSujet().toLowerCase().contains(keyword) || r.getDescription().toLowerCase().contains(keyword)) {
                reclamationList.add("📝 ID: " + r.getId_reclamation() + " | " + r.getSujet() + " | 📌 " + r.getStatut());
            }
        }
        listReclamations.setItems(reclamationList);
    }



    private int extraireIdReclamation(String selectedItem) {
        try {
            String[] parts = selectedItem.split("ID: ");
            if (parts.length < 2) {
                throw new IllegalArgumentException("Format de texte invalide");
            }
            String idPart = parts[1].split(" \\| ")[0].trim();  // Extraction plus sûre
            return Integer.parseInt(idPart);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'extraction de l'ID de la réclamation", e);
            afficherAlerte(Alert.AlertType.ERROR, "Erreur", "Impossible d'extraire l'ID de la réclamation !");
            return -1;
        }
    }


    private void afficherAlerte(Alert.AlertType type, String titre, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    @FXML
    private void handleCancel(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/ProfileMembre.fxml"));
        ((Node) event.getSource()).getScene().setRoot(root);
    }
}
