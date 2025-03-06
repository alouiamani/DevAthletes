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
        String sql = "INSERT INTO reponse (id_rec, message, dateReponse) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, reponse.getId_rec());
            pstmt.setString(2, reponse.getMessage());
            pstmt.setTimestamp(3, new Timestamp(System.currentTimeMillis())); // Ajout automatique de la date
            pstmt.executeUpdate();

            mettreAJourStatutReclamation(reponse.getId_rec(), "Traitée");
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de la réponse : " + e.getMessage());
        }
        return 0;
    }


    public void modifier(Reponse reponse) throws SQLException {

    }

    public void supprimer(int id_Reponse) {
        String sql = "DELETE FROM reponse WHERE id_Reponse = ?";
        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, id_Reponse);
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
                        rs.getInt("id_Reponse"),
                        rs.getInt("id_rec"),
                        rs.getString("message"),
                        rs.getTimestamp("dateReponse")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'affichage des réponses : " + e.getMessage());
        }
        return list;
    }

    public Reponse recupererParReclamation(int id_rec) {
        String sql = "SELECT * FROM reponse WHERE id_rec = ?";
        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, id_rec);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Reponse(
                        rs.getInt("id_Reponse"),
                        rs.getInt("id_rec"),
                        rs.getString("message"),
                        rs.getTimestamp("dateReponse")
                );
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de la réponse : " + e.getMessage());
        }
        return null;
    }

    private void mettreAJourStatutReclamation(int id_rec, String statut) {
        String sql = "UPDATE reclamation SET statut = ? WHERE id_reclamation = ?";
        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, statut);
            pstmt.setInt(2, id_rec);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du statut de la réclamation : " + e.getMessage());
        }
    }
}
