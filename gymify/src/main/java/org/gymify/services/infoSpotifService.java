package org.gymify.services;

import org.gymify.entities.Activité;
import org.gymify.entities.Objectifs;
import org.gymify.entities.User;
import org.gymify.entities.infoSportif;
import org.gymify.utils.gymifyDataBase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class infoSpotifService implements IService<infoSportif>{
    private Connection con;
    public infoSpotifService() {
        con= gymifyDataBase.getInstance().getConnection();
    }
    @Override
    public void Add(infoSportif infoSportif) {
        try{
            PreparedStatement req= con.prepareStatement("INSERT INTO `infosportif`(`poids`, `taille`, `age`, `sexe`, `objectif`, `sportif_id`) VALUES (?,?,?,?,?,?)");
            req.setFloat(1, infoSportif.getPoids());
            req.setFloat(2, infoSportif.getTaille());
            req.setInt(3, infoSportif.getAge());
            req.setString(4, infoSportif.getSexe());
            req.setString(5,infoSportif.getObjectifs().name());
            req.setInt(6,infoSportif.getUser().getId_User());
            req.executeUpdate();
            System.out.println("info sportif added");


        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    @Override
    public void Update(infoSportif infoSportif) {
        String query = "UPDATE `infosportif` SET `poids`=?, `taille`=?, `age`=?, `sexe`=?, `objectif`=? WHERE `id`=?";
        try (PreparedStatement req = con.prepareStatement(query)) {
            req.setFloat(1, infoSportif.getPoids());
            req.setFloat(2, infoSportif.getTaille());
            req.setInt(3, infoSportif.getAge());
            req.setString(4, infoSportif.getSexe());
            req.setString(5, infoSportif.getObjectifs().name()); // Utilisez .name() pour l'énumération
            req.setInt(6, infoSportif.getId()); // Assurez-vous que l'ID est correct
            int rowsUpdated = req.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("Informations sportives mises à jour avec succès !");
            } else {
                System.out.println("Aucune ligne mise à jour. Vérifiez l'ID de l'infoSportif.");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour des informations sportives : " + e.getMessage());
        }
    }

    @Override
    public void Delete(infoSportif infoSportif) {
        try {
            PreparedStatement req= con.prepareStatement("DELETE FROM `infosportif` WHERE id=?");
            req.setInt(1, infoSportif.getId());
            req.executeUpdate();
            System.out.println("info sportif deleted");


        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    @Override
    public List<infoSportif> Display() {
        List<infoSportif> infoSportif = new ArrayList<>();
        try {
            String req="SELECT * FROM `infosportif`";
            PreparedStatement req1= con.prepareStatement(req);
            ResultSet rs=req1.executeQuery();
            while (rs.next()) {
                infoSportif info = new infoSportif();
                info.setId(rs.getInt("id"));
                info.setPoids(rs.getFloat("poids"));
                info.setTaille(rs.getFloat("taille"));
                info.setAge(rs.getInt("age"));
                info.setSexe(rs.getString("sexe"));
                info.setObjectifs(Objectifs.valueOf(rs.getString("objectif")));

            }


        }catch (Exception e) {
            throw new RuntimeException(e);
        }
        return infoSportif;
    }
    public boolean existsByUserId(int userId) {
        String query = "SELECT COUNT(*) FROM `infosportif` WHERE `sportif_id` = ?";
        try (PreparedStatement req = con.prepareStatement(query)) {
            req.setInt(1, userId);
            try (ResultSet rs = req.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0; // Retourne true si un enregistrement existe
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la vérification de l'existence d'un infoSportif", e);
        }
        return false;
    }

        public infoSportif getInfoSportifByUserId(int userId) {
            String query = "SELECT * FROM `infosportif` WHERE `sportif_id` = ?";
            try (PreparedStatement req = con.prepareStatement(query)) {
                req.setInt(1, userId);
                try (ResultSet rs = req.executeQuery()) {
                    if (rs.next()) {
                        infoSportif info = new infoSportif();
                        info.setId(rs.getInt("id"));
                        info.setPoids(rs.getFloat("poids"));
                        info.setTaille(rs.getFloat("taille"));
                        info.setAge(rs.getInt("age"));
                        info.setSexe(rs.getString("sexe"));
                        info.setObjectifs(Objectifs.valueOf(rs.getString("objectif")));
                        return info;
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException("Erreur lors de la récupération des informations sportives", e);
            }
            return null;
        }

}
