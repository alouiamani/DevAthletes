package org.gymify.services;

import org.gymify.entities.Activité;
import org.gymify.entities.Cours;
import org.gymify.entities.Planning;
import org.gymify.entities.User;
import org.gymify.utils.gymifyDataBase;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CoursService implements IService<Cours> {
    private Connection con;
    public CoursService() {
        con= gymifyDataBase.getInstance().getConnection();
    }

    @Override
    public void Add(Cours cours) {
        try {
            PreparedStatement req = con.prepareStatement("INSERT INTO `cours`(`title`, `description`, `dateDebut`, `dateFin`, `heurDebut`, `heurFin`, `activité_id`, `planning_id`, `entaineur_id`) VALUES (?,?,?,?,?,?,?,?,?)");
            req.setString(1, cours.getTitle());
            req.setString(2, cours.getDescription());
            req.setDate(3, new Date(cours.getDateDebut().getTime()));
            req.setDate(4, new Date(cours.getDateFin().getTime()));
            req.setTime(5, Time.valueOf(cours.getHeureDebut()));
            req.setTime(6, Time.valueOf(cours.getHeureFin()));
            req.setInt(7, cours.getActivité().getId());
            req.setInt(8, cours.getPlanning().getId());
            req.setInt(9,cours.getUser().getId_User());




            req.executeUpdate();
            System.out.println("Cours ajouté avec succès.");

        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout du cours : " + e.getMessage());
        }

    }

    @Override
    public void Update(Cours cours) {
        try {
            PreparedStatement req= con.prepareStatement("UPDATE `cours` SET `title`=?,`description`=?,`dateDebut`=?,`dateFin`=?,`heurDebut`=?,`heurFin`=?,`activité_id`=?,`planning_id`=?,`entaineur_id`=? WHERE id=?");
            req.setString(1, cours.getTitle());
            req.setString(2, cours.getDescription());
            req.setDate(3, new Date(cours.getDateDebut().getTime()));
            req.setDate(4, new Date(cours.getDateFin().getTime()));
            req.setTime(5, Time.valueOf(cours.getHeureDebut()));
            req.setTime(6, Time.valueOf(cours.getHeureFin()));
            if(cours.getActivité() != null && cours.getPlanning() != null){
            req.setInt(7,cours.getActivité().getId());
            req.setInt(8,cours.getPlanning().getId());
            }
            req.setInt(9,cours.getUser().getId_User());
            req.setInt(10,cours.getId());


            req.executeUpdate();
            System.out.println("Cours mis à jour avec succès.");

        } catch (SQLException e) {
            System.out.println("Erreur lors de la mise à jour du cours : " + e.getMessage());

        }

    }

    @Override
    public void Delete(Cours cours) {
        try{
            PreparedStatement req= con.prepareStatement("DELETE FROM `cours` WHERE id=?");
            req.setInt(1,cours.getId());
            req.executeUpdate();
            System.out.println("Cours supprimée avec succès !");
        }catch(SQLException e){
            System.out.println("Erreur lors de la suppression de Cours : " + e.getMessage());

        }

    }

    @Override
    public List<Cours> Display() {
        List<Cours> cours= new ArrayList<>();
        try {
            String req = "SELECT * FROM `cours`";
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery(req);
            while (rs.next()) {
                Cours cour= new Cours();
                cour.setId(rs.getInt("id"));
                cour.setTitle(rs.getString("title"));
                cour.setHeureDebut(rs.getTime("heurDebut").toLocalTime());
                cour.setHeureFin(rs.getTime("heurFin").toLocalTime());
                cour.setDescription(rs.getString("description"));
                cour.setDateDebut(rs.getDate("dateDebut"));
                cour.setDateFin(rs.getDate("dateFin"));
                int activitéId = rs.getInt("activité_id");
                if (activitéId != 0) {
                    Activité activité = new Activité(); // Créez une instance de l'activité
                    activité.setId(activitéId); // Vous pouvez récupérer d'autres informations si nécessaire
                    cour.setActivité(activité);
                }

                // Récupérer le planning associé
                int planningId = rs.getInt("planning_id");
                if (planningId != 0) {
                    Planning planning = new Planning(); // Créez une instance du planning
                    planning.setId(planningId); // Vous pouvez récupérer d'autres informations si nécessaire
                    cour.setPlanning(planning);
                }
                int entaineurId = rs.getInt("entaineur_id");
                if (entaineurId != 0) {
                    User entaineur = new User();
                    entaineur.setId_User(entaineurId);
                    cour.setUser(entaineur);
                }




                cours.add(cour);
            }


        }
        catch (SQLException e) {
            System.out.println("Erreur lors de l'affichage des cours : " + e.getMessage());

        }
        return cours;


    }
    public List<Cours> getCoursByPlanning(Planning planning) {
        List<Cours> coursList = new ArrayList<>();
        try {
            String query = "SELECT * FROM `cours` WHERE `planning_id` = ?";
            PreparedStatement statement = con.prepareStatement(query);
            statement.setInt(1, planning.getId()); // Utiliser l'ID du planning pour filtrer les cours
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                Cours cours = new Cours();
                cours.setId(rs.getInt("id"));
                cours.setTitle(rs.getString("title"));
                cours.setHeureDebut(rs.getTime("heurDebut").toLocalTime());
                cours.setHeureFin(rs.getTime("heurFin").toLocalTime());
                cours.setDescription(rs.getString("description"));
                cours.setDateDebut(rs.getDate("dateDebut"));
                cours.setDateFin(rs.getDate("dateFin"));

                // Récupérer l'activité associée
                int activitéId = rs.getInt("activité_id");
                if (activitéId != 0) {
                    Activité activité = new Activité();
                    activité.setId(activitéId);
                    cours.setActivité(activité);
                }

                // Récupérer le planning associé
                int planningId = rs.getInt("planning_id");
                if (planningId != 0) {
                    Planning planningCours = new Planning();
                    planningCours.setId(planningId);
                    cours.setPlanning(planningCours);
                }

                // Récupérer l'entraîneur associé
                int entraineurId = rs.getInt("entaineur_id");
                if (entraineurId != 0) {
                    User entraineur = new User();
                    entraineur.setId_User(entraineurId);
                    cours.setUser(entraineur);
                }

                coursList.add(cours);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des cours par planning : " + e.getMessage());
        }
        return coursList;
    }
}
