package org.gymify.services;

import org.gymify.entities.Event;
import org.gymify.entities.EventType;
import org.gymify.utils.gymifyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EventService implements IServiceEvent<Event> {
    private final Connection connection;

    public EventService() {
        connection = gymifyDataBase.getInstance().getConnection();
    }

    @Override
    public void ajouter(Event event) throws SQLException {
        String req = "INSERT INTO events (nom, date, heure_debut, heure_fin, type, description, image_url, reward, latitude, longitude, lieu, id_salle) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            setEventStatement(ps, event);
            ps.executeUpdate();
        }
    }

    @Override
    public void modifier(Event event) throws SQLException {
        String req = "UPDATE events SET nom=?, date=?, heure_debut=?, heure_fin=?, type=?, description=?, image_url=?, reward=?, latitude=?, longitude=?, lieu=?, id_salle=? WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            setEventStatement(ps, event);
            ps.setInt(13, event.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String req = "DELETE FROM events WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public List<Event> afficher() throws SQLException {
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
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                events.add(mapResultSetToEvent(rs));
            }
        }
        return events;
    }

    public boolean eventNameExists(String nom) throws SQLException {
        String req = "SELECT COUNT(*) FROM events WHERE nom = ?";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setString(1, nom);
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
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
        event.setType(EventType.valueOf(rs.getString("type")));
        event.setDescription(rs.getString("description"));
        event.setImageUrl(rs.getString("image_url"));
        event.setReward(EventType.Reward.valueOf(rs.getString("reward")));
        event.setLatitude(rs.getDouble("latitude"));
        event.setLongitude(rs.getDouble("longitude"));
        event.setLieu(rs.getString("lieu"));
        event.setIdSalle(rs.getInt("id_salle"));
        return event;
    }
}
