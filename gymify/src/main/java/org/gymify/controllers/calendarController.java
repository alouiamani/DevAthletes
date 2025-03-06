package org.gymify.controllers;

import com.google.api.services.calendar.model.Event;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import jfxtras.scene.control.agenda.Agenda;
import org.gymify.entities.Cours;
import org.gymify.services.CoursService;
import org.gymify.services.GoogleCalendarService;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

public class calendarController {

    @FXML
    private StackPane calendarContainer;

    private Agenda agenda;
    private CoursService coursService;

    @FXML
    public void initialize() {
        agenda = new Agenda();
        agenda.getStylesheets().add(getClass().getResource("/agenda-style.css").toExternalForm());
        agenda.setStyle("-fx-background-color: #5894da;");
        disableDragAndDrop(agenda);
        coursService = new CoursService();
        loadCoursIntoAgenda();
        calendarContainer.getChildren().add(agenda);
    }

    private void disableDragAndDrop(Agenda agenda) {
        agenda.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> event.consume());
        agenda.addEventFilter(MouseEvent.MOUSE_DRAGGED, event -> event.consume());
    }

    private void loadCoursIntoAgenda() {
        try {
            if (coursService == null) {
                System.err.println("Erreur : Le service CoursService n'est pas initialisé.");
                return;
            }

            List<Cours> coursList = coursService.Displaycours();

            if (coursList == null || coursList.isEmpty()) {
                System.err.println("Erreur : Aucun cours trouvé.");
                return;
            }

            System.out.println("Nombre de cours récupérés : " + coursList.size());

            for (Cours cours : coursList) {
                Agenda.AppointmentImplLocal appointment = convertCoursToAppointment(cours);
                if (appointment != null) {
                    agenda.appointments().add(appointment);
                } else {
                    System.err.println("Le cours n'a pas pu être converti en rendez-vous.");
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des cours : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Agenda.AppointmentImplLocal convertCoursToAppointment(Cours cours) {
        try {
            if (cours == null || cours.getDateDebut() == null || cours.getHeureDebut() == null || cours.getHeureFin() == null) {
                System.err.println("Erreur : Les informations du cours sont manquantes.");
                return null;
            }

            java.util.Date utilDateDebut = new java.util.Date(cours.getDateDebut().getTime());
            LocalDate startDate = utilDateDebut.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDateTime startDateTime = LocalDateTime.of(startDate, cours.getHeureDebut());
            LocalDateTime endDateTime = LocalDateTime.of(startDate, cours.getHeureFin());

            String salleNom = (cours.getSalle() != null) ? cours.getSalle().getNom() : "Non spécifié";
            String userNom = (cours.getUser() != null) ? cours.getUser().getNom() : "Non spécifié";

            Agenda.AppointmentImplLocal appointment = new Agenda.AppointmentImplLocal()
                    .withStartLocalDateTime(startDateTime)
                    .withEndLocalDateTime(endDateTime)
                    .withSummary(cours.getTitle() + "\n" + salleNom + "\n" + userNom)
                    .withLocation("Gymnase");

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
            e.printStackTrace();
            return null;
        }
    }

    public void refreshCalendar() {
        agenda.appointments().clear();
        loadCoursIntoAgenda();
    }

    private Agenda.AppointmentImplLocal convertGoogleEventToAppointment(Event googleEvent) {
        try {
            if (googleEvent == null || googleEvent.getStart() == null || googleEvent.getEnd() == null) {
                System.err.println("Erreur : Les informations de l'événement Google Calendar sont manquantes.");
                return null;
            }

            // Handle start date/time
            LocalDateTime startDateTime;
            if (googleEvent.getStart().getDateTime() != null) {
                startDateTime = LocalDateTime.ofInstant(
                        Instant.ofEpochMilli(googleEvent.getStart().getDateTime().getValue()),
                        ZoneId.systemDefault());
            } else if (googleEvent.getStart().getDate() != null) {
                startDateTime = LocalDateTime.ofInstant(
                        Instant.ofEpochMilli(googleEvent.getStart().getDate().getValue()),
                        ZoneId.systemDefault());
            } else {
                System.err.println("Erreur : Aucune date de début valide pour l'événement Google Calendar.");
                return null;
            }

            // Handle end date/time
            LocalDateTime endDateTime;
            if (googleEvent.getEnd().getDateTime() != null) {
                endDateTime = LocalDateTime.ofInstant(
                        Instant.ofEpochMilli(googleEvent.getEnd().getDateTime().getValue()),
                        ZoneId.systemDefault());
            } else if (googleEvent.getEnd().getDate() != null) {
                endDateTime = LocalDateTime.ofInstant(
                        Instant.ofEpochMilli(googleEvent.getEnd().getDate().getValue()),
                        ZoneId.systemDefault());
            } else {
                System.err.println("Erreur : Aucune date de fin valide pour l'événement Google Calendar.");
                return null;
            }
            Agenda.AppointmentGroupImpl googleEventGroup = new Agenda.AppointmentGroupImpl().withStyleClass("google-event");



            // Créer un rendez-vous pour l'agenda
            Agenda.AppointmentImplLocal appointment = new Agenda.AppointmentImplLocal()
                    .withStartLocalDateTime(startDateTime)
                    .withEndLocalDateTime(endDateTime)
                    .withSummary(googleEvent.getSummary())
                    .withDescription(googleEvent.getDescription())
                    .withLocation(googleEvent.getLocation())
                    .withAppointmentGroup(new Agenda.AppointmentGroupImpl().withStyleClass("google-event"));

            return appointment;
        } catch (Exception e) {
            System.err.println("Erreur lors de la conversion de l'événement Google Calendar : " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @FXML
    public void OpenWithGoogleCalendar(ActionEvent event) {
        try {
            // Récupérer les événements Google Calendar
            List<Event> googleEvents = GoogleCalendarService.getGoogleCalendarEvents();

            // Convertir et ajouter les événements à l'agenda
            for (Event googleEvent : googleEvents) {
                Agenda.AppointmentImplLocal appointment = convertGoogleEventToAppointment(googleEvent);
                if (appointment != null) {
                    agenda.appointments().add(appointment);
                }
            }

            System.out.println("Événements Google Calendar ajoutés à l'agenda.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}