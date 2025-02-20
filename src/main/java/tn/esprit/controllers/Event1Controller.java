package tn.esprit.controllers;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import tn.esprit.entities.Event;
import tn.esprit.services.EventService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Event1Controller {

    @FXML private VBox eventsContainer;
    @FXML private Button ajouterBtn;

    private final EventService eventService = new EventService();
    private static final Logger logger = Logger.getLogger(Event1Controller.class.getName());

    @FXML
    public void initialize() {
        // Bouton d'ajout d'événement
        ajouterBtn.setOnAction(event -> ajouterevent());

        // Charger les événements au démarrage
        loadEvents();
    }

    /**
     * Charge et affiche tous les événements disponibles.
     */
    @FXML
    private void loadEvents() {
        eventsContainer.getChildren().clear();

        try {
            List<Event> events = eventService.getAllEvents();
            for (Event event : events) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/ressources/Cards.fxml"));
                Parent eventCard = loader.load();

                // Vérification de l'initialisation du contrôleur
                CardsController controller = loader.getController();
                if (controller != null) {
                    controller.setEvent(event, this);
                    eventsContainer.getChildren().add(eventCard);
                } else {
                    logger.log(Level.WARNING, "Impossible d'initialiser CardsController pour l'événement: " + event.getNom());
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erreur lors de la récupération des événements depuis la base de données", e);
            showAlert(Alert.AlertType.ERROR, "Erreur Base de Données", "Impossible de récupérer les événements.");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erreur lors du chargement du fichier FXML pour les cartes d'événements", e);
            showAlert(Alert.AlertType.ERROR, "Erreur FXML", "Impossible de charger les événements.");
        }
    }

    /**
     * Ouvre la fenêtre d'ajout d'un nouvel événement et rafraîchit la liste après ajout.
     */
    @FXML
    private void ajouterevent() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/resources/ServiceEvent.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Ajouter un événement");
            stage.showAndWait(); // Attend la fermeture avant de rafraîchir
            loadEvents();
        } catch (IOException e) {
            e.printStackTrace();
            logger.log(Level.SEVERE, "Erreur lors de l'ouverture du formulaire d'ajout", e);
            showAlert(Alert.AlertType.ERROR, "Erreur d'affichage", "Impossible d'ouvrir la fenêtre d'ajout.");
        }
    }



    /**
     * Rafraîchit la liste des événements après une modification ou suppression.
     */
    public void refreshList() {
        loadEvents();
    }

    /**
     * Affiche une boîte de dialogue d'alerte.
     */
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
