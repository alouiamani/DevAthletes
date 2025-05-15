package org.gymify.services;

import org.gymify.entities.Commande;
import org.gymify.utils.gymifyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CommandeService {
    public void addCommande(Commande commande) throws SQLException {
        String sql = "INSERT INTO commande (total_c, date_c, statut_c, user_id, phone_number) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = gymifyDataBase.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDouble(1, commande.getTotal_c());
            ps.setTimestamp(2, commande.getDate_c() != null ? new Timestamp(commande.getDate_c().getTime()) : null);
            ps.setString(3, commande.getStatut_c());
            if (commande.getUser_id() != null) {
                ps.setInt(4, commande.getUser_id());
            } else {
                ps.setNull(4, Types.INTEGER);
            }
            ps.setString(5, commande.getPhone_number());
            ps.executeUpdate();
        }
    }

    public void updateCommande(Commande commande) throws SQLException {
        String sql = "UPDATE commande SET total_c=?, date_c=?, statut_c=?, user_id=?, phone_number=? WHERE id_c=?";
        try (Connection con = gymifyDataBase.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDouble(1, commande.getTotal_c());
            ps.setTimestamp(2, commande.getDate_c() != null ? new Timestamp(commande.getDate_c().getTime()) : null);
            ps.setString(3, commande.getStatut_c());
            if (commande.getUser_id() != null) {
                ps.setInt(4, commande.getUser_id());
            } else {
                ps.setNull(4, Types.INTEGER);
            }
            ps.setString(5, commande.getPhone_number());
            ps.setInt(6, commande.getId_c());
            ps.executeUpdate();
        }
    }

    public void deleteCommande(int id_c) throws SQLException {
        String sql = "DELETE FROM commande WHERE id_c=?";
        try (Connection con = gymifyDataBase.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id_c);
            ps.executeUpdate();
        }
    }

    public List<Commande> getAllCommandes() throws SQLException {
        List<Commande> commandes = new ArrayList<>();
        String sql = "SELECT * FROM commande";
        try (Connection con = gymifyDataBase.getInstance().getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Commande commande = new Commande(
                    rs.getInt("id_c"),
                    rs.getDouble("total_c"),
                    rs.getTimestamp("date_c"),
                    rs.getString("statut_c"),
                    rs.getObject("user_id") != null ? rs.getInt("user_id") : null,
                    rs.getString("phone_number")
                );
                commandes.add(commande);
            }
        }
        return commandes;
    }

    public Commande getCommandeById(int id_c) throws SQLException {
        String sql = "SELECT * FROM commande WHERE id_c=?";
        try (Connection con = gymifyDataBase.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id_c);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Commande(
                        rs.getInt("id_c"),
                        rs.getDouble("total_c"),
                        rs.getTimestamp("date_c"),
                        rs.getString("statut_c"),
                        rs.getObject("user_id") != null ? rs.getInt("user_id") : null,
                        rs.getString("phone_number")
                    );
                }
            }
        }
        return null;
    }
} 