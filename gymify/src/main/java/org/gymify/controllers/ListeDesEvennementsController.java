package org.gymify.controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.gymify.entities.Event;
import org.gymify.services.EventService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class ListeDesEvennementsController {

    @FXML
    private TextField searchField;
    @FXML
    private Button btnAjouter;
    @FXML
    private VBox vboxEvenements;
    private final EventService eventService = new EventService();

    @FXML
    public void initialize() {
        // Ajouter un listener pour la recherche
        searchField.textProperty().addListener((observable, oldValue, newValue) -> loadEvents(newValue.trim()));

        // Charger les événements au démarrage
        loadEvents("");

        // Associer l'action au bouton "Ajouter Événement"
        btnAjouter.setOnAction(e -> openAjoutEvenement());

        // Ajouter un listener pour la propriété scene de searchField afin de gérer la fermeture
        searchField.sceneProperty().addListener(new ChangeListener<Scene>() {
            @Override
            public void changed(ObservableValue<? extends Scene> observable, Scene oldValue, Scene newValue) {
                if (newValue != null && searchField.getScene() != null) {
                    searchField.sceneProperty().removeListener(this);
                }
            }
        });
    }

    @FXML
    public void loadEvents(String searchQuery) {
        try {
            List<Event> events = eventService.recupererTous(searchQuery);
            vboxEvenements.getChildren().clear();

            if (events.isEmpty()) {
                Label noEventsLabel = new Label("Aucun événement trouvé pour la recherche : \"" + searchQuery + "\"");
                noEventsLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #555;");
                vboxEvenements.getChildren().add(noEventsLabel);
            } else {
                for (Event event : events) {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/CardEvennement.fxml"));
                    Parent eventCard = loader.load();
                    CardEvennementController controller = loader.getController();
                    controller.setEvenement(event, this);
                    vboxEvenements.getChildren().add(eventCard);
                }
            }
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors du chargement des événements depuis la base de données : " + e.getMessage(), ButtonType.OK);
            alert.showAndWait();
            e.printStackTrace();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors du chargement des cartes d'événements : " + e.getMessage(), ButtonType.OK);
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    private void openAjoutEvenement() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjoutEvennement.fxml"));
            Parent root = loader.load();
            AjoutEvennementController controller = loader.getController();
            controller.setParentController(this);
            Stage stage = new Stage();
            stage.setTitle("Ajouter un événement");
            stage.setScene(new Scene(root));
            stage.setOnHidden(e -> loadEvents(searchField.getText().trim()));
            stage.show();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors de l'ouverture de l'ajout : " + e.getMessage(), ButtonType.OK);
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    public String getSearchFieldText() {
        return searchField != null ? searchField.getText().trim() : "";
    }
}