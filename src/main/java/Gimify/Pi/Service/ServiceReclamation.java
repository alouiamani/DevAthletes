package Gimify.Pi.Service;

import Gimify.Pi.entities.Reclamation;
import Gimify.Pi.utils.MyData;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceReclamation implements IService <Reclamation>{
    Connection connection = MyData.getInstance().getConnection();
    public ServiceReclamation() {}

    @Override
    public void ajouter(Reclamation reclamation) throws SQLException {
        if (!utilisateurExiste(reclamation.getId_user())) {
            System.err.println(" Erreur : L'utilisateur avec l'ID " + reclamation.getId_user() + " n'existe pas !");

        }
        String req = "INSERT INTO reclamation (id_user, sujet, description, statut) VALUES (?, ?, ?, ?)";
        PreparedStatement pstmt = connection.prepareStatement(req);
        pstmt.setInt(1, reclamation.getId_user());
        pstmt.setString(2, reclamation.getSujet());
        pstmt.setString(3, reclamation.getDescription());
        pstmt.setString(4, "En attente");  // Statut par défaut

        pstmt.executeUpdate();
        System.out.println("Réclamation ajoutée avec succès !");

    }

    private boolean utilisateurExiste(int idUser) throws SQLException {
        String req = "SELECT COUNT(*) FROM user WHERE id_User = ?";

        PreparedStatement pstmt = connection.prepareStatement(req);
        pstmt.setInt(1, idUser);
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            return rs.getInt(1) > 0; // Vérifie s'il y a au moins un utilisateur avec cet ID
        }

        return true;
    }

    @Override
    public void modifier(Reclamation reclamation) throws SQLException {
        String req = "UPDATE reclamation SET sujet=?, description=?, statut=? WHERE id_reclamation=?";

        PreparedStatement preparedStatement = this.connection.prepareStatement(req);
        preparedStatement.setString(1, reclamation.getSujet());
        preparedStatement.setString(2, reclamation.getDescription());
        preparedStatement.setString(3, reclamation.getStatut());
        preparedStatement.setInt(4, reclamation.getId_reclamation());
        preparedStatement.executeUpdate();
        System.out.println(" Utilisateur modifié avec succès !");


    }

    @Override
    public void supprimer(int id_reclamation) throws SQLException {
        String req = "DELETE FROM reclamation WHERE id_reclamation=?";
        PreparedStatement pstmt = connection.prepareStatement(req);
        pstmt.setInt(1, id_reclamation);
        pstmt.executeUpdate();
        System.out.println(" Réclamation supprimée avec succès !");

    }

    @Override
    public List<Reclamation> afficher() throws SQLException {
        List<Reclamation> reclamations = new ArrayList<>();
        String req = "SELECT * FROM reclamation";
        Statement statement = this.connection.createStatement();
        ResultSet rs = statement.executeQuery(req);

        while (rs.next()) {
            Reclamation reclamation = new Reclamation();
            reclamation.setId_reclamation(rs.getInt("id_reclamation"));
            reclamation.setId_user(rs.getInt("id_user"));
            reclamation.setSujet(rs.getString("sujet"));
            reclamation.setDescription(rs.getString("description"));
            reclamation.setStatut(rs.getString("statut"));


            reclamations.add(reclamation);
        }


        return reclamations;
    }

}

