package tn.gimify.services;



import tn.gimify.entities.Paiement;
import tn.gimify.utils.MyDatabase;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PaiementService {
    private Connection connection;

    public PaiementService() {
        connection = MyDatabase.getInstance().getConnection();
    }

    // Ajouter un paiement
    public void ajouterPaiement(Paiement paiement) throws SQLException {
        String query = "INSERT INTO paiement (id_abonnement, id_user, montant, date_paiement, methode, statut) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement pst = connection.prepareStatement(query);
        pst.setInt(1, paiement.getIdAbonnement());
        pst.setInt(2, paiement.getIdUser());
        pst.setDouble(3, paiement.getMontant());
        pst.setDate(4, Date.valueOf(paiement.getDatePaiement()));
        pst.setString(5, paiement.getMethode());
        pst.setString(6, paiement.getStatut());
        pst.executeUpdate();
    }

    // Récupérer les paiements d'un utilisateur
    public List<Paiement> getPaiementsParUser(int idUser) throws SQLException {
        List<Paiement> paiements = new ArrayList<>();
        String query = "SELECT * FROM paiement WHERE id_user = ?";
        PreparedStatement pst = connection.prepareStatement(query);
        pst.setInt(1, idUser);
        ResultSet rs = pst.executeQuery();

        while (rs.next()) {
            Paiement paiement = new Paiement(
                    rs.getInt("id_abonnement"),
                    rs.getInt("id_user"),
                    rs.getDouble("montant"),
                    rs.getDate("date_paiement").toLocalDate(),
                    rs.getString("methode"),
                    rs.getString("statut")
            );
            paiement.setIdPaiement(rs.getInt("id_paiement"));
            paiements.add(paiement);
        }
        return paiements;
    }
}

