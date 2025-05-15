package org.gymify.services;

import org.gymify.entities.Salle;
import org.gymify.entities.User;
import org.gymify.utils.AuthToken;
import org.gymify.utils.gymifyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SalleService implements Iservices<Salle> {
    private Connection connection;

    public SalleService() {
        connection = gymifyDataBase.getInstance().getConnection();
    }

    @Override
    public int ajouter(Salle salle) throws SQLException {
        if (salle == null || salle.getNom() == null || salle.getAdresse() == null || salle.getUrl_photo() == null) {
            throw new IllegalArgumentException("Tous les champs requis doivent être remplis !");
        }

        if (!isValidImagePath(salle.getUrl_photo())) {
            throw new IllegalArgumentException("URL photo invalide : " + salle.getUrl_photo());
        }

        String req = "INSERT INTO salle (nom, adresse, details, num_tel, email, url_photo, responsable_id) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(req, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, salle.getNom());
            pstmt.setString(2, salle.getAdresse());
            pstmt.setString(3, salle.getDetails());
            pstmt.setString(4, salle.getNum_tel());
            pstmt.setString(5, salle.getEmail());
            pstmt.setString(6, salle.getUrl_photo());
            pstmt.setInt(7, salle.getResponsable_id());

            pstmt.executeUpdate();
            System.out.println("✅ Salle ajoutée avec succès !");

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int idSalle = generatedKeys.getInt(1);
                    System.out.println("✅ Salle ajoutée avec succès. ID de la salle : " + idSalle);
                    return idSalle;
                } else {
                    throw new SQLException("Échec de la récupération de l'ID de la salle.");
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'ajout de la salle : " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void modifier(Salle salle) throws SQLException {
        if (salle == null || salle.getNom() == null || salle.getAdresse() == null || salle.getUrl_photo() == null) {
            throw new IllegalArgumentException("Tous les champs requis doivent être remplis !");
        }

        if (!isValidImagePath(salle.getUrl_photo())) {
            throw new IllegalArgumentException("URL photo invalide : " + salle.getUrl_photo());
        }

        String req = "UPDATE salle SET nom=?, adresse=?, details=?, num_tel=?, email=?, url_photo=?, responsable_id=? WHERE id=?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
            preparedStatement.setString(1, salle.getNom());
            preparedStatement.setString(2, salle.getAdresse());
            preparedStatement.setString(3, salle.getDetails());
            preparedStatement.setString(4, salle.getNum_tel());
            preparedStatement.setString(5, salle.getEmail());
            preparedStatement.setString(6, salle.getUrl_photo());
            preparedStatement.setInt(7, salle.getResponsable_id());
            preparedStatement.setInt(8, salle.getId());

            System.out.println("🔍 ID de la salle à modifier : " + salle.getId());

            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("✅ Modification réussie !");
            } else {
                System.out.println("⚠️ Aucune ligne mise à jour. ID incorrect ?");
            }
        }
    }

    @Override
    public void supprimer(int idSalle) throws SQLException {
        String deleteSalleQuery = "DELETE FROM salle WHERE id = ?";
        try (PreparedStatement deleteSalleStatement = connection.prepareStatement(deleteSalleQuery)) {
            deleteSalleStatement.setInt(1, idSalle);
            deleteSalleStatement.executeUpdate();
            System.out.println("Salle supprimée avec succès !");
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de la salle : " + e.getMessage());
            throw e;
        }
    }

    @Override
    public List<Salle> afficher() throws SQLException {
        List<Salle> salles = new ArrayList<>();
        String req = "SELECT * FROM salle";
        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(req)) {
            while (rs.next()) {
                Salle salle = new Salle(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("adresse"),
                        rs.getString("details"),
                        rs.getString("num_tel"),
                        rs.getString("email"),
                        rs.getString("url_photo"),
                        rs.getInt("responsable_id")
                );
                salles.add(salle);
            }
        }
        return salles;
    }

    public List<Salle> getAllSalles(String search) {
        List<Salle> salles = new ArrayList<>();
        String query = "SELECT * FROM salle WHERE nom LIKE ? OR adresse LIKE ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, "%" + search + "%");
            stmt.setString(2, "%" + search + "%");

            System.out.println("🔍 Exécution de la requête: " + stmt.toString());

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Salle salle = new Salle(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("adresse"),
                        rs.getString("details"),
                        rs.getString("num_tel"),
                        rs.getString("email"),
                        rs.getString("url_photo"),
                        rs.getInt("responsable_id")
                );
                salles.add(salle);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return salles;
    }

    public List<Salle> searchSalles(String searchText) {
        List<Salle> allSalles = getAllSalles(searchText);
        return allSalles.stream()
                .filter(salle -> salle.getNom().toLowerCase().contains(searchText.toLowerCase()) ||
                        salle.getAdresse().toLowerCase().contains(searchText.toLowerCase()))
                .collect(Collectors.toList());
    }

    public boolean salleExiste(int salleId) throws SQLException {
        String query = "SELECT COUNT(*) FROM salle WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, salleId);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    public Salle getSalleById(int salleId) throws SQLException {
        String query = "SELECT * FROM salle WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, salleId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Salle(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("adresse"),
                        rs.getString("details"),
                        rs.getString("num_tel"),
                        rs.getString("email"),
                        rs.getString("url_photo"),
                        rs.getInt("responsable_id")
                );
            }
        }
        return null;
    }

    public Salle findSalleById(int idSalle) {
        try {
            return getSalleById(idSalle);
        } catch (SQLException e) {
            e.printStackTrace();
            return new Salle(idSalle, "Salle Exemple");
        }
    }

    private boolean isValidImagePath(String urlPhoto) {
        if (urlPhoto == null || urlPhoto.trim().isEmpty()) {
            return false;
        }
        if (urlPhoto.startsWith("/images/")) {
            return getClass().getResource(urlPhoto) != null;
        }
        try {
            new java.net.URL(urlPhoto).toURI();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    public Salle getSalleForCurrentResponsable() throws SQLException {
        User currentUser = AuthToken.getCurrentUser();
        if (currentUser == null) {
            System.out.println("Aucun utilisateur connecté !");
            return null;
        }

        int responsableId = currentUser.getId();
        String query = "SELECT id FROM user WHERE id_User = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, responsableId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int salleId = rs.getInt("id");
                return getSalleById(salleId);
            } else {
                System.out.println("Aucune salle associée à ce responsable !");
                return null;
            }
        }
    }

    public Salle getSalleByResponsableId(int responsableId) throws SQLException {
        String query = "SELECT * FROM salle WHERE responsable_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, responsableId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Salle(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("adresse"),
                        rs.getString("details"),
                        rs.getString("num_tel"),
                        rs.getString("email"),
                        rs.getString("url_photo"),
                        rs.getInt("responsable_id")
                );
            }
        }
        return null;
    }

}