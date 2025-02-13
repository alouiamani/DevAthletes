package tn.esprit;

import tn.esprit.entities.Equipe;
import tn.esprit.entities.EquipeEvent;
import tn.esprit.entities.Event;
import tn.esprit.services.EquipeEventService;
import tn.esprit.services.EquipeService;
import tn.esprit.services.EventService;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        // Services pour gérer les événements et les équipes
        EventService eventService= new EventService();
        EquipeService equipeService = new EquipeService();
        EquipeEventService equipeEventService = new EquipeEventService();

        // Création de nouveaux événements
        Event e1 = new Event(3, "Tournoi de Basketthhgh", "Gymnase Central", LocalDate.of(2025, 3, 20),
                LocalTime.of(14, 0), LocalTime.of(16, 0), "Compétition", 100,
                "Un grand tournoi avec plusieurs équipes", "basket_event.jpg", "Trophée de la victoire");

        Event ev2 = new Event("Yoga & Méditation ssssss ", "Parc Municipal", LocalDate.of(2025, 4, 10),
                LocalTime.of(9, 0), LocalTime.of(10, 50), "Séance bien-être", 30,
                "Séance de yoga en plein air avec un coach expert", "yoga_event.jpg", null);

        Event e2 = new Event(100, "Yoga & Méditation sirine ", "Parc Municipal", LocalDate.of(2025, 4, 10),
                LocalTime.of(9, 0), LocalTime.of(10, 50), "Séance bien-être", 30,
                "Séance de yoga en plein air avec un coach expert", "yoga_event.jpg", "Certificat de participation");

        Event e3 = new Event(11, "Sortie en plein air ", "jardin public", LocalDate.of(2025, 4, 10),
                LocalTime.of(9, 0), LocalTime.of(10, 30), "Séance bien-être", 50,
                "Séance de sortie en plein air avec un coach expert", "yoga_event.jpg", "Voucher for a free session");

        // 2️⃣ Création de nouvelles équipes
        Equipe eq1 = new Equipe(3, "Équipe Alpha sirineselmi");
        Equipe eq2 = new Equipe(4, "Équipe Beta");


        try {
            // ✅ Gestion des Événements

            // 1️⃣ Ajouter des événements
            System.out.println("\n🟢 Ajout des événements...");
            eventService.ajouter(e1);
            //eventService.ajouter(e3);

            // 2️⃣ Afficher la liste des événements
            System.out.println("\n📌 Liste des événements après ajout :");
            List<Event> events = eventService.afficher();
            for (Event e : events) {
                System.out.println(e);
            }

            // 3️⃣ Modifier un événement (on modifie e2)
            // Augmentation de la capacité
            eventService.modifier(e2);


            // 4️⃣ Rechercher un événement par ID
            System.out.println("\n🔍 Recherche de l'événement avec ID 100...");
            Event eventFound = eventService.chercherParId(100);
            System.out.println("✅ Événement trouvé : " + eventFound);

            // 5️⃣ Supprimer un événement
            System.out.println("\n🛑 Suppression de l'événement Basket...");
            eventService.supprimer(75);

            // 6️⃣ Afficher la liste des événements après suppression
            System.out.println("\n📌 Liste des événements après suppression :");
            List<Event> eventsAfterDelete = eventService.afficher();
            for (Event e : eventsAfterDelete) {
                System.out.println(e);
            }

            // ✅ Gestion des Équipes
            System.out.println("\n🟢 Ajout des équipes...");
            equipeService.ajouter(eq1);
            //equipeService.ajouter(eq2);

            System.out.println("\n📌 Liste des équipes après ajout :");
            List<Equipe> equipes = equipeService.afficher();
            for (Equipe eq : equipes) {
                System.out.println(eq);
            }

            System.out.println("\n🟡 Modification de l'équipe Beta...");
            //equipeService.modifier(eq1);

            System.out.println("\n🔍 Recherche de l'équipe avec ID 9...");
            Equipe equipeFound = equipeService.chercherParId(9);
            System.out.println("✅ Équipe trouvée : " + equipeFound);

            System.out.println("\n🛑 Suppression de l'équipe Alpha...");
            equipeService.supprimer(10);


            System.out.println("\n📌 Liste des équipes après suppression :");
            List<Equipe> equipesAfterDelete = equipeService.afficher();
            for (Equipe eq : equipesAfterDelete) {
                System.out.println(eq);
            }
            //Gestion association

            // ✅ Ajouter une association entre une équipe et un événement
            System.out.println("\n🔗 Ajout d'associations Équipe-Événement...");
            equipeEventService.ajouter(eq1, e1); // Ajouter l'association de l'Équipe Alpha avec l'événement e1
            //equipeEventService.ajouter(eq2, e2);

            // ✅ Afficher la liste des associations avant modification
            System.out.println("\n📌 Liste des associations avant modification :");
            List<EquipeEvent> equipeEvents = equipeEventService.afficher();
            for (EquipeEvent ee : equipeEvents) {
                System.out.println(ee);
            }

            // ✅ Modifier une association (changer l'événement de l'Équipe Alpha)
            System.out.println("\n🟡 Modification de l'association de l'Équipe Alpha...");
            //equipeEventService.modifier(eq1, e2); // eq1 = Equipe, e1 = Event// Modifier l'association de l'Équipe Alpha pour la lier à e2 au lieu de e1


            // ✅ Afficher la liste des associations après modification
            System.out.println("\n📌 Liste des associations après modification :");
            List<EquipeEvent> equipeEventsAfterModification = equipeEventService.afficher();
            for (EquipeEvent ee : equipeEventsAfterModification) {
                System.out.println(ee);
            }

            // ✅ Supprimer une association entre une équipe et un événement
            System.out.println("\n🛑 Suppression de l'association de l'Équipe Alpha avec l'événement e2...");
            //equipeEventService.supprimer(eq2, e2); // Supprimer l'association de l'Équipe Alpha avec l'événement e2

            // ✅ Afficher la liste des associations après suppression
            System.out.println("\n📌 Liste des associations après suppression :");
            List<EquipeEvent> equipeEventsAfterDeletion = equipeEventService.afficher();
            for (EquipeEvent ee : equipeEventsAfterDeletion) {
                System.out.println(ee);
            }

        } catch (SQLException e) {
            System.out.println("❌ Erreur SQL : " + e.getMessage());
        }
    }
}