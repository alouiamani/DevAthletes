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

    @Override
    public void ajouter(Reponse reponse) {
        String sql = "INSERT INTO reponse (id_rec, id_user_admin, message, dateReponse) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, reponse.getId_rec()); // ID de la réclamation
            pstmt.setInt(2, reponse.getId_user_admin()); // ID de l'admin
            pstmt.setString(3, reponse.getMessage()); // Message de la réponse
            pstmt.setTimestamp(4, new Timestamp(System.currentTimeMillis())); // Date actuelle
            pstmt.executeUpdate();

            mettreAJourStatutReclamation(reponse.getId_rec(), "Traitée");
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de la réponse : " + e.getMessage());
        }
    }

    @Override
    public void modifier(Reponse reponse) throws SQLException {
        // Cette méthode peut être implémentée pour modifier une réponse (éventuellement)
    }

    @Override
    public void supprimer(int id_Reponse) {
        String sql = "DELETE FROM reponse WHERE id_Reponse = ?";
        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, id_Reponse);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de la réponse : " + e.getMessage());
        }
    }

    @Override
    public List<Reponse> afficher() {
        List<Reponse> list = new ArrayList<>();
        String sql = "SELECT * FROM reponse";
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Reponse(
                        rs.getInt("id_Reponse"),
                        rs.getInt("id_rec"),
                        rs.getInt("id_user_admin"),  // Récupération de l'id_admin
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
                        rs.getInt("id_user_admin"), // Récupération de l'admin
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
        // Vérifier que le statut est dans la taille autorisée
        if (statut.length() > 50) { // 50 est un exemple, ajustez en fonction de la longueur de votre colonne
            System.err.println("Le statut est trop long.");
            return;
        }

        String sql = "UPDATE reclamation SET statut = ? WHERE id_reclamation = ?";
        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, statut); // Insérer le statut dans la colonne
            pstmt.setInt(2, id_rec);     // Spécifier l'ID de la réclamation
            pstmt.executeUpdate();       // Exécuter la mise à jour
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du statut de la réclamation : " + e.getMessage());
        }
    }

}
