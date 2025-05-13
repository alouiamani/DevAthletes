package org.gymify.controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import org.gymify.entities.Reponse;
import org.gymify.services.ServiceReclamation ;
import org.gymify.entities.Reclamation;
import org.gymify.services.ServiceReponse;
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
    private TextField searchField, txtSujet;

    @FXML
    private TextArea txtDescription, txtReponseAdmin;

    @FXML
    private ListView<String> listReclamations;

    @FXML
    private ComboBox<String> filtreStatut;

    @FXML
    private Button btnEnvoyer, btnSupprimer, btnCancel, btnRechercher;

    private final ObservableList<String> reclamationList = FXCollections.observableArrayList();
    private final ServiceReclamation serviceReclamation = new ServiceReclamation();
    private final ServiceReponse serviceReponse = new ServiceReponse();

    private static final Logger LOGGER = Logger.getLogger(ReclamationSportifController.class.getName());

    private List<Reclamation> reclamations;
    private Reclamation selectedReclamation = null;


    @FXML
    public void initialize() {
        setupFiltreStatut();
        chargerReclamations();

        listReclamations.setOnMouseClicked(event -> {
            try {
                afficherReponse(event);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    /** Initialisation du filtre de statut */
    private void setupFiltreStatut() {
        filtreStatut.getItems().addAll("Tous", "⏳ En attente", "✅ Traité");
        filtreStatut.setValue("Tous");
        filtreStatut.setOnAction(event -> filtrerParStatut());
    }

    /** Charge et affiche les réclamations du sportif connecté */
    private void chargerReclamations() {
        try {
            reclamations = serviceReclamation.recupererParSportif(AuthToken.getCurrentUser().getId());
            afficherReclamations(reclamations);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur de chargement des réclamations", e);
        }
    }

    /** Filtrage par statut */
    private void filtrerParStatut() {
        String statutSelectionne = filtreStatut.getValue();
        try {
            List<Reclamation> filteredList = serviceReclamation.recupererParSportif(AuthToken.getCurrentUser().getId());

            if (!"Tous".equals(statutSelectionne)) {
                filteredList.removeIf(r -> {
                    Reponse rep = serviceReponse.recupererParReclamation(r.getId());
                    return ("✅ Traité".equals(statutSelectionne) && rep == null) ||
                            ("⏳ En attente".equals(statutSelectionne) && rep != null);
                });
            }

            afficherReclamations(filteredList);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du filtrage", e);
        }
    }

    /** Affichage des réclamations */
    private void afficherReclamations(List<Reclamation> list) throws SQLException {
        reclamationList.clear();
        for (Reclamation r : list) {
            Reponse rep = serviceReponse.recupererParReclamation(r.getId());
            String statut = (rep != null) ? "✅ Traité" : "⏳ En attente";
            reclamationList.add("📝 " + r.getSujet() + " | 📌 " + statut);
        }
        listReclamations.setItems(reclamationList);
    }

    /** Affichage de la réponse admin */
    private void afficherReponse(MouseEvent event) throws SQLException {
        int selectedIndex = listReclamations.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            selectedReclamation = reclamations.get(selectedIndex);
            Reponse reponse = serviceReponse.recupererParReclamation(selectedReclamation.getId());
            if (reponse != null) {
                txtReponseAdmin.setText("🗨 Réponse : " + reponse.getMessage());
            } else {
                txtReponseAdmin.setText("⏳ En attente de réponse de l'admin...");
            }
        }
    }

    /** Recherche une réclamation */


    /** Envoi d'une nouvelle réclamation */
    @FXML
    private void envoyerReclamation() {
        String sujet = txtSujet.getText().trim();
        String description = txtDescription.getText().trim();

        if (sujet.isEmpty() || description.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Veuillez remplir tous les champs !");
            return;
        }

        Reclamation nouvelleReclamation = new Reclamation(AuthToken.getCurrentUser().getId(), sujet, description);

        try {
            serviceReclamation.ajouter(nouvelleReclamation);
            showAlert(Alert.AlertType.INFORMATION, "Réclamation envoyée avec succès !");
            txtSujet.clear();
            txtDescription.clear();
            chargerReclamations();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'envoi", e);
            showAlert(Alert.AlertType.ERROR, "Erreur lors de l'envoi !");
        }
    }

    /** Suppression d'une réclamation */
    @FXML
    private void supprimerReclamation() {
        int selectedIndex = listReclamations.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0) {
            showAlert(Alert.AlertType.WARNING, "Veuillez sélectionner une réclamation à supprimer !");
            return;
        }

        selectedReclamation = reclamations.get(selectedIndex);

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Voulez-vous vraiment supprimer cette réclamation ?");
        alert.setContentText("Cette action est irréversible !");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                serviceReclamation.supprimer(selectedReclamation.getId());
                showAlert(Alert.AlertType.INFORMATION, "Réclamation supprimée !");
                chargerReclamations();
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Erreur lors de la suppression", e);
                showAlert(Alert.AlertType.ERROR, "Erreur lors de la suppression !");
            }
        }
    }

    /** Affiche une alerte */
    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setContentText(message);
        alert.show();
    }
}
