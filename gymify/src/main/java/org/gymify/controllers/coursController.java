package org.gymify.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import org.gymify.entities.Activité;
import org.gymify.entities.Cours;
import org.gymify.entities.Planning;
import org.gymify.services.ActivityService;
import org.gymify.services.CoursService;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class coursController {

    private final ActivityService activityService = new ActivityService();

    @FXML
    private ComboBox<String> activityComboBox;
    @FXML
    public StackPane contentArea;// Modifiez ici pour stocker des String (les noms des activités)
    @FXML
    private TextField titleCours;
    @FXML
    private TextArea description;
    @FXML
    private DatePicker dateDebut;
    @FXML
    private DatePicker dateFin;
    @FXML
    private Label titleCoursError;
    @FXML
    private Label dateError;
    @FXML
    private Label descriptionCoursError;
    @FXML
    private Label activityError;
    private CoursService coursService = new CoursService();
    private Planning planning;
    @FXML
    private ComboBox<Integer> hourDebut, minuteDebut, secondDebut;
    @FXML
    private ComboBox<Integer> hourFin, minuteFin, secondFin;

    @FXML
    public void initialize() {
        loadActivities();
        setupTimeComboBox(hourDebut, 0, 23);
        setupTimeComboBox(minuteDebut, 0, 59);
        setupTimeComboBox(secondDebut, 0, 59);


        setupTimeComboBox(hourFin, 0, 23);
        setupTimeComboBox(minuteFin, 0, 59);
        setupTimeComboBox(secondFin, 0, 59);

        // Chargez les activités dès l'initialisation du contrôleur
    }
    private void setupTimeComboBox(ComboBox<Integer> comboBox, int min, int max) {
        for (int i = min; i <= max; i++) {
            comboBox.getItems().add(i);
        }
        comboBox.setValue(min); // Valeur par défaut
    }
    public LocalDateTime getDateTime(DatePicker datePicker, ComboBox<Integer> hourBox, ComboBox<Integer> minuteBox, ComboBox<Integer> secondBox) {
        LocalDate date = datePicker.getValue();
        Integer hour = hourBox.getValue();
        Integer minute = minuteBox.getValue();
        Integer second = secondBox.getValue();
        System.out.println("Date sélectionnée: " + date);
        System.out.println("Heure sélectionnée: " + hour);
        System.out.println("Minute sélectionnée: " + minute);
        System.out.println("Seconde sélectionnée: " + second);


        if (date != null && hour != null && minute != null && second != null) {
            return LocalDateTime.of(date, LocalTime.of(hour, minute, second));
        }
        return null; // Si une valeur est manquante
    }
    public void setPlanningSelect(Planning planning) {
        if (planning != null) {
            this.planning = planning;
            System.out.println("Le planning n'est pas null");
            // Affiche le titre du planning
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

    @FXML
    private void addCourse() {
        if (planning == null) {
            System.err.println("Le planning n'a pas été sélectionné.");
            return;
        }

        String title = titleCours.getText().trim();
        String desc = description.getText().trim();
        String selectedActivityName = activityComboBox.getValue();

        LocalDate startDate = dateDebut.getValue();
        LocalDate endDate = dateFin.getValue();

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

        if (startDate == null || endDate == null) {
            dateError.setText("Les dates de début et de fin sont requises.");
            hasError = true;
        } else if (endDate.isBefore(startDate)) {
            dateError.setText("La date de fin doit être après la date de début.");
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

        if (hasError) return; // Arrêter la méthode si des erreurs sont présentes

        // Récupération des valeurs de temps
        LocalTime heureDebut = LocalTime.of(hourDebut.getValue(), minuteDebut.getValue(), secondDebut.getValue());
        LocalTime heureFin = LocalTime.of(hourFin.getValue(), minuteFin.getValue(), secondFin.getValue());

        // Conversion des dates et heures en Timestamp
        Timestamp timestampDebut = Timestamp.valueOf(LocalDateTime.of(startDate, heureDebut));
        Timestamp timestampFin = Timestamp.valueOf(LocalDateTime.of(endDate, heureFin));

        // Recherche de l'activité
        Activité selectedActivity = findActivityByName(selectedActivityName);
        if (selectedActivity == null) {
            activityError.setText("Activité non trouvée.");
            return;
        }

        // Création et configuration du Cours
        Cours cours = new Cours();
        cours.setTitle(title);
        cours.setDescription(desc);
        cours.setDateDebut(timestampDebut);
        cours.setDateFin(timestampFin);
        cours.setHeureDebut(heureDebut);
        cours.setHeureFin(heureFin);
        cours.setActivité(selectedActivity);
        cours.setPlanning(planning);
        System.out.println("test");

        // Enregistrement du cours via le service
        coursService.Add(cours);
        System.out.println("Cours enregistré avec succès : " + cours.getTitle() +
                ", Début : " + timestampDebut +
                ", Fin : " + timestampFin);


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







    // Autres méthodes du contrôleur
    }







