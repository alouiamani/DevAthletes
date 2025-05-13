package org.gymify.services;

import org.gymify.entities.Reponse;
import org.gymify.utils.gymifyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceReponse implements Iservices<Reponse> {
    private final Connection con;

    public ServiceReponse() {
        con = gymifyDataBase.getInstance().getConnection();
    }

    public int ajouter(Reponse reponse) {
        String sql = "INSERT INTO reponse (reclamation_id, admin_id, message, dateReponse) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, reponse.getReclamation_id());
            pstmt.setInt(2, reponse.getAdmin_id());
            pstmt.setString(3, reponse.getMessage());
            pstmt.setTimestamp(4, reponse.getDateReponse() != null ? reponse.getDateReponse() : new Timestamp(System.currentTimeMillis()));
            pstmt.executeUpdate();

            mettreAJourStatutReclamation(reponse.getReclamation_id(), "Traitée");
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de la réponse : " + e.getMessage());
        }
        return 0;
    }

    public void modifier(Reponse reponse) throws SQLException {
        String sql = "UPDATE reponse SET reclamation_id=?, admin_id=?, message=?, dateReponse=? WHERE id=?";
        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, reponse.getReclamation_id());
            pstmt.setInt(2, reponse.getAdmin_id());
            pstmt.setString(3, reponse.getMessage());
            pstmt.setTimestamp(4, reponse.getDateReponse());
            pstmt.setInt(5, reponse.getId());

            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Réponse modifiée avec succès !");
            } else {
                System.err.println("Erreur : Réponse introuvable !");
            }
        }
    }

    public void supprimer(int id) {
        String sql = "DELETE FROM reponse WHERE id = ?";
        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de la réponse : " + e.getMessage());
        }
    }

    public List<Reponse> afficher() {
        List<Reponse> list = new ArrayList<>();
        String sql = "SELECT * FROM reponse";
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Reponse(
                        rs.getInt("id"),
                        rs.getInt("reclamation_id"),
                        rs.getInt("admin_id"),
                        rs.getString("message"),
                        rs.getTimestamp("dateReponse")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'affichage des réponses : " + e.getMessage());
        }
        return list;
    }

    public Reponse recupererParReclamation(int reclamation_id) {
        String sql = "SELECT * FROM reponse WHERE reclamation_id = ?";
        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, reclamation_id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Reponse(
                        rs.getInt("id"),
                        rs.getInt("reclamation_id"),
                        rs.getInt("admin_id"),
                        rs.getString("message"),
                        rs.getTimestamp("dateReponse")
                );
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de la réponse : " + e.getMessage());
        }
        return null;
    }

    private void mettreAJourStatutReclamation(int reclamation_id, String statut) {
        String sql = "UPDATE reclamation SET statut = ? WHERE id = ?";
        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, statut);
            pstmt.setInt(2, reclamation_id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du statut de la réclamation : " + e.getMessage());
        }
    }
}