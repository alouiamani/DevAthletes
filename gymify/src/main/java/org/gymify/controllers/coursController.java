package org.gymify.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import org.gymify.entities.*;
import org.gymify.services.ActivityService;
import org.gymify.services.CoursService;
import org.gymify.services.SalleService;
import org.gymify.utils.AuthToken;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

public class coursController {

    private final ActivityService activityService = new ActivityService();
    private final SalleService salleService = new SalleService();

    @FXML
    private ComboBox<String> activityComboBox;
    @FXML
    public StackPane contentArea;
    @FXML
    private TextField titleCours;
    @FXML
    private TextArea description;
    @FXML
    private DatePicker dateDebut;
    @FXML
    private Label titleCoursError;
    @FXML
    private Label dateError;
    @FXML
    private Label descriptionCoursError;
    @FXML
    private Label activityError;
    @FXML
    private Label gymError;
    @FXML
    private Label objectifError;
    @FXML
    private ComboBox<String> GymcomoBox;
    @FXML
    private ComboBox<Integer> hourDebut, minuteDebut, secondDebut;
    @FXML
    private ComboBox<Integer> hourFin, minuteFin, secondFin;
    @FXML
    private RadioButton Pertedepoidsradio;
    @FXML
    private RadioButton prisedemasseradio;
    @FXML
    private RadioButton enduranceradio;
    @FXML
    private RadioButton relaxationradio;
    private User userId= AuthToken.getCurrentUser();

    private CoursService coursService = new CoursService();
    private Planning planning;
    private User user;

    @FXML
    public void initialize() throws SQLException {
        loadActivities();
        loadSalles();
        setupTimeComboBox(hourDebut, 0, 23);
        setupTimeComboBox(minuteDebut, 0, 59);
        setupTimeComboBox(secondDebut, 0, 59);
        setupTimeComboBox(hourFin, 0, 23);
        setupTimeComboBox(minuteFin, 0, 59);
        setupTimeComboBox(secondFin, 0, 59);

        // Gestion des RadioButton
        Pertedepoidsradio.setOnAction(event -> handleRadioButtonSelection(Pertedepoidsradio));
        prisedemasseradio.setOnAction(event -> handleRadioButtonSelection(prisedemasseradio));
        enduranceradio.setOnAction(event -> handleRadioButtonSelection(enduranceradio));
        relaxationradio.setOnAction(event -> handleRadioButtonSelection(relaxationradio));
    }

    private void setupTimeComboBox(ComboBox<Integer> comboBox, int min, int max) {
        for (int i = min; i <= max; i++) {
            comboBox.getItems().add(i);
        }
        comboBox.setValue(min); // Valeur par défaut
    }

    private void handleRadioButtonSelection(RadioButton selectedRadioButton) {
        // Désélectionner tous les autres RadioButton
        if (selectedRadioButton.isSelected()) {
            Pertedepoidsradio.setSelected(false);
            prisedemasseradio.setSelected(false);
            enduranceradio.setSelected(false);
            relaxationradio.setSelected(false);
            selectedRadioButton.setSelected(true); // Réactiver celui qui a été sélectionné
        }
    }

    private Objectifs getSelectedObjective() {
        if (Pertedepoidsradio.isSelected()) {
            return Objectifs.PERTE_PROIDS;
        } else if (prisedemasseradio.isSelected()) {
            return Objectifs.PRISE_DE_MASSE;
        } else if (enduranceradio.isSelected()) {
            return Objectifs.ENDURANCE;
        } else if (relaxationradio.isSelected()) {
            return Objectifs.RELAXATION;
        } else {
            return null; // Aucun objectif sélectionné
        }
    }

    public void setPlanningSelect(Planning planning, User user) {
        if (planning != null) {
            this.user = user;
            System.out.println(user);
            this.planning = planning;
            System.out.println("Le planning n'est pas null");
        } else {
            System.out.println("Le planning est null");
        }
    }

    private void loadActivities() {
        List<Activité> activities = activityService.Display(); // Récupérer la liste des activités
        if (activityComboBox != null) {
            for (Activité activité : activities) {
                activityComboBox.getItems().add(activité.getNom()); // Ajouter uniquement le nom des activités dans le ComboBox
            }
        } else {
            System.out.println("activityComboBox is null");
        }
    }

    private void loadSalles() throws SQLException {
        List<Salle> salles = salleService.afficher(); // Récupérer la liste des salles
        if (GymcomoBox != null) {
            for (Salle salle : salles) {
                GymcomoBox.getItems().add(salle.getNom()); // Ajouter uniquement le nom des salles dans le ComboBox
            }
        } else {
            System.out.println("GymcomoBox is null");
        }
    }

    @FXML
    private void addCourse() throws SQLException {
        if (planning == null) {
            System.err.println("Le planning n'a pas été sélectionné.");
            return;
        }

        String title = titleCours.getText().trim();
        String desc = description.getText().trim();
        String selectedActivityName = activityComboBox.getValue();
        String selectedSalleName = GymcomoBox.getValue();
        Objectifs selectedObjective = getSelectedObjective();
        LocalDate planningStartDate = new java.sql.Date(planning.getdateDebut().getTime()).toLocalDate();
        LocalDate planningEndDate = new java.sql.Date(planning.getdateFin().getTime()).toLocalDate();

        LocalDate startDate = dateDebut.getValue();


        // Validation des champs
        boolean hasError = false;

        if (title.isEmpty()) {
            titleCoursError.setText("Le titre est requis.");
            hasError = true;
        } else {
            titleCoursError.setText("");
        }

        if (desc.isEmpty()) {
            descriptionCoursError.setText("La description est requise.");
            hasError = true;
        } else {
            descriptionCoursError.setText("");
        }

        if (startDate == null) {
            dateError.setText("La date est requise.");
            hasError = true;
        } else {
            dateError.setText("");
        }
        if (startDate.isBefore(planningStartDate)||startDate.isAfter(planningEndDate)){
            dateError.setText("La date incorrecte");
            hasError = true;
        } else {
            dateError.setText("");
        }

        if (selectedActivityName == null) {
            activityError.setText("L'activité est requise.");
            hasError = true;
        } else {
            activityError.setText("");
        }

        if (selectedSalleName == null) {
            gymError.setText("La salle est requise.");
            hasError = true;
        } else {
            gymError.setText("");
        }

        if (selectedObjective == null) {
            objectifError.setText("Veuillez sélectionner un objectif.");
            hasError = true;
        } else {
            objectifError.setText("");
        }

        if (hasError) return; // Arrêter la méthode si des erreurs sont présentes

        // Récupération des valeurs de temps
        LocalTime heureDebut = LocalTime.of(hourDebut.getValue(), minuteDebut.getValue(), secondDebut.getValue());
        LocalTime heureFin = LocalTime.of(hourFin.getValue(), minuteFin.getValue(), secondFin.getValue());

        // Conversion des dates et heures en Timestamp
        Timestamp timestampDebut = Timestamp.valueOf(LocalDateTime.of(startDate, heureDebut));

        // Recherche de l'activité et de la salle
        Activité selectedActivity = findActivityByName(selectedActivityName);
        if (selectedActivity == null) {
            activityError.setText("Activité non trouvée.");
            return;
        }

        Salle selectedSalle = findSalleByName(selectedSalleName);
        if (selectedSalle == null) {
            gymError.setText("Salle non trouvée.");
            return;
        }

        // Création et configuration du Cours
        Cours cours = new Cours();
        cours.setTitle(title);
        cours.setDescription(desc);
        cours.setDateDebut(timestampDebut);
        cours.setHeureDebut(heureDebut);
        cours.setHeureFin(heureFin);
        cours.setActivité(selectedActivity);
        cours.setSalle(selectedSalle);
        cours.setPlanning(planning);
        cours.setUser(userId);
        cours.setObjectifs(selectedObjective); // Assigner l'objectif sélectionné

        // Enregistrement du cours via le service
        coursService.Add(cours);
        System.out.println("Cours enregistré avec succès : " + cours.getTitle() +
                ", Date : " + timestampDebut +
                ", Objectif : " + selectedObjective.getLabel());
    }

    public Activité findActivityByName(String name) {
        List<Activité> activities = activityService.Display(); // Récupérer la liste des activités
        for (Activité activité : activities) {
            if (activité.getNom().equals(name)) {
                return activité;
            }
        }
        return null; // Si l'activité n'est pas trouvée
    }

    public Salle findSalleByName(String name) throws SQLException {
        List<Salle> salles = salleService.afficher(); // Récupérer la liste des salles
        for (Salle salle : salles) {
            if (salle.getNom().equals(name)) {
                return salle;
            }
        }
        return null; // Si la salle n'est pas trouvée
    }
}