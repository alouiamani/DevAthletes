package org.gymify.services;

import org.gymify.utils.gymifyDataBase;
import org.gymify.entities.Equipe;
import org.gymify.entities.EquipeEvent;
import org.gymify.entities.Event;
import org.gymify.entities.EventType;
import org.gymify.utils.gymifyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Service class for managing the association between Equipe and Event entities in the database.
 */
public class EquipeEventService {
    private static final Logger logger = Logger.getLogger(EquipeEventService.class.getName());
    private final Connection connection;

    public EquipeEventService() {
        connection = gymifyDataBase.getInstance().getConnection();
    }

    /**
     * Adds an association between a team and an event.
     *
     * @param equipe the team to associate
     * @param event  the event to associate
     * @throws SQLException if a database error occurs
     */
    public void ajouter(Equipe equipe, Event event) throws SQLException {
        if (!equipeExiste(equipe.getId()) || !eventExiste(event.getId())) {
            throw new SQLException("⚠ L'équipe ou l'événement n'existe pas dans la base de données.");
        }

        // Vérifier si l'équipe est déjà inscrite à l'événement
        if (participationExiste(equipe.getId(), event.getId())) {
            System.out.println("⚠ Cette équipe participe déjà à cet événement !");
            return;
        }

        // Insérer l'association
        String req = "INSERT INTO equipe_event (id_equipe, id_event) VALUES (?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
            preparedStatement.setInt(1, equipe.getId());
            preparedStatement.setInt(2, event.getId());
            preparedStatement.executeUpdate();
            System.out.println("✅ Association ajoutée avec succès !");
        }
    }

    /**
     * Modifies an existing association between a team and an event.
     *
     * @param equipe the team to modify
     * @param event  the new event to associate
     * @throws SQLException if a database error occurs
     */
    public void modifier(Equipe equipe, Event event) throws SQLException {
        // Vérifier si l'équipe et l'événement existent
        if (!equipeExiste(equipe.getId()) || !eventExiste(event.getId())) {
            throw new SQLException("⚠ L'équipe ou l'événement n'existe pas dans la base de données.");
        }

        // Vérifier si l'équipe participe déjà à l'événement
        if (!participationExiste(equipe.getId(), event.getId())) {
            System.out.println("⚠ Cette équipe ne participe pas à cet événement, impossible de modifier.");
            return;
        }

        // Modifier l'association
        String req = "UPDATE equipe_event SET id_event = ? WHERE id_equipe = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
            preparedStatement.setInt(1, event.getId());
            preparedStatement.setInt(2, equipe.getId());
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("✅ Association modifiée avec succès !");
            } else {
                System.out.println("⚠ Aucun changement effectué.");
            }
        }
    }

    /**
     * Deletes an association between a team and an event.
     *
     * @param equipe the team to disassociate
     * @param event  the event to disassociate
     * @throws SQLException if a database error occurs
     */
    public void supprimer(Equipe equipe, Event event) throws SQLException {
        String req = "DELETE FROM equipe_event WHERE id_equipe = ? AND id_event = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
            preparedStatement.setInt(1, equipe.getId());
            preparedStatement.setInt(2, event.getId());
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✅ Association supprimée avec succès !");
            } else {
                System.out.println("⚠ Aucune association trouvée pour cette équipe et cet événement.");
            }
        }
    }

    /**
     * Retrieves all team-event associations.
     *
     * @return a list of EquipeEvent objects
     * @throws SQLException if a database error occurs
     */
    public List<EquipeEvent> afficher() throws SQLException {
        List<EquipeEvent> liste = new ArrayList<>();
        String req = "SELECT * FROM equipe_event";
        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(req)) {
            while (rs.next()) {
                int idEquipe = rs.getInt("id_equipe");
                int idEvent = rs.getInt("id_event");

                // Récupérer l'événement et ses champs associés
                String eventQuery = "SELECT * FROM events WHERE id = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(eventQuery)) {
                    preparedStatement.setInt(1, idEvent);
                    try (ResultSet eventRs = preparedStatement.executeQuery()) {
                        if (eventRs.next()) {
                            Event event = new Event();
                            event.setId(eventRs.getInt("id"));
                            event.setNom(eventRs.getString("nom"));
                            event.setDate(eventRs.getDate("date").toLocalDate());
                            event.setHeureDebut(eventRs.getTime("heure_debut").toLocalTime());
                            event.setHeureFin(eventRs.getTime("heure_fin").toLocalTime());
                            event.setType(EventType.valueOf(eventRs.getString("type")));
                            event.setDescription(eventRs.getString("description"));
                            event.setImageUrl(eventRs.getString("image_url"));
                            String rewardStr = eventRs.getString("reward");
                            if (rewardStr != null && !rewardStr.trim().isEmpty()) {
                                event.setReward(EventType.Reward.valueOf(rewardStr));
                            } else {
                                event.setReward(null);
                            }

                            // Créer l'objet EquipeEvent avec l'événement et l'équipe
                            EquipeEvent equipeEvent = new EquipeEvent(idEquipe, idEvent, event);
                            liste.add(equipeEvent);
                        }
                    }
                }
            }
        }
        return liste;
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
        }
        return equipes;
    }

    /**
     * Checks if a team exists in the database.
     *
     * @param idEquipe the ID of the team
     * @return true if the team exists, false otherwise
     * @throws SQLException if a database error occurs
     */
    private boolean equipeExiste(int idEquipe) throws SQLException {
        String req = "SELECT COUNT(*) FROM equipe WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
            preparedStatement.setInt(1, idEquipe);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next() && resultSet.getInt(1) > 0;
            }
        }
    }

    /**
     * Checks if an event exists in the database.
     *
     * @param idEvent the ID of the event
     * @return true if the event exists, false otherwise
     * @throws SQLException if a database error occurs
     */
    private boolean eventExiste(int idEvent) throws SQLException {
        String req = "SELECT COUNT(*) FROM events WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
            preparedStatement.setInt(1, idEvent);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next() && resultSet.getInt(1) > 0;
            }
        }
    }

    /**
     * Checks if a team is already associated with an event.
     *
     * @param idEquipe the ID of the team
     * @param idEvent  the ID of the event
     * @return true if the association exists, false otherwise
     * @throws SQLException if a database error occurs
     */
    private boolean participationExiste(int idEquipe, int idEvent) throws SQLException {
        String req = "SELECT COUNT(*) FROM equipe_event WHERE id_equipe = ? AND id_event = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
            preparedStatement.setInt(1, idEquipe);
            preparedStatement.setInt(2, idEvent);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next() && resultSet.getInt(1) > 0;
            }
        }
    }
}