package org.gymify.services;

import org.gymify.entities.Planning;
import org.gymify.utiles.gymifyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlanningService implements IService<Planning> {
    private Connection con;
    public PlanningService() {
        con = gymifyDataBase.getInstance().getConnection();
    }

    @Override
    public void Add(Planning planning) {
        try{
            PreparedStatement req=con.prepareStatement ("INSERT INTO `planning`(`dateDebut`, `description`, `title`, `dateFin`) VALUES (?,?,?,?)");
            req.setDate(1,new Date(planning.getdateDebut().getTime()));
            req.setString(2,planning.getDescription());
            req.setString(3,planning.getTitle());
            req.setDate(4,new Date(planning.getdateFin().getTime()));
            req.executeUpdate();
            System.out.println("Planning ajouté avec succès.");

        }catch (SQLException e){
            System.out.println("Erreur lors de l'ajout du planning : " + e.getMessage());

        }


    }

    @Override
    public void Update(Planning planning) {
        try{
            PreparedStatement req=con.prepareStatement ("UPDATE `planning` SET description=?,title=? WHERE id=?");
            req.setString(1,planning.getDescription());
            req.setString(2,planning.getTitle());
            req.setInt(3,planning.getId());
            req.executeUpdate();
            System.out.println("Planning mis à jour avec succès.");

        }catch (SQLException e){
            System.out.println("Erreur lors de la mise à jour du planning : " + e.getMessage());

        }

    }

    @Override
    public void Delete(Planning planning) {
        try{
            PreparedStatement req= con.prepareStatement("DELETE FROM planning WHERE id=?");
            req.setInt(1,planning.getId());
            req.executeUpdate();
            System.out.println("Planning supprimé avec succès.");

        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression du planning : " + e.getMessage());

        }

    }

    @Override
    public List<Planning> Display() {
        List<Planning> plannings = new ArrayList<>();
        try {
            String sql = "SELECT * FROM planning";
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()) {
                Planning planning = new Planning();
                planning.setId(rs.getInt("id"));
                planning.setdateDebut(rs.getDate("dateDebut"));
                planning.setDescription(rs.getString("description"));
                planning.setTitle(rs.getString("title"));
                planning.setDateFin(rs.getDate("dateFin"));
                plannings.add(planning);
            }


        } catch (SQLException e) {
            System.out.println("Erreur lors de l'affichage des plannings : " + e.getMessage());

        }
        return plannings;
    }
}
