package tn.esprit.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import tn.esprit.entities.Event;
import tn.esprit.entities.EventType;
import tn.esprit.services.EventService;

import java.io.File;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class ServiceEventController {

    @FXML
    private DatePicker datetf;
    @FXML
    private TextArea descriptiontf;
    @FXML
    private TextField heuredebuttf, heurefintf, imagetf, lieutf, nomtf, rewardtf;
    @FXML
    private ImageView imagetf1;
    @FXML
    private ComboBox<EventType> typetf;

    private String imageUrl;
    private final EventService eventService = new EventService();

    @FXML
    public void initialize() {
        typetf.getItems().setAll(EventType.values());
    }

    @FXML
    void uploadBtn(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"));
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            imageUrl = file.toURI().toString();
            imagetf.setText(file.getAbsolutePath());
            imagetf1.setImage(new Image(imageUrl));
        }
    }

    @FXML
    void enregistrer(ActionEvent event) {
        if (!validerChamps()) {
            afficherMessage(Alert.AlertType.WARNING, "Attention", "Tous les champs doivent être remplis.");
            return;
        }

        try {
            Event newEvent = new Event(
                    nomtf.getText(),
                    lieutf.getText(),
                    datetf.getValue(),
                    LocalTime.parse(heuredebuttf.getText(), DateTimeFormatter.ofPattern("HH:mm")),
                    LocalTime.parse(heurefintf.getText(), DateTimeFormatter.ofPattern("HH:mm")),
                    typetf.getValue(),
                    descriptiontf.getText(),
                    imageUrl,
                    rewardtf.getText()
            );

            eventService.ajouter(newEvent);
            afficherMessage(Alert.AlertType.INFORMATION, "Succès", "Événement ajouté avec succès !");
            annuler(null);

        } catch (DateTimeParseException e) {
            afficherMessage(Alert.AlertType.ERROR, "Erreur", "Veuillez entrer l'heure au format HH:mm.");
        } catch (SQLException e) {
            afficherMessage(Alert.AlertType.ERROR, "Erreur", "Impossible d'ajouter l'événement.");
        }
    }

    @FXML
    void annuler(ActionEvent event) {
        nomtf.clear();
        lieutf.clear();
        datetf.setValue(null);
        heuredebuttf.clear();
        heurefintf.clear();
        typetf.setValue(null);
        descriptiontf.clear();
        rewardtf.clear();
        imagetf.clear();
        imagetf1.setImage(null);
        imageUrl = null;
    }

    private boolean validerChamps() {
        return !nomtf.getText().trim().isEmpty() &&
                !lieutf.getText().trim().isEmpty() &&
                datetf.getValue() != null &&
                !heuredebuttf.getText().trim().isEmpty() &&
                !heurefintf.getText().trim().isEmpty() &&
                typetf.getValue() != null &&
                !descriptiontf.getText().trim().isEmpty() &&
                !rewardtf.getText().trim().isEmpty() &&
                imageUrl != null;
    }

    private void afficherMessage(Alert.AlertType type, String titre, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
