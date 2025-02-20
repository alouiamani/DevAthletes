package tn.gimify.services;

import tn.gimify.entities.Abonnement;
import tn.gimify.entities.Salle;
import tn.gimify.entities.type_Abonnement;
import tn.gimify.utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AbonnementService implements Iservices<Abonnement> {
    Connection connection;

    public AbonnementService() {
        connection = MyDatabase.getInstance().getConnection();
    }

    @Override
    public void ajouter(Abonnement abonnement, int id_Salle) throws SQLException {
        String req = "INSERT INTO abonnement (date_Début, date_Fin, type_Abonnement, id_Salle, tarif) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
            preparedStatement.setDate(1, new java.sql.Date(abonnement.getDate_Début().getTime()));
            preparedStatement.setDate(2, new java.sql.Date(abonnement.getDate_Fin().getTime()));
            preparedStatement.setString(3, abonnement.getType_Abonnement().name());
            preparedStatement.setInt(4, id_Salle);
            preparedStatement.setDouble(5, abonnement.getTarif());

            int rowsInserted = preparedStatement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("✅ Abonnement ajouté avec succès !");
            } else {
                System.out.println("⚠️ Échec de l'ajout de l'abonnement !");
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur SQL lors de l'ajout : " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void modifier(Abonnement abonnement) throws SQLException {
        String req = "UPDATE abonnement SET date_Début=?, date_Fin=?, type_Abonnement=?, tarif=? WHERE id_Abonnement=?";
        PreparedStatement preparedStatement = connection.prepareStatement(req);

        preparedStatement.setDate(1, new java.sql.Date(abonnement.getDate_Début().getTime()));
        preparedStatement.setDate(2, new java.sql.Date(abonnement.getDate_Fin().getTime()));
        preparedStatement.setString(3, abonnement.getType_Abonnement().name());
        preparedStatement.setDouble(4, abonnement.getTarif());
        preparedStatement.setInt(5, abonnement.getId_Abonnement());

        preparedStatement.executeUpdate();
        System.out.println("✅ Abonnement modifié avec succès !");
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String req = "DELETE FROM abonnement WHERE id_Abonnement=?";
        PreparedStatement preparedStatement = connection.prepareStatement(req);

        preparedStatement.setInt(1, id);
        preparedStatement.executeUpdate();
        System.out.println("✅ Abonnement supprimé avec succès !");
    }

    @Override
    public List<Abonnement> afficher() throws SQLException {
        List<Abonnement> abonnements = new ArrayList<>();
        String req = "SELECT a.*, s.id_Salle, s.nom, s.adresse, s.details, s.num_tel, s.email " +
                "FROM abonnement a " +
                "JOIN salle s ON a.id_Salle = s.id_Salle"; // Jointure avec la table salle
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(req);

        while (rs.next()) {
            Abonnement abonnement = new Abonnement();
            abonnement.setId_Abonnement(rs.getInt("id_Abonnement"));
            abonnement.setDate_Début(rs.getDate("date_Début"));
            abonnement.setDate_Fin(rs.getDate("date_Fin"));
            abonnement.setType_Abonnement(type_Abonnement.valueOf(rs.getString("type_Abonnement")));
            abonnement.setTarif(rs.getDouble("tarif"));

            // Création et initialisation de l'objet Salle
            Salle salle = new Salle();
            salle.setId_Salle(rs.getInt("id_Salle"));
            salle.setNom(rs.getString("nom"));
            salle.setAdresse(rs.getString("adresse"));
            salle.setDetails(rs.getString("details"));
            salle.setNum_tel(rs.getString("num_tel"));
            salle.setEmail(rs.getString("email"));

            abonnement.setSalle(salle);  // Associer la salle à l'abonnement

            abonnements.add(abonnement);
        }

        return abonnements;
    }
    public List<Abonnement> getAbonnementsParSalle(int id_Salle) throws SQLException {
        List<Abonnement> abonnements = new ArrayList<>();
        String req = "SELECT * FROM abonnement WHERE id_Salle = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
            preparedStatement.setInt(1, id_Salle);
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                Abonnement abonnement = new Abonnement();
                abonnement.setId_Abonnement(rs.getInt("id_Abonnement"));
                abonnement.setDate_Début(rs.getDate("date_Début"));
                abonnement.setDate_Fin(rs.getDate("date_Fin"));
                abonnement.setType_Abonnement(type_Abonnement.valueOf(rs.getString("type_Abonnement")));
                abonnement.setTarif(rs.getDouble("tarif"));

                abonnements.add(abonnement);
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur SQL lors de la récupération des abonnements par salle : " + e.getMessage());
            throw e;
        }

        return abonnements;
    }

}
