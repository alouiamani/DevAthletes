package org.gymify.services;

import org.gymify.entities.Salle;
import org.gymify.utils.gymifyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SalleService implements Iservices<Salle> {
    private final Connection connection;

    public SalleService() {
        connection = gymifyDataBase.getInstance().getConnection();
    }

    @Override
    public void ajouter(Salle salle) throws SQLException {
        if (salle == null || salle.getNom() == null || salle.getAdresse() == null || salle.getIdResponsable() == 0) {
            throw new IllegalArgumentException("Tous les champs requis doivent être remplis !");
        }

        String req = "INSERT INTO salle (nom, adresse, details, num_tel, email, url_photo, responsableId) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(req, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, salle.getNom());
            pstmt.setString(2, salle.getAdresse());
            pstmt.setString(3, salle.getDetails());
            pstmt.setString(4, salle.getNum_tel());
            pstmt.setString(5, salle.getEmail());
            pstmt.setString(6, salle.getUrl_photo());
            pstmt.setInt(7, salle.getIdResponsable());

            pstmt.executeUpdate();
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    salle.setId_Salle(rs.getInt(1));
                }
            }
            // Update the user's id_Salle
            updateUserSalle(salle.getIdResponsable(), salle.getId_Salle());
            System.out.println("✅ Salle ajoutée avec succès !");
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'ajout de la salle : " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void modifier(Salle salle) throws SQLException {
        String req = "UPDATE salle SET nom=?, adresse=?, details=?, num_tel=?, email=?, url_photo=?, responsableId=? WHERE id_Salle=?";
        if (connection == null) {
            throw new SQLException("Erreur : connexion fermée !");
        }

        try (PreparedStatement pstmt = connection.prepareStatement(req)) {
            pstmt.setString(1, salle.getNom());
            pstmt.setString(2, salle.getAdresse());
            pstmt.setString(3, salle.getDetails());
            pstmt.setString(4, salle.getNum_tel());
            pstmt.setString(5, salle.getEmail());
            pstmt.setString(6, salle.getUrl_photo());
            pstmt.setInt(7, salle.getIdResponsable());
            pstmt.setInt(8, salle.getId_Salle());

            System.out.println("🔍 ID de la salle à modifier : " + salle.getId_Salle());
            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                // Update the user's id_Salle
                updateUserSalle(salle.getIdResponsable(), salle.getId_Salle());
                System.out.println("✅ Modification réussie !");
            } else {
                System.out.println("⚠️ Aucune ligne mise à jour. ID incorrect ?");
            }
        }
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String req = "DELETE FROM salle WHERE id_Salle=?";
        try (PreparedStatement pstmt = connection.prepareStatement(req)) {
            pstmt.setInt(1, id);
            int rowsDeleted = pstmt.executeUpdate();
            if (rowsDeleted > 0) {
                // Update the user's id_Salle to 0
                updateUserSalleForDeletion(id);
                System.out.println("✅ Salle supprimée avec succès ! (Les événements associés ont également été supprimés en raison de la suppression en cascade)");
            } else {
                System.out.println("⚠️ Aucune salle trouvée avec l'ID : " + id);
            }
        }
    }

    @Override
    public  List<Salle> afficher() throws SQLException {
        List<Salle> salles = new ArrayList<>();
        String req = "SELECT * FROM salle";
        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(req)) {
            while (rs.next()) {
                Salle salle = new Salle();
                salle.setId_Salle(rs.getInt("id_Salle"));
                salle.setNom(rs.getString("nom"));
                salle.setAdresse(rs.getString("adresse"));
                salle.setDetails(rs.getString("details"));
                salle.setNum_tel(rs.getString("num_tel"));
                salle.setEmail(rs.getString("email"));
                salle.setUrl_photo(rs.getString("url_photo"));
                salle.setIdResponsable(rs.getInt("responsableId"));

                EventService eventService = new EventService();
                salle.setEvents(eventService.getEventsBySalleId(salle.getId_Salle()));

                salles.add(salle);
            }
        }
        return salles;
    }

    public List<Salle> getAllSalles(String search) throws SQLException {
        List<Salle> salles = new ArrayList<>();
        String query = "SELECT * FROM salle WHERE nom LIKE ? OR adresse LIKE ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, "%" + search + "%");
            stmt.setString(2, "%" + search + "%");

            System.out.println("🔍 Exécution de la requête: " + stmt.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id_salle");
                    String nom = rs.getString("nom");
                    String adresse = rs.getString("adresse");
                    String details = rs.getString("details");
                    String numTel = rs.getString("num_tel");
                    String email = rs.getString("email");
                    String urlPhoto = rs.getString("url_photo");
                    int responsableId = rs.getInt("responsableId");

                    System.out.println("✅ Salle récupérée - ID: " + id + ", Nom: " + nom + ", Adresse: " + adresse);

                    Salle salle = new Salle(id, nom, adresse, details, numTel, email, urlPhoto);
                    salle.setIdResponsable(responsableId);
                    EventService eventService = new EventService();
                    salle.setEvents(eventService.getEventsBySalleId(id));
                    salles.add(salle);
                }
            }
        }
        return salles;
    }

    public List<Salle> searchSalles(String searchText) throws SQLException {
        return getAllSalles(searchText);
    }

    public boolean isResponsableDejaAffecte(int responsableId, int i) throws SQLException {
        String query = "SELECT COUNT(*) FROM salle WHERE responsableId = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, responsableId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public Salle getSalleById(int id) throws SQLException {
        String query = "SELECT * FROM salle WHERE id_Salle = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Salle salle = new Salle();
                    salle.setId_Salle(rs.getInt("id_Salle"));
                    salle.setNom(rs.getString("nom"));
                    salle.setAdresse(rs.getString("adresse"));
                    salle.setDetails(rs.getString("details"));
                    salle.setNum_tel(rs.getString("num_tel"));
                    salle.setEmail(rs.getString("email"));
                    salle.setUrl_photo(rs.getString("url_photo"));
                    salle.setIdResponsable(rs.getInt("responsableId"));

                    EventService eventService = new EventService();
                    salle.setEvents(eventService.getEventsBySalleId(id));
                    return salle;
                }
            }
        }
        return null;
    }

    public Salle getSalleByResponsableId(int responsableId) throws SQLException {
        String query = "SELECT * FROM salle WHERE responsableId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, responsableId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Salle salle = new Salle();
                    salle.setId_Salle(rs.getInt("id_Salle"));
                    salle.setNom(rs.getString("nom"));
                    salle.setAdresse(rs.getString("adresse"));
                    salle.setDetails(rs.getString("details"));
                    salle.setNum_tel(rs.getString("num_tel"));
                    salle.setEmail(rs.getString("email"));
                    salle.setUrl_photo(rs.getString("url_photo"));
                    salle.setIdResponsable(rs.getInt("responsableId"));

                    EventService eventService = new EventService();
                    salle.setEvents(eventService.getEventsBySalleId(salle.getId_Salle()));
                    return salle;
                }
            }
        }
        return null;
    }

    private void updateUserSalle(int responsableId, int idSalle) throws SQLException {
        String query = "UPDATE user SET id_Salle = ? WHERE id_User = ? AND role = 'responsable_salle'";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, idSalle);
            pstmt.setInt(2, responsableId);
            pstmt.executeUpdate();
        }
    }

    private void updateUserSalleForDeletion(int idSalle) throws SQLException {
        String query = "UPDATE user SET id_Salle = 0 WHERE id_Salle = ? AND role = 'responsable_salle'";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, idSalle);
            pstmt.executeUpdate();
        }
    }

    // New method to check if a salle exists
    public boolean salleExiste(int salleId) throws SQLException {
        Salle salle = getSalleById(salleId);
        return salle != null;
    }
}