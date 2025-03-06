package org.gymify.services;

import org.gymify.entities.Reclamation;
import org.gymify.utils.gymifyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;



public class ServiceReclamation implements Iservices<Reclamation> {
    Connection connection = gymifyDataBase.getInstance().getConnection();

    public ServiceReclamation() {}


    public int ajouter(Reclamation reclamation) throws SQLException {
        if (!utilisateurExiste(reclamation.getId_user())) {
            System.err.println("Erreur : L'utilisateur avec l'ID " + reclamation.getId_user() + " n'existe pas !");
            return 0;
        }

        String req = "INSERT INTO reclamation (id_user, sujet, description, statut) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(req)) {
            pstmt.setInt(1, reclamation.getId_user());
            pstmt.setString(2, reclamation.getSujet());
            pstmt.setString(3, reclamation.getDescription());
            pstmt.setString(4, "En attente"); // Statut par défaut

            pstmt.executeUpdate();
            System.out.println("Réclamation ajoutée avec succès !");
        }
        return 0;
    }

    private boolean utilisateurExiste(int idUser) throws SQLException {
        String req = "SELECT COUNT(*) FROM user WHERE id_User = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(req)) {
            pstmt.setInt(1, idUser);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0; // Vérifie si l'utilisateur existe
            }
        }
    }


    public void modifier(Reclamation reclamation) throws SQLException {
        String req = "UPDATE reclamation SET sujet=?, description=?, statut=? WHERE id_reclamation=?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
            preparedStatement.setString(1, reclamation.getSujet());
            preparedStatement.setString(2, reclamation.getDescription());
            preparedStatement.setString(3, reclamation.getStatut());
            preparedStatement.setInt(4, reclamation.getId_reclamation());

            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Réclamation modifiée avec succès !");
            } else {
                System.err.println("Erreur : Réclamation introuvable !");
            }
        }
    }
    public List<Reclamation> rechercherReclamationsParStatutEtUtilisateur(String statut, int id_user) throws SQLException {
        List<Reclamation> listeReclamations = new ArrayList<>();
        String query = "SELECT * FROM reclamation WHERE statut = ? AND id_user = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, statut);
            pstmt.setInt(2, id_user);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    listeReclamations.add(new Reclamation(
                            rs.getInt("id_reclamation"),
                            rs.getInt("id_user"),
                            rs.getString("sujet"),
                            rs.getString("description"),
                            rs.getString("statut")
                    ));
                }
            }
        }
        return listeReclamations;
    }


    public void supprimer(int id_reclamation) throws SQLException {
        String req = "DELETE FROM reclamation WHERE id_reclamation=?";
        try (PreparedStatement pstmt = connection.prepareStatement(req)) {
            pstmt.setInt(1, id_reclamation);
            int rowsDeleted = pstmt.executeUpdate();

            if (rowsDeleted > 0) {
                System.out.println("Réclamation supprimée avec succès !");
            } else {
                System.err.println("Erreur : Réclamation introuvable !");
            }
        }
    }


    public List<Reclamation> afficher() throws SQLException {
        List<Reclamation> reclamations = new ArrayList<>();
        String req = "SELECT id_reclamation, id_user, sujet, description, statut FROM reclamation";

        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(req)) {

            while (rs.next()) {
                reclamations.add(new Reclamation(
                        rs.getInt("id_reclamation"),
                        rs.getInt("id_user"),
                        rs.getString("sujet"),
                        rs.getString("description"),
                        rs.getString("statut")
                ));
            }
        }
        return reclamations;
    }

    public void updateStatut(int idReclamation, String nouveauStatut) throws SQLException {
        String checkQuery = "SELECT COUNT(*) FROM reclamation WHERE id_reclamation = ?";
        try (PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {
            checkStmt.setInt(1, idReclamation);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) == 0) {
                System.err.println("Erreur : Réclamation introuvable !");
                return;
            }
        }

        String req = "UPDATE reclamation SET statut = ? WHERE id_reclamation = ?";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setString(1, nouveauStatut);
            ps.setInt(2, idReclamation);
            ps.executeUpdate();
            System.out.println("Statut de la réclamation mis à jour avec succès !");
        }
    }


    public List<Reclamation> recupererParSportif(int idSportif) throws SQLException {
        List<Reclamation> reclamations = new ArrayList<>();
        String req = "SELECT id_reclamation, id_user, sujet, description, statut FROM reclamation WHERE id_user = ?";

        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setInt(1, idSportif);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    reclamations.add(new Reclamation(
                            rs.getInt("id_reclamation"),
                            rs.getInt("id_user"),
                            rs.getString("sujet"),
                            rs.getString("description"),
                            rs.getString("statut")
                    ));
                }
            }
        }
        return reclamations;
    }
}
