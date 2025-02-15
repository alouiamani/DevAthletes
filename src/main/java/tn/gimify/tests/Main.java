package tn.gimify.tests;

import tn.gimify.entities.Abonnement;
import tn.gimify.entities.Salle;
import tn.gimify.services.AbonnementService;
import tn.gimify.services.SalleService;
import tn.gimify.utils.MyDatabase;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static tn.gimify.entities.type_Abonnement.*;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {

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



}}