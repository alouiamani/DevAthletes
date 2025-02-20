package Gimify.Pi.tests;

import Gimify.Pi.Service.ServiceReclamation;
import Gimify.Pi.Service.ServiceReponse;
import Gimify.Pi.Service.ServiceUser;
import Gimify.Pi.entities.Entraineur;
import Gimify.Pi.entities.Reclamation;
import Gimify.Pi.entities.Reponse;
import Gimify.Pi.entities.User;

import java.sql.SQLException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws SQLException {
        ServiceReponse serviceReponse = new ServiceReponse();
        ServiceUser serviceUser = new ServiceUser();
        ServiceReclamation serviceReclamation = new ServiceReclamation();
        // 🔹 Création d'un utilisateur fictif
        User user = new User(  8,"Dupont", "Ahmed", "password123", "Sportif","jean@gmail.com");

        // 🔹 Création de réclamations
        Reclamation rec1 = new Reclamation(8, "Problème de facturation", "Erreur sur le montant.");

        Reponse rep1 = new Reponse( 13, "Nous avons bien pris .");

//        // 🔹 Affichage des réclamations
        System.out.println("Test des réclamations :");
        System.out.println(rec1);


      // serviceUser.modifier(user);
        //serviceReclamation.ajouter(rec1);
        //serviceReponse.ajouter(rep1);
       // serviceReponse.modifier(rep1);
        serviceReponse.supprimer(14);
        //serviceUser.modifier(user);
        }
    }