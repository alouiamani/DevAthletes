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
        String query = "INSERT INTO produit (nom_p, prix_p, stock_p, categorie_p) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = MyDatabase.getInstance().getConnection().prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, produit.getNom_p());
            pstmt.setDouble(2, produit.getPrix_p());
            pstmt.setInt(3, produit.getStock_p());
            pstmt.setString(4, produit.getCategorie_p());
            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1); // Retourne l'ID généré
            } else {
                throw new SQLException("Erreur lors de la génération de l'ID du produit.");
            }
        }
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

    public void modifier(Produit produit) throws SQLException {
        String req = "UPDATE produit SET nom_p = ?, prix_p = ?, stock_p = ?, categorie_p = ? WHERE id_p = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
            preparedStatement.setString(1, produit.getNom_p());
            preparedStatement.setFloat(2, produit.getPrix_p());
            preparedStatement.setInt(3, produit.getStock_p());
            preparedStatement.setString(4, produit.getCategorie_p());
            preparedStatement.setInt(5, produit.getId_p());

            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Produit modifié avec succès !");
            } else {
                System.out.println("Aucun produit trouvé avec cet ID.");
            }
        }
    }

    @Override
    public List<Produit> afficher() throws SQLException {
        List<Produit> produits = new ArrayList<>();
        String req = "SELECT * FROM produit";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(req)) {

            while (resultSet.next()) {
                Produit produit = new Produit(
                        resultSet.getInt("id_p"),
                        resultSet.getString("nom_p"),
                        resultSet.getFloat("prix_p"),
                        resultSet.getInt("stock_p"),
                        resultSet.getString("categorie_p")
                );
                produits.add(produit);
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
