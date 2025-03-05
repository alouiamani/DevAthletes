package org.gymify.controllers;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
import org.gymify.services.AbonnementService;
import org.gymify.services.ActivityService;
import org.gymify.services.SalleService;
import org.gymify.services.ServiceUser;
import java.io.IOException;
import java.sql.Connection;
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
    private AbonnementService abonnementService = new AbonnementService();
    private SalleService salleService = new SalleService();
    private ServiceUser serviceUser = new ServiceUser();
    private ActivityService activiteService = new ActivityService(); // Service for activities
    private Connection connection;
/*
    // Initialize method for setting up the salle and loading abonnements
    public void initialize() {

        setupChoiceBox();

        activiteChoiceBox.setOnAction(event -> loadFilteredAbonnements());
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            loadFilteredAbonnements();});
    }
    private void setupChoiceBox() {
        List<String> activites = new ArrayList<>(activiteService.getActivityTypes());
        if (!activites.contains("Tous")) {
            activites.add( 0,"Tous");  // Assurez-vous que "Tous" est toujours en première position
        }

        activiteChoiceBox.setItems(FXCollections.observableArrayList(activites));

        activiteChoiceBox.setOnAction(event -> loadFilteredAbonnements());
        activiteChoiceBox.setValue("Tous");

    }
    private void loadFilteredAbonnements() {
        try {
            abonnementContainer.getChildren().clear(); // Effacer les abonnements existants

            // Récupérer la sélection du type d'activité et du texte de recherche
            String selectedActivity = activiteChoiceBox.getValue();
            String searchQuery = searchField.getText().toLowerCase();  // Convertir en minuscules pour une recherche insensible à la casse

            List<Abonnement> abonnements;

            // Vérifier le type d'activité sélectionné
            if ("Tous".equals(selectedActivity)) {
                abonnements = abonnementService.afficherParSalle(salleId);
            } else {
                abonnements = abonnementService.getAbonnementsBySalleAndActivityType(salleId, selectedActivity);
            }

            // Filtrer les abonnements selon le texte de recherche dans le champ "searchField"
            abonnements = abonnements.stream()
                    .filter(abonnement -> abonnement.getActivite() != null &&
                            abonnement.getActivite().getNom().toLowerCase().contains(searchQuery)) // Filtrer par nom d'activité
                    .sorted(Comparator.comparing(Abonnement::getDate_Début))  // Tri par date de début
                    .collect(Collectors.toList());

            // Ajouter les abonnements filtrés à l'interface
            abonnements.forEach(ab -> abonnementContainer.getChildren().add(createAbonnementCard(ab)));
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement des abonnements.");
        }}


    // Method to set Salle ID and reload data if necessary
    public void setSalleId(int salleId) {
        this.salleId = salleId;
        System.out.println("Salle ID reçu : " + salleId); // Debug
        loadSalleData(); // Reload salle data when salleId is updated
    }

    // Method to load salle data and abonnements
    private void loadSalleData() {
        System.out.println("Chargement des données de la salle pour ID: " + salleId);

        try {
            Salle salle = salleService.getSalleById(salleId);
            if (salle != null) {
                System.out.println("Salle trouvée: " + salle.getNom());
                salleLabel.setText("Abonnements de la salle: " + salle.getNom());
                loadFilteredAbonnements();  // Reload abonnements after setting salle data
            } else {
                System.out.println("Aucune salle trouvée !");
                showAlert(Alert.AlertType.ERROR, "Erreur", "Salle introuvable.");
            }
        } catch (SQLException e) {
            System.out.println("Erreur SQL: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur SQL lors du chargement des données de salle.");
        }
    }

    // Method to load abonnements based on activity type
    // Method to load abonnements based on the selected activity type
    private void loadAbonnements(int salleId, String activite) throws SQLException {
        abonnementContainer.getChildren().clear(); // Effacer les abonnements actuels

        List<Abonnement> abonnements;
        if ("Tous".equals(activite)) {
            abonnements = abonnementService.afficherParSalle(salleId); // Charger tous les abonnements
        } else {
            abonnements = abonnementService.getAbonnementsByActivityType(ActivityType.valueOf(activite.toUpperCase()));
        }

        // Afficher les abonnements filtrés
        for (Abonnement abonnement : abonnements) {
            VBox card = createAbonnementCard(abonnement);
            abonnementContainer.getChildren().add(card);
        }
    }

    // New method to fetch abonnements filtered by activity type directly from the database

    // Method to handle the Modify action
    private void handleModify(Abonnement abonnement) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AbonnementFormRS.fxml"));
            Parent root = loader.load();
            AbonnementFormRSController controller = loader.getController();
            // Pass the salleId to the form controller
            controller.preFillForm(abonnement); // Fill the form with the details

            Stage stage = (Stage) salleLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Modifier un abonnement");
            stage.show();

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture du formulaire de modification.");
        }
    }

    // Method to create the card for abonnement
    private VBox createAbonnementCard(Abonnement abonnement) {

        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: #ffffff; -fx-padding: 15px; -fx-border-radius: 10px; " +
                "-fx-border-color: #cccccc; -fx-border-width: 1px; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 10, 0.5, 0, 0);");

        Label activiteLabel = new Label("Activité: " + (abonnement.getActivite() != null ? abonnement.getActivite().getNom() : "Non spécifiée"));
        Label typeLabel = new Label("Type: " + (abonnement.getActivite() != null ? abonnement.getActivite().getType().toString() : "Non spécifié"));
        Label dateDebut = new Label("Début: " + abonnement.getDate_Début());
        Label dateFin = new Label("Fin: " + abonnement.getDate_Fin());
        Label tarif = new Label("Tarif: " + abonnement.getTarif() + " DT");

        // Modify button
        Button modifyButton = new Button("Modifier");
        modifyButton.setStyle("-fx-background-color: #ffc107; -fx-text-fill: white; -fx-font-size: 12px; " +
                "-fx-padding: 3px 8px; -fx-border-radius: 5px; -fx-pref-width: 100px; -fx-pref-height: 30px;");
        ImageView editIcon = new ImageView(new Image("images/editer.png"));
        editIcon.setFitWidth(16);  // Icon width
        editIcon.setFitHeight(16); // Icon height
        modifyButton.setGraphic(editIcon);
        modifyButton.setOnAction(event -> handleModify(abonnement));

        // Delete button
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

    // Handle delete confirmation
    private void handleDeleteConfirmation(Abonnement abonnement) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Êtes-vous sûr de vouloir supprimer cet abonnement?");
        alert.setContentText("Cette action est irréversible.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                handleDelete(abonnement);
            }
        });
    }

    // Handle actual deletion
    private void handleDelete(Abonnement abonnement) {
        try {
            abonnementService.supprimer(abonnement.getId_Abonnement());
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Abonnement supprimé avec succès !");
            loadAbonnements(salleId, "Tous"); // Reload abonnements after deletion
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la suppression : " + e.getMessage());
        }
    }

    // Handle filtering abonnements based on selected activity
    @FXML
    private void handleFilter(ActionEvent event) {
        try {
            String activite = activiteChoiceBox.getValue();

            if ("Tous".equalsIgnoreCase(activite)) {
                loadAbonnements(salleId, null);
            } else {
                ActivityType type = ActivityType.valueOf(activite.toUpperCase());
                loadAbonnements(salleId, String.valueOf(type));
            }
        } catch (IllegalArgumentException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Type d'activité invalide.");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du filtrage des abonnements.");
        }
    }

@FXML
    // Handle adding new abonnement
    private void handleAddAbonnement(ActionEvent event) {
        try {
            if (salleService.salleExiste(salleId)) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/AbonnementFormRS.fxml"));
                Parent root = loader.load();

                // Pass salleId to the form controller
                AbonnementFormRSController controller = loader.getController();
                controller.setSalleId(salleId);

                // Get the current stage from the event
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Add un abonnement");
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


    // Method to check if the salle exists in the database


    // Method to show alert dialog
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Handle returning to dashboard
    @FXML
    private void retourDashboard(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DashboardReasponsable.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du retour au dashboard.");
        }

    }*/

}

