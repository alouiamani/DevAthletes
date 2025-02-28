package services;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import entities.Commande;
import utils.MyDatabase;
import utils.AuthToken;
import entities.User;

public class ServiceCommande implements IService<Commande> {
    private final Connection connection;

    public ServiceCommande() {
        connection = MyDatabase.getInstance().getConnection();
    }

    @Override
    public int ajouter(Commande commande) throws SQLException {
        String req = "INSERT INTO commande (total_c, statut_c, dateC, user_id) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(req, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setFloat(1, commande.getTotal_c());
            pstmt.setString(2, commande.getStatut_c());
            pstmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            
            User currentUser = AuthToken.getCurrentUser();
            if (currentUser != null) {
                pstmt.setInt(4, currentUser.getId_User());
            } else {
                throw new SQLException("No user logged in");
            }
            
            pstmt.executeUpdate();
            
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return -1;
    }

    @Override
    public List<Commande> afficher() throws SQLException {
        List<Commande> commandes = new ArrayList<>();
        String req = "SELECT c.*, u.id_User, u.nom, u.prenom FROM commande c " +
                     "LEFT JOIN user u ON c.user_id = u.id_User";
        
        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(req)) {
            
            while (rs.next()) {
                Commande commande = new Commande(
                    rs.getInt("id_c"),
                    rs.getFloat("total_c"),
                    rs.getString("statut_c"),
                    rs.getTimestamp("dateC")
                );
                commande.setUser_id(rs.getInt("user_id"));
                commandes.add(commande);
            }
        }
        return commandes;
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String req = "DELETE FROM commande WHERE id_c=?";
        
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public void modifier(Commande commande) throws SQLException {
        String req = "UPDATE commande SET total_c=?, statut_c=?, dateC=NOW() WHERE id_c=?";
        
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setFloat(1, commande.getTotal_c());
            ps.setString(2, commande.getStatut_c());
            ps.setInt(3, commande.getId_c());
            
            ps.executeUpdate();
        }
    }

    public void ajouterCommandeProduit(int commandeId, int produitId, int quantite) throws SQLException {
        String query = "INSERT INTO commande_produit (commande_id, produit_id, quantite) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = MyDatabase.getInstance().getConnection().prepareStatement(query)) {
            pstmt.setInt(1, commandeId);
            pstmt.setInt(2, produitId);
            pstmt.setInt(3, quantite);
            pstmt.executeUpdate();
            System.out.println("Liaison commande-produit ajoutée avec succès !");
        }
    }

    public void mettreAJourTotal(int commandeId, double total) throws SQLException {
        String query = "UPDATE commande SET total_c = ? WHERE id_c = ?";
        try (PreparedStatement pstmt = MyDatabase.getInstance().getConnection().prepareStatement(query)) {
            pstmt.setDouble(1, total);
            pstmt.setInt(2, commandeId);
            pstmt.executeUpdate();
            System.out.println("Total de la commande mis à jour avec succès !");
        }
    }

    public void modifierStatut(int commandeId, String nouveauStatut) throws SQLException {
        String req = "UPDATE commande SET statut_c=? WHERE id_c=?";
        
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setString(1, nouveauStatut);
            ps.setInt(2, commandeId);
            ps.executeUpdate();
        }
    }
}
