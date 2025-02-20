package tn.esprit.tests;

import tn.esprit.entities.Equipe;
import tn.esprit.entities.EquipeEvent;
import tn.esprit.entities.Event;
import tn.esprit.entities.EventType;
import tn.esprit.services.EquipeEventService;
import tn.esprit.services.EquipeService;
import tn.esprit.services.EventService;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class Main {
    public static void main(String[] args) throws SQLException {
        EventService eventService = new EventService();
        EquipeService equipeService = new EquipeService();
        EquipeEventService equipeEventService = new EquipeEventService();

        // Création d'événements avec l'attribut reward non null et type enum EventType
        Event e1 = new Event(119, "Tournoi de Basketsirine", "Gymnase Central", LocalDate.of(2025, 3, 20),
               LocalTime.of(14, 0), LocalTime.of(16, 0), EventType.COMPETITION,
                 "Un grand tournoi avec plusieurs équipes", "téléchargement.jpg", "Trophée de la victoire");

        Event e2 = new Event(80, "Yoga & Méditation sirine", "Parc Municipal", LocalDate.of(2025, 4, 10),
                LocalTime.of(9, 0), LocalTime.of(10, 50), EventType.ENTRAINEMENT,
               "Séance de yoga en plein air avec un coach expert", "yoga_event.jpg", "Certificat de participation");

        Equipe eq1 = new Equipe(27, "Équipe Alpha");
        Equipe eq2 = new Equipe(4, "Équipe Beta");

        try {
             //Ajouter les événements
            System.out.println("\nAjout des événements...");
            eventService.ajouter(e1);
            eventService.ajouter(e2);

             //Afficher les événements après ajout
            System.out.println("\nListe des événements après ajout :");
            List<Event> events = eventService.afficher();
            for (Event e : events) {
                System.out.println(e);
            }

            // Modifier l'événement Basket
            eventService.modifier(e1);
            System.out.println("\nListe des événements après modification de l'événement Basket :");
            events = eventService.afficher();
            for (Event e : events) {
                System.out.println(e);
            }

            // Ajouter les équipes
            System.out.println("\nAjout des équipes...");
            equipeService.ajouter(eq2);
            equipeService.ajouter(eq1);

            // Afficher les équipes après ajout
            System.out.println("\nListe des équipes après ajout :");
            List<Equipe> equipes = equipeService.afficher();
            for (Equipe eq : equipes) {
                System.out.println(eq);
            }

            // Modifier l'équipe Alpha
            eq1.setNom("Équipe Alpha Modifiée");
            equipeService.modifier(eq1);
            System.out.println("\nListe des équipes après modification de l'équipe Alpha :");
            equipes = equipeService.afficher();
            for (Equipe eq : equipes) {
                System.out.println(eq);
            }

            // Ajouter l'association Équipe-Événement
            System.out.println("\nAjout de l'association Équipe-Événement...");
            equipeEventService.ajouter(eq1, e1);

            // Afficher les associations
            List<EquipeEvent> equipeEvents = equipeEventService.afficher();
            System.out.println("\nListe des associations Équipe-Événement :");
            for (EquipeEvent equipeEvent : equipeEvents) {
                System.out.println(equipeEvent);
            }

            // Modifier l'association Équipe-Événement
            equipeEventService.supprimer(eq1, e1);  // Supprimer l'association existante
            equipeEventService.ajouter(eq2, e2);   // Ajouter une nouvelle association
            System.out.println("\nListe des associations après modification :");
            equipeEvents = equipeEventService.afficher();
            for (EquipeEvent equipeEvent : equipeEvents) {
                System.out.println(equipeEvent);
            }

            // Suppression de l'événement Basket
            System.out.println("\nSuppression de l'événement Basket...");
            eventService.supprimer(e1.getId());  // Passer l'ID de l'événement pour la suppression

            // Afficher les événements après suppression
            System.out.println("\nListe des événements après suppression :");
            List<Event> eventsAfterDelete = eventService.afficher();
            for (Event e : eventsAfterDelete) {
                System.out.println(e);
            }

            // Suppression de l'équipe Alpha
            System.out.println("\nSuppression de l'équipe Alpha...");
            equipeService.supprimer(eq1.getId());  // Passer l'ID de l'équipe pour la suppression

            // Afficher les équipes après suppression
            System.out.println("\nListe des équipes après suppression :");
            List<Equipe> equipesAfterDelete = equipeService.afficher();
            for (Equipe eq : equipesAfterDelete) {
                System.out.println(eq);
            }

            // Suppression de l'association entre l'Équipe Alpha et l'événement e2
            System.out.println("\nSuppression de l'association de l'Équipe Alpha avec l'événement e2...");
            equipeEventService.supprimer(eq1, e2);  // Utilisation des objets eq1 et e2 pour la suppression

            // Afficher les associations après suppression
            System.out.println("\nListe des associations après suppression :");
            List<EquipeEvent> equipeEventsAfterDeletion = equipeEventService.afficher();
            for (EquipeEvent ee : equipeEventsAfterDeletion) {
                System.out.println(ee);
            }

        } catch (SQLException e) {
            System.out.println(" Erreur SQL : " + e.getMessage());
        }
    }
}
