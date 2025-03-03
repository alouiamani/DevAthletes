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
    private static final Logger logger = Logger.getLogger(EquipeEventService.class.getName());
    private final Connection connection;

    public EquipeEventService() {
        connection = gymifyDataBase.getInstance().getConnection();
    }

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
        PreparedStatement preparedStatement = connection.prepareStatement(req);
        preparedStatement.setInt(1, equipe.getId());
        preparedStatement.setInt(2, event.getId());
        preparedStatement.executeUpdate();
        System.out.println("✅ Association ajoutée avec succès !");
    }

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
        PreparedStatement preparedStatement = connection.prepareStatement(req);
        preparedStatement.setInt(1, event.getId());
        preparedStatement.setInt(2, equipe.getId());
        int rowsAffected = preparedStatement.executeUpdate();

        if (rowsAffected > 0) {
            System.out.println("✅ Association modifiée avec succès !");
        } else {
            System.out.println("⚠ Aucun changement effectué.");
        }
    }

    public void supprimer(Equipe equipe, Event event) throws SQLException {
        String req = "DELETE FROM equipe_event WHERE id_equipe = ? AND id_event = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(req);
        preparedStatement.setInt(1, equipe.getId());
        preparedStatement.setInt(2, event.getId());
        int rowsAffected = preparedStatement.executeUpdate();
        if (rowsAffected > 0) {
            System.out.println("✅ Association supprimée avec succès !");
        } else {
            System.out.println("⚠ Aucune association trouvée pour cette équipe et cet événement.");
        }
    }

    public List<EquipeEvent> afficher() throws SQLException {
        List<EquipeEvent> liste = new ArrayList<>();
        String req = "SELECT * FROM equipe_event";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(req);
        while (rs.next()) {
            int idEquipe = rs.getInt("id_equipe");
            int idEvent = rs.getInt("id_event");

            // Récupérer l'événement et ses champs associés
            String eventQuery = "SELECT * FROM events WHERE id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(eventQuery);
            preparedStatement.setInt(1, idEvent);
            ResultSet eventRs = preparedStatement.executeQuery();

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
        return liste;
    }

    private boolean equipeExiste(int idEquipe) throws SQLException {
        String req = "SELECT COUNT(*) FROM equipe WHERE id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(req);
        preparedStatement.setInt(1, idEquipe);
        ResultSet resultSet = preparedStatement.executeQuery();
        return resultSet.next() && resultSet.getInt(1) > 0;
    }

    private boolean eventExiste(int idEvent) throws SQLException {
        String req = "SELECT COUNT(*) FROM events WHERE id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(req);
        preparedStatement.setInt(1, idEvent);
        ResultSet resultSet = preparedStatement.executeQuery();
        return resultSet.next() && resultSet.getInt(1) > 0;
    }

    private boolean participationExiste(int idEquipe, int idEvent) throws SQLException {
        String req = "SELECT COUNT(*) FROM equipe_event WHERE id_equipe = ? AND id_event = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(req);
        preparedStatement.setInt(1, idEquipe);
        preparedStatement.setInt(2, idEvent);
        ResultSet resultSet = preparedStatement.executeQuery();
        return resultSet.next() && resultSet.getInt(1) > 0;
    }
}
