package org.gymify.services;

import org.gymify.entities.Produit;
import org.gymify.utils.gymifyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProduitService {
    public void addProduit(Produit produit) throws SQLException {
        String sql = "INSERT INTO produit (nom_p, prix_p, stock_p, categorie_p, image_path, updated_at) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = gymifyDataBase.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, produit.getNom_p());
            ps.setDouble(2, produit.getPrix_p());
            ps.setInt(3, produit.getStock_p());
            ps.setString(4, produit.getCategorie_p());
            ps.setString(5, produit.getImage_path());
            ps.setTimestamp(6, produit.getUpdated_at() != null ? new Timestamp(produit.getUpdated_at().getTime()) : null);
            ps.executeUpdate();
        }
    }

    public void updateProduit(Produit produit) throws SQLException {
        String sql = "UPDATE produit SET nom_p=?, prix_p=?, stock_p=?, categorie_p=?, image_path=?, updated_at=? WHERE id_p=?";
        try (Connection con = gymifyDataBase.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, produit.getNom_p());
            ps.setDouble(2, produit.getPrix_p());
            ps.setInt(3, produit.getStock_p());
            ps.setString(4, produit.getCategorie_p());
            ps.setString(5, produit.getImage_path());
            ps.setTimestamp(6, produit.getUpdated_at() != null ? new Timestamp(produit.getUpdated_at().getTime()) : null);
            ps.setInt(7, produit.getId_p());
            ps.executeUpdate();
        }
    }

    public void deleteProduit(int id_p) throws SQLException {
        String sql = "DELETE FROM produit WHERE id_p=?";
        try (Connection con = gymifyDataBase.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id_p);
            ps.executeUpdate();
        }
    }

    public List<Produit> getAllProduits() throws SQLException {
        List<Produit> produits = new ArrayList<>();
        String sql = "SELECT * FROM produit";
        try (Connection con = gymifyDataBase.getInstance().getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Produit produit = new Produit(
                    rs.getInt("id_p"),
                    rs.getString("nom_p"),
                    rs.getDouble("prix_p"),
                    rs.getInt("stock_p"),
                    rs.getString("categorie_p"),
                    rs.getString("image_path"),
                    rs.getTimestamp("updated_at")
                );
                produits.add(produit);
            }
        }
        return produits;
    }

    public Produit getProduitById(int id_p) throws SQLException {
        String sql = "SELECT * FROM produit WHERE id_p=?";
        try (Connection con = gymifyDataBase.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id_p);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Produit(
                        rs.getInt("id_p"),
                        rs.getString("nom_p"),
                        rs.getDouble("prix_p"),
                        rs.getInt("stock_p"),
                        rs.getString("categorie_p"),
                        rs.getString("image_path"),
                        rs.getTimestamp("updated_at")
                    );
                }
            }
        }
        return null;
    }
} 