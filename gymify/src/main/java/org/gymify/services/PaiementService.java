//import org.gymify.utils.gymifyDataBase;
//import org.gymify.entities.Paiement;
//import com.google.api.client.util.DateTime;
//
//import java.sql.*;
//import java.util.ArrayList;
//import java.util.List;
//
//public class PaiementService {
//    private Connection con;
//
//    public PaiementService() {
//        con = gymifyDataBase.getInstance().getConnection();
//    }
//
//    // Ajouter un paiement
//    public void ajouterPaiement(Paiement paiement) throws SQLException {
//        String query = "INSERT INTO paiement (id_abonnement, id_user, amount, statut, payment_intent_id, created_at, updated_at, currency, date_debut, date_fin) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
//        PreparedStatement pst = con.prepareStatement(query);
//        pst.setInt(1, paiement.getId_abonnement());
//        pst.setInt(2, paiement.getId_user());
//        pst.setDouble(3, paiement.getAmount());
//        pst.setString(4, paiement.getStatut());
//        pst.setString(5, paiement.getPayment_intent_id());
//        pst.setTimestamp(6, new Timestamp(paiement.getCreated_at().getValue()));
//        pst.setTimestamp(7, new Timestamp(paiement.getUpdated_at().getValue()));
//        pst.setString(8, paiement.getCurrency());
//        pst.setTimestamp(9, new Timestamp(paiement.getDate_debut().getValue()));
//        pst.setTimestamp(10, new Timestamp(paiement.getDate_fin().getValue()));
//        pst.executeUpdate();
//    }
//
//    // Récupérer les paiements d'un utilisateur
//    public List<Paiement> getPaiementsParUser(int idUser) throws SQLException {
//        List<Paiement> paiements = new ArrayList<>();
//        String query = "SELECT * FROM paiement WHERE id_user = ?";
//        PreparedStatement pst = con.prepareStatement(query);
//        pst.setInt(1, idUser);
//        ResultSet rs = pst.executeQuery();
//
//        while (rs.next()) {
//            Paiement paiement = new Paiement(
//                    rs.getInt("id_paiement"),
//                    rs.getDouble("amount"),
//                    rs.getString("statut"),
//                    rs.getString("payment_intent_id"),
//                    new DateTime(rs.getTimestamp("created_at").getTime()),
//                    new DateTime(rs.getTimestamp("updated_at").getTime()),
//                    rs.getString("currency"),
//                    new DateTime(rs.getTimestamp("date_debut").getTime()),
//                    new DateTime(rs.getTimestamp("date_fin").getTime()),
//                    rs.getInt("id_user"),
//                    rs.getInt("id_abonnement")
//            );
//            paiements.add(paiement);
//        }
//        return paiements;
//    }
//}