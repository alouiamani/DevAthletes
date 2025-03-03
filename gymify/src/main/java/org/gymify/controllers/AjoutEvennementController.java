package org.gymify.controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.gymify.entities.Event;
import org.gymify.entities.EventType;
import org.gymify.services.EventService;
import org.gymify.utils.gymifyDataBase;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONArray;
import org.json.JSONObject;

public class AjoutEvennementController {

    @FXML private Label ErrorNom, ErrorImage, ErrorType, ErrorReward, ErrorDate, Errorhboxheuredebut, Errorhboxheurefin, ErrorDescription, ErrorLieu;
    @FXML private ComboBox<String> cbHeureDebut, cbHeureFin, cbMinuteDebut, cbMinuteFin, cbSecondeDebut, cbSecondeFin;
    @FXML private DatePicker datetf;
    @FXML private TextArea descriptiontf;
    @FXML private HBox hboxcartetf, hboxheuredebut, hboxheurefin;
    @FXML private TextField imagetf;
    @FXML private ImageView imagetf1;
    @FXML private Label errorLabel, enregistre, annuler;
    @FXML private Button uploadBtn;
    @FXML private TextField lieutf;
    @FXML private TextField nomtf;
    @FXML private ComboBox<String> rewardtf;
    @FXML private ComboBox<String> typetf;
    @FXML private Button search;
    @FXML private WebView webviewtf;

    private WebEngine webEngine;
    private double[] coords;
    private Connection connection;
    private ListeDesEvennementsController parentController;
    private Event currentEvent;
    private final EventService eventService = new EventService();

    @FXML
    public void initialize() {
        connection = gymifyDataBase.getInstance().getConnection();

        if (webviewtf != null) {
            try {
                webEngine = webviewtf.getEngine();
                if (webEngine == null) {
                    System.err.println("WebEngine est null après initialisation !");
                    return;
                }
                webEngine.setOnError(event -> {
                    System.err.println("Erreur WebView : " + event.getMessage());
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors du chargement de la carte OpenStreetMap : " + event.getMessage(), ButtonType.OK);
                    alert.showAndWait();
                });
            } catch (Exception e) {
                System.err.println("Erreur lors de l'initialisation du WebView : " + e.getMessage());
                Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors de l'initialisation de la carte OpenStreetMap : " + e.getMessage(), ButtonType.OK);
                alert.showAndWait();
            }
        } else {
            System.err.println("WebView non initialisé dans le FXML !");
            Alert alert = new Alert(Alert.AlertType.ERROR, "Le composant de la carte n'est pas initialisé dans l'interface.", ButtonType.OK);
            alert.showAndWait();
        }

        for (EventType et : EventType.values()) {
            typetf.getItems().add(et.name());
        }
        for (EventType.Reward r : EventType.Reward.values()) {
            rewardtf.getItems().add(r.name());
        }

        for (int i = 0; i < 24; i++) {
            cbHeureDebut.getItems().add(String.format("%02d", i));
            cbHeureFin.getItems().add(String.format("%02d", i));
        }
        for (int i = 0; i < 60; i++) {
            cbMinuteDebut.getItems().add(String.format("%02d", i));
            cbMinuteFin.getItems().add(String.format("%02d", i));
            cbSecondeDebut.getItems().add(String.format("%02d", i));
            cbSecondeFin.getItems().add(String.format("%02d", i));
        }

        cbHeureDebut.setValue("00");
        cbMinuteDebut.setValue("00");
        cbSecondeDebut.setValue("00");
        cbHeureFin.setValue("00");
        cbMinuteFin.setValue("00");
        cbSecondeFin.setValue("00");

        // Charger la carte après que la scène est initialisée
        nomtf.sceneProperty().addListener(new ChangeListener<Scene>() {
            @Override
            public void changed(ObservableValue<? extends Scene> observable, Scene oldValue, Scene newValue) {
                if (newValue != null) {
                    loadMap(36.8065, 10.1815); // Coordonnées par défaut (Tunis)
                    Stage stage = (Stage) nomtf.getScene().getWindow();
                    stage.setOnHidden(e -> {
                        if (webEngine != null) {
                            webEngine.loadContent(""); // Libérer les ressources
                            webEngine = null;
                        }
                    });
                    nomtf.sceneProperty().removeListener(this);
                }
            }
        });
    }

    public void setParentController(ListeDesEvennementsController parentController) {
        this.parentController = parentController;
    }

    public void setEvenement(Event event, ListeDesEvennementsController parentController) {
        this.currentEvent = event;
        this.parentController = parentController;

        nomtf.setText(event.getNom());
        lieutf.setText(event.getLieu());
        datetf.setValue(event.getDate());
        descriptiontf.setText(event.getDescription());
        if (typetf != null) {
            typetf.setValue(event.getType() != null ? event.getType().name() : null);
        }
        if (rewardtf != null) {
            rewardtf.setValue(event.getReward() != null ? event.getReward().name() : null);
        }
        imagetf.setText(event.getImageUrl());
        if (event.getImageUrl() != null && !event.getImageUrl().isEmpty()) {
            try {
                imagetf1.setImage(new Image(event.getImageUrl()));
            } catch (Exception e) {
                imagetf1.setImage(new Image("/default_image.jpg"));
            }
        }
        coords = new double[]{event.getLatitude(), event.getLongitude()};
        loadMap(event.getLatitude(), event.getLongitude());

        if (event.getHeureDebut() != null) {
            String[] heureDebut = event.getHeureDebut().toString().split(":");
            cbHeureDebut.setValue(heureDebut[0]);
            cbMinuteDebut.setValue(heureDebut[1]);
            cbSecondeDebut.setValue(heureDebut[2]);
        }
        if (event.getHeureFin() != null) {
            String[] heureFin = event.getHeureFin().toString().split(":");
            cbHeureFin.setValue(heureFin[0]);
            cbMinuteFin.setValue(heureFin[1]);
            cbSecondeFin.setValue(heureFin[2]);
        }
    }

    private boolean validateFields() {
        boolean isValid = true;

        if (nomtf.getText().trim().isEmpty()) {
            ErrorNom.setText("Le nom est obligatoire.");
            ErrorNom.setVisible(true);
            isValid = false;
        } else {
            ErrorNom.setVisible(false);
        }

        if (imagetf.getText().trim().isEmpty()) {
            ErrorImage.setText("L'URL de l'image est obligatoire.");
            ErrorImage.setVisible(true);
            isValid = false;
        } else {
            ErrorImage.setVisible(false);
        }

        if (typetf.getValue() == null) {
            ErrorType.setText("Le type d'événement est obligatoire.");
            ErrorType.setVisible(true);
            isValid = false;
        } else {
            ErrorType.setVisible(false);
        }

        if (rewardtf.getValue() == null) {
            ErrorReward.setText("La récompense est obligatoire.");
            ErrorReward.setVisible(true);
            isValid = false;
        } else {
            ErrorReward.setVisible(false);
        }

        if (datetf.getValue() == null) {
            ErrorDate.setText("La date est obligatoire.");
            ErrorDate.setVisible(true);
            isValid = false;
        } else if (datetf.getValue().isBefore(java.time.LocalDate.now())) {
            ErrorDate.setText("La date ne peut être antérieure à aujourd'hui.");
            ErrorDate.setVisible(true);
            isValid = false;
        } else {
            ErrorDate.setVisible(false);
        }

        if (cbHeureDebut.getValue() == null || cbMinuteDebut.getValue() == null || cbSecondeDebut.getValue() == null) {
            Errorhboxheuredebut.setText("Veuillez renseigner l'heure de début complète.");
            Errorhboxheuredebut.setVisible(true);
            isValid = false;
        } else {
            Errorhboxheuredebut.setVisible(false);
        }

        if (cbHeureFin.getValue() == null || cbMinuteFin.getValue() == null || cbSecondeFin.getValue() == null) {
            Errorhboxheurefin.setText("Veuillez renseigner l'heure de fin complète.");
            Errorhboxheurefin.setVisible(true);
            isValid = false;
        } else {
            Errorhboxheurefin.setVisible(false);
        }

        if (cbHeureDebut.getValue() != null && cbHeureFin.getValue() != null) {
            int startHour = Integer.parseInt(cbHeureDebut.getValue());
            int startMinute = Integer.parseInt(cbMinuteDebut.getValue());
            int startSecond = Integer.parseInt(cbSecondeDebut.getValue());
            int endHour = Integer.parseInt(cbHeureFin.getValue());
            int endMinute = Integer.parseInt(cbMinuteFin.getValue());
            int endSecond = Integer.parseInt(cbSecondeFin.getValue());

            if (endHour < startHour ||
                    (endHour == startHour && endMinute < startMinute) ||
                    (endHour == startHour && endMinute == startMinute && endSecond <= startSecond)) {
                Errorhboxheurefin.setText("L'heure de fin doit être après l'heure de début.");
                Errorhboxheurefin.setVisible(true);
                isValid = false;
            } else {
                Errorhboxheurefin.setVisible(false);
            }
        }

        if (lieutf.getText().trim().isEmpty()) {
            ErrorLieu.setText("Le lieu est obligatoire.");
            ErrorLieu.setVisible(true);
            isValid = false;
        } else {
            ErrorLieu.setVisible(false);
        }

        if (coords == null) {
            ErrorLieu.setText("Veuillez rechercher un lieu d'abord.");
            ErrorLieu.setVisible(true);
            isValid = false;
        } else {
            ErrorLieu.setVisible(false);
        }

        if (descriptiontf.getText().trim().isEmpty()) {
            ErrorDescription.setText("La description est obligatoire.");
            ErrorDescription.setVisible(true);
            isValid = false;
        } else {
            ErrorDescription.setVisible(false);
        }

        return isValid;
    }

    @FXML
    private void rechercherBtn() {
        String location = lieutf.getText().trim();
        if (!location.isEmpty()) {
            double[] coords = getCoordinatesFromNominatim(location);
            if (coords != null) {
                this.coords = coords;
                loadMap(coords[0], coords[1]);
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Lieu introuvable. Veuillez vérifier le nom du lieu ou votre connexion Internet.", ButtonType.OK);
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Veuillez entrer un lieu avant de rechercher.", ButtonType.OK);
            alert.showAndWait();
        }
    }

    private double[] getCoordinatesFromNominatim(String location) {
        try {
            String urlStr = "https://nominatim.openstreetmap.org/search?format=json&q=" + location.replace(" ", "%20");
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            JSONArray jsonArray = new JSONArray(response.toString());
            if (jsonArray.length() > 0) {
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                double lat = Double.parseDouble(jsonObject.getString("lat"));
                double lon = Double.parseDouble(jsonObject.getString("lon"));
                return new double[]{lat, lon};
            } else {
                System.err.println("Aucune coordonnée trouvée pour le lieu : " + location);
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération des coordonnées pour le lieu : " + location + " - " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    private void loadMap(double latitude, double longitude) {
        String html = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "  <meta charset='utf-8' />\n" +
                "  <meta name='viewport' content='width=device-width, initial-scale=1.0'>\n" +
                "  <link rel='stylesheet' href='https://unpkg.com/leaflet@1.7.1/dist/leaflet.css' />\n" +
                "  <script src='https://unpkg.com/leaflet@1.7.1/dist/leaflet.js'></script>\n" +
                "</head>\n" +
                "<body>\n" +
                "  <div id='map' style='width: 100%; height: 100vh;'></div>\n" +
                "  <script>\n" +
                "    var map = L.map('map').setView([" + latitude + ", " + longitude + "], 15);\n" +
                "    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png').addTo(map);\n" +
                "    L.marker([" + latitude + ", " + longitude + "]).addTo(map)\n" +
                "       .bindPopup('Position sélectionnée').openPopup();\n" +
                "  </script>\n" +
                "</body>\n" +
                "</html>";

        webEngine.loadContent(html);
    }

    @FXML
    void enregistrer(ActionEvent event) {
        if (!validateFields()) {
            return;
        }

        String nom = nomtf.getText().trim();
        if (eventNameExists(nom) && (currentEvent == null || !currentEvent.getNom().equals(nom))) {
            ErrorNom.setText("Cet événement existe déjà. Veuillez choisir un autre nom.");
            ErrorNom.setVisible(true);
            return;
        }

        String imageUrl = imagetf.getText().trim();
        String type = typetf.getValue();
        String reward = rewardtf.getValue();
        String lieu = lieutf.getText().trim();
        String description = descriptiontf.getText().trim();
        Date date = Date.valueOf(datetf.getValue());
        String heureDebut = cbHeureDebut.getValue() + ":" + cbMinuteDebut.getValue() + ":" + cbSecondeDebut.getValue();
        String heureFin = cbHeureFin.getValue() + ":" + cbMinuteFin.getValue() + ":" + cbSecondeFin.getValue();

        Event eventToSave = currentEvent != null ? currentEvent : new Event();
        eventToSave.setNom(nom);
        eventToSave.setImageUrl(imageUrl);
        eventToSave.setType(EventType.valueOf(type));
        eventToSave.setReward(EventType.Reward.valueOf(reward));
        eventToSave.setLieu(lieu);
        eventToSave.setDescription(description);
        eventToSave.setDate(datetf.getValue());
        eventToSave.setHeureDebut(java.time.LocalTime.parse(heureDebut));
        eventToSave.setHeureFin(java.time.LocalTime.parse(heureFin));
        eventToSave.setLatitude(coords[0]);
        eventToSave.setLongitude(coords[1]);

        try {
            if (currentEvent == null) {
                eventService.ajouter(eventToSave);
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Événement ajouté avec succès !", ButtonType.OK);
                alert.showAndWait();
            } else {
                eventService.modifier(eventToSave);
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Événement modifié avec succès !", ButtonType.OK);
                alert.showAndWait();
            }
            clearForm();
            if (parentController != null) {
                parentController.loadEvents("");
            }
            Stage stage = (Stage) nomtf.getScene().getWindow();
            if (stage != null) {
                stage.close();
            }
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors de l'enregistrement de l'événement : " + e.getMessage(), ButtonType.OK);
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    private boolean eventNameExists(String eventName) {
        String query = "SELECT COUNT(*) FROM events WHERE nom = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, eventName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                return count > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @FXML
    void annuler(ActionEvent event) {
        clearForm();
    }

    private void clearForm() {
        nomtf.clear();
        imagetf.clear();
        lieutf.clear();
        descriptiontf.clear();
        typetf.setValue(null);
        rewardtf.setValue(null);
        datetf.setValue(null);
        cbHeureDebut.setValue("00");
        cbMinuteDebut.setValue("00");
        cbSecondeDebut.setValue("00");
        cbHeureFin.setValue("00");
        cbMinuteFin.setValue("00");
        cbSecondeFin.setValue("00");
        imagetf1.setImage(null);
        coords = null;

        ErrorNom.setVisible(false);
        ErrorImage.setVisible(false);
        ErrorType.setVisible(false);
        ErrorReward.setVisible(false);
        ErrorDate.setVisible(false);
        Errorhboxheuredebut.setVisible(false);
        Errorhboxheurefin.setVisible(false);
        ErrorDescription.setVisible(false);
        ErrorLieu.setVisible(false);
    }

    @FXML
    void uploadBtn(ActionEvent event) {
        System.out.println("Upload button clicked.");

        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png", "*.gif", "*.bmp", "*.jpeg");
        fileChooser.getExtensionFilters().add(extFilter);

        Stage stage = (Stage) nomtf.getScene().getWindow();
        if (stage == null) {
            System.err.println("Erreur : Impossible de récupérer la scène pour le FileChooser.");
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur : Impossible d'ouvrir le sélecteur de fichiers.", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        java.io.File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            String imagePath = selectedFile.toURI().toString();
            imagetf.setText(imagePath);
            Image image = new Image(imagePath);
            imagetf1.setImage(image);
        }
    }
}