package org.gymify.controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.gymify.entities.Abonnement;
import org.gymify.entities.ActivityType;
import org.gymify.entities.Salle;
import org.gymify.entities.User;
import org.gymify.services.AbonnementService;
import org.gymify.services.ActivityService;
import org.gymify.services.SalleService;
import org.gymify.utils.AuthToken;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ListeAbonnementRSController {

    @FXML
    private FlowPane abonnementContainer;
    @FXML
    private Label salleLabel;
    @FXML
    private TextField searchField;
    @FXML
    private ChoiceBox<String> activiteChoiceBox;

    private int salleId;
    private final AbonnementService abonnementService = new AbonnementService();
    private final SalleService salleService = new SalleService();
    private final ActivityService activiteService = new ActivityService();

    // Méthode pour définir l'ID de la salle
    public void setSalleId(int salleId) {
        this.salleId = salleId;
        loadSalleData(); // Charger les données de la salle
    }

    // Initialisation du contrôleur
    @FXML
    public void initialize() {
        setupChoiceBox(); // Configurer la ChoiceBox pour les activités

        // Ajouter des écouteurs pour les filtres
        activiteChoiceBox.setOnAction(event -> loadFilteredAbonnements());
        searchField.textProperty().addListener((observable, oldValue, newValue) -> loadFilteredAbonnements());
    }

    private void setupChoiceBox() {
        try {
            List<String> activites = abonnementService.getActivityTypes();
            System.out.println("Activity Types from Database: " + activites); // Debug log
            activites.add(0, "Tous"); // Add "Tous" at the beginning
            activiteChoiceBox.setItems(FXCollections.observableArrayList(activites));
            activiteChoiceBox.setValue("Tous"); // Set default value
        } catch (SQLException e) {
            System.err.println("Erreur lors du chargement des types d'activités : " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement des types d'activités.");
        }
    }
    @FXML
    private void handleAddAbonnement(ActionEvent event) {
        try {
            if (salleService.salleExiste(salleId)) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/AbonnementFormRS.fxml"));
                Parent root = loader.load();

                // Pass salleId to the form controller
                AbonnementFormRSController controller = loader.getController();
                controller.setSalleId(salleId);

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Modifier un abonnement");
                stage.show();
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur", "La salle avec l'ID " + salleId + " n'existe pas.");
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la vérification de l'existence de la salle : " + e.getMessage());
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture du formulaire d'abonnement.");
        }
    }
    // Charger les abonnements filtrés
    public void loadFilteredAbonnements() {
        try {
            abonnementContainer.getChildren().clear(); // Effacer les abonnements existants

            // Récupérer l'activité sélectionnée et la requête de recherche
            String selectedActivity = activiteChoiceBox.getValue();
            String searchQuery = searchField.getText().toLowerCase(); // Recherche insensible à la casse

            // Debug logs
            System.out.println("Selected Activity: " + selectedActivity);
            System.out.println("Search Query: " + searchQuery);

            // Récupérer les abonnements de la salle
            List<Abonnement> abonnements = abonnementService.afficherParSalle(salleId);

            // Appliquer le filtre par activité
            if (!"Tous".equals(selectedActivity)) {
                abonnements = abonnements.stream()
                        .filter(abonnement -> {
                            String typeActivite = abonnement.getTypeActivite();
                            return typeActivite != null && selectedActivity.equalsIgnoreCase(typeActivite.trim());
                        })
                        .collect(Collectors.toList());
            }

            // Appliquer le filtre par texte de recherche
            if (searchQuery != null && !searchQuery.isEmpty()) {
                abonnements = abonnements.stream()
                        .filter(abonnement -> abonnement.getActivite() != null &&
                                abonnement.getActivite().getNom().contains(searchQuery))
                        .collect(Collectors.toList());
            }

            // Debug log pour les abonnements filtrés
            System.out.println("Filtered Abonnements Count: " + abonnements.size());

            // Afficher les abonnements filtrés
            for (Abonnement abonnement : abonnements) {
                VBox card = createAbonnementCard(abonnement);
                abonnementContainer.getChildren().add(card);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors du chargement des abonnements : " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement des abonnements.");
        }
    }

    private void loadSalleData() {
        try {
            Salle salle = salleService.getSalleForCurrentResponsable();
            if (salle != null) {
                System.out.println("Salle trouvée : " + salle.getNom());
                salleLabel.setText("Abonnements de la salle: " + salle.getNom());
                loadFilteredAbonnements(); // Recharger les abonnements
            } else {
                System.out.println("Aucune salle trouvée pour le responsable connecté.");
                showAlert(Alert.AlertType.ERROR, "Erreur", "Aucune salle trouvée pour le responsable connecté.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la récupération de la salle.");
        }
    }



    // Créer une carte pour un abonnement
    private VBox createAbonnementCard(Abonnement abonnement) {
        VBox card = new VBox(10);
        abonnementContainer.setHgap(10); // Espace horizontal entre les cartes
        abonnementContainer.setVgap(10); // Espace vertical entre les cartes
        abonnementContainer.setPadding(new Insets(10)); // Marge intérieure
        card.setStyle("-fx-background-color: #ffffff; -fx-padding: 15px; -fx-border-radius: 10px; " +
                "-fx-border-color: #cccccc; -fx-border-width: 1px; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 10, 0.5, 0, 0);");

        Label activiteLabel = new Label("Activité: " + (abonnement.getActivite() != null ? abonnement.getActivite().getNom() : "Non spécifiée"));
        Label typeLabel = new Label("Type: " + (abonnement.getActivite() != null ? abonnement.getActivite().getType().toString() : "Non spécifié"));
        Label dateDebut = new Label("Début: " + abonnement.getDate_Début());
        Label dateFin = new Label("Fin: " + abonnement.getDate_Fin());
        Label tarif = new Label("Tarif: " + abonnement.getTarif() + " DT");

        // Bouton Modifier
        Button modifyButton = new Button("Modifier");
        modifyButton.setStyle("-fx-background-color: #ffc107; -fx-text-fill: white; -fx-font-size: 12px; " +
                "-fx-padding: 3px 8px; -fx-border-radius: 5px; -fx-pref-width: 100px; -fx-pref-height: 30px;");
        ImageView editIcon = new ImageView(new Image("images/editer.png"));
        editIcon.setFitWidth(16);
        editIcon.setFitHeight(16);
        modifyButton.setGraphic(editIcon);
        modifyButton.setOnAction(event -> handleModify(abonnement));

        // Bouton Supprimer
        Button deleteButton = new Button("Supprimer");
        deleteButton.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-font-size: 12px; " +
                "-fx-padding: 3px 8px; -fx-border-radius: 5px; -fx-pref-width: 100px; -fx-pref-height: 30px;");
        ImageView deleteIcon = new ImageView(new Image("images/delete.png"));
        deleteIcon.setFitWidth(16);
        deleteIcon.setFitHeight(16);
        deleteButton.setGraphic(deleteIcon);
        deleteButton.setOnAction(event -> handleDeleteConfirmation(abonnement));

        HBox buttonContainer = new HBox(10, modifyButton, deleteButton);
        buttonContainer.setAlignment(Pos.CENTER);

        card.getChildren().addAll(activiteLabel, typeLabel, dateDebut, dateFin, tarif, buttonContainer);
        return card;
    }

    // Gérer la modification d'un abonnement
    private void handleModify(Abonnement abonnement) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AbonnementFormRS.fxml"));
            Parent root = loader.load();
            AbonnementFormRSController controller = loader.getController();
            controller.preFillForm(abonnement); // Pré-remplir le formulaire
            int salleId = getSalleIdForConnectedUser(); // Récupérer l'ID de la salle du responsable connecté
            controller.setSalleId(salleId); // Passer l'ID de la salle au contrôleur
            controller.preFillForm(abonnement); // Pré-remplir le formulaire

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Modifier un abonnement");
            stage.showAndWait(); // Attendre que la fenêtre de modification soit fermée

            // Recharger les abonnements après la modification
            loadFilteredAbonnements();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture du formulaire de modification.");
        }
    }

    private int getSalleIdForConnectedUser() {
        User connectedUser = AuthToken.getCurrentUser(); // Récupérer l'utilisateur connecté
        if (connectedUser != null && "responsable_salle".equals(connectedUser.getRole())) {
            return connectedUser.getId_Salle(); // Assure-toi que l'entité User a bien un champ salleId
        } else {
            throw new IllegalStateException("Utilisateur non connecté ou non autorisé.");
        }
    }

    // Gérer la suppression d'un abonnement
    private void handleDeleteConfirmation(Abonnement abonnement) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Êtes-vous sûr de vouloir supprimer cet abonnement ?");
        alert.setContentText("Cette action est irréversible.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                handleDelete(abonnement);
            }
        });
    }

    // Supprimer un abonnement
    private void handleDelete(Abonnement abonnement) {
        try {
            abonnementService.supprimer(abonnement.getId_Abonnement());
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Abonnement supprimé avec succès !");
            loadFilteredAbonnements(); // Recharger les abonnements après suppression
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la suppression : " + e.getMessage());
        }
    }

    // Afficher une alerte
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void retourDashboard(ActionEvent actionEvent) {
        try {
            // Charger le fichier FXML de la liste des salles
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DashboardReasponsable.fxml"));
            Parent root = loader.load();

            // Créer une nouvelle scène avec la liste des salles
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Dashboard Responsable");

            // Afficher la nouvelle scène
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}