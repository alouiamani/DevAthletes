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
        String req = "INSERT INTO abonnement (id_Abonnement, activite_id, type_abonnement, tarif, id_Salle) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
            preparedStatement.setInt(1, abonnement.getId_Abonnement());
            preparedStatement.setInt(2, abonnement.getActivite() != null ? abonnement.getActivite().getId() : 0);
            preparedStatement.setString(3, abonnement.getType_Abonnement().name());
            preparedStatement.setDouble(4, abonnement.getTarif());
            preparedStatement.setInt(5, id_Salle);

            int rowsInserted = preparedStatement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("✅ Abonnement added successfully!");
            } else {
                System.out.println("⚠️ Failed to add abonnement!");
            }
        } catch (SQLException e) {
            System.err.println("❌ SQL error during addition: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void modifier(Abonnement abonnement) throws SQLException {
        String req = "UPDATE abonnement SET activite_id=?, type_abonnement=?, tarif=?, id_Salle=? WHERE id_Abonnement=?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
            preparedStatement.setInt(1, abonnement.getActivite() != null ? abonnement.getActivite().getId() : 0);
            preparedStatement.setString(2, abonnement.getType_Abonnement().name());
            preparedStatement.setDouble(3, abonnement.getTarif());
            preparedStatement.setInt(4, abonnement.getSalle().getId());
            preparedStatement.setInt(5, abonnement.getId_Abonnement());

            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("✅ Abonnement updated successfully!");
            } else {
                System.out.println("⚠️ No abonnement updated!");
            }
        } catch (SQLException e) {
            System.err.println("❌ SQL error during update: " + e.getMessage());
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
                System.out.println("✅ Abonnement deleted successfully!");
            } else {
                System.out.println("⚠️ No abonnement deleted!");
            }
        } catch (SQLException e) {
            System.err.println("❌ SQL error during deletion: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public List<Abonnement> afficher() throws SQLException {
        List<Abonnement> abonnements = new ArrayList<>();
        String req = "SELECT a.*, s.id AS salle_id, s.nom AS salle_nom, s.adresse, s.details, s.num_tel, s.email, " +
                "act.id AS activite_id, act.nom AS activite_nom, act.type AS activite_type " +
                "FROM abonnement a " +
                "JOIN salle s ON a.id_Salle = s.id " +
                "LEFT JOIN activité act ON a.activite_id = act.id";

        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(req)) {
            while (rs.next()) {
                abonnements.add(mapResultSetToAbonnement(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ SQL error while retrieving abonnements: " + e.getMessage());
            throw e;
        }
        return abonnements;
    }

    public List<Abonnement> afficherParSalle(int salleId) throws SQLException {
        List<Abonnement> abonnements = new ArrayList<>();
        String req = "SELECT a.*, s.id AS salle_id, s.nom AS salle_nom, s.adresse, s.details, s.num_tel, s.email, " +
                "act.id AS activite_id, act.nom AS activite_nom, act.type AS activite_type " +
                "FROM abonnement a " +
                "JOIN salle s ON a.id_Salle = s.id " +
                "LEFT JOIN activité act ON a.activite_id = act.id " +
                "WHERE a.id_Salle = ?";

        try (PreparedStatement statement = connection.prepareStatement(req)) {
            statement.setInt(1, salleId);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    abonnements.add(mapResultSetToAbonnement(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ SQL error while retrieving abonnements by salle: " + e.getMessage());
            throw e;
        }
        return abonnements;
    }

    public List<Abonnement> afficherParResponsable(int responsableId) throws SQLException {
        List<Abonnement> abonnements = new ArrayList<>();
        String req = "SELECT a.*, s.id AS salle_id, s.nom AS salle_nom, s.adresse, s.details, s.num_tel, s.email, " +
                "act.id AS activite_id, act.nom AS activite_nom, act.type AS activite_type " +
                "FROM abonnement a " +
                "JOIN salle s ON a.id_Salle = s.id " +
                "LEFT JOIN activité act ON a.activite_id = act.id " +
                "WHERE s.responsable_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(req)) {
            statement.setInt(1, responsableId);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    abonnements.add(mapResultSetToAbonnement(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ SQL error while retrieving abonnements by responsable: " + e.getMessage());
            throw e;
        }
        return abonnements;
    }

    public List<String> getActivityTypes() throws SQLException {
        List<String> activityTypes = new ArrayList<>();
        String query = "SELECT DISTINCT type FROM activité WHERE type IS NOT NULL";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                activityTypes.add(rs.getString("type"));
            }
        } catch (SQLException e) {
            System.err.println("❌ SQL error while retrieving activity types: " + e.getMessage());
            throw e;
        }
        return activityTypes;
    }

    public List<Abonnement> getAbonnementsByActivityType(String typeActivite) throws SQLException {
        List<Abonnement> abonnements = new ArrayList<>();
        String query = "SELECT a.*, s.id AS salle_id, s.nom AS salle_nom, s.adresse, s.details, s.num_tel, s.email, " +
                "act.id AS activite_id, act.nom AS activite_nom, act.type AS activite_type " +
                "FROM abonnement a " +
                "JOIN salle s ON a.id_Salle = s.id " +
                "LEFT JOIN activité act ON a.activite_id = act.id " +
                "WHERE act.type = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, typeActivite);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    abonnements.add(mapResultSetToAbonnement(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ SQL error while retrieving abonnements by activity type: " + e.getMessage());
            throw e;
        }
        return abonnements;
    }

    public List<Abonnement> getAbonnementsParSalle(int id_Salle) throws SQLException {
        return afficherParSalle(id_Salle);
    }

    public List<Abonnement> getAbonnementsBySalleAndActivityType(int salleId, String typeActivite) throws SQLException {
        List<Abonnement> abonnements = new ArrayList<>();
        String query = "SELECT a.*, s.id AS salle_id, s.nom AS salle_nom, s.adresse, s.details, s.num_tel, s.email, " +
                "act.id AS activite_id, act.nom AS activite_nom, act.type AS activite_type " +
                "FROM abonnement a " +
                "JOIN salle s ON a.id_Salle = s.id " +
                "LEFT JOIN activité act ON a.activite_id = act.id " +
                "WHERE a.id_Salle = ? AND act.type = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, salleId);
            pstmt.setString(2, typeActivite);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    abonnements.add(mapResultSetToAbonnement(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ SQL error while retrieving abonnements by salle and activity type: " + e.getMessage());
            throw e;
        }
        return abonnements;
    }

    private Abonnement mapResultSetToAbonnement(ResultSet rs) throws SQLException {
        Abonnement abonnement = new Abonnement();
        abonnement.setId_Abonnement(rs.getInt("id_Abonnement"));
        abonnement.setType_Abonnement(type_Abonnement.valueOf(rs.getString("type_abonnement")));
        abonnement.setTarif(rs.getDouble("tarif"));

        Salle salle = new Salle();
        salle.setId(rs.getInt("salle_id"));
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
            abonnement.setTypeActivite(activite.getType().toString()); // Set typeActivite from activite.type
        } else {
            abonnement.setTypeActivite(null); // Set to null if no activity is associated
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
        String query = "SELECT * FROM salle WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, salleId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Salle salle = new Salle();
                    salle.setId(rs.getInt("id"));
                    salle.setNom(rs.getString("nom"));
                    salle.setAdresse(rs.getString("adresse"));
                    salle.setDetails(rs.getString("details"));
                    salle.setNum_tel(rs.getString("num_tel"));
                    salle.setEmail(rs.getString("email"));
                    salle.setResponsable_id(rs.getInt("responsable_id"));
                    return salle;
                }
            }
        }
        return null;
    }

    public Salle getSalleByResponsableId(int responsableId) throws SQLException {
        String query = "SELECT * FROM salle WHERE responsable_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, responsableId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Salle salle = new Salle();
                    salle.setId(rs.getInt("id"));
                    salle.setNom(rs.getString("nom"));
                    salle.setAdresse(rs.getString("adresse"));
                    salle.setDetails(rs.getString("details"));
                    salle.setNum_tel(rs.getString("num_tel"));
                    salle.setEmail(rs.getString("email"));
                    salle.setResponsable_id(rs.getInt("responsable_id"));
                    return salle;
                }
            }
        }
        return null;
    }
}