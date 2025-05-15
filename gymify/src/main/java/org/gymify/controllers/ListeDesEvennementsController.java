package org.gymify.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.gymify.entities.Event;
import org.gymify.services.EventService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ListeDesEvennementsController {

    @FXML
    private VBox vboxEvennements;
    @FXML
    private ScrollPane scrollPaneEvennements;
    @FXML
    private TextField searchField;
    @FXML
    private Button btnAjouter;

    private final EventService eventService = new EventService();
    private static final Logger LOGGER = Logger.getLogger(ListeDesEvennementsController.class.getName());

    @FXML
    public void initialize() {
        if (vboxEvennements == null || scrollPaneEvennements == null || searchField == null || btnAjouter == null) {
            LOGGER.severe("One or more FXML elements are not properly initialized: " +
                    "vboxEvennements=" + vboxEvennements + ", " +
                    "scrollPaneEvennements=" + scrollPaneEvennements + ", " +
                    "searchField=" + searchField + ", " +
                    "btnAjouter=" + btnAjouter);
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur d'initialisation de l'interface. Vérifiez le fichier FXML.", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        loadEvents("");
        searchField.textProperty().addListener((observable, oldValue, newValue) -> loadEvents(newValue));
    }

    public void loadEvents(String searchQuery) {
        if (vboxEvennements == null) {
            LOGGER.severe("vboxEvennements is null. Cannot load events.");
            return;
        }

        vboxEvennements.getChildren().clear();
        try {
            List<Event> events = eventService.recupererTous(searchQuery);
            LOGGER.info("Chargement de " + events.size() + " événements pour la requête : " + searchQuery);
            for (Event event : events) {
                LOGGER.info("Chargement de l'événement : " + event);
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/CardEvennement.fxml"));
                if (loader.getLocation() == null) {
                    LOGGER.severe("Cannot find CardEvennement.fxml. Ensure the file exists in src/main/resources/");
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur: Cannot find CardEvennement.fxml.", ButtonType.OK);
                    alert.showAndWait();
                    return;
                }
                AnchorPane card = loader.load();
                CardEvennementController controller = loader.getController();
                controller.setEvenement(event, this);
                vboxEvennements.getChildren().add(card);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du chargement des événements", e);
            Alert alert = new Alert(Alert.AlertType.ERROR, "Impossible de charger les événements: " + e.getMessage(), ButtonType.OK);
            alert.showAndWait();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du chargement de la carte FXML", e);
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors du chargement de la carte FXML: " + e.getMessage(), ButtonType.OK);
            alert.showAndWait();
        }
    }

    @FXML
    private void ajouterEvenement(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjoutEvennement.fxml"));
            if (loader.getLocation() == null) {
                LOGGER.severe("Cannot find AjoutEvennement.fxml. Ensure the file exists in src/main/resources/");
                Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur: Cannot find AjoutEvennement.fxml.", ButtonType.OK);
                alert.showAndWait();
                return;
            }
            Parent root = loader.load();
            AjoutEvennementController controller = loader.getController();
            controller.setParentController(this);

            Stage stage = new Stage();
            stage.setTitle("Ajouter Événement");
            stage.setScene(new Scene(root));
            stage.setOnHidden(e -> loadEvents(""));
            stage.show();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'ouverture de la fenêtre d'ajout d'événement", e);
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors de l'ouverture de la fenêtre d'ajout: " + e.getMessage(), ButtonType.OK);
            alert.showAndWait();
        }
    }

    private void modifierEvent(Event event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjoutEvennement.fxml"));
            if (loader.getLocation() == null) {
                LOGGER.severe("Cannot find AjoutEvennement.fxml. Ensure the file exists in src/main/resources/");
                Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur: Cannot find AjoutEvennement.fxml.", ButtonType.OK);
                alert.showAndWait();
                return;
            }
            Parent root = loader.load();
            AjoutEvennementController controller = loader.getController();
            controller.setEvenement(event, this);

            Stage stage = new Stage();
            stage.setTitle("Modifier Événement");
            stage.setScene(new Scene(root));
            stage.setOnHidden(e -> loadEvents(""));
            stage.show();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'ouverture de la fenêtre de modification", e);
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors de l'ouverture de la fenêtre de modification: " + e.getMessage(), ButtonType.OK);
            alert.showAndWait();
        }
    }

    private void supprimerEvent(int eventId) {
        try {
            eventService.supprimer(eventId);
            loadEvents("");
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Événement supprimé avec succès !", ButtonType.OK);
            alert.showAndWait();
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors de la suppression de l'événement : " + e.getMessage(), ButtonType.OK);
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    public String getSearchFieldText() {
        return searchField != null ? searchField.getText() : "";
    }

    @FXML
    private void ListeEquipes(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListeDesEquipes.fxml"));
            if (loader.getLocation() == null) {
                LOGGER.severe("Cannot find ListeDesEquipes.fxml. Ensure the file exists in src/main/resources/");
                Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur: Cannot find ListeDesEquipes.fxml.", ButtonType.OK);
                alert.showAndWait();
                return;
            }
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("📩 Gérer mes Events");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'ouverture de la fenêtre des équipes", e);
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors de l'ouverture de la fenêtre des équipes: " + e.getMessage(), ButtonType.OK);
            alert.showAndWait();
        }
    }
}