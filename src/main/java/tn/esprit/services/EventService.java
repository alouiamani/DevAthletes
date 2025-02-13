package tn.esprit.services;

import tn.esprit.entities.Event;
import tn.esprit.utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EventService implements IService<Event> {
    private Connection connection;

    public EventService() {
        connection = MyDataBase.getInstance().getConnection();
    }

    @Override
    public void ajouter(Event event) throws SQLException {
        String req = "INSERT INTO events (nom, lieu, date, heure_debut, heure_fin, type, capacite, description, image_url, reward) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
            preparedStatement.setString(1, event.getNom());
            preparedStatement.setString(2, event.getLieu());
            preparedStatement.setDate(3, Date.valueOf(event.getDate()));
            preparedStatement.setTime(4, Time.valueOf(event.getHeureDebut()));
            preparedStatement.setTime(5, Time.valueOf(event.getHeureFin()));
            preparedStatement.setString(6, event.getType());
            preparedStatement.setInt(7, event.getCapacite());
            preparedStatement.setString(8, event.getDescription());
            preparedStatement.setString(9, event.getImageUrl());


            if (event.getReward() == null || event.getReward().trim().isEmpty()) {
                preparedStatement.setNull(10, Types.VARCHAR);
            } else {
                preparedStatement.setString(10, event.getReward());
            }

            preparedStatement.executeUpdate();
            System.out.println(" Événement ajouté avec succès !");
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de l'événement : " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void modifier(Event event) throws SQLException {
        String req = "UPDATE events SET nom=?, lieu=?, date=?, heure_debut=?, heure_fin=?, type=?, capacite=?, description=?, image_url=?, reward=? WHERE id=?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
            preparedStatement.setString(1, event.getNom());
            preparedStatement.setString(2, event.getLieu());
            preparedStatement.setDate(3, Date.valueOf(event.getDate()));
            preparedStatement.setTime(4, Time.valueOf(event.getHeureDebut()));
            preparedStatement.setTime(5, Time.valueOf(event.getHeureFin()));
            preparedStatement.setString(6, event.getType());
            preparedStatement.setInt(7, event.getCapacite());
            preparedStatement.setString(8, event.getDescription());
            preparedStatement.setString(9, event.getImageUrl());


            if (event.getReward() == null || event.getReward().trim().isEmpty()) {
                preparedStatement.setNull(10, Types.VARCHAR);
            } else {
                preparedStatement.setString(10, event.getReward());
            }

            preparedStatement.setInt(11, event.getId());
            preparedStatement.executeUpdate();
            System.out.println(" Événement modifié avec succès !");
        } catch (SQLException e) {
            System.err.println("Erreur lors de la modification de l'événement : " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String req = "DELETE FROM events WHERE id=?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            System.out.println(" Événement supprimé avec succès !");
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de l'événement : " + e.getMessage());
            throw e;
        }
    }

    @Override
    public List<Event> afficher() throws SQLException {
        List<Event> events = new ArrayList<>();
        String req = "SELECT * FROM events";

        try (Statement statement = connection.createStatement(); ResultSet rs = statement.executeQuery(req)) {
            while (rs.next()) {
                Event event = new Event();
                event.setId(rs.getInt("id"));
                event.setNom(rs.getString("nom"));
                event.setLieu(rs.getString("lieu"));
                event.setDate(rs.getDate("date").toLocalDate());
                event.setHeureDebut(rs.getTime("heure_debut").toLocalTime());
                event.setHeureFin(rs.getTime("heure_fin").toLocalTime());
                event.setType(rs.getString("type"));
                event.setCapacite(rs.getInt("capacite"));
                event.setDescription(rs.getString("description"));
                event.setImageUrl(rs.getString("image_url"));
                event.setReward(rs.getString("reward"));

                events.add(event);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'affichage des événements : " + e.getMessage());
            throw e;
        }

        return events;
    }

    // Méthode pour chercher un événement par son id
    public Event chercherParId(int id) throws SQLException {
        String req = "SELECT * FROM events WHERE id=?";
        Event event = null;

        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
            preparedStatement.setInt(1, id);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    event = new Event();
                    event.setId(rs.getInt("id"));
                    event.setNom(rs.getString("nom"));
                    event.setLieu(rs.getString("lieu"));
                    event.setDate(rs.getDate("date").toLocalDate());
                    event.setHeureDebut(rs.getTime("heure_debut").toLocalTime());
                    event.setHeureFin(rs.getTime("heure_fin").toLocalTime());
                    event.setType(rs.getString("type"));
                    event.setCapacite(rs.getInt("capacite"));
                    event.setDescription(rs.getString("description"));
                    event.setImageUrl(rs.getString("image_url"));
                    event.setReward(rs.getString("reward"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de l'événement : " + e.getMessage());
            throw e;
        }

        return event;
    }
}
