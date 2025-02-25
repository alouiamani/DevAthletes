package org.gymify.services;


import org.gymify.entities.Reclamation;
import org.gymify.entities.Reponse;
import org.gymify.services.IGestionUser;
import org.gymify.utils.gymifyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class ServiceReponse implements IGestionUser<Reponse> {
    Connection connection = gymifyDataBase.getInstance().getConnection();
    public ServiceReponse() {}
    @Override
    public void ajouter(Reponse reponse) throws SQLException {
        String req = "INSERT INTO reponse (id_rec, message) VALUES ('" + reponse.getId_rec() + "', '" + reponse.getMessage() + "')";
        Statement statement = connection.createStatement();
        statement.executeUpdate(req);
        System.out.println(" Réponse ajoutée !");


    }

    @Override
    public void modifier(Reponse reponse) throws SQLException {
        String req = "UPDATE reponse SET message=? WHERE id_Reponse=?";
        PreparedStatement preparedStatement = connection.prepareStatement(req);
        preparedStatement.setString(1, reponse.getMessage());
        preparedStatement.setInt(2, reponse.getId_Reponse());
        preparedStatement.executeUpdate();
        System.out.println("Réponse modifiée !");


    }

    @Override
    public void supprimer(int id_Reponse) throws SQLException {
        String req = "DELETE FROM reponse WHERE id_Reponse=" + id_Reponse;
        Statement statement = connection.createStatement();
        statement.executeUpdate(req);
        System.out.println(" Réponse supprimée !");

    }

    @Override
    public List<Reponse> afficher() throws SQLException {
        List<Reponse> reponses = new ArrayList<>();
        String req = "SELECT r.id_Reponse, r.id_rec, r.message, r.date_reponse, "
                + "rec.id_reclamation, rec.sujet, rec.description "
                + "FROM reponse r "
                + "JOIN reclamation rec ON r.id_rec = rec.id_reclamation";

        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(req);
        while ( rs.next()) {
            Reclamation reclamation = new Reclamation(
                    rs.getInt("id_reclamation"),
                    rs.getString("sujet"),
                    rs.getString("description"));

            Reponse reponse = new Reponse();
            reponse.setId_Reponse(rs.getInt("id_Reponse"));
            reponse.setId_rec(rs.getInt("id_rec"));
            reponse.setMessage(rs.getString("message"));
            reponse.setDateReponse(rs.getTimestamp("date_reponse"));
            reponse.setReclamation(reclamation);
            reponses.add(reponse);
        }
        return reponses;
    }
    }

