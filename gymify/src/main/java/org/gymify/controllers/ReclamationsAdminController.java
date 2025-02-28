package org.gymify.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import org.gymify.services.ServiceReclamation;
import org.gymify.services.ServiceReponse;
import org.gymify.services.ServiceUser;
import org.gymify.entities.Reclamation;
import org.gymify.entities.Reponse;
import org.gymify.entities.User;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ReclamationsAdminController {

    @FXML private VBox listContainer;
    @FXML private TextArea textReponse;
    @FXML private Button btnEnvoyer, btnToutSelectionner;

    private final ServiceReclamation serviceReclamation = new ServiceReclamation();
    private final ServiceReponse serviceReponse = new ServiceReponse();
    private List<Reclamation> reclamationList;
    private final Map<CheckBox, Reclamation> selectedReclamations = new HashMap<>();

    public void initialize() {
        chargerReclamations();
    }

    private void chargerReclamations() {
        try {
            reclamationList = serviceReclamation.afficher();
            listContainer.getChildren().clear();
            ServiceUser serviceUtilisateur = new ServiceUser();
            selectedReclamations.clear();

            for (Reclamation rec : reclamationList) {
                Optional<User> utilisateur = serviceUtilisateur.getUtilisateurById(rec.getId_user());
                String utilisateurNom = utilisateur.map(u -> u.getNom() + " " + u.getPrenom()).orElse("Utilisateur inconnu");

                // Création d'une carte avec une case à cocher
                HBox card = new HBox(10);
                card.setStyle("-fx-background-color: #f4f4f4; -fx-padding: 10px; -fx-border-radius: 5px; -fx-border-color: #ccc;");
                card.setPrefWidth(300);

                // Case à cocher pour sélectionner la réclamation
                CheckBox checkBox = new CheckBox();
                checkBox.setOnAction(event -> toggleSelection(checkBox, rec));

                // Sujet de la réclamation
                Text sujetText = new Text("📌 " + utilisateurNom + " | Sujet : " + rec.getSujet());
                sujetText.setFont(new Font(12));

                // Ajout des éléments dans la carte
                card.getChildren().addAll(checkBox, sujetText);
                listContainer.getChildren().add(card);

                // Stocker la correspondance entre la CheckBox et la réclamation
                selectedReclamations.put(checkBox, rec);
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger les réclamations.");
        }
    }

    private void toggleSelection(CheckBox checkBox, Reclamation rec) {
        if (checkBox.isSelected()) {
            selectedReclamations.put(checkBox, rec);
        } else {
            selectedReclamations.remove(checkBox);
        }
    }

    @FXML
    private void toutSelectionner() {
        boolean allSelected = selectedReclamations.size() == reclamationList.size();
        selectedReclamations.clear();

        for (CheckBox checkBox : selectedReclamations.keySet()) {
            checkBox.setSelected(!allSelected);
            if (!allSelected) {
                selectedReclamations.put(checkBox, selectedReclamations.get(checkBox));
            }
        }
    }

    @FXML
    private void ajouterReponse() {
        if (selectedReclamations.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Avertissement", "Veuillez sélectionner au moins une réclamation.");
            return;
        }

        String message = textReponse.getText().trim();
        if (message.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Avertissement", "Le message de réponse ne peut pas être vide.");
            return;
        }

        try {
            for (Reclamation rec : selectedReclamations.values()) {
                Reponse reponse = new Reponse(rec.getId_reclamation(), message);
                serviceReponse.ajouter(reponse);
            }

            showAlert(Alert.AlertType.INFORMATION, "Succès", "Réponses envoyées avec succès !");
            textReponse.clear();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur SQL", "Impossible d'ajouter les réponses.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    @FXML
    private void handleCancel(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/AdminDash.fxml"));
        ((Node) event.getSource()).getScene().setRoot(root);
    }
}
