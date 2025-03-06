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
        String req = "SELECT a.*, s.id_Salle, s.nom, s.adresse, s.details, s.num_tel, s.email " +
                "FROM abonnement a " +
                "JOIN salle s ON a.id_Salle = s.id_Salle " +
                "WHERE a.id_Salle = ?"; // Jointure avec la table salle

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
        } catch (SQLException e) {
            System.err.println("❌ Erreur SQL lors de la récupération des abonnements par salle : " + e.getMessage());
            throw e;
        }

        return abonnements;
    }
    // Récupérer la liste des types d'activités distincts
    public List<String> getActivityTypes() throws SQLException {
        List<String> activityTypes = new ArrayList<>();
        String query = "SELECT DISTINCT typeActivite FROM Abonnement";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                activityTypes.add(rs.getString("typeActivite"));
            }
        }
        return activityTypes;
    }

    // Afficher les abonnements d'une salle spécifique
    public List<Abonnement> afficherParSalle(int salleId) throws SQLException {
        List<Abonnement> abonnements = new ArrayList<>();
        String query = "SELECT * FROM Abonnement WHERE id_salle = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, salleId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                abonnements.add(mapResultSetToAbonnement(rs));
            }
        }
        return abonnements;
    }

    // Récupérer une salle par ID


    // Récupérer les abonnements par type d'activité
    public List<Abonnement> getAbonnementsByActivityType(String typeActivite) throws SQLException {
        List<Abonnement> abonnements = new ArrayList<>();
        String query = "SELECT * FROM Abonnement WHERE typeActivite = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, typeActivite);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                abonnements.add(mapResultSetToAbonnement(rs));
            }
        }
        return abonnements;
    }

    private Abonnement mapResultSetToAbonnement(ResultSet rs) throws SQLException {
        Abonnement abonnement = new Abonnement();

        abonnement.setId_Abonnement(rs.getInt("id_Abonnement"));
        abonnement.setDate_Début(rs.getDate("Date_Début"));
        abonnement.setDate_Fin(rs.getDate("Date_Fin"));
        abonnement.setType_Abonnement(type_Abonnement.valueOf(rs.getString("type_Abonnement").toUpperCase()));
        abonnement.setTarif(rs.getDouble("tarif"));

        // Récupération de la salle associée
        int salleId = rs.getInt("salle_id");
        if (!rs.wasNull()) {
            abonnement.setSalle(getSalleById(salleId));
        }

        // Récupération de l'activité associée
        int activiteId = rs.getInt("activite_id");
        if (!rs.wasNull()) {
            abonnement.setActivite(getActiviteById(activiteId));
        }

        // Récupération du type d'activité
        abonnement.setTypeActivite(rs.getString("typeActivite"));

        return abonnement;
    }
// Vérifier si une salle existe

    public List<Abonnement> getAbonnementsBySalleAndActivityType(int salleId, String selectedActivity) throws SQLException {
        List<Abonnement> abonnements = new ArrayList<>();

        String query = "SELECT * FROM Abonnement WHERE salle_id = ? AND typeActivite = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, salleId);
            pstmt.setString(2, selectedActivity); // Convert enum to String

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                abonnements.add(mapResultSetToAbonnement(rs));
            }
        }

        return abonnements;
    }
    public Activité getActiviteById(int activiteId) throws SQLException {
        Activité activite = null;
        String query = "SELECT * FROM Activité WHERE id_activite = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, activiteId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                activite = new Activité();
                activite.setId(rs.getInt("id_activite"));
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
        String query = "SELECT * FROM Salle WHERE id_salle = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, salleId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                salle = new Salle();
                salle.setId_Salle(rs.getInt("id_salle"));
                salle.setNom(rs.getString("nom"));
                salle.setAdresse(rs.getString("adresse"));
                salle.setDetails(rs.getString("détails"));
                salle.setNum_tel(rs.getString("num_tel"));
                salle.setEmail(rs.getString("email"));
            }
        }
        return salle;
    }
}
