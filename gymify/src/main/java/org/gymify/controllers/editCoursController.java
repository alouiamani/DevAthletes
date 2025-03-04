package org.gymify.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.gymify.entities.*;
import org.gymify.services.ActivityService;
import org.gymify.services.CoursService;
import org.gymify.services.SalleService;

import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class editCoursController {
    private final ActivityService activityService = new ActivityService();
    private final SalleService salleService = new SalleService();
    private final CoursService coursService = new CoursService();

    @FXML
    private ComboBox<String> GymcomoBoxEdit;
    @FXML
    private RadioButton PertedepoidsradioEdit;
    @FXML
    private RadioButton prisedemasseradioEdit;
    @FXML
    private RadioButton enduranceradioEdit;
    @FXML
    private RadioButton relaxationradioEdit;
    @FXML
    private TextField titleCoursedit;
    @FXML
    private TextArea descriptionedit;
    @FXML
    private DatePicker dateDebutedit;
    @FXML
    private ComboBox<Integer> hourDebutedit, minuteDebutedit, secondDebutedit;
    @FXML
    private ComboBox<Integer> hourFinedit, minuteFinedit, secondFinedit;
    @FXML
    private ComboBox<String> activityComboBoxedit;
    @FXML
    private Label titleCoursError, dateError, activityError, gymError, objectifError;

    private Cours currentCours;

    @FXML
    public void initialize() throws SQLException {
        loadActivities();
        loadSalles();
        setupTimeComboBox(hourDebutedit, 0, 23);
        setupTimeComboBox(minuteDebutedit, 0, 59);
        setupTimeComboBox(secondDebutedit, 0, 59);
        setupTimeComboBox(hourFinedit, 0, 23);
        setupTimeComboBox(minuteFinedit, 0, 59);
        setupTimeComboBox(secondFinedit, 0, 59);

        // Ajouter des écouteurs d'événements pour les RadioButton
        PertedepoidsradioEdit.setOnAction(event -> deselectOtherRadioButtons(PertedepoidsradioEdit));
        prisedemasseradioEdit.setOnAction(event -> deselectOtherRadioButtons(prisedemasseradioEdit));
        enduranceradioEdit.setOnAction(event -> deselectOtherRadioButtons(enduranceradioEdit));
        relaxationradioEdit.setOnAction(event -> deselectOtherRadioButtons(relaxationradioEdit));
    }

    private void setupTimeComboBox(ComboBox<Integer> comboBox, int min, int max) {
        for (int i = min; i <= max; i++) {
            comboBox.getItems().add(i);
        }
        comboBox.setValue(min); // Valeur par défaut
    }

    private void loadActivities() {
        List<Activité> activities = activityService.Display();
        if (activityComboBoxedit != null) {
            for (Activité activité : activities) {
                activityComboBoxedit.getItems().add(activité.getNom());
            }
        } else {
            System.out.println("activityComboBox is null");
        }
    }

    private void loadSalles() throws SQLException {
        List<Salle> salles = salleService.afficher();
        if (salles != null) {
            for (Salle salle : salles) {
                GymcomoBoxEdit.getItems().add(salle.getNom());
            }
        } else {
            System.out.println("salleComboBox is null");
        }
    }

    public void setCoursData(Cours cours) {
        this.currentCours = cours;
        if (cours != null) {
            titleCoursedit.setText(cours.getTitle());
            descriptionedit.setText(cours.getDescription());

            // Convertir java.util.Date en LocalDate
            Date dateDebut = cours.getDateDebut();
            if (dateDebut != null) {
                LocalDate localDateDebut = Instant.ofEpochMilli(dateDebut.getTime())
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
                dateDebutedit.setValue(localDateDebut);
            }

            // Définir les heures de début
            LocalTime heureDebut = cours.getHeureDebut();
            if (heureDebut != null) {
                hourDebutedit.setValue(heureDebut.getHour());
                minuteDebutedit.setValue(heureDebut.getMinute());
                secondDebutedit.setValue(heureDebut.getSecond());
            }

            // Définir les heures de fin
            LocalTime heureFin = cours.getHeureFin();
            if (heureFin != null) {
                hourFinedit.setValue(heureFin.getHour());
                minuteFinedit.setValue(heureFin.getMinute());
                secondFinedit.setValue(heureFin.getSecond());
            }

            // Définir l'activité sélectionnée
            Activité activité = cours.getActivité();
            if (activité != null) {
                activityComboBoxedit.setValue(activité.getNom());
            }

            // Définir la salle sélectionnée
            Salle salle = cours.getSalle();
            if (salle != null) {
                GymcomoBoxEdit.setValue(salle.getNom());
            }

            // Définir l'objectif sélectionné
            switch (cours.getObjectifs()) {
                case PERTE_PROIDS:
                    PertedepoidsradioEdit.setSelected(true);
                    break;
                case PRISE_DE_MASSE:
                    prisedemasseradioEdit.setSelected(true);
                    break;
                case ENDURANCE:
                    enduranceradioEdit.setSelected(true);
                    break;
                case RELAXATION:
                    relaxationradioEdit.setSelected(true);
                    break;
            }
        }
    }

    @FXML
    public void editCourse(ActionEvent actionEvent) throws SQLException {
        // Validation des champs obligatoires
        if (currentCours == null) {
            System.out.println("Aucun cours sélectionné pour la mise à jour.");
            return;
        }

        String newTitle = titleCoursedit.getText();
        if (newTitle == null || newTitle.trim().isEmpty()) {
            titleCoursError.setText("Le titre du cours est obligatoire.");
            return;
        } else {
            titleCoursError.setText(""); // Effacer l'erreur si le champ est valide
        }

        LocalDate newDateDebut = dateDebutedit.getValue();
        if (newDateDebut == null) {
            dateError.setText("La date de début est obligatoire.");
            return;
        } else {
            dateError.setText(""); // Effacer l'erreur si le champ est valide
        }

        LocalTime newHeureDebut = LocalTime.of(
                hourDebutedit.getValue() != null ? hourDebutedit.getValue() : 0,
                minuteDebutedit.getValue() != null ? minuteDebutedit.getValue() : 0,
                secondDebutedit.getValue() != null ? secondDebutedit.getValue() : 0
        );

        LocalTime newHeureFin = LocalTime.of(
                hourFinedit.getValue() != null ? hourFinedit.getValue() : 0,
                minuteFinedit.getValue() != null ? minuteFinedit.getValue() : 0,
                secondFinedit.getValue() != null ? secondFinedit.getValue() : 0
        );

        if (newHeureFin.isBefore(newHeureDebut)) {
            dateError.setText("L'heure de fin doit être après l'heure de début.");
            return;
        } else {
            dateError.setText(""); // Effacer l'erreur si le champ est valide
        }

        String selectedActivityName = activityComboBoxedit.getValue();
        if (selectedActivityName == null || selectedActivityName.trim().isEmpty()) {
            activityError.setText("Veuillez sélectionner une activité.");
            return;
        } else {
            activityError.setText(""); // Effacer l'erreur si le champ est valide
        }

        String selectedSalleName = GymcomoBoxEdit.getValue();
        if (selectedSalleName == null || selectedSalleName.trim().isEmpty()) {
            gymError.setText("Veuillez sélectionner une salle.");
            return;
        } else {
            gymError.setText(""); // Effacer l'erreur si le champ est valide
        }

        // Vérifier qu'un seul RadioButton est sélectionné
        int selectedCount = 0;
        if (PertedepoidsradioEdit.isSelected()) selectedCount++;
        if (prisedemasseradioEdit.isSelected()) selectedCount++;
        if (enduranceradioEdit.isSelected()) selectedCount++;
        if (relaxationradioEdit.isSelected()) selectedCount++;

        if (selectedCount != 1) {
            objectifError.setText("Veuillez sélectionner un seul objectif.");
            return;
        } else {
            objectifError.setText(""); // Effacer l'erreur si le champ est valide
        }

        // Récupérer l'activité et la salle sélectionnées
        Activité selectedActivity = findActivityByName(selectedActivityName);
        Salle selectedSalle = findSalleByName(selectedSalleName);

        if (selectedActivity == null) {
            activityError.setText("Activité introuvable.");
            return;
        }

        if (selectedSalle == null) {
            gymError.setText("Salle introuvable.");
            return;
        }

        // Définir l'objectif du cours
        Objectifs objectifs = null;
        if (PertedepoidsradioEdit.isSelected()) {
            objectifs = Objectifs.PERTE_PROIDS;
        } else if (prisedemasseradioEdit.isSelected()) {
            objectifs = Objectifs.PRISE_DE_MASSE;
        } else if (enduranceradioEdit.isSelected()) {
            objectifs = Objectifs.ENDURANCE;
        } else if (relaxationradioEdit.isSelected()) {
            objectifs = Objectifs.RELAXATION;
        }

        // Mettre à jour les propriétés du cours
        currentCours.setTitle(newTitle);
        currentCours.setDescription(descriptionedit.getText());
        currentCours.setDateDebut(Date.from(newDateDebut.atTime(newHeureDebut).atZone(ZoneId.systemDefault()).toInstant()));
        currentCours.setHeureDebut(newHeureDebut);
        currentCours.setHeureFin(newHeureFin);
        currentCours.setActivité(selectedActivity);
        currentCours.setSalle(selectedSalle);
        currentCours.setObjectifs(objectifs);
        System.out.println(currentCours);

        // Mettre à jour le cours dans la base de données
        coursService.Update(currentCours);

        // Afficher un message de confirmation
        System.out.println("Le cours a été mis à jour avec succès.");
    }

    private Activité findActivityByName(String name) {
        List<Activité> activities = activityService.Display();
        for (Activité activité : activities) {
            if (activité.getNom().equals(name)) {
                return activité;
            }
        }
        return null; // Si l'activité n'est pas trouvée
    }

    private Salle findSalleByName(String name) throws SQLException {
        List<Salle> salles = salleService.afficher();
        for (Salle salle : salles) {
            if (salle.getNom().equals(name)) {
                return salle;
            }
        }
        return null; // Si la salle n'est pas trouvée
    }

    private void deselectOtherRadioButtons(RadioButton selectedRadioButton) {
        if (selectedRadioButton.isSelected()) {
            if (selectedRadioButton != PertedepoidsradioEdit) PertedepoidsradioEdit.setSelected(false);
            if (selectedRadioButton != prisedemasseradioEdit) prisedemasseradioEdit.setSelected(false);
            if (selectedRadioButton != enduranceradioEdit) enduranceradioEdit.setSelected(false);
            if (selectedRadioButton != relaxationradioEdit) relaxationradioEdit.setSelected(false);
        }
    }
}