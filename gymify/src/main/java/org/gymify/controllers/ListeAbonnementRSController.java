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
import org.gymify.entities.Salle;
import org.gymify.services.AbonnementService;
import org.gymify.services.SalleService;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class ListeAbonnementRSController {

    @FXML private FlowPane abonnementContainer;
    @FXML private Label salleLabel;
    @FXML private TextField searchField;
    @FXML private ChoiceBox<String> activiteChoiceBox;
    @FXML private Label loadingLabel;

    private int responsableId;
    private final AbonnementService abonnementService = new AbonnementService();
    private final SalleService salleService = new SalleService();

    public void setResponsableId(int responsableId) {
        this.responsableId = responsableId;
        loadGymData();
    }

    @FXML
    public void initialize() {
        setupChoiceBox();

        activiteChoiceBox.setOnAction(event -> loadFilteredAbonnements());
        searchField.textProperty().addListener((observable, oldValue, newValue) -> loadFilteredAbonnements());
    }

    private void setupChoiceBox() {
        try {
            List<String> activities = abonnementService.getActivityTypes();
            System.out.println("Activity Types from Database: " + activities);
            activities.add(0, "All");
            activiteChoiceBox.setItems(FXCollections.observableArrayList(activities));
            activiteChoiceBox.setValue("All");
        } catch (SQLException e) {
            System.err.println("Error loading activity types: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load activity types.");
        }
    }

    @FXML
    private void handleAddAbonnement(ActionEvent event) {
        try {
            Salle salle = abonnementService.getSalleByResponsableId(responsableId);
            if (salle != null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/AbonnementFormRS.fxml"));
                Parent root = loader.load();

                AbonnementFormRSController controller = loader.getController();
                controller.setResponsableId(responsableId);

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Add Subscription");
                stage.show();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "No gym associated with this manager.");
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to verify gym existence: " + e.getMessage());
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to open subscription form.");
        }
    }



    private void loadGymData() {
        try {
            Salle salle = abonnementService.getSalleByResponsableId(responsableId);
            if (salle != null) {
                System.out.println("Gym found: " + salle.getNom());
                salleLabel.setText("Subscriptions for Gym: " + salle.getNom());
                loadFilteredAbonnements();
            } else {
                System.out.println("No gym found for the logged-in manager.");
                showAlert(Alert.AlertType.ERROR, "Error", "No gym found for the logged-in manager.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to retrieve gym data.");
        }
    }

    public void loadFilteredAbonnements() {
        try {
            loadingLabel.setVisible(true);
            abonnementContainer.getChildren().clear();

            String selectedActivity = activiteChoiceBox.getValue();
            String searchQuery = searchField.getText().toLowerCase();

            System.out.println("Selected Activity: " + selectedActivity);
            System.out.println("Search Query: " + searchQuery);

            List<Abonnement> abonnements = abonnementService.afficherParResponsable(responsableId);

            if (!"All".equals(selectedActivity)) {
                abonnements = abonnements.stream()
                        .filter(abonnement -> {
                            String typeActivite = abonnement.getTypeActivite();
                            return typeActivite != null && selectedActivity.equalsIgnoreCase(typeActivite.trim());
                        })
                        .collect(Collectors.toList());
            }

            if (searchQuery != null && !searchQuery.isEmpty()) {
                abonnements = abonnements.stream()
                        .filter(abonnement -> abonnement.getActivite() != null &&
                                abonnement.getActivite().getNom().toLowerCase().contains(searchQuery))
                        .collect(Collectors.toList());
            }

            System.out.println("Filtered Subscriptions Count: " + abonnements.size());

            if (abonnements.isEmpty()) {
                Label emptyLabel = new Label("No subscriptions found");
                emptyLabel.getStyleClass().add("label");
                abonnementContainer.getChildren().add(emptyLabel);
                return;
            }

            for (Abonnement abonnement : abonnements) {
                VBox card = createSubscriptionCard(abonnement);
                abonnementContainer.getChildren().add(card);
            }
        } catch (SQLException e) {
            System.err.println("Error loading subscriptions: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load subscriptions: " + e.getMessage());
        } finally {
            loadingLabel.setVisible(false);
        }
    }

    private VBox createSubscriptionCard(Abonnement abonnement) {
        VBox card = new VBox(10);
        card.getStyleClass().add("subscription-card");
        card.setAlignment(Pos.CENTER);

        Label activiteLabel = new Label("Activity: " + (abonnement.getActivite() != null ? abonnement.getActivite().getNom() : "Not specified"));
        activiteLabel.getStyleClass().add("label");

        Label typeLabel = new Label("Type: " + (abonnement.getActivite() != null ? abonnement.getActivite().getType().toString() : "Not specified"));
        typeLabel.getStyleClass().add("label");

        Label tarif = new Label("Price: " + abonnement.getTarif() + " DT");
        tarif.getStyleClass().add("label");

        Button modifyButton = new Button("Edit");
        modifyButton.getStyleClass().add("button");
        // Load edit icon with fallback
        Image editImage = loadImage("/images/edit.png");
        if (editImage != null) {
            ImageView editIcon = new ImageView(editImage);
            editIcon.setFitWidth(16);
            editIcon.setFitHeight(16);
            modifyButton.setGraphic(editIcon);
        } else {
            System.err.println("Warning: Edit icon not found at /images/edit.png");
        }
        modifyButton.setOnAction(event -> handleModify(abonnement));

        Button deleteButton = new Button("Delete");
        deleteButton.getStyleClass().addAll("button", "button-cancel");
        // Load delete icon with fallback
        Image deleteImage = loadImage("/images/delete.png");
        if (deleteImage != null) {
            ImageView deleteIcon = new ImageView(deleteImage);
            deleteIcon.setFitWidth(16);
            deleteIcon.setFitHeight(16);
            deleteButton.setGraphic(deleteIcon);
        } else {
            System.err.println("Warning: Delete icon not found at /images/delete.png");
        }
        deleteButton.setOnAction(event -> handleDeleteConfirmation(abonnement));

        HBox buttonContainer = new HBox(10, modifyButton, deleteButton);
        buttonContainer.setAlignment(Pos.CENTER);

        card.getChildren().addAll(activiteLabel, typeLabel, tarif, buttonContainer);
        return card;
    }

    // Helper method to load images with error handling
    private Image loadImage(String path) {
        try {
            InputStream stream = getClass().getResourceAsStream(path);
            if (stream == null) {
                System.err.println("Resource not found: " + path);
                return null;
            }
            return new Image(stream);
        } catch (Exception e) {
            System.err.println("Error loading image " + path + ": " + e.getMessage());
            return null;
        }
    }
    private void handleModify(Abonnement abonnement) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AbonnementFormRS.fxml"));
            Parent root = loader.load();
            AbonnementFormRSController controller = loader.getController();
            controller.setResponsableId(responsableId);
            controller.preFillForm(abonnement);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Edit Subscription");
            stage.showAndWait();

            loadFilteredAbonnements();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to open edit form.");
        }
    }

    private void handleDeleteConfirmation(Abonnement abonnement) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText("Are you sure you want to delete this subscription?");
        alert.setContentText("This action is irreversible.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                handleDelete(abonnement);
            }
        });
    }

    private void handleDelete(Abonnement abonnement) {
        try {
            abonnementService.supprimer(abonnement.getId_Abonnement());
            showAlert(Alert.AlertType.INFORMATION, "Success", "Subscription deleted successfully!");
            loadFilteredAbonnements();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete subscription: " + e.getMessage());
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
    private void clearSearch(ActionEvent event) {
        searchField.clear();
        activiteChoiceBox.setValue("All");
        loadFilteredAbonnements();
    }

    @FXML
    public void retourDashboard(ActionEvent actionEvent) {
        try {
            URL resource = getClass().getResource("/DashboardReasponsable.fxml");
            if (resource == null) {
                showAlert(Alert.AlertType.ERROR, "Error", "Dashboard FXML file not found.");
                return;
            }
            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();

            DashboardResponsableSalleController controller = loader.getController();
            // Set responsableId if needed
            // controller.setResponsableId(responsableId);

            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root, 800, 800);
            stage.setScene(scene);
            stage.setTitle("Manager Dashboard");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load the dashboard: " + e.getMessage());
        }
    }
}
