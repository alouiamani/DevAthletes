package org.gimify.services;

import org.gimify.entities.Activité;
import org.gimify.utiles.gymifyDataBase;

import java.sql.*;

import java.util.ArrayList;
import java.util.List;

public class ActivityService implements IService<Activité>{
    private Connection con;
    public ActivityService() {
        con= gymifyDataBase.getInstance().getConnection();
    }
    @Override
    public void Add(Activité activité) {
        try {
            PreparedStatement req= con.prepareStatement("INSERT INTO `activité`(`nom`, `type`, `description`, `url`) VALUES (?,?,?,?)");
            req.setString(1,activité.getNom());
            req.setString(2, activité.getType());
            req.setString(3,activité.getDescription());
            req.setString(4, activité.getUrl());
            req.executeUpdate();
            System.out.println("Activité ajoutée avec succès !");

        }catch (SQLException e){
            System.out.println("Erreur lors de l'ajout de l'activité : " + e.getMessage());
        }


    }

    @Override
    public void Update(Activité activité) {
        try {
            PreparedStatement req = con.prepareStatement("UPDATE `activité` SET `nom`=?,`type`=?,`description`=?,`url`=? WHERE id=?");
            req.setString(1, activité.getNom());
            req.setString(2, activité.getType());
            req.setString(3, activité.getDescription());
            req.setString(4, activité.getUrl());
            req.setInt(5,activité.getId());
            req.executeUpdate();
            System.out.println("Activité ajoutée avec succès !");
        } catch (SQLException e) {
            System.out.println("Erreur lors de la mise à jour de l'activité : " + e.getMessage());

        }

    }

    @Override
    public void Delete(Activité activité) {
        try{
            PreparedStatement req= con.prepareStatement("DELETE FROM `activité` WHERE id=?");
            req.setInt(1,activité.getId());
            req.executeUpdate();
            System.out.println("Activité supprimée avec succès !");
        }catch(SQLException e){
            System.out.println("Erreur lors de la suppression de l'activité : " + e.getMessage());

        }

    }

    @Override
    public List<Activité> Display() {
        List<Activité> activités = new ArrayList<>();
        try {
            String req = "SELECT id, nom, type, description, url FROM activité";
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery(req);
            while (rs.next()) {
                System.out.println("Debug: ID=" + rs.getInt("id") + ", Nom=" + rs.getString("nom"));

                Activité activité = new Activité();
                activité.setId(rs.getInt("id"));
                activité.setNom(rs.getString("nom"));
                activité.setType(rs.getString("type"));
                activité.setDescription(rs.getString("description"));
                activité.setUrl(rs.getString("url"));
                activités.add(activité);

            }


        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return activités;
    }
}
