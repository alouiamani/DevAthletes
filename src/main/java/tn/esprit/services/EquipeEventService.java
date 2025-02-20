package tn.esprit.services;

import tn.esprit.entities.Equipe;
import tn.esprit.entities.EquipeEvent;
import tn.esprit.entities.Event;
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


    private boolean equipeExiste(int idEquipe) throws SQLException {
        String req = "SELECT COUNT(*) FROM equipe WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
            preparedStatement.setInt(1, idEquipe);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        }
        return false;
    }


    private boolean eventExiste(int idEvent) throws SQLException {
        String req = "SELECT COUNT(*) FROM event WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
            preparedStatement.setInt(1, idEvent);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        }
        return false;
    }



    public void ajouter(Equipe equipe, Event event) throws SQLException {
        String req = "INSERT INTO equipe_event (id_equipe, id_event) VALUES (?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {

            int idEquipe = equipe.getId();
            int idEvent = event.getId();

            preparedStatement.setInt(1, idEquipe);
            preparedStatement.setInt(2, idEvent);
            preparedStatement.executeUpdate();
            System.out.println(" Association ajoutée avec succès !");
        } catch (SQLException e) {
            logger.severe(" Erreur lors de l'ajout de l'association : " + e.getMessage());
        }
    }

    public void modifier(Equipe equipe, Event event) throws SQLException {
        String req = "UPDATE equipe_event SET id_event = ? WHERE id_equipe = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
            preparedStatement.setInt(1, event.getId());
            preparedStatement.setInt(2, equipe.getId());
            int rowsUpdated = preparedStatement.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println(" Association mise à jour avec succès !");
            } else {
                System.out.println(" Aucun enregistrement mis à jour. Vérifiez si l'équipe existe.");
            }
        } catch (SQLException e) {
            System.err.println(" Erreur lors de la mise à jour de l'association : " + e.getMessage());
            e.printStackTrace();
        }
    }



    public void supprimer(Equipe equipe, Event event) throws SQLException {
        String req = "DELETE FROM equipe_event WHERE id_equipe = ? AND id_event = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
            preparedStatement.setInt(1, equipe.getId());
            preparedStatement.setInt(2, event.getId());
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println(" Association supprimée avec succès !");
            } else {
                System.out.println(" Aucune association trouvée pour cette équipe et cet événement.");
            }
        } catch (SQLException e) {
            logger.severe(" Erreur lors de la suppression de l'association : " + e.getMessage());
        }
    }


    public List<EquipeEvent> afficher() throws SQLException {
        List<EquipeEvent> liste = new ArrayList<>();
        String req = "SELECT * FROM equipe_event";

        try (Statement statement = connection.createStatement(); ResultSet rs = statement.executeQuery(req)) {
            while (rs.next()) {
                int idEquipe = rs.getInt("id_equipe");
                int idEvent = rs.getInt("id_event");
                liste.add(new EquipeEvent(idEquipe, idEvent));
            }
        } catch (SQLException e) {
            logger.severe(" Erreur lors de l'affichage des associations : " + e.getMessage());
        }
        return liste;
    }

}