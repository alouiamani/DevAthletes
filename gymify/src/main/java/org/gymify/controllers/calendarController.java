package org.gymify.controllers;

import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import jfxtras.scene.control.agenda.Agenda;
import org.gymify.entities.Cours;
import org.gymify.services.CoursService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

public class calendarController {

    @FXML
    private StackPane calendarContainer; // Conteneur pour l'agenda

    private Agenda agenda; // Agenda pour afficher les cours
    private CoursService coursService; // Service pour interagir avec la base de données

    @FXML
    public void initialize() {
        // Initialiser l'agenda
        agenda = new Agenda();

        // Charger le fichier CSS
        agenda.getStylesheets().add(getClass().getResource("/agenda-style.css").toExternalForm());

        agenda.setStyle("-fx-background-color: #5894da;");


        // Désactiver le glisser-déposer des rendez-vous
        disableDragAndDrop(agenda);

        // Initialiser le service CoursService
        coursService = new CoursService();

        // Charger les cours depuis la base de données et les afficher dans l'agenda
        loadCoursIntoAgenda();

        // Ajouter l'agenda au conteneur
        calendarContainer.getChildren().add(agenda);
    }

    /**
     * Désactive le glisser-déposer des rendez-vous dans l'Agenda.
     *
     * @param agenda L'Agenda à configurer.
     */
    private void disableDragAndDrop(Agenda agenda) {
        // Ajouter un filtre d'événements pour intercepter les événements de souris
        agenda.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            // Empêcher l'utilisateur de déplacer les rendez-vous
            event.consume(); // Consommer l'événement pour empêcher son traitement par l'Agenda
        });

        agenda.addEventFilter(MouseEvent.MOUSE_DRAGGED, event -> {
            // Empêcher l'utilisateur de déplacer les rendez-vous
            event.consume(); // Consommer l'événement pour empêcher son traitement par l'Agenda
        });
    }

    /**
     * Charge les cours depuis la base de données et les ajoute à l'agenda.
     */
    private void loadCoursIntoAgenda() {
        try {
            // Vérifier si le service CoursService est initialisé
            if (coursService == null) {
                System.err.println("Erreur : Le service CoursService n'est pas initialisé.");
                return;
            }

            // Récupérer tous les cours depuis la base de données
            List<Cours> coursList = coursService.Display();

            // Vérifier si la liste des cours est null ou vide
            if (coursList == null) {
                System.err.println("Erreur : La liste des cours est null.");
                return;
            }
            System.out.println("Nombre de cours récupérés : " + coursList.size());

            // Convertir chaque cours en un rendez-vous (Appointment) et l'ajouter à l'agenda
            for (Cours cours : coursList) {
                System.out.println("Conversion du cours : " + cours.getTitle());
                Agenda.AppointmentImplLocal appointment = convertCoursToAppointment(cours);
                if (appointment != null) {
                    agenda.appointments().add(appointment);
                } else {
                    System.err.println("Le cours n'a pas pu être converti en rendez-vous.");
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des cours : " + e.getMessage());
            e.printStackTrace(); // Afficher la stack trace pour plus de détails
        }
    }

    /**
     * Convertit un objet Cours en un rendez-vous (Appointment) pour l'agenda.
     *
     * @param cours Le cours à convertir.
     * @return Un objet Appointment compatible avec Agenda, ou null si la conversion échoue.
     */
    private Agenda.AppointmentImplLocal convertCoursToAppointment(Cours cours) {
        try {
            // Vérifier si l'objet Cours est null
            if (cours == null) {
                System.err.println("Erreur : L'objet Cours est null.");
                return null;
            }

            // Vérifier si les dates sont null
            if (cours.getDateDebut() == null ) {
                System.err.println("Erreur : Les dates du cours sont null.");
                return null;
            }

            // Convertir java.sql.Date en java.util.Date
            java.util.Date utilDateDebut = new java.util.Date(cours.getDateDebut().getTime());

            // Convertir java.util.Date en LocalDate
            LocalDate startDate = utilDateDebut.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();

            // Vérifier si les heures sont null
            if (cours.getHeureDebut() == null || cours.getHeureFin() == null) {
                System.err.println("Erreur : Les heures du cours sont null.");
                return null;
            }

            // Combiner LocalDate et LocalTime pour obtenir LocalDateTime
            LocalDateTime startDateTime = LocalDateTime.of(startDate, cours.getHeureDebut());
            LocalDateTime endDateTime = LocalDateTime.of(startDate, cours.getHeureFin());

            // Créer un rendez-vous pour l'agenda
            Agenda.AppointmentImplLocal appointment = new Agenda.AppointmentImplLocal()
                    .withStartLocalDateTime(startDateTime)
                    .withEndLocalDateTime(endDateTime)
                    .withSummary(cours.getTitle()) // Titre du cours
                    .withDescription(cours.getDescription()) // Description du cours
                    .withLocation("Gymnase"); // Emplacement du cours

            // Appliquer un style différent en fonction du type de cours
            if (cours.getTitle().toLowerCase().contains("yoga")) {
                appointment.withAppointmentGroup(new Agenda.AppointmentGroupImpl().withStyleClass("yoga-group"));
            } else if (cours.getTitle().toLowerCase().contains("salsa")) {
                appointment.withAppointmentGroup(new Agenda.AppointmentGroupImpl().withStyleClass("salsa-group"));
            } else {
                appointment.withAppointmentGroup(new Agenda.AppointmentGroupImpl().withStyleClass("default-group"));
            }

            return appointment;
        } catch (Exception e) {
            System.err.println("Erreur lors de la conversion du cours en rendez-vous : " + e.getMessage());
            e.printStackTrace(); // Afficher la stack trace pour plus de détails
            return null;
        }
    }

    /**
     * Rafraîchit l'agenda en rechargeant les cours depuis la base de données.
     */
    public void refreshCalendar() {
        agenda.appointments().clear(); // Supprimer les rendez-vous existants
        loadCoursIntoAgenda(); // Recharger les cours
    }
}