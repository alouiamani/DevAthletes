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
    public static void main(String[] args) {

        try {
            ServiceUser serviceUser = new ServiceUser();
            ServiceReclamation serviceReclamation = new ServiceReclamation();
            ServiceReponse serviceReponse = new ServiceReponse();
            Entraineur entraineur = new Entraineur(0, "John", "Doe", "jpassword123", "Entraineur","johndoe@gmail.com", "Fitnesse");

//            serviceUser.ajouter(entraineur);
//            System.out.println("Entraîneur ajouté ✅");

            List<User> users = serviceUser.afficher();
            System.out.println("Liste des utilisateurs :");
            for (User user : users) {
                System.out.println(user);


                // 🔹 Ajouter une réclamation
                Reclamation reclamation = new Reclamation();
                reclamation.setId_user(3);  // ✅ Utilise un ID valide
                reclamation.setSujet("Problème d'accès");
                reclamation.setDescription("Je n'arrive pas à me connecter.");

                serviceReclamation.ajouter(reclamation);


                // 🔹 Afficher les réclamations
                List<Reclamation> reclamations = serviceReclamation.afficher();
                System.out.println("Liste des réclamations :");
                for (Reclamation rec : reclamations) {
                    System.out.println(rec);
                }

                // 🔹 Ajouter une réponse à une réclamation
                Reponse reponse = new Reponse(reclamations.get(0).getId_reclamation(), "Nous avons résolu votre problème.");
                serviceReponse.ajouter(reponse);
                System.out.println("Réponse ajoutée ✅");

                // 🔹 Afficher les réponses
                List<Reponse> reponses = serviceReponse.afficher();
                System.out.println("Liste des réponses :");
                for (Reponse rep : reponses) {
                    System.out.println(rep);
                }

            }
        }catch(Exception e){
                System.err.println("Erreur : " + e.getMessage());
            }
        }
    }