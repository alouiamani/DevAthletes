package services;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import entities.Produit;
import utils.MyDatabase;

public class ServiceProduit implements IService<Produit> {
    private final Connection connection;

    public ServiceProduit() {
        connection = MyDatabase.getInstance().getConnection();
    }

    @Override
    public int ajouter(Produit produit) throws SQLException {
        String req = "INSERT INTO produit (nom_p, prix_p, stock_p, categorie_p, image_path) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(req, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, produit.getNom_p());
            ps.setFloat(2, produit.getPrix_p());
            ps.setInt(3, produit.getStock_p());
            ps.setString(4, produit.getCategorie_p());
            ps.setString(5, produit.getImage_path());
            ps.executeUpdate();

            // Get the generated ID
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return -1;
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String req = "DELETE FROM produit WHERE id_p = ?";

        try (PreparedStatement statement = connection.prepareStatement(req)) {
            statement.setInt(1, id);

            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted == 0) {
                System.out.println("Produit non trouvé, suppression échouée.");
            } else {
                System.out.println("Produit supprimé avec succès.");
            }
        }
    }

    @Override
    public void modifier(Produit produit) throws SQLException {
        String req = "UPDATE produit SET nom_p=?, prix_p=?, stock_p=?, categorie_p=?, image_path=? WHERE id_p=?";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setString(1, produit.getNom_p());
            ps.setFloat(2, produit.getPrix_p());
            ps.setInt(3, produit.getStock_p());
            ps.setString(4, produit.getCategorie_p());
            ps.setString(5, produit.getImage_path());
            ps.setInt(6, produit.getId_p());
            ps.executeUpdate();
        }
    }

    @Override
    public List<Produit> afficher() throws SQLException {
        List<Produit> produits = new ArrayList<>();
        String req = "SELECT * FROM produit";
        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(req)) {
            while (rs.next()) {
                Produit p = new Produit(
                    rs.getInt("id_p"),
                    rs.getString("nom_p"),
                    rs.getFloat("prix_p"),
                    rs.getInt("stock_p"),
                    rs.getString("categorie_p"),
                    rs.getString("image_path")
                );
                produits.add(p);
            }
        }
        return produits;
    }

    public double getPrix(int produitId) throws SQLException {
        String query = "SELECT prix_p FROM produit WHERE id_p = ?";
        try (PreparedStatement pstmt = MyDatabase.getInstance().getConnection().prepareStatement(query)) {
            pstmt.setInt(1, produitId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("prix_p");
            } else {
                throw new SQLException("Produit avec ID " + produitId + " non trouvé.");
            }
        }
    }
}
