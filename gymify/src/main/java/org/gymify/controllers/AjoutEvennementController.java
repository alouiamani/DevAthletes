/*package org.gymify.controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.gymify.entities.Equipe;
import org.gymify.entities.Event;
import org.gymify.entities.EventType;
import org.gymify.entities.Salle;
import org.gymify.services.EquipeEventService;
import org.gymify.services.EventService;
import org.gymify.services.SalleService;
import org.gymify.utils.gymifyDataBase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;

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
    @FXML private ListView<String> equipesListView;
    @FXML private Label equipesLabel;
    @FXML private Button addTypeBtn, editTypeBtn, deleteTypeBtn;
    @FXML private Button addRewardBtn, editRewardBtn, deleteRewardBtn;

    private WebEngine webEngine;
    private double[] coords;
    private Connection connection;
    private ListeDesEvennementsController parentController;
    private Event currentEvent;
    private final EventService eventService = new EventService();
    private final EquipeEventService equipeEventService = new EquipeEventService();
    private List<Equipe> addedEquipes = new ArrayList<>();
    private ObservableList<String> equipeNames = FXCollections.observableArrayList();
    private ObservableList<String> typeItems = FXCollections.observableArrayList();
    private ObservableList<String> rewardItems = FXCollections.observableArrayList();
    private static final Logger LOGGER = Logger.getLogger(AjoutEvennementController.class.getName());
    private int responsableId;
/*
    public void setResponsableId(int responsableId) {
        this.responsableId = responsableId;
        LOGGER.info("Responsable_Salle ID set: " + responsableId);
    }

   @FXML
    public void initialize() {
        connection = gymifyDataBase.getInstance().getConnection();

        // Validate that critical FXML elements are initialized
        if (nomtf == null || lieutf == null || datetf == null || descriptiontf == null || typetf == null ||
                rewardtf == null || imagetf == null || imagetf1 == null || webviewtf == null || equipesListView == null ||
                addTypeBtn == null || editTypeBtn == null || deleteTypeBtn == null ||
                addRewardBtn == null || editRewardBtn == null || deleteRewardBtn == null) {
            LOGGER.severe("One or more FXML elements are not properly initialized: " +
                    "nomtf=" + nomtf + ", lieutf=" + lieutf + ", datetf=" + datetf + ", descriptiontf=" + descriptiontf +
                    ", typetf=" + typetf + ", rewardtf=" + rewardtf + ", imagetf=" + imagetf + ", imagetf1=" + imagetf1 +
                    ", webviewtf=" + webviewtf + ", equipesListView=" + equipesListView +
                    ", addTypeBtn=" + addTypeBtn + ", editTypeBtn=" + editTypeBtn + ", deleteTypeBtn=" + deleteTypeBtn +
                    ", addRewardBtn=" + addRewardBtn + ", editRewardBtn=" + editRewardBtn + ", deleteRewardBtn=" + deleteRewardBtn);
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur d'initialisation de l'interface. Vérifiez le fichier FXML.", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        // Validate that the Responsable_Salle has an associated Salle
        SalleService salleService = new SalleService();
        Salle salle = salleService.getSalleByResponsableId(responsableId);
        if (salle == null) {
            LOGGER.severe("Aucune salle associée au Responsable_Salle ID: " + responsableId);
            Alert alert = new Alert(Alert.AlertType.ERROR, "Aucune salle n'est associée à votre compte. Veuillez contacter un administrateur ou ajouter une salle.", ButtonType.OK);
            alert.showAndWait();
            Stage stage = (Stage) nomtf.getScene().getWindow();
            if (stage != null) {
                stage.close();
            }
            return;
        }

        if (webviewtf != null) {
            try {
                webEngine = webviewtf.getEngine();
                if (webEngine == null) {
                    LOGGER.severe("WebEngine est null après initialisation !");
                    return;
                }
                String userDataDir = System.getProperty("java.io.tmpdir") + "/webview-" + System.currentTimeMillis();
                System.setProperty("webview.user.data.dir", userDataDir);

                webEngine.setOnError(event -> {
                    LOGGER.severe("Erreur WebView : " + event.getMessage());
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors du chargement de la carte OpenStreetMap : " + event.getMessage(), ButtonType.OK);
                    alert.showAndWait();
                });
            } catch (Exception e) {
                LOGGER.severe("Erreur lors de l'initialisation du WebView : " + e.getMessage());
                Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors de l'initialisation de la carte OpenStreetMap : " + e.getMessage(), ButtonType.OK);
                alert.showAndWait();
            }
        } else {
            LOGGER.severe("WebView non initialisé dans le FXML !");
            Alert alert = new Alert(Alert.AlertType.ERROR, "Le composant de la carte n'est pas initialisé dans l'interface.", ButtonType.OK);
            alert.showAndWait();
        }

        // Initialiser les ComboBox avec les valeurs de l'ENUM
        for (EventType et : EventType.values()) {
            typeItems.add(et.name());
        }
        for (EventType.Reward r : EventType.Reward.values()) {
            rewardItems.add(r.name());
        }
        typetf.setItems(typeItems);
        rewardtf.setItems(rewardItems);

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

        equipesListView.setItems(equipeNames);

        equipesListView.setOnMouseClicked(this::handleEquipeListClick);

        nomtf.sceneProperty().addListener(new ChangeListener<Scene>() {
            @Override
            public void changed(ObservableValue<? extends Scene> observable, Scene oldValue, Scene newValue) {
                if (newValue != null) {
                    loadMap(36.8065, 10.1815); // Coordonnées par défaut (Tunis)
                    Stage stage = (Stage) nomtf.getScene().getWindow();
                    stage.setOnHidden(e -> {
                        if (webEngine != null) {
                            webEngine.loadContent("");
                            webEngine = null;
                        }
                    });
                    nomtf.sceneProperty().removeListener(this);
                }
            }
        });

        // Activer/désactiver les boutons de modification et suppression en fonction de la sélection
        typetf.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            editTypeBtn.setDisable(newVal == null);
            deleteTypeBtn.setDisable(newVal == null);
        });

        rewardtf.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            editRewardBtn.setDisable(newVal == null);
            deleteRewardBtn.setDisable(newVal == null);
        });
    }

    @FXML
    private void addType() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Nouveau Type");
        dialog.setHeaderText("Ajouter un nouveau type d'événement");
        dialog.setContentText("Nom du type :");

        dialog.showAndWait().ifPresent(newType -> {
            newType = newType.trim().toUpperCase();
            if (!newType.isEmpty() && !typeItems.contains(newType)) {
                typeItems.add(newType);
                typetf.setValue(newType);
                showWarning("Attention", "Le nouveau type '" + newType + "' a été ajouté dans l'interface, mais il ne sera pas sauvegardé dans la base de données tant que l'ENUM de la colonne 'type' dans la table 'events' n'est pas mis à jour par un administrateur.");
            } else {
                showAlert(Alert.AlertType.WARNING, "Erreur", "Le type existe déjà ou est vide !");
            }
        });
    }

    @FXML
    private void editType() {
        String selectedType = typetf.getValue();
        if (selectedType == null) {
            showAlert(Alert.AlertType.WARNING, "Avertissement", "Veuillez sélectionner un type à modifier.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog(selectedType);
        dialog.setTitle("Modifier Type");
        dialog.setHeaderText("Modifier le type d'événement");
        dialog.setContentText("Nouveau nom du type :");

        dialog.showAndWait().ifPresent(newType -> {
            newType = newType.trim().toUpperCase();
            if (!newType.isEmpty() && !newType.equals(selectedType) && !typeItems.contains(newType)) {
                int index = typeItems.indexOf(selectedType);
                typeItems.set(index, newType);
                typetf.setValue(newType);
                showWarning("Attention", "Le type a été modifié dans l'interface, mais les changements ne seront pas sauvegardés dans la base de données tant que l'ENUM de la colonne 'type' dans la table 'events' n'est pas mis à jour par un administrateur.");
            } else if (newType.equals(selectedType)) {
                // Pas de changement
            } else {
                showAlert(Alert.AlertType.WARNING, "Erreur", "Le nouveau type existe déjà ou est vide !");
            }
        });
    }

    @FXML
    private void deleteType() {
        String selectedType = typetf.getValue();
        if (selectedType == null) {
            showAlert(Alert.AlertType.WARNING, "Avertissement", "Veuillez sélectionner un type à supprimer.");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION, "Êtes-vous sûr de vouloir supprimer le type '" + selectedType + "' ?", ButtonType.YES, ButtonType.NO);
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                typeItems.remove(selectedType);
                typetf.setValue(null);
                showWarning("Attention", "Le type a été supprimé de l'interface, mais les changements ne seront pas sauvegardés dans la base de données tant que l'ENUM de la colonne 'type' dans la table 'events' n'est pas mis à jour par un administrateur.");
            }
        });
    }

    @FXML
    private void addReward() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Nouvelle Récompense");
        dialog.setHeaderText("Ajouter une nouvelle récompense");
        dialog.setContentText("Nom de la récompense :");

        dialog.showAndWait().ifPresent(newReward -> {
            newReward = newReward.trim().toUpperCase();
            if (!newReward.isEmpty() && !rewardItems.contains(newReward)) {
                rewardItems.add(newReward);
                rewardtf.setValue(newReward);
                showWarning("Attention", "La nouvelle récompense '" + newReward + "' a été ajoutée dans l'interface, mais elle ne sera pas sauvegardée dans la base de données tant que l'ENUM de la colonne 'reward' dans la table 'events' n'est pas mis à jour par un administrateur.");
            } else {
                showAlert(Alert.AlertType.WARNING, "Erreur", "La récompense existe déjà ou est vide !");
            }
        });
    }

    @FXML
    private void editReward() {
        String selectedReward = rewardtf.getValue();
        if (selectedReward == null) {
            showAlert(Alert.AlertType.WARNING, "Avertissement", "Veuillez sélectionner une récompense à modifier.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog(selectedReward);
        dialog.setTitle("Modifier Récompense");
        dialog.setHeaderText("Modifier la récompense");
        dialog.setContentText("Nouveau nom de la récompense :");

        dialog.showAndWait().ifPresent(newReward -> {
            newReward = newReward.trim().toUpperCase();
            if (!newReward.isEmpty() && !newReward.equals(selectedReward) && !rewardItems.contains(newReward)) {
                int index = rewardItems.indexOf(selectedReward);
                rewardItems.set(index, newReward);
                rewardtf.setValue(newReward);
                showWarning("Attention", "La récompense a été modifiée dans l'interface, mais les changements ne seront pas sauvegardés dans la base de données tant que l'ENUM de la colonne 'reward' dans la table 'events' n'est pas mis à jour par un administrateur.");
            } else if (newReward.equals(selectedReward)) {
                // Pas de changement
            } else {
                showAlert(Alert.AlertType.WARNING, "Erreur", "La nouvelle récompense existe déjà ou est vide !");
            }
        });
    }

    @FXML
    private void deleteReward() {
        String selectedReward = rewardtf.getValue();
        if (selectedReward == null) {
            showAlert(Alert.AlertType.WARNING, "Avertissement", "Veuillez sélectionner une récompense à supprimer.");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION, "Êtes-vous sûr de vouloir supprimer la récompense '" + selectedReward + "' ?", ButtonType.YES, ButtonType.NO);
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                rewardItems.remove(selectedReward);
                rewardtf.setValue(null);
                showWarning("Attention", "La récompense a été supprimée de l'interface, mais les changements ne seront pas sauvegardés dans la base de données tant que l'ENUM de la colonne 'reward' dans la table 'events' n'est pas mis à jour par un administrateur.");
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
            LocalTime heureDebut = event.getHeureDebut();
            cbHeureDebut.setValue(String.format("%02d", heureDebut.getHour()));
            cbMinuteDebut.setValue(String.format("%02d", heureDebut.getMinute()));
            cbSecondeDebut.setValue(String.format("%02d", heureDebut.getSecond()));
        } else {
            cbHeureDebut.setValue("00");
            cbMinuteDebut.setValue("00");
            cbSecondeDebut.setValue("00");
        }

        if (event.getHeureFin() != null) {
            LocalTime heureFin = event.getHeureFin();
            cbHeureFin.setValue(String.format("%02d", heureFin.getHour()));
            cbMinuteFin.setValue(String.format("%02d", heureFin.getMinute()));
            cbSecondeFin.setValue(String.format("%02d", heureFin.getSecond()));
        } else {
            cbHeureFin.setValue("00");
            cbMinuteFin.setValue("00");
            cbSecondeFin.setValue("00");
        }

        if (event.getEquipes() != null) {
            addedEquipes.clear();
            equipeNames.clear();
            for (Equipe equipe : event.getEquipes()) {
                addedEquipes.add(equipe);
                equipeNames.add(equipe.getNom());
            }
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

        if (typetf.getValue() == null || !typeItems.contains(typetf.getValue())) {
            ErrorType.setText("Le type d'événement doit être une valeur valide de l'ENUM.");
            ErrorType.setVisible(true);
            isValid = false;
        } else {
            ErrorType.setVisible(false);
        }

        if (rewardtf.getValue() == null || !rewardItems.contains(rewardtf.getValue())) {
            ErrorReward.setText("La récompense doit être une valeur valide de l'ENUM.");
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

        if (addedEquipes.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Vous devez ajouter au moins une équipe avant d'enregistrer l'événement.", ButtonType.OK);
            alert.showAndWait();
            isValid = false;
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
                LOGGER.warning("Aucune coordonnée trouvée pour le lieu : " + location);
            }
        } catch (Exception e) {
            LOGGER.severe("Erreur lors de la récupération des coordonnées pour le lieu : " + location + " - " + e.getMessage());
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

        if (webEngine != null) {
            webEngine.loadContent(html);
        } else {
            LOGGER.severe("WebEngine is null. Cannot load map.");
        }
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

        // Vérifier si type et reward sont dans les valeurs initiales de l'ENUM
        if (!EventType.valueOf(type).name().equals(type) || !EventType.Reward.valueOf(reward).name().equals(reward)) {
            showWarning("Attention", "Les valeurs 'type' ou 'reward' modifiées ne sont pas valides dans la base de données actuelle. L'enregistrement peut échouer si elles ne correspondent pas à l'ENUM défini.");
        }

        Event eventToSave = currentEvent != null ? currentEvent : new Event();
        eventToSave.setNom(nom);
        eventToSave.setImageUrl(imageUrl);
        eventToSave.setType(EventType.valueOf(type));
        eventToSave.setReward(EventType.Reward.valueOf(reward));
        eventToSave.setLieu(lieu);
        eventToSave.setDescription(description);
        eventToSave.setDate(datetf.getValue());
        eventToSave.setHeureDebut(LocalTime.parse(heureDebut));
        eventToSave.setHeureFin(LocalTime.parse(heureFin));
        eventToSave.setLatitude(coords[0]);
        eventToSave.setLongitude(coords[1]);
        eventToSave.setEquipes(new HashSet<>(addedEquipes));

        try {
            if (currentEvent == null) {
                eventService.ajouter(eventToSave, responsableId);
                for (Equipe equipe : addedEquipes) {
                    System.out.println("Associating team with ID: " + equipe.getId() + " to event with ID: " + eventToSave.getId());
                    equipeEventService.ajouter(equipe, eventToSave);
                }
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Événement ajouté avec succès !", ButtonType.OK);
                alert.showAndWait();
            } else {
                eventService.modifier(eventToSave);
                for (Equipe equipe : eventService.getEquipesForEvent(eventToSave.getId())) {
                    equipeEventService.supprimer(equipe, eventToSave);
                }
                for (Equipe equipe : addedEquipes) {
                    System.out.println("Associating team with ID: " + equipe.getId() + " to event with ID: " + eventToSave.getId());
                    equipeEventService.ajouter(equipe, eventToSave);
                }
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
        } catch (IllegalArgumentException e) {
            LOGGER.severe("Erreur : La valeur de 'type' ou 'reward' ne correspond pas à l'ENUM défini dans la base de données : " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Erreur", "La valeur de 'type' ou 'reward' sélectionnée n'est pas valide dans la base de données actuelle. Veuillez utiliser uniquement les valeurs prédéfinies ou demander une mise à jour de l'ENUM.");
        } catch (SQLException e) {
            LOGGER.severe("Erreur lors de l'enregistrement de l'événement : " + e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors de l'enregistrement de l'événement : " + e.getMessage(), ButtonType.OK);
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    private boolean eventNameExists(String eventName) {
        String query = "SELECT COUNT(*) FROM events WHERE nom = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, eventName);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            LOGGER.severe("Erreur lors de la vérification de l'existence du nom de l'événement : " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    @FXML
    void annuler(ActionEvent event) {
        clearForm();
        Stage stage = (Stage) nomtf.getScene().getWindow();
        if (stage != null) {
            stage.close();
        }
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
        addedEquipes.clear();
        equipeNames.clear();

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
        LOGGER.info("Upload button clicked.");

        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png", "*.gif", "*.bmp", "*.jpeg");
        fileChooser.getExtensionFilters().add(extFilter);

        Stage stage = (Stage) nomtf.getScene().getWindow();
        if (stage == null) {
            LOGGER.severe("Erreur : Impossible de récupérer la scène pour le FileChooser.");
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

    @FXML
    private void AjouterEquipe(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjoutEquipe.fxml"));
            if (loader.getLocation() == null) {
                LOGGER.severe("Cannot find AjoutEquipe.fxml. Ensure the file exists in src/main/resources/");
                return;
            }
            Parent root = loader.load();

            AjoutEquipeController ajoutEquipeController = loader.getController();
            ajoutEquipeController.setAjoutEvennementController(this);

            Stage stage = new Stage();
            stage.setTitle("📩 Ajouter Equipe");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            LOGGER.severe("Erreur lors de l'ouverture de la fenêtre d'ajout d'équipe : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleEquipeListClick(MouseEvent event) {
        try {
            String selectedTeamName = equipesListView.getSelectionModel().getSelectedItem();
            if (selectedTeamName == null) {
                return;
            }

            Equipe selectedEquipe = addedEquipes.stream()
                    .filter(e -> e.getNom().equals(selectedTeamName))
                    .findFirst()
                    .orElse(null);
            if (selectedEquipe != null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjoutEquipe.fxml"));
                if (loader.getLocation() == null) {
                    LOGGER.severe("Cannot find AjoutEquipe.fxml. Ensure the file exists in src/main/resources/");
                    return;
                }
                Parent root = loader.load();
                AjoutEquipeController ajoutEquipeController = loader.getController();
                ajoutEquipeController.setAjoutEvennementController(this);
                ajoutEquipeController.handleModifier(selectedEquipe);

                Stage stage = new Stage();
                stage.setTitle("📩 Modifier Equipe");
                stage.setScene(new Scene(root));
                stage.show();
            }
        } catch (IOException e) {
            LOGGER.severe("Erreur lors de l'ouverture de la fenêtre de modification d'équipe : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void addEquipeToEvent(Equipe equipe) {
        addedEquipes.add(equipe);
        equipeNames.add(equipe.getNom());
    }

    public void updateEquipeInList(Equipe oldEquipe, Equipe newEquipe) {
        int index = addedEquipes.indexOf(oldEquipe);
        if (index != -1) {
            addedEquipes.set(index, newEquipe);
            equipeNames.set(index, newEquipe.getNom());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
*/