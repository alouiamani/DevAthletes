package org.gymify.controllers;

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
import org.gymify.entities.*;
import org.gymify.services.EquipeEventService;
import org.gymify.services.EventService;
import org.gymify.services.SalleService;
import org.gymify.utils.AuthToken;
import org.gymify.utils.gymifyDataBase;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

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
    private static final String IMAGE_DIR = "src/main/resources/Uploads/events/";
    private static final String DEFAULT_IMAGE = "/Uploads/events/default.jpg";

    public AjoutEvennementController() {
        User currentUser = AuthToken.getCurrentUser();
        if (currentUser == null) {
            LOGGER.severe("Aucun utilisateur connecté !");
            Alert alert = new Alert(Alert.AlertType.ERROR, "Aucun utilisateur connecté. Veuillez vous connecter.", ButtonType.OK);
            alert.showAndWait();
            return;
        }
        this.responsableId = currentUser.getId();
    }

    @FXML
    public void initialize() {
        connection = gymifyDataBase.getInstance().getConnection();

        if (nomtf == null || lieutf == null || datetf == null || descriptiontf == null || typetf == null ||
                rewardtf == null || imagetf == null || imagetf1 == null || webviewtf == null || equipesListView == null ||
                addTypeBtn == null || editTypeBtn == null || deleteTypeBtn == null ||
                addRewardBtn == null || editRewardBtn == null || deleteRewardBtn == null) {
            LOGGER.severe("One or more FXML elements are not properly initialized.");
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur d'initialisation de l'interface. Vérifiez le fichier FXML.", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        SalleService salleService = new SalleService();
        try {
            Salle salle = salleService.getSalleByResponsableId(responsableId);
            if (salle == null) {
                LOGGER.severe("Aucune salle associée au Responsable_Salle ID: " + responsableId);
                Alert alert = new Alert(Alert.AlertType.ERROR, "Aucune salle n'est associée à votre compte.", ButtonType.OK);
                alert.showAndWait();
                Stage stage = (Stage) nomtf.getScene().getWindow();
                if (stage != null) {
                    stage.close();
                }
                return;
            }
        } catch (SQLException e) {
            LOGGER.severe("Erreur lors de la vérification de la salle associée : " + e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors de la vérification de la salle associée : " + e.getMessage(), ButtonType.OK);
            alert.showAndWait();
            return;
        }

        if (webviewtf != null) {
            webEngine = webviewtf.getEngine();
            loadMap(36.8065, 10.1815); // Coordonnées par défaut (Tunis)
        }

        // Charger les types et récompenses depuis la base de données
        loadTypesFromDatabase();
        loadRewardsFromDatabase();

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

        typetf.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            editTypeBtn.setDisable(newVal == null);
            deleteTypeBtn.setDisable(newVal == null);
        });

        rewardtf.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            editRewardBtn.setDisable(newVal == null);
            deleteRewardBtn.setDisable(newVal == null);
        });
    }

    private void loadTypesFromDatabase() {
        typeItems.clear();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SHOW COLUMNS FROM events LIKE 'type'")) {
            if (rs.next()) {
                String enumDefinition = rs.getString("Type");
                String[] values = enumDefinition.substring(5, enumDefinition.length() - 1).split(",");
                for (String value : values) {
                    typeItems.add(value.trim().replace("'", ""));
                }
            }
        } catch (SQLException e) {
            LOGGER.severe("Erreur lors du chargement des types depuis la base de données : " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger les types : " + e.getMessage());
        }
        typetf.setItems(typeItems);
    }

    private void loadRewardsFromDatabase() {
        rewardItems.clear();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SHOW COLUMNS FROM events LIKE 'reward'")) {
            if (rs.next()) {
                String enumDefinition = rs.getString("Type");
                String[] values = enumDefinition.substring(5, enumDefinition.length() - 1).split(",");
                for (String value : values) {
                    rewardItems.add(value.trim().replace("'", ""));
                }
            }
        } catch (SQLException e) {
            LOGGER.severe("Erreur lors du chargement des récompenses depuis la base de données : " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger les récompenses : " + e.getMessage());
        }
        rewardtf.setItems(rewardItems);
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
                try (Statement stmt = connection.createStatement()) {
                    String currentEnum = getCurrentEnumDefinition("type");
                    String newEnum = currentEnum + ",'" + newType + "'";
                    stmt.executeUpdate("ALTER TABLE events MODIFY COLUMN type ENUM(" + newEnum + ")");
                    typeItems.add(newType);
                    typetf.setValue(newType);
                    showAlert(Alert.AlertType.INFORMATION, "Succès", "Le type '" + newType + "' a été ajouté avec succès à la base de données.");
                } catch (SQLException e) {
                    LOGGER.severe("Erreur lors de l'ajout du type dans la base de données : " + e.getMessage());
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ajouter le type : " + e.getMessage());
                }
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
                try (Statement stmt = connection.createStatement()) {
                    String currentEnum = getCurrentEnumDefinition("type");
                    String newEnum = currentEnum.replace("'" + selectedType + "'", "'" + newType + "'");
                    stmt.executeUpdate("ALTER TABLE events MODIFY COLUMN type ENUM(" + newEnum + ")");
                    int index = typeItems.indexOf(selectedType);
                    typeItems.set(index, newType);
                    typetf.setValue(newType);
                    showAlert(Alert.AlertType.INFORMATION, "Succès", "Le type '" + selectedType + "' a été modifié en '" + newType + "' dans la base de données.");
                } catch (SQLException e) {
                    LOGGER.severe("Erreur lors de la modification du type dans la base de données : " + e.getMessage());
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de modifier le type : " + e.getMessage());
                }
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
                try (Statement stmt = connection.createStatement()) {
                    String currentEnum = getCurrentEnumDefinition("type");
                    String newEnum = currentEnum.replace("'" + selectedType + "',", "").replace(",'" + selectedType + "'", "").replace("'" + selectedType + "'", "");
                    if (newEnum.endsWith(",")) newEnum = newEnum.substring(0, newEnum.length() - 1);
                    stmt.executeUpdate("ALTER TABLE events MODIFY COLUMN type ENUM(" + newEnum + ")");
                    typeItems.remove(selectedType);
                    typetf.setValue(null);
                    showAlert(Alert.AlertType.INFORMATION, "Succès", "Le type '" + selectedType + "' a été supprimé de la base de données.");
                } catch (SQLException e) {
                    LOGGER.severe("Erreur lors de la suppression du type dans la base de données : " + e.getMessage());
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de supprimer le type : " + e.getMessage());
                }
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
                try (Statement stmt = connection.createStatement()) {
                    String currentEnum = getCurrentEnumDefinition("reward");
                    String newEnum = currentEnum + ",'" + newReward + "'";
                    stmt.executeUpdate("ALTER TABLE events MODIFY COLUMN reward ENUM(" + newEnum + ")");
                    rewardItems.add(newReward);
                    rewardtf.setValue(newReward);
                    showAlert(Alert.AlertType.INFORMATION, "Succès", "La récompense '" + newReward + "' a été ajoutée avec succès à la base de données.");
                } catch (SQLException e) {
                    LOGGER.severe("Erreur lors de l'ajout de la récompense dans la base de données : " + e.getMessage());
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ajouter la récompense : " + e.getMessage());
                }
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
                try (Statement stmt = connection.createStatement()) {
                    String currentEnum = getCurrentEnumDefinition("reward");
                    String newEnum = currentEnum.replace("'" + selectedReward + "'", "'" + newReward + "'");
                    stmt.executeUpdate("ALTER TABLE events MODIFY COLUMN reward ENUM(" + newEnum + ")");
                    int index = rewardItems.indexOf(selectedReward);
                    rewardItems.set(index, newReward);
                    rewardtf.setValue(newReward);
                    showAlert(Alert.AlertType.INFORMATION, "Succès", "La récompense '" + selectedReward + "' a été modifiée en '" + newReward + "' dans la base de données.");
                } catch (SQLException e) {
                    LOGGER.severe("Erreur lors de la modification de la récompense dans la base de données : " + e.getMessage());
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de modifier la récompense : " + e.getMessage());
                }
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
                try (Statement stmt = connection.createStatement()) {
                    String currentEnum = getCurrentEnumDefinition("reward");
                    String newEnum = currentEnum.replace("'" + selectedReward + "',", "").replace(",'" + selectedReward + "'", "").replace("'" + selectedReward + "'", "");
                    if (newEnum.endsWith(",")) newEnum = newEnum.substring(0, newEnum.length() - 1);
                    stmt.executeUpdate("ALTER TABLE events MODIFY COLUMN reward ENUM(" + newEnum + ")");
                    rewardItems.remove(selectedReward);
                    rewardtf.setValue(null);
                    showAlert(Alert.AlertType.INFORMATION, "Succès", "La récompense '" + selectedReward + "' a été supprimée de la base de données.");
                } catch (SQLException e) {
                    LOGGER.severe("Erreur lors de la suppression de la récompense dans la base de données : " + e.getMessage());
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de supprimer la récompense : " + e.getMessage());
                }
            }
        });
    }

    private String getCurrentEnumDefinition(String columnName) throws SQLException {
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SHOW COLUMNS FROM events LIKE '" + columnName + "'")) {
            if (rs.next()) {
                String enumDefinition = rs.getString("Type");
                return enumDefinition.substring(5, enumDefinition.length() - 1);
            }
        }
        throw new SQLException("Impossible de récupérer la définition de l'ENUM pour la colonne " + columnName);
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
        typetf.setValue(event.getType() != null ? event.getType().name() : null);
        rewardtf.setValue(event.getReward() != null ? event.getReward().name() : null);
        imagetf.setText(event.getImageUrl());

        if (event.getImageUrl() != null && !event.getImageUrl().isEmpty()) {
            String imagePath = resolveImagePath(event.getImageUrl());
            try {
                imagetf1.setImage(new Image(imagePath));
                LOGGER.info("Image loaded for editing: " + imagePath);
            } catch (Exception e) {
                LOGGER.warning("Erreur lors du chargement de l'image pour modification: " + e.getMessage());
                imagetf1.setImage(new Image(resolveImagePath(DEFAULT_IMAGE)));
            }
        } else {
            imagetf1.setImage(new Image(resolveImagePath(DEFAULT_IMAGE)));
        }

        coords = new double[]{event.getLatitude(), event.getLongitude()};
        loadMap(event.getLatitude(), event.getLongitude());

        if (event.getHeureDebut() != null) {
            LocalTime heureDebut = event.getHeureDebut();
            cbHeureDebut.setValue(String.format("%02d", heureDebut.getHour()));
            cbMinuteDebut.setValue(String.format("%02d", heureDebut.getMinute()));
            cbSecondeDebut.setValue(String.format("%02d", heureDebut.getSecond()));
        }

        if (event.getHeureFin() != null) {
            LocalTime heureFin = event.getHeureFin();
            cbHeureFin.setValue(String.format("%02d", heureFin.getHour()));
            cbMinuteFin.setValue(String.format("%02d", heureFin.getMinute()));
            cbSecondeFin.setValue(String.format("%02d", heureFin.getSecond()));
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

    private String resolveImagePath(String imageUrl) {
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            LOGGER.warning("Image URL is null or empty, using default image");
            Path defaultPath = Paths.get(IMAGE_DIR + "default.jpg");
            return Files.exists(defaultPath) ? defaultPath.toUri().toString() : "file:" + IMAGE_DIR + "default.jpg";
        }

        // Ensure the imageUrl starts with "/Uploads/events/"
        String normalizedImageUrl = imageUrl.startsWith("/Uploads/events/") ? imageUrl : "/Uploads/events/" + imageUrl;

        try {
            Path filePath = Paths.get(IMAGE_DIR + normalizedImageUrl.replace("/Uploads/events/", ""));
            if (Files.exists(filePath)) {
                return filePath.toUri().toString();
            }
            LOGGER.warning("Image not found on filesystem: " + filePath);
            Path defaultPath = Paths.get(IMAGE_DIR + "default.jpg");
            return Files.exists(defaultPath) ? defaultPath.toUri().toString() : "file:" + IMAGE_DIR + "default.jpg";
        } catch (Exception e) {
            LOGGER.warning("Erreur lors de la résolution du chemin de l'image " + normalizedImageUrl + ": " + e.getMessage());
            Path defaultPath = Paths.get(IMAGE_DIR + "default.jpg");
            return Files.exists(defaultPath) ? defaultPath.toUri().toString() : "file:" + IMAGE_DIR + "default.jpg";
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
            ErrorType.setText("Le type d'événement doit être une valeur valide.");
            ErrorType.setVisible(true);
            isValid = false;
        } else {
            ErrorType.setVisible(false);
        }

        if (rewardtf.getValue() == null || !rewardItems.contains(rewardtf.getValue())) {
            ErrorReward.setText("La récompense doit être une valeur valide.");
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
            Alert alert = new Alert(Alert.AlertType.ERROR, "Vous devez ajouter au moins une équipe.", ButtonType.OK);
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
                Alert alert = new Alert(Alert.AlertType.ERROR, "Lieu introuvable.", ButtonType.OK);
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Veuillez entrer un lieu.", ButtonType.OK);
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
            }
        } catch (Exception e) {
            LOGGER.severe("Erreur lors de la récupération des coordonnées : " + e.getMessage());
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
        }
    }

    @FXML
    void enregistrer(ActionEvent event) throws SQLException {
        if (!validateFields()) {
            return;
        }

        String nom = nomtf.getText().trim();
        if (eventNameExists(nom) && (currentEvent == null || !currentEvent.getNom().equals(nom))) {
            ErrorNom.setText("Cet événement existe déjà.");
            ErrorNom.setVisible(true);
            return;
        }

        Event eventToSave = currentEvent != null ? currentEvent : new Event();
        eventToSave.setNom(nom);
        eventToSave.setImageUrl(imagetf.getText().trim());
        eventToSave.setType(EventType.valueOf(typetf.getValue()));
        eventToSave.setReward(EventType.Reward.valueOf(rewardtf.getValue()));
        eventToSave.setLieu(lieutf.getText().trim());
        eventToSave.setDescription(descriptiontf.getText().trim());
        eventToSave.setDate(datetf.getValue());
        eventToSave.setHeureDebut(LocalTime.parse(cbHeureDebut.getValue() + ":" + cbMinuteDebut.getValue() + ":" + cbSecondeDebut.getValue()));
        eventToSave.setHeureFin(LocalTime.parse(cbHeureFin.getValue() + ":" + cbMinuteFin.getValue() + ":" + cbSecondeFin.getValue()));
        eventToSave.setLatitude(coords[0]);
        eventToSave.setLongitude(coords[1]);
        eventToSave.setEquipes(new HashSet<>(addedEquipes));
        SalleService salleService = new SalleService();
        Salle salle = salleService.getSalleByResponsableId(responsableId);
        eventToSave.setIdSalle(salle.getId());

        try {
            if (currentEvent == null) {
                eventService.ajouter(eventToSave);
                for (Equipe equipe : addedEquipes) {
                    equipeEventService.ajouter(equipe, eventToSave);
                }
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Événement ajouté avec succès !", ButtonType.OK);
                alert.showAndWait();
            } else {
                // Delete old image if a new one is uploaded
                if (!imagetf.getText().equals(currentEvent.getImageUrl()) && currentEvent.getImageUrl() != null) {
                    Path oldImagePath = Paths.get(IMAGE_DIR + currentEvent.getImageUrl().replace("/Uploads/events/", ""));
                    if (Files.exists(oldImagePath)) {
                        Files.delete(oldImagePath);
                        LOGGER.info("Deleted old image: " + oldImagePath);
                    }
                }
                eventService.modifier(eventToSave);
                for (Equipe equipe : eventService.getEquipesForEvent(eventToSave.getId())) {
                    equipeEventService.supprimer(equipe, eventToSave);
                }
                for (Equipe equipe : addedEquipes) {
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
        } catch (SQLException | IOException e) {
            LOGGER.severe("Erreur lors de l'enregistrement de l'événement : " + e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors de l'enregistrement : " + e.getMessage(), ButtonType.OK);
            alert.showAndWait();
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
            LOGGER.severe("Erreur lors de la vérification du nom de l'événement : " + e.getMessage());
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
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png", "*.gif", "*.bmp", "*.jpeg"));
        Stage stage = (Stage) nomtf.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            try {
                File dir = new File(IMAGE_DIR);
                if (!dir.exists()) {
                    dir.mkdirs();
                    LOGGER.info("Created directory: " + IMAGE_DIR);
                }

                String extension = selectedFile.getName().substring(selectedFile.getName().lastIndexOf(".") + 1);
                String fileName = System.currentTimeMillis() + "." + extension;
                File targetFile = new File(IMAGE_DIR + fileName);

                Files.copy(selectedFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                LOGGER.info("Image copied to: " + targetFile.getAbsolutePath());

                String formattedImageUrl = "/Uploads/events/" + fileName;
                imagetf.setText(formattedImageUrl);

                String imagePath = resolveImagePath(formattedImageUrl);
                imagetf1.setImage(new Image(imagePath));
                LOGGER.info("Image preview set: " + imagePath);
            } catch (IOException e) {
                LOGGER.severe("Erreur lors de l'enregistrement de l'image : " + e.getMessage());
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'enregistrer l'image : " + e.getMessage());
            }
        }
    }

    @FXML
    private void AjouterEquipe(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjoutEquipe.fxml"));
            Parent root = loader.load();
            AjoutEquipeController ajoutEquipeController = loader.getController();
            ajoutEquipeController.setAjoutEvennementController(this);

            Stage stage = new Stage();
            stage.setTitle("📩 Ajouter Equipe");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            LOGGER.severe("Erreur lors de l'ouverture de la fenêtre d'ajout d'équipe : " + e.getMessage());
        }
    }

    @FXML
    private void handleEquipeListClick(MouseEvent event) {
        try {
            String selectedTeamName = equipesListView.getSelectionModel().getSelectedItem();
            if (selectedTeamName == null) return;

            Equipe selectedEquipe = addedEquipes.stream()
                    .filter(e -> e.getNom().equals(selectedTeamName))
                    .findFirst()
                    .orElse(null);
            if (selectedEquipe != null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjoutEquipe.fxml"));
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
}