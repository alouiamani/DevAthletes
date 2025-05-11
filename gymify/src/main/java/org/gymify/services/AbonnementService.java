package org.gymify.services;

import org.gymify.entities.*;
import org.gymify.utils.gymifyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AbonnementService implements Iservices<Abonnement> {
    private Connection connection;

    public AbonnementService() {
        connection = gymifyDataBase.getInstance().getConnection();
    }

    @Override
    public void ajouter(Abonnement abonnement, int id_Salle) throws SQLException {
        String req = "INSERT INTO abonnement (id_Abonnement, type_Abonnement, tarif, id_Salle, id_activite, typeActivite) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
            preparedStatement.setInt(1, abonnement.getId_Abonnement());
            preparedStatement.setString(2, abonnement.getType_Abonnement().name());
            preparedStatement.setDouble(3, abonnement.getTarif());
            preparedStatement.setInt(4, id_Salle);
            preparedStatement.setInt(5, abonnement.getActivite() != null ? abonnement.getActivite().getId() : 0);
            preparedStatement.setString(6, abonnement.getTypeActivite());

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
        String req = "UPDATE abonnement SET type_Abonnement=?, tarif=?, id_Salle=?, id_activite=?, typeActivite=? WHERE id_Abonnement=?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
            preparedStatement.setString(1, abonnement.getType_Abonnement().name());
            preparedStatement.setDouble(2, abonnement.getTarif());
            preparedStatement.setInt(3, abonnement.getSalle().getId());
            preparedStatement.setInt(4, abonnement.getActivite() != null ? abonnement.getActivite().getId() : 0);
            preparedStatement.setString(5, abonnement.getTypeActivite());
            preparedStatement.setInt(6, abonnement.getId_Abonnement());

            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("✅ Abonnement modifié avec succès !");
            } else {
                System.out.println("⚠️ Aucun abonnement modifié !");
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur SQL lors de la modification : " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String req = "DELETE FROM abonnement WHERE id_Abonnement=?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
            preparedStatement.setInt(1, id);
            int rowsDeleted = preparedStatement.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("✅ Abonnement supprimé avec succès !");
            } else {
                System.out.println("⚠️ Aucun abonnement supprimé !");
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur SQL lors de la suppression : " + e.getMessage());
            throw e;
        }
    }

    @Override
    public List<Abonnement> afficher() throws SQLException {
        List<Abonnement> abonnements = new ArrayList<>();
        String req = "SELECT a.*, s.id_Salle, s.nom AS salle_nom, s.adresse, s.details, s.num_tel, s.email, " +
                "act.id AS activite_id, act.nom AS activite_nom, act.type AS activite_type " +
                "FROM abonnement a " +
                "JOIN salle s ON a.id_Salle = s.id_Salle " +
                "LEFT JOIN activité act ON a.id_activite = act.id";

        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(req)) {
            while (rs.next()) {
                abonnements.add(mapResultSetToAbonnement(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur SQL lors de la récupération des abonnements : " + e.getMessage());
            throw e;
        }
        return abonnements;
    }

    public List<Abonnement> afficherParSalle(int salleId) throws SQLException {
        List<Abonnement> abonnements = new ArrayList<>();
        String req = "SELECT a.*, s.id_Salle, s.nom AS salle_nom, s.adresse, s.details, s.num_tel, s.email, " +
                "act.id AS activite_id, act.nom AS activite_nom, act.type AS activite_type " +
                "FROM abonnement a " +
                "JOIN salle s ON a.id_Salle = s.id_Salle " +
                "LEFT JOIN activité act ON a.id_activite = act.id " +
                "WHERE a.id_Salle = ?";

        try (PreparedStatement statement = connection.prepareStatement(req)) {
            statement.setInt(1, salleId);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    abonnements.add(mapResultSetToAbonnement(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur SQL lors de la récupération des abonnements par salle : " + e.getMessage());
            throw e;
        }
        return abonnements;
    }

    public List<String> getActivityTypes() throws SQLException {
        List<String> activityTypes = new ArrayList<>();
        String query = "SELECT DISTINCT typeActivite FROM abonnement WHERE typeActivite IS NOT NULL";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                activityTypes.add(rs.getString("typeActivite"));
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur SQL lors de la récupération des types d'activité : " + e.getMessage());
            throw e;
        }
        return activityTypes;
    }

    public List<Abonnement> getAbonnementsByActivityType(String typeActivite) throws SQLException {
        List<Abonnement> abonnements = new ArrayList<>();
        String query = "SELECT a.*, s.id_Salle, s.nom AS salle_nom, s.adresse, s.details, s.num_tel, s.email, " +
                "act.id AS activite_id, act.nom AS activite_nom, act.type AS activite_type " +
                "FROM abonnement a " +
                "JOIN salle s ON a.id_Salle = s.id_Salle " +
                "LEFT JOIN activité act ON a.id_activite = act.id " +
                "WHERE a.typeActivite = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, typeActivite);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    abonnements.add(mapResultSetToAbonnement(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur SQL lors de la récupération des abonnements par type d'activité : " + e.getMessage());
            throw e;
        }
        return abonnements;
    }
    public List<Abonnement> getAbonnementsParSalle(int id_Salle) throws SQLException {
        List<Abonnement> abonnements = new ArrayList<>();
        String req = "SELECT a.*, s.id_Salle, s.nom, s.adresse, s.details, s.num_tel, s.email " +
                "FROM abonnement a " +
                "JOIN salle s ON a.id_Salle = s.id_Salle " +
                "WHERE a.id_Salle = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
            preparedStatement.setInt(1, id_Salle);
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                Abonnement abonnement = new Abonnement();
                abonnement.setId_Abonnement(rs.getInt("id_Abonnement"));

                abonnement.setType_Abonnement(type_Abonnement.valueOf(rs.getString("type_Abonnement")));
                abonnement.setTarif(rs.getDouble("tarif"));

                Salle salle = new Salle();
                salle.setId_Salle(rs.getInt("id_Salle"));
                salle.setNom(rs.getString("nom"));
                salle.setAdresse(rs.getString("adresse"));
                salle.setDetails(rs.getString("details"));
                salle.setNum_tel(rs.getString("num_tel"));
                salle.setEmail(rs.getString("email"));
                abonnement.setSalle(salle);

                abonnements.add(abonnement);
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur SQL lors de la récupération des abonnements par salle : " + e.getMessage());
            throw e;
        }

        return abonnements;
    }

    public List<Abonnement> getAbonnementsBySalleAndActivityType(int salleId, String typeActivite) throws SQLException {
        List<Abonnement> abonnements = new ArrayList<>();
        String query = "SELECT a.*, s.id_Salle, s.nom AS salle_nom, s.adresse, s.details, s.num_tel, s.email, " +
                "act.id AS activite_id, act.nom AS activite_nom, act.type AS activite_type " +
                "FROM abonnement a " +
                "JOIN salle s ON a.id_Salle = s.id_Salle " +
                "LEFT JOIN activité act ON a.id_activite = act.id " +
                "WHERE a.id_Salle = ? AND a.typeActivite = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, salleId);
            pstmt.setString(2, typeActivite);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    abonnements.add(mapResultSetToAbonnement(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur SQL lors de la récupération des abonnements par salle et type d'activité : " + e.getMessage());
            throw e;
        }
        return abonnements;
    }

    private Abonnement mapResultSetToAbonnement(ResultSet rs) throws SQLException {
        Abonnement abonnement = new Abonnement();
        abonnement.setId_Abonnement(rs.getInt("id_Abonnement"));
        abonnement.setType_Abonnement(type_Abonnement.valueOf(rs.getString("type_Abonnement")));
        abonnement.setTarif(rs.getDouble("tarif"));
        abonnement.setTypeActivite(rs.getString("typeActivite"));

        Salle salle = new Salle();
        salle.setId_Salle(rs.getInt("id_Salle"));
        salle.setNom(rs.getString("salle_nom"));
        salle.setAdresse(rs.getString("adresse"));
        salle.setDetails(rs.getString("details"));
        salle.setNum_tel(rs.getString("num_tel"));
        salle.setEmail(rs.getString("email"));
        abonnement.setSalle(salle);

        int activiteId = rs.getInt("activite_id");
        if (activiteId != 0) {
            Activité activite = new Activité();
            activite.setId(activiteId);
            activite.setNom(rs.getString("activite_nom"));
            activite.setType(ActivityType.valueOf(rs.getString("activite_type")));
            abonnement.setActivite(activite);
        }

        return abonnement;
    }

    public Activité getActiviteById(int activiteId) throws SQLException {
        String query = "SELECT * FROM activité WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, activiteId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Activité activite = new Activité();
                    activite.setId(rs.getInt("id"));
                    activite.setNom(rs.getString("nom"));
                    activite.setType(ActivityType.valueOf(rs.getString("type")));
                    activite.setDescription(rs.getString("description"));
                    activite.setUrl(rs.getString("URL"));
                    return activite;
                }
            }
        }
        return null;
    }

    public Salle getSalleById(int salleId) throws SQLException {
        String query = "SELECT * FROM salle WHERE id_Salle = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, salleId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Salle salle = new Salle();
                    salle.setId_Salle(rs.getInt("id_Salle"));
                    salle.setNom(rs.getString("nom"));
                    salle.setAdresse(rs.getString("adresse"));
                    salle.setDetails(rs.getString("details"));
                    salle.setNum_tel(rs.getString("num_tel"));
                    salle.setEmail(rs.getString("email"));
                    return salle;
                }
            }
        }
        return null;
    }
}