package org.gymify.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.gymify.entities.Reclamation;
import org.gymify.entities.Reponse;
import org.gymify.entities.User;
import org.gymify.services.ServiceReclamation;
import org.gymify.services.ServiceReponse;
import org.gymify.services.ServiceUser;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ReclamationsAdminController {
    @FXML
    private VBox listReponsesContainer;
    @FXML private VBox listContainer;
    @FXML private TextArea textReponse;

    private final ServiceReclamation serviceReclamation = new ServiceReclamation();
    private final ServiceReponse serviceReponse = new ServiceReponse();
    private final List<Reclamation> selectedReclamations = new ArrayList<>();
    private final List<Reponse> selectedReponses = new ArrayList<>();
    private final List<CheckBox> checkBoxList = new ArrayList<>();
    private final List<CheckBox> checkBoxReponsesList = new ArrayList<>();

    private int idAdmin; // ID de l'administrateur

    public void initialize() {
        try {
            // Récupérer l'utilisateur administrateur connecté (vous pouvez ajuster cette partie selon votre logique)
            ServiceUser serviceUtilisateur = new ServiceUser();
            List<User> adminList = serviceUtilisateur.getUtilisateurByRole("Admin"); // Recherche l'utilisateur avec le rôle 'Admin'

            if (!adminList.isEmpty()) {
                idAdmin = adminList.get(0).getId_User(); // Récupérer l'ID de l'administrateur
            } else {
                throw new SQLException("Administrateur introuvable.");
            }

            chargerReclamations();
            chargerReponses();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement des données : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void chargerReclamations() {
        try {
            List<Reclamation> reclamationList = serviceReclamation.afficher();
            listContainer.getChildren().clear();
            selectedReclamations.clear();
            checkBoxList.clear();
            ServiceUser serviceUtilisateur = new ServiceUser();

            for (Reclamation rec : reclamationList) {
                Optional<User> utilisateur = serviceUtilisateur.getUtilisateurById(rec.getId_user());
                String utilisateurNom = utilisateur.map(u -> u.getNom() + " " + u.getPrenom()).orElse("Utilisateur inconnu");

                HBox card = new HBox(10);
                card.setStyle("-fx-background-color: #f4f4f4; -fx-padding: 10px; -fx-border-radius: 5px; -fx-border-color: #ccc;");
                card.setPrefWidth(300);

                CheckBox checkBox = new CheckBox();
                checkBox.setUserData(rec);
                checkBox.setOnAction(event -> toggleSelection(checkBox));

                Text sujetText = new Text("\uD83D\uDCCC " + utilisateurNom + " | Sujet : " + rec.getSujet() + " | Description : " + rec.getDescription());
                sujetText.setFont(new Font(12));

                checkBoxList.add(checkBox);
                card.getChildren().addAll(checkBox, sujetText);
                listContainer.getChildren().add(card);
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger les réclamations.");
            e.printStackTrace();
        }
    }

    private void chargerReponses() throws SQLException {
        if (listReponsesContainer == null) {
            System.err.println("Erreur : listReponsesContainer est null.");
            return;
        }

        listReponsesContainer.getChildren().clear(); // Vider avant de recharger
        checkBoxReponsesList.clear(); // Vider la liste des CheckBox

        List<Reponse> reponseList = serviceReponse.afficher(); // Récupère toutes les réponses

        for (Reponse rep : reponseList) {
            HBox card = new HBox(10);
            card.setStyle("-fx-background-color: #e3f2fd; -fx-padding: 10px; -fx-border-radius: 5px; -fx-border-color: #90caf9;");
            card.setPrefWidth(300);

            CheckBox checkBox = new CheckBox();
            checkBox.setUserData(rep); // Stocker l'objet Reponse
            checkBox.setOnAction(event -> toggleSelectionReponse(checkBox));

            Text text = new Text("📨 " + rep.getMessage());
            text.setFont(new Font(12));

            checkBoxReponsesList.add(checkBox);
            card.getChildren().addAll(checkBox, text);
            listReponsesContainer.getChildren().add(card);
        }
    }

    private void toggleSelection(CheckBox checkBox) {
        Reclamation rec = (Reclamation) checkBox.getUserData();
        if (checkBox.isSelected()) {
            selectedReclamations.add(rec);
        } else {
            selectedReclamations.remove(rec);
        }
    }

    private void toggleSelectionReponse(CheckBox checkBox) {
        Reponse rep = (Reponse) checkBox.getUserData();
        if (checkBox.isSelected()) {
            selectedReponses.add(rep);
        } else {
            selectedReponses.remove(rep);
        }
    }

    @FXML
    private void toutSelectionner() {
        boolean allSelected = selectedReclamations.size() == checkBoxList.size();
        selectedReclamations.clear();

        for (CheckBox box : checkBoxList) {
            box.setSelected(!allSelected);
            if (!allSelected) {
                selectedReclamations.add((Reclamation) box.getUserData());
            }
        }
    }

    @FXML
    private void toutSelectionnerReponses() {
        boolean allSelected = checkBoxReponsesList.stream().allMatch(CheckBox::isSelected);
        selectedReponses.clear();

        for (CheckBox box : checkBoxReponsesList) {
            box.setSelected(!allSelected);
            if (!allSelected) {
                selectedReponses.add((Reponse) box.getUserData());
            }
        }
    }

    @FXML
    private void supprimerReponses() {
        if (selectedReponses.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Avertissement", "Veuillez sélectionner au moins une réponse à supprimer.");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Voulez-vous vraiment supprimer les réponses sélectionnées ?");
        Optional<ButtonType> result = confirmation.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                for (Reponse rep : selectedReponses) {
                    serviceReponse.supprimer(rep.getId_Reponse());
                }

                showAlert(Alert.AlertType.INFORMATION, "Succès", "Réponses supprimées avec succès !");
                selectedReponses.clear();
                chargerReponses();
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la suppression des réponses : " + e.getMessage());
                e.printStackTrace();
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

        System.out.println("Réclamations sélectionnées : " + selectedReclamations.size());

        try {
            for (Reclamation rec : selectedReclamations) {
                // Utiliser l'ID de l'administrateur récupéré dynamiquement
                Reponse reponse = new Reponse(rec.getId_reclamation(), idAdmin, message); // ID de l'administrateur dynamique
                serviceReponse.ajouter(reponse);
            }

            showAlert(Alert.AlertType.INFORMATION, "Succès", "Réponses envoyées avec succès !");
            textReponse.clear();
            selectedReclamations.clear();

            // Recharge les réclamations et réponses après ajout
            chargerReclamations();
            chargerReponses();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ajout de la réponse : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
