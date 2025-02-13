package org.gimify.test;

import org.gimify.entities.Activité;
import org.gimify.entities.Cours;
import org.gimify.entities.Planning;
import org.gimify.services.ActivityService;
import org.gimify.services.CoursService;
import org.gimify.services.PlanningService;
import org.gimify.utiles.gymifyDataBase;

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















    }
}