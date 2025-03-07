package org.gymify.services;



import org.gymify.entities.*;
import org.gymify.utils.gymifyDataBase;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AbonnementService implements Iservices<Abonnement> {
    Connection connection;

    public AbonnementService() {

        connection = gymifyDataBase.getInstance().getConnection();
    }

    @Override
    public void ajouter(Abonnement abonnement, int id_Salle) throws SQLException {
        String req = "INSERT INTO abonnement (date_Début, date_Fin, type_Abonnement, id_Salle, tarif,id_activite, typeActivite) VALUES (?, ?, ?, ?, ?,?,?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
            preparedStatement.setDate(1, new java.sql.Date(abonnement.getDate_Début().getTime()));
            preparedStatement.setDate(2, new java.sql.Date(abonnement.getDate_Fin().getTime()));
            preparedStatement.setString(3, abonnement.getType_Abonnement().name());
            preparedStatement.setInt(4, id_Salle);
            preparedStatement.setDouble(5, abonnement.getTarif());
            preparedStatement.setInt(6, abonnement.getActivite().getId()); // Associer l'abonnement à l'activité
            preparedStatement.setString(7, abonnement.getActivite().getType().toString());
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
        String req = "SELECT a.*, s.id_Salle, s.nom AS salle_nom, s.adresse, s.details, s.num_tel, s.email, " +
                "act.id AS activite_id, act.nom AS activite_nom, act.type AS activite_type " +
                "FROM abonnement a " +
                "JOIN salle s ON a.id_Salle = s.id_Salle " +
                "LEFT JOIN activité act ON a.id_activite = act.id";

        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(req)) {

            while (rs.next()) {
                Abonnement abonnement = new Abonnement();
                abonnement.setId_Abonnement(rs.getInt("id_Abonnement"));
                abonnement.setDate_Début(rs.getDate("date_Début"));
                abonnement.setDate_Fin(rs.getDate("date_Fin"));
                abonnement.setType_Abonnement(type_Abonnement.valueOf(rs.getString("type_Abonnement")));
                abonnement.setTarif(rs.getDouble("tarif"));

                Salle salle = new Salle();
                salle.setId_Salle(rs.getInt("id_Salle"));
                salle.setNom(rs.getString("salle_nom"));
                salle.setAdresse(rs.getString("adresse"));
                salle.setDetails(rs.getString("details"));
                salle.setNum_tel(rs.getString("num_tel"));
                salle.setEmail(rs.getString("email"));
                abonnement.setSalle(salle);

                if (rs.getInt("activite_id") != 0) {
                    Activité activite = new Activité();
                    activite.setId(rs.getInt("activite_id"));
                    activite.setNom(rs.getString("activite_nom"));
                    activite.setType(ActivityType.valueOf(rs.getString("activite_type")));
                    abonnement.setActivite(activite);
                }

                abonnements.add(abonnement);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des abonnements : " + e.getMessage());
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
                abonnement.setDate_Début(rs.getDate("date_Début"));
                abonnement.setDate_Fin(rs.getDate("date_Fin"));
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
    public List<String> getActivityTypes() throws SQLException {
        List<String> activityTypes = new ArrayList<>();
        String query = "SELECT DISTINCT typeActivite FROM abonnement";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                activityTypes.add(rs.getString("typeActivite"));
            }
        }
        return activityTypes;
    }
    public List<Abonnement> getAbonnementsByActivityType(String typeActivite) throws SQLException {
        List<Abonnement> abonnements = new ArrayList<>();
        String query = "SELECT * FROM abonnement WHERE typeActivite = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, typeActivite);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                abonnements.add(mapResultSetToAbonnement(rs));
            }
        }
        return abonnements;
    }

// Vérifier si une salle existe

    private Abonnement mapResultSetToAbonnement(ResultSet rs) throws SQLException {
        Abonnement abonnement = new Abonnement();

        abonnement.setId_Abonnement(rs.getInt("id_Abonnement"));
        abonnement.setDate_Début(rs.getDate("date_Début"));
        abonnement.setDate_Fin(rs.getDate("date_Fin"));
        abonnement.setType_Abonnement(type_Abonnement.valueOf(rs.getString("type_Abonnement").toUpperCase()));
        abonnement.setTarif(rs.getDouble("tarif"));

        // Map salle
        int salleId = rs.getInt("id_Salle");
        if (!rs.wasNull()) {
            abonnement.setSalle(getSalleById(salleId));
        }

        // Map activité
        int activiteId = rs.getInt("id_activite");
        if (!rs.wasNull()) {
            abonnement.setActivite(getActiviteById(activiteId));
        }

        // Map typeActivite
        abonnement.setTypeActivite(rs.getString("typeActivite"));

        return abonnement;
    }
    public Activité getActiviteById(int activiteId) throws SQLException {
        Activité activite = null;
        String query = "SELECT * FROM Activité WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, activiteId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                activite = new Activité();
                activite.setId(rs.getInt("id"));
                activite.setNom(rs.getString("nom"));
                activite.setType(ActivityType.valueOf(rs.getString("type")));
                activite.setDescription(rs.getString("description"));
                activite.setUrl(rs.getString("URL"));
            }
        }
        return activite;
    }
    public Salle getSalleById(int salleId) throws SQLException {
        Salle salle = null;
        String query = "SELECT * FROM Salle WHERE id_Salle = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, salleId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                salle = new Salle();
                salle.setId_Salle(rs.getInt("id_Salle"));
                salle.setNom(rs.getString("nom"));
                salle.setAdresse(rs.getString("adresse"));
                salle.setDetails(rs.getString("détails"));
                salle.setNum_tel(rs.getString("num_tel"));
                salle.setEmail(rs.getString("email"));
            }
        }
        return salle;
    }
    public List<Abonnement> afficherParSalle(int salleId) throws SQLException {
        List<Abonnement> abonnements = new ArrayList<>();
        String query = "SELECT a.id_Abonnement, a.date_Début, a.date_Fin, a.type_Abonnement, a.tarif, " +
                "s.id_Salle, s.nom AS salle_nom, s.adresse, s.details, s.num_tel, s.email, " +
                "act.id AS activite_id, act.nom AS activite_nom, act.type AS activite_type " +
                "FROM abonnement a " +
                "JOIN salle s ON a.id_Salle = s.id_Salle " +
                "LEFT JOIN activité act ON a.id_activite = act.id " +
                "WHERE a.id_Salle = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, salleId);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                Abonnement abonnement = new Abonnement();
                abonnement.setId_Abonnement(rs.getInt("id_Abonnement"));
                abonnement.setDate_Début(rs.getDate("date_Début"));
                abonnement.setDate_Fin(rs.getDate("date_Fin"));
                abonnement.setType_Abonnement(type_Abonnement.valueOf(rs.getString("type_Abonnement")));
                abonnement.setTarif(rs.getDouble("tarif"));

                Salle salle = new Salle();
                salle.setId_Salle(rs.getInt("id_Salle"));
                salle.setNom(rs.getString("salle_nom"));
                salle.setAdresse(rs.getString("adresse"));
                salle.setDetails(rs.getString("details"));
                salle.setNum_tel(rs.getString("num_tel"));
                salle.setEmail(rs.getString("email"));
                abonnement.setSalle(salle);

                if (rs.getInt("activite_id") != 0) {
                    Activité activite = new Activité();
                    activite.setId(rs.getInt("activite_id"));
                    activite.setNom(rs.getString("activite_nom"));
                    activite.setType(ActivityType.valueOf(rs.getString("activite_type")));
                    abonnement.setActivite(activite);
                }

                abonnements.add(abonnement);
            }
        }
        return abonnements;
    }
    public List<Abonnement> getAbonnementsBySalleAndActivityType(int salleId, String typeActivite) throws SQLException {
        List<Abonnement> abonnements = new ArrayList<>();
        String query = "SELECT * FROM abonnement WHERE id_Salle = ? AND typeActivite = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, salleId);
            pstmt.setString(2, typeActivite);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                abonnements.add(mapResultSetToAbonnement(rs));
            }
        }

        return abonnements;
    }
}
