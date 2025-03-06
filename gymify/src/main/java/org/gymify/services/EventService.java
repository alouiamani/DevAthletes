package org.gymify.services;

import org.gymify.entities.Equipe;
import org.gymify.entities.Event;
import org.gymify.entities.EventType;
import org.gymify.entities.Salle;
import org.gymify.utils.gymifyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class EventService implements IServiceEvent<Event> {
    private final Connection connection;

    public EventService() {
        connection = gymifyDataBase.getInstance().getConnection();
    }

    @Override
    public void ajouter(Event event) throws SQLException {
        throw new UnsupportedOperationException("Use ajouter(Event event, int responsableId) instead.");
    }

    public void ajouter(Event event, int responsableId) throws SQLException {
        // Fetch the Salle associated with the Responsable_Salle
        SalleService salleService = new SalleService();
        Salle salle = salleService.getSalleByResponsableId(responsableId);
        if (salle == null) {
            // Enhanced error message for better debugging
            String query = "SELECT id_Salle, role FROM user WHERE id_User = ? AND role = 'responsable_salle'";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setInt(1, responsableId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int idSalle = rs.getInt("id_Salle");
                        throw new SQLException("No Salle associated with Responsable_Salle ID: " + responsableId + ". User's id_Salle is: " + idSalle);
                    } else {
                        throw new SQLException("No Responsable_Salle found with ID: " + responsableId + " or role is not 'responsable_salle'.");
                    }
                }
            }
        }
        int idSalle = salle.getId_Salle();
        event.setIdSalle(idSalle);

        // Validate that the Salle exists (redundant but kept for safety)
        if (!salleExists(event.getIdSalle())) {
            throw new SQLException("Impossible d'ajouter un événement : la salle avec l'ID " + event.getIdSalle() + " n'existe pas.");
        }

        String req = "INSERT INTO events (nom, date, heure_debut, heure_fin, type, description, image_url, reward, latitude, longitude, lieu, id_salle) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(req, Statement.RETURN_GENERATED_KEYS)) {
            setEventStatement(ps, event);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    event.setId(rs.getInt(1));
                }
            }
            // Update the Salle's events list
            event.setSalle(salle);
            salle.addEvent(event);
            System.out.println("✅ Event ajouté avec succès dans la salle ID: " + event.getIdSalle());
        } catch (SQLException e) {
            throw new SQLException("Erreur lors de l'ajout de l'événement : " + e.getMessage(), e);
        }
    }

    @Override
    public void modifier(Event event) throws SQLException {
        if (!salleExists(event.getIdSalle())) {
            throw new SQLException("Impossible de modifier l'événement : la salle avec l'ID " + event.getIdSalle() + " n'existe pas.");
        }

        String req = "UPDATE events SET nom=?, date=?, heure_debut=?, heure_fin=?, type=?, description=?, image_url=?, reward=?, latitude=?, longitude=?, lieu=?, id_salle=? WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            setEventStatement(ps, event);
            ps.setInt(13, event.getId());
            int rowsUpdated = ps.executeUpdate();
            if (rowsUpdated > 0) {
                SalleService salleService = new SalleService();
                Salle salle = salleService.getSalleById(event.getIdSalle());
                if (salle != null) {
                    event.setSalle(salle);
                    salle.getEvents().removeIf(e -> e.getId() == event.getId());
                    salle.addEvent(event);
                    System.out.println("✅ Event modifié avec succès dans la salle ID: " + event.getIdSalle());
                }
            } else {
                System.out.println("⚠️ Aucune modification effectuée. Vérifiez l'ID de l'événement.");
            }
        } catch (SQLException e) {
            throw new SQLException("Erreur lors de la modification de l'événement : " + e.getMessage(), e);
        }
    }

    @Override
    public void supprimer(int id) throws SQLException {
        Event event = recupererParId(id);
        if (event == null) {
            throw new SQLException("L'événement avec l'ID " + id + " n'existe pas.");
        }

        String req = "DELETE FROM events WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setInt(1, id);
            int rowsDeleted = ps.executeUpdate();
            if (rowsDeleted > 0) {
                SalleService salleService = new SalleService();
                Salle salle = salleService.getSalleById(event.getIdSalle());
                if (salle != null) {
                    salle.removeEvent(event);
                    System.out.println("✅ Event supprimé avec succès. La salle ID " + event.getIdSalle() + " reste intacte.");
                }
            } else {
                System.out.println("⚠️ Aucun événement trouvé avec l'ID : " + id);
            }
        } catch (SQLException e) {
            throw new SQLException("Erreur lors de la suppression de l'événement : " + e.getMessage(), e);
        }
    }

    @Override
    public List<Event> afficher() throws SQLException {
        return recupererTous("");
    }

    @Override
    public List<Event> recuperer() throws SQLException {
        return recupererTous("");
    }

    public List<Event> recupererTous(String searchQuery) throws SQLException {
        List<Event> events = new ArrayList<>();
        String req = "SELECT * FROM events WHERE nom LIKE ? OR lieu LIKE ? OR type LIKE ?";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            String query = "%" + searchQuery + "%";
            ps.setString(1, query);
            ps.setString(2, query);
            ps.setString(3, query);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Event event = mapResultSetToEvent(rs);
                    SalleService salleService = new SalleService();
                    Salle salle = salleService.getSalleById(event.getIdSalle());
                    event.setSalle(salle);
                    event.setEquipes(new HashSet<>(getEquipesForEvent(event.getId())));
                    events.add(event);
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Erreur lors de la récupération des événements : " + e.getMessage(), e);
        }
        return events;
    }

    public boolean eventNameExists(String nom) throws SQLException {
        String req = "SELECT COUNT(*) FROM events WHERE nom = ?";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setString(1, nom);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Erreur lors de la vérification de l'existence du nom de l'événement : " + e.getMessage(), e);
        }
        return false;
    }

    private void setEventStatement(PreparedStatement ps, Event event) throws SQLException {
        ps.setString(1, event.getNom());
        ps.setDate(2, event.getDate() != null ? Date.valueOf(event.getDate()) : null);
        ps.setTime(3, event.getHeureDebut() != null ? Time.valueOf(event.getHeureDebut()) : null);
        ps.setTime(4, event.getHeureFin() != null ? Time.valueOf(event.getHeureFin()) : null);
        ps.setString(5, event.getType() != null ? event.getType().toString() : null);
        ps.setString(6, event.getDescription());
        ps.setString(7, event.getImageUrl());
        ps.setString(8, event.getReward() != null ? event.getReward().toString() : null);
        ps.setDouble(9, event.getLatitude());
        ps.setDouble(10, event.getLongitude());
        ps.setString(11, event.getLieu());
        ps.setInt(12, event.getIdSalle());
    }

    private Event mapResultSetToEvent(ResultSet rs) throws SQLException {
        Event event = new Event();
        event.setId(rs.getInt("id"));
        event.setNom(rs.getString("nom"));
        event.setDate(rs.getDate("date") != null ? rs.getDate("date").toLocalDate() : null);
        event.setHeureDebut(rs.getTime("heure_debut") != null ? rs.getTime("heure_debut").toLocalTime() : null);
        event.setHeureFin(rs.getTime("heure_fin") != null ? rs.getTime("heure_fin").toLocalTime() : null);

        String typeStr = rs.getString("type");
        if (typeStr != null && !typeStr.trim().isEmpty()) {
            try {
                event.setType(EventType.valueOf(typeStr));
            } catch (IllegalArgumentException e) {
                System.err.println("Type d'événement invalide dans la base de données : " + typeStr);
                event.setType(null);
            }
        }

        event.setDescription(rs.getString("description"));
        event.setImageUrl(rs.getString("image_url"));

        String rewardStr = rs.getString("reward");
        if (rewardStr != null && !rewardStr.trim().isEmpty()) {
            try {
                event.setReward(EventType.Reward.valueOf(rewardStr));
            } catch (IllegalArgumentException e) {
                System.err.println("Valeur invalide pour reward dans la base de données : " + rewardStr);
                event.setReward(null);
            }
        }

        event.setLatitude(rs.getDouble("latitude"));
        event.setLongitude(rs.getDouble("longitude"));
        event.setLieu(rs.getString("lieu"));
        event.setIdSalle(rs.getInt("id_salle"));
        return event;
    }

    public List<Equipe> getEquipesForEvent(int eventId) throws SQLException {
        List<Equipe> equipes = new ArrayList<>();
        String req = "SELECT e.* FROM equipe e JOIN equipe_event ee ON e.id = ee.id_equipe WHERE ee.id_event = ?";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setInt(1, eventId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Equipe equipe = new Equipe();
                    equipe.setId(rs.getInt("id"));
                    equipe.setNom(rs.getString("nom"));
                    equipe.setImageUrl(rs.getString("image_url"));
                    equipe.setNiveau(EventType.Niveau.valueOf(rs.getString("niveau")));
                    equipe.setNombreMembres(rs.getInt("nombre_membres"));
                    equipes.add(equipe);
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Erreur lors de la récupération des équipes pour l'événement : " + e.getMessage(), e);
        }
        return equipes;
    }

    public Event recupererParId(int id) throws SQLException {
        String query = "SELECT * FROM events WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, id);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    Event event = mapResultSetToEvent(rs);
                    SalleService salleService = new SalleService();
                    Salle salle = salleService.getSalleById(event.getIdSalle());
                    event.setSalle(salle);
                    event.setEquipes(new HashSet<>(getEquipesForEvent(event.getId())));
                    return event;
                }
            }
        }
        return null;
    }

    public List<Event> getEventsBySalleId(int salleId) throws SQLException {
        List<Event> events = new ArrayList<>();
        String query = "SELECT * FROM events WHERE id_salle = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, salleId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Event event = mapResultSetToEvent(rs);
                    event.setEquipes(new HashSet<>(getEquipesForEvent(event.getId())));
                    events.add(event);
                }
            }
        }
        return events;
    }

    private boolean salleExists(int salleId) throws SQLException {
        if (salleId <= 0) {
            return false;
        }
        String query = "SELECT COUNT(*) FROM salle WHERE id_Salle = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, salleId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
}