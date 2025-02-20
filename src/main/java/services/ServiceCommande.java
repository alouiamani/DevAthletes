package services;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import entities.Commande;
import utils.MyDatabase;

public class ServiceCommande implements IService<Commande> {
    private final Connection connection;

    public ServiceCommande() {
        connection = MyDatabase.getInstance().getConnection();
    }


    public int ajouter(Commande commande) throws SQLException {
        String query = "INSERT INTO commande (total_c, statut_c) VALUES (?, ?)";
        try (PreparedStatement pstmt = MyDatabase.getInstance().getConnection().prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            pstmt.setDouble(1, commande.getTotal_c());
            pstmt.setString(2, commande.getStatut_c());
            pstmt.executeUpdate();

            // Récupérer l'ID généré
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1); // Retourner l'ID de la commande insérée
            } else {
                throw new SQLException("Erreur lors de la génération de l'ID de la commande.");
            }
        }
    }


    @Override
    public List<Commande> afficher() throws SQLException {
        List<Commande> commandes = new ArrayList<>();
        String req = "SELECT * FROM commande";
// statment plus rapide
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(req)) {

            while (resultSet.next()) {
                Commande commande = new Commande(
                        resultSet.getInt("id_c"),
                        resultSet.getFloat("total_c"),
                        resultSet.getString("statut_c")
                );
                commandes.add(commande);
            }
        }
        return commandes;
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String req = "DELETE FROM commande WHERE id_c = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
            preparedStatement.setInt(1, id);

            int rowsDeleted = preparedStatement.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Commande supprimée avec succès !");
            } else {
                System.out.println("Aucune commande trouvée avec cet ID.");
            }
        }
    }

    public void modifier(Commande commande) throws SQLException {
        String req = "UPDATE commande SET total_c = ?, statut_c = ? WHERE id_c = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
            preparedStatement.setFloat(1, commande.getTotal_c());
            preparedStatement.setString(2, commande.getStatut_c());
            preparedStatement.setInt(3, commande.getId_c());

            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Commande mise à jour avec succès !");
            } else {
                System.out.println("Aucune commande trouvée avec cet ID.");
            }
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
// preperestatment protuction contre sql ingection
    public void modifierStatut(int commandeId, String nouveauStatut) throws SQLException {
        String req = "UPDATE commande SET statut_c = ? WHERE id_c = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
            preparedStatement.setString(1, nouveauStatut);
            preparedStatement.setInt(2, commandeId);

            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Statut de la commande mis à jour avec succès !");
            } else {
                System.out.println("Aucune commande trouvée avec cet ID.");
            }
        }
    }

}
