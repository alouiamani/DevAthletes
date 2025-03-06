package org.gymify.services;

import org.gymify.services.IServiceEvent;
import org.gymify.entities.Equipe;
import org.gymify.entities.Event;
import org.gymify.entities.EventType;
import org.gymify.utils.gymifyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Service class for managing Event entities in the database.
 */
public class EventService implements IServiceEvent<Event> {
    private final Connection connection;

    public EventService() {
        connection = gymifyDataBase.getInstance().getConnection();
    }

    @Override
    public void ajouter(Event event) throws SQLException {
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
        } catch (SQLException e) {
            throw new SQLException("Erreur lors de l'ajout de l'événement : " + e.getMessage(), e);
        }
    }

    @Override
    public void modifier(Event event) throws SQLException {
        String req = "UPDATE events SET nom=?, date=?, heure_debut=?, heure_fin=?, type=?, description=?, image_url=?, reward=?, latitude=?, longitude=?, lieu=?, id_salle=? WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            setEventStatement(ps, event);
            ps.setInt(13, event.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Erreur lors de la modification de l'événement : " + e.getMessage(), e);
        }
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String req = "DELETE FROM events WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setInt(1, id);
            ps.executeUpdate();
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
        return recupererTous(""); // Delegate to recupererTous with an empty search query
    }

    /**
     * Retrieves all events matching the search query, including their associated teams.
     *
     * @param searchQuery the search query to filter events by name, location, or type
     * @return a list of events with their associated teams
     * @throws SQLException if a database error occurs
     */
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
                    // Convert List<Equipe> to Set<Equipe> to match the Event class's setEquipes method
                    event.setEquipes(new HashSet<>(getEquipesForEvent(event.getId())));
                    events.add(event);
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Erreur lors de la récupération des événements : " + e.getMessage(), e);
        }
        return events;
    }

    /**
     * Checks if an event with the given name already exists in the database.
     *
     * @param nom the name of the event to check
     * @return true if an event with the given name exists, false otherwise
     * @throws SQLException if a database error occurs
     */
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

    /**
     * Sets the PreparedStatement parameters for an Event object.
     *
     * @param ps the PreparedStatement to set parameters on
     * @param event the Event object to get data from
     * @throws SQLException if a database error occurs
     */
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

    /**
     * Maps a ResultSet row to an Event object.
     *
     * @param rs the ResultSet containing event data
     * @return an Event object populated with data from the ResultSet
     * @throws SQLException if a database error occurs
     */
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

    /**
     * Retrieves the list of teams associated with a given event.
     *
     * @param eventId the ID of the event
     * @return a list of Equipe objects associated with the event
     * @throws SQLException if a database error occurs
     */
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
                    Event event = new Event();
                    event.setId(rs.getInt("id"));
                    event.setNom(rs.getString("nom"));
                    event.setLieu(rs.getString("lieu"));
                    event.setDate(rs.getDate("date").toLocalDate());
                    event.setHeureDebut(rs.getTime("heure_debut").toLocalTime());
                    event.setHeureFin(rs.getTime("heure_fin").toLocalTime());
                    event.setType(EventType.valueOf(rs.getString("type")));
                    event.setDescription(rs.getString("description"));
                    event.setImageUrl(rs.getString("image_url"));
                    String rewardStr = rs.getString("reward");
                    if (rewardStr != null && !rewardStr.trim().isEmpty()) {
                        event.setReward(EventType.Reward.valueOf(rewardStr));
                    } else {
                        event.setReward(null);
                    }
                    return event;
                }
            }
        }
        return null;
    }
}