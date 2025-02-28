package org.gymify.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.gymify.entities.Activité;
import org.gymify.entities.Cours;
import org.gymify.services.ActivityService;
import org.gymify.services.CoursService;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class editCoursController {
    private final ActivityService activityService = new ActivityService();

    @FXML
    private TextField titleCoursedit;
    @FXML
    private TextArea descriptionedit;
    @FXML
    private DatePicker dateDebutedit;
    @FXML
    private DatePicker dateFinedit;
    @FXML
    private ComboBox<Integer> hourDebutedit, minuteDebutedit, secondDebutedit;
    @FXML
    private ComboBox<Integer> hourFinedit, minuteFinedit, secondFinedit;
    @FXML
    private ComboBox<String> activityComboBoxedit;
    private Cours currentCours;



    public void initialize() {
        loadActivities();
        setupTimeComboBox(hourDebutedit, 0, 23);
        setupTimeComboBox(minuteDebutedit, 0, 59);
        setupTimeComboBox(secondDebutedit, 0, 59);


        setupTimeComboBox(hourFinedit, 0, 23);
        setupTimeComboBox(minuteFinedit, 0, 59);
        setupTimeComboBox(secondFinedit, 0, 59);
    }

    private void setupTimeComboBox(ComboBox<Integer> comboBox, int min, int max) {
        for (int i = min; i <= max; i++) {
            comboBox.getItems().add(i);
        }
        comboBox.setValue(min); // Valeur par défaut
    }


    private void loadActivities() {
        List<Activité> activities = activityService.Display(); // Récupérer la liste des activités
        if (activityComboBoxedit != null) {
            for (Activité activité : activities) {
                activityComboBoxedit.getItems().add(activité.getNom()); // Ajouter uniquement le nom des activités dans le ComboBox
            }
        } else {
            System.out.println("activityComboBox is null");
        }

    }

    public void setCoursData(Cours cours) {
        this.currentCours = cours;
        if (cours != null) {
            titleCoursedit.setText(cours.getTitle());
            descriptionedit.setText(cours.getDescription());

            // Convert java.util.Date to LocalDate
            Date dateDebut = cours.getDateDebut();
            if (dateDebut != null) {
                LocalDate localDateDebut = Instant.ofEpochMilli(dateDebut.getTime())
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
                dateDebutedit.setValue(localDateDebut);
            }

            Date dateFin = cours.getDateFin();
            if (dateFin != null) {
                LocalDate localDateFin = Instant.ofEpochMilli(dateFin.getTime())
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
                dateFinedit.setValue(localDateFin);
            }

            // Set time fields for start time
            LocalTime heureDebut = cours.getHeureDebut();
            if (heureDebut != null) {
                hourDebutedit.setValue(heureDebut.getHour());
                minuteDebutedit.setValue(heureDebut.getMinute());
                secondDebutedit.setValue(heureDebut.getSecond());
            }

            // Set time fields for end time
            LocalTime heureFin = cours.getHeureFin();
            if (heureFin != null) {
                hourFinedit.setValue(heureFin.getHour());
                minuteFinedit.setValue(heureFin.getMinute());
                secondFinedit.setValue(heureFin.getSecond());
            }

            // Set the selected activity in the ComboBox
            Activité activité = cours.getActivité();
            if (activité != null) {
                activityComboBoxedit.setValue(activité.getNom());
            }


        }
    }

    public void editCourse(ActionEvent actionEvent) {
        if (currentCours != null) {
            // Récupérer les valeurs des champs de texte
            String newTitle = titleCoursedit.getText();
            String newDescription = descriptionedit.getText();

            // Récupérer les dates et heures sélectionnées
            LocalDate newDateDebut = dateDebutedit.getValue();
            LocalDate newDateFin = dateFinedit.getValue();
            LocalTime newHeureDebut = LocalTime.of(hourDebutedit.getValue(), minuteDebutedit.getValue(), secondDebutedit.getValue());
            LocalTime newHeureFin = LocalTime.of(hourFinedit.getValue(), minuteFinedit.getValue(), secondFinedit.getValue());

            // Combiner les dates et heures en objets Date
            Date newDateDebutTime = Date.from(newDateDebut.atTime(newHeureDebut).atZone(ZoneId.systemDefault()).toInstant());
            Date newDateFinTime = Date.from(newDateFin.atTime(newHeureFin).atZone(ZoneId.systemDefault()).toInstant());

            // Récupérer l'activité sélectionnée
            String selectedActivityName = activityComboBoxedit.getValue();
            Activité selectedActivity = findActivityByName(selectedActivityName);

            // Mettre à jour les propriétés du cours
            currentCours.setTitle(newTitle);
            currentCours.setDescription(newDescription);
            currentCours.setDateDebut(newDateDebutTime);
            currentCours.setDateFin(newDateFinTime);
            currentCours.setHeureDebut(newHeureDebut);
            currentCours.setHeureFin(newHeureFin);
            currentCours.setActivité(selectedActivity);


            CoursService coursService = new CoursService();
            coursService.Update(currentCours);

            // Afficher un message de confirmation ou effectuer d'autres actions nécessaires

            System.out.println("Le cours a été mis à jour avec succès.");
        } else {
            System.out.println("Aucun cours sélectionné pour la mise à jour.");
        }
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

}
    
