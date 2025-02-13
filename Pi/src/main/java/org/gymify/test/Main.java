package org.gymify.test;

import org.gymify.entities.Activité;
import org.gymify.entities.Cours;
import org.gymify.entities.Planning;
import org.gymify.services.ActivityService;
import org.gymify.services.CoursService;
import org.gymify.services.PlanningService;
import org.gymify.utiles.gymifyDataBase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        gymifyDataBase db = gymifyDataBase.getInstance();
        ActivityService activityService = new ActivityService();
        PlanningService planningService = new PlanningService();
        CoursService coursService = new CoursService();
        Activité activité1 = new Activité("Yoga", "sport collective", "Séance de yoga pour débutants", "https://yoga.com");
        Activité activité2 = new Activité("Box", "personal coaching", "Séance de box avancé pour les profesionnels", "https://lesportif.com.tn/29295-home_default/gant-boxe-boxfist.jpg");
        activityService.Add(activité1);
        //activityService.Add(activité2);
        List<Activité> activities = activityService.Display();
        //System.out.println("Liste des activités : " + activities);
        Activité activity = activities.get(0);
        //activity.setNom("Yoga de rire");
        //activityService.Update(activity);
        //activityService.Delete(activity);
        Planning planning1 = new Planning(new Date(),"Planning de semaine","Semaine1",new Date());
        Planning planning2= new Planning(new Date(),"Planning de semaine","Semaine2",new Date());
        //planningService.Add(planning1);
        //planningService.Add(planning2);
        List<Planning> plannings = planningService.Display();
        System.out.println("Liste des plannings : " + plannings);
        Planning planning = plannings.get( 0);
        /*SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try{
        planning.setDateFin(sdf.parse("2025-03-10"));
        planningService.Update(planning);
        } catch (ParseException e) {
            System.out.println("Erreur : " + e.getMessage());
        }*/
        //planningService.Delete(planning);
        Cours cours = new Cours("Cours de Yoga",new Date(),new Date() ,"Apprentissage du Yoga", activity,planning);
        //coursService.Add(cours);
        List<Cours> coursList = coursService.Display();
        System.out.println("Liste des cours : " + coursList);
        //Cours cour = coursList.get(0);
        //cour.setTitle("Yoga ");
        //coursService.Update(cour);
        //coursService.Delete(cour);
        SalleService salleService = new SalleService();
        Salle s1 = new Salle("Gym Proooooo" , "10 Ariana" , "Salle bien équipée" , "+21652441882" , "gym1@example.com");
        Salle s2 = new Salle("Gym poommm", "20 Sidi-Hassin", "Salle équipée", "+21655053765", "gym@example.com");
        Salle s3 = new Salle("Gym poromax", "20 Sidi-Hassin", "Salle équipée", "+21655053765", "gym@example.com");
        Salle s4=new Salle (4,"Gym poromax", "20 Sidi-Hassine", "Salle équipée", "+21655053765", "gym@example.com");
        AbonnementService abonnementService = new AbonnementService();
        Abonnement ab1 = new Abonnement(Date.valueOf("2025-02-10"), Date.valueOf("2025-05-10"),
                trimestre);
        Abonnement ab2 = new Abonnement(Date.valueOf("2025-03-11"), Date.valueOf("2025-04-11"),
                mois);
        Abonnement ab3 = new Abonnement(Date.valueOf("2025-03-10"), Date.valueOf("2026-04-10"),
                année);
        Abonnement ab5 = new Abonnement(6,Date.valueOf("2025-03-11"), Date.valueOf("2026-04-11"),
                année);
        try {
//classe Salle
            //salleService.ajouter(s1);
            //salleService.ajouter(s3);
            //s1.setNom("Gymro");
            //salleService.modifier(s1);
            //System.out.println(salleService.afficher(s2));
            //salleService.supprimer(2);



            //abonnementService.ajouter(ab2,4);
            //abonnementService.modifier(ab5);
            //abonnementService.supprimer(3);
            //System.out.println(abonnementService.afficher());




        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }



    }














    }
}