package tn.gimify.services;

import tn.gimify.entities.Abonnement;
import tn.gimify.entities.Salle;
import tn.gimify.entities.type_Abonnement;
import tn.gimify.utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AbonnementService implements Iservices<Abonnement>{
    Connection connection;
    public AbonnementService(){
        connection= MyDatabase.getInstance().getConnection();

    }
    @Override
    public void ajouter(Abonnement abonnement,int id_Salle) throws SQLException {
        String req = "INSERT INTO abonnement (date_Début, date_Fin, type_Abonnement,id_Salle) VALUES (?, ?, ?,?)";
        PreparedStatement preparedStatement = connection.prepareStatement(req);
        preparedStatement.setDate(1, (Date) abonnement.getDate_Début());
        preparedStatement.setDate(2, (Date) abonnement.getDate_Fin());
        preparedStatement.setString(3, String.valueOf(abonnement.getType_Abonnement()));
        preparedStatement.setInt(4, id_Salle);

        preparedStatement.executeUpdate();
        System.out.println(" Abonnement ajouté avec succès !");
    }

    @Override
    public void modifier(Abonnement abonnement) throws SQLException {
        String req = "UPDATE abonnement SET date_Début=?, date_Fin=?, type_Abonnement=? WHERE id_Abonnement=?";
        PreparedStatement preparedStatement = connection.prepareStatement(req);
        preparedStatement.setDate(1, (Date) abonnement.getDate_Début());
        preparedStatement.setDate(2, (Date) abonnement.getDate_Fin());
        preparedStatement.setString(3, String.valueOf(abonnement.getType_Abonnement()));
        preparedStatement.setInt(4, abonnement.getId_Abonnement());

        preparedStatement.executeUpdate();

        System.out.println("Abonnement modifié avec succès !");
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String req = "DELETE FROM abonnement WHERE id_Abonnement=?";
        PreparedStatement preparedStatement= connection.prepareStatement(req);

        preparedStatement.setInt(1, id);
        preparedStatement.executeUpdate();
        System.out.println("Abonnement supprimé avec succès !");

    }



    @Override
    public List<Abonnement> afficher() throws SQLException {
        List<Abonnement> abonnements = new ArrayList<>();
        String req = "SELECT * FROM abonnement";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(req);

        while (rs.next()) {
            Abonnement abonnement = new Abonnement();
            abonnement.setId_Abonnement(rs.getInt("id_Abonnement"));
            abonnement.setDate_Début(rs.getDate("date_Début"));
            abonnement.setDate_Fin(rs.getDate("date_Fin"));
            abonnement.setType_Abonnement(type_Abonnement.valueOf(rs.getString("type_Abonnement")));
            abonnements.add(abonnement);
        }

        return abonnements;
    }


}
