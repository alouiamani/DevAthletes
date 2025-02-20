package tn.esprit.services;

import tn.esprit.entities.Event;
import tn.esprit.entities.EventType;
import tn.esprit.utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EventService implements IService<Event> {
    private final Connection connection;

    public EventService() {
        connection = MyDataBase.getInstance().getConnection();
    }

    @Override
    public void ajouter(Event event) throws SQLException {
        String req = "INSERT INTO events (nom, lieu, date, heure_debut, heure_fin, type, description, image_url, reward) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
            preparedStatement.setString(1, event.getNom());
            preparedStatement.setString(2, event.getLieu());
            preparedStatement.setDate(3, Date.valueOf(event.getDate()));
            preparedStatement.setTime(4, Time.valueOf(event.getHeureDebut()));
            preparedStatement.setTime(5, Time.valueOf(event.getHeureFin()));
            preparedStatement.setString(6, event.getType().toString());  // Convert enum to String
            preparedStatement.setString(7, event.getDescription());
            preparedStatement.setString(8, event.getImageUrl());

            if (event.getReward() == null || event.getReward().trim().isEmpty()) {
                preparedStatement.setNull(9, Types.VARCHAR);  // Si reward est null ou vide
            } else {
                preparedStatement.setString(9, event.getReward());
            }

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error while adding event: " + e.getMessage());
            throw e;  // Rethrow to propagate exception
        }
    }

    @Override
    public void modifier(Event event) throws SQLException {
        String req = "UPDATE events SET nom=?, lieu=?, date=?, heure_debut=?, heure_fin=?, type=?, description=?, image_url=?, reward=? WHERE id=?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
            preparedStatement.setString(1, event.getNom());
            preparedStatement.setString(2, event.getLieu());
            preparedStatement.setDate(3, Date.valueOf(event.getDate()));
            preparedStatement.setTime(4, Time.valueOf(event.getHeureDebut()));
            preparedStatement.setTime(5, Time.valueOf(event.getHeureFin()));
            preparedStatement.setString(6, event.getType().toString());  // Convert enum to String
            preparedStatement.setString(7, event.getDescription());
            preparedStatement.setString(8, event.getImageUrl());

            if (event.getReward() == null || event.getReward().trim().isEmpty()) {
                preparedStatement.setNull(9, Types.VARCHAR);  // Si reward est null ou vide
            } else {
                preparedStatement.setString(9, event.getReward());
            }

            preparedStatement.setInt(10, event.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error while updating event: " + e.getMessage());
            throw e;  // Rethrow to propagate exception
        }
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String req = "DELETE FROM events WHERE id=?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
            preparedStatement.setInt(1, id);  // Utilisation de l'ID pour la suppression
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error while deleting event with id " + id + ": " + e.getMessage());
            throw e;  // Rethrow to propagate exception
        }
    }

    @Override
    public List<Event> afficher() throws SQLException {
        List<Event> events = new ArrayList<>();
        String req = "SELECT * FROM events";
        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(req)) {

            while (rs.next()) {
                Event event = new Event();
                event.setId(rs.getInt("id"));
                event.setNom(rs.getString("nom"));
                event.setLieu(rs.getString("lieu"));
                event.setDate(rs.getDate("date").toLocalDate());
                event.setHeureDebut(rs.getTime("heure_debut").toLocalTime());
                event.setHeureFin(rs.getTime("heure_fin").toLocalTime());

                // Convert String back to enum, with handling in case of invalid values
                try {
                    String typeStr = rs.getString("type");
                    if (typeStr != null && !typeStr.trim().isEmpty()) {
                        event.setType(EventType.valueOf(typeStr));  // Convert String to enum safely
                    }
                } catch (IllegalArgumentException e) {
                    System.err.println("Invalid event type in database: " + rs.getString("type"));
                    event.setType(null);  // Or you can choose to set a default value
                }

                event.setDescription(rs.getString("description"));
                event.setImageUrl(rs.getString("image_url"));
                event.setReward(rs.getString("reward"));
                events.add(event);
            }
        } catch (SQLException e) {
            System.err.println("Error while fetching events: " + e.getMessage());
            throw e;  // Rethrow to propagate exception
        }
        return events;
    }


    public List<Event> getAllEvents() throws SQLException {
        List<Event> events = new ArrayList<>();
        String query = "SELECT * FROM events"; // Assurez-vous que cette requête correspond à votre schéma de base de données.
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);

        while (resultSet.next()) {
            Event event = new Event(
                    resultSet.getInt("id"),
                    resultSet.getString("nom"),
                    resultSet.getString("lieu"),
                    resultSet.getDate("date").toLocalDate(),
                    resultSet.getTime("heure_debut").toLocalTime(),
                    resultSet.getTime("heure_fin").toLocalTime(),
                    EventType.valueOf(resultSet.getString("type")),
                    resultSet.getString("description"),
                    resultSet.getString("image_url"),
                    resultSet.getString("reward")
            );
            events.add(event);
        }
        return events;
    }

}
