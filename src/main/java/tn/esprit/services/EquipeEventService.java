package tn.esprit.services;

import tn.esprit.entities.Equipe;
import tn.esprit.entities.EquipeEvent;
import tn.esprit.entities.Event;
import tn.esprit.entities.EventType;
import tn.esprit.utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class EquipeEventService {
    private static final Logger logger = Logger.getLogger(EquipeEventService.class.getName());
    private final Connection connection;

    public EquipeEventService() {
        connection = MyDataBase.getInstance().getConnection();
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

            // Fetch the event and its associated fields (reward and type)
            String eventQuery = "SELECT * FROM events WHERE id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(eventQuery);
            preparedStatement.setInt(1, idEvent);
            ResultSet eventRs = preparedStatement.executeQuery();

            if (eventRs.next()) {
                Event event = new Event();
                event.setId(eventRs.getInt("id"));
                event.setNom(eventRs.getString("nom"));
                event.setLieu(eventRs.getString("lieu"));
                event.setDate(eventRs.getDate("date").toLocalDate());
                event.setHeureDebut(eventRs.getTime("heure_debut").toLocalTime());
                event.setHeureFin(eventRs.getTime("heure_fin").toLocalTime());
                event.setType(EventType.valueOf(eventRs.getString("type")));  // Set the event type (enum)
                event.setDescription(eventRs.getString("description"));
                event.setImageUrl(eventRs.getString("image_url"));
                event.setReward(eventRs.getString("reward"));

                // Create the EquipeEvent object with the event and team
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
