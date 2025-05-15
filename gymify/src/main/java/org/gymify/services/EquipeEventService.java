package org.gymify.services;

import org.gymify.entities.Equipe;
import org.gymify.entities.EquipeEvent;
import org.gymify.entities.Event;
import org.gymify.entities.EventType;
import org.gymify.utils.gymifyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class EquipeEventService {
    private static final Logger LOGGER = Logger.getLogger(EquipeEventService.class.getName());
    private final Connection connection;

    public EquipeEventService() {
        connection = gymifyDataBase.getInstance().getConnection();
    }

    public void ajouter(Equipe equipe, Event event) throws SQLException {
        if (!equipeExiste(equipe.getId()) || !eventExiste(event.getId())) {
            throw new SQLException("L'équipe ou l'événement n'existe pas.");
        }
        if (participationExiste(equipe.getId(), event.getId())) {
            LOGGER.info("Cette équipe participe déjà à cet événement.");
            return;
        }
        String req = "INSERT INTO equipe_event (equipe_id, event_id) VALUES (?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
            preparedStatement.setInt(1, equipe.getId());
            preparedStatement.setInt(2, event.getId());
            preparedStatement.executeUpdate();
        }
    }

    public void modifier(Equipe equipe, Event event) throws SQLException {
        if (!equipeExiste(equipe.getId()) || !eventExiste(event.getId())) {
            throw new SQLException("L'équipe ou l'événement n'existe pas.");
        }
        if (!participationExiste(equipe.getId(), event.getId())) {
            LOGGER.info("Cette équipe ne participe pas à cet événement.");
            return;
        }
        String req = "UPDATE equipe_event SET event_id = ? WHERE equipe_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
            preparedStatement.setInt(1, event.getId());
            preparedStatement.setInt(2, equipe.getId());
            preparedStatement.executeUpdate();
        }
    }

    public void supprimer(Equipe equipe, Event event) throws SQLException {
        String req = "DELETE FROM equipe_event WHERE equipe_id = ? AND event_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
            preparedStatement.setInt(1, equipe.getId());
            preparedStatement.setInt(2, event.getId());
            preparedStatement.executeUpdate();
        }
    }

    public List<EquipeEvent> afficher() throws SQLException {
        List<EquipeEvent> liste = new ArrayList<>();
        String req = "SELECT * FROM equipe_event";
        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(req)) {
            while (rs.next()) {
                int idEquipe = rs.getInt("equipe_id");
                int idEvent = rs.getInt("event_id");
                String eventQuery = "SELECT * FROM events WHERE id = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(eventQuery)) {
                    preparedStatement.setInt(1, idEvent);
                    try (ResultSet eventRs = preparedStatement.executeQuery()) {
                        if (eventRs.next()) {
                            Event event = new Event();
                            event.setId(eventRs.getInt("id"));
                            event.setNom(eventRs.getString("nom"));
                            event.setDate(eventRs.getDate("date") != null ? eventRs.getDate("date").toLocalDate() : null);
                            event.setHeureDebut(eventRs.getTime("heure_debut") != null ? eventRs.getTime("heure_debut").toLocalTime() : null);
                            event.setHeureFin(eventRs.getTime("heure_fin") != null ? eventRs.getTime("heure_fin").toLocalTime() : null);
                            String typeStr = eventRs.getString("type");
                            event.setType(typeStr != null && !typeStr.trim().isEmpty() ? EventType.valueOf(typeStr) : null);
                            event.setDescription(eventRs.getString("description"));
                            event.setImageUrl(eventRs.getString("image_url"));
                            String rewardStr = eventRs.getString("reward");
                            event.setReward(rewardStr != null && !rewardStr.trim().isEmpty() ? EventType.Reward.valueOf(rewardStr) : null);
                            EquipeEvent equipeEvent = new EquipeEvent(idEquipe, idEvent, event);
                            liste.add(equipeEvent);
                        }
                    }
                }
            }
        }
        LOGGER.info("Retrieved " + liste.size() + " equipe-event associations");
        return liste;
    }

    public List<Equipe> getEquipesForEvent(int eventId) throws SQLException {
        List<Equipe> equipes = new ArrayList<>();
        String req = "SELECT e.* FROM equipe e JOIN equipe_event ee ON e.id = ee.equipe_id WHERE ee.event_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setInt(1, eventId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Equipe equipe = new Equipe();
                    equipe.setId(rs.getInt("id"));
                    equipe.setNom(rs.getString("nom"));
                    equipe.setImageUrl(rs.getString("image_url"));
                    String niveauStr = rs.getString("niveau");
                    try {
                        equipe.setNiveau(EventType.Niveau.valueOf(niveauStr));
                    } catch (IllegalArgumentException e) {
                        LOGGER.warning("Niveau invalide : " + niveauStr);
                        equipe.setNiveau(null);
                    }
                    equipe.setNombreMembres(rs.getInt("nombre_membres"));
                    equipes.add(equipe);
                }
            }
        }
        LOGGER.info("Retrieved " + equipes.size() + " equipes for event ID: " + eventId);
        return equipes;
    }

    private boolean equipeExiste(int idEquipe) throws SQLException {
        String req = "SELECT COUNT(*) FROM equipe WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
            preparedStatement.setInt(1, idEquipe);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next() && resultSet.getInt(1) > 0;
            }
        }
    }

    private boolean eventExiste(int idEvent) throws SQLException {
        String req = "SELECT COUNT(*) FROM events WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
            preparedStatement.setInt(1, idEvent);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next() && resultSet.getInt(1) > 0;
            }
        }
    }

    private boolean participationExiste(int idEquipe, int idEvent) throws SQLException {
        String req = "SELECT COUNT(*) FROM equipe_event WHERE equipe_id = ? AND event_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
            preparedStatement.setInt(1, idEquipe);
            preparedStatement.setInt(2, idEvent);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next() && resultSet.getInt(1) > 0;
            }
        }
    }
}