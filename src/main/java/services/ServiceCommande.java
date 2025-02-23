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


    @Override
    public int ajouter(Commande commande) throws SQLException {
        String query = "INSERT INTO commande (total_c, statut_c, dateC) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            pstmt.setDouble(1, commande.getTotal_c());
            pstmt.setString(2, commande.getStatut_c());
            pstmt.setDate(3, commande.getDateC());
            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                throw new SQLException("Erreur lors de la génération de l'ID de la commande.");
            }
        }
    }


    @Override
    public List<Commande> afficher() throws SQLException {
        List<Commande> commandes = new ArrayList<>();
        String req = "SELECT * FROM commande";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(req)) {

            while (resultSet.next()) {
                Commande commande = new Commande(
                        resultSet.getInt("id_c"),
                        resultSet.getFloat("total_c"),
                        resultSet.getString("statut_c"),
                        resultSet.getDate("dateC")
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

    @Override
    public void modifier(Commande commande) throws SQLException {
        String req = "UPDATE commande SET total_c = ?, statut_c = ?, dateC = ? WHERE id_c = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
            preparedStatement.setFloat(1, commande.getTotal_c());
            preparedStatement.setString(2, commande.getStatut_c());
            preparedStatement.setDate(3, commande.getDateC());
            preparedStatement.setInt(4, commande.getId_c());

            preparedStatement.executeUpdate();
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
