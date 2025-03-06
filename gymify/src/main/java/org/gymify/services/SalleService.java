package org.gymify.services;

import org.gymify.entities.Salle;
import org.gymify.entities.User;
import org.gymify.utils.AuthToken;
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
    public int ajouter(Salle salle) throws SQLException {
        if (salle == null || salle.getNom() == null || salle.getAdresse() == null ) {
            throw new IllegalArgumentException("Tous les champs requis doivent être remplis !");
        }

<<<<<<< HEAD
        String req = "INSERT INTO salle (nom, adresse, details, num_tel, email,url_photo) VALUES (?, ?, ?, ?, ?, ?)";
=======
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
>>>>>>> aea46390f69b2bb6a9b587a90aaa9dc13d0bcff7

        try (PreparedStatement pstmt = connection.prepareStatement(req, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, salle.getNom());
            pstmt.setString(2, salle.getAdresse());
            pstmt.setString(3, salle.getDetails());
            pstmt.setString(4, salle.getNum_tel());
            pstmt.setString(5, salle.getEmail());
            pstmt.setString(6, salle.getUrl_photo());
<<<<<<< HEAD



            pstmt.executeUpdate();
            System.out.println("✅ Salle ajoutée avec succès !");
// L'ID du responsable de salle
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
        String req = "UPDATE salle SET nom=?, adresse=?, details=?, num_tel=?, email=?, url_photo=? WHERE id_Salle=?";

        // Vérifier la connexion
        if (connection == null) {
            System.out.println("❌ Erreur : connexion fermée !");
            return;
        }

        PreparedStatement preparedStatement = connection.prepareStatement(req);
        preparedStatement.setString(1, salle.getNom());
        preparedStatement.setString(2, salle.getAdresse());
        preparedStatement.setString(3, salle.getDetails());
        preparedStatement.setString(4, salle.getNum_tel());
        preparedStatement.setString(5, salle.getEmail());
        preparedStatement.setString(6, salle.getUrl_photo());
        preparedStatement.setInt(7, salle.getId_Salle());


        // Vérifier l'ID
        System.out.println("🔍 ID de la salle à modifier : " + salle.getId_Salle());

        int rowsUpdated = preparedStatement.executeUpdate(); // Appelé une seule fois
        if (rowsUpdated > 0) {
            System.out.println("✅ Modification réussie !");
        } else {
            System.out.println("⚠️ Aucune ligne mise à jour. ID incorrect ?");
        }
    }


    @Override
    public void supprimer(int idSalle) throws SQLException {
        // Supprimer d'abord les utilisateurs associés à la salle
        String deleteUsersQuery = "DELETE FROM user WHERE id_Salle = ?";
        try (PreparedStatement deleteUsersStatement = connection.prepareStatement(deleteUsersQuery)) {
            deleteUsersStatement.setInt(1, idSalle);
            deleteUsersStatement.executeUpdate();
            System.out.println("Utilisateurs associés à la salle supprimés avec succès !");
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression des utilisateurs associés : " + e.getMessage());
            throw e;
        }

        // Ensuite, supprimer la salle
        String deleteSalleQuery = "DELETE FROM salle WHERE id_Salle = ?";
        try (PreparedStatement deleteSalleStatement = connection.prepareStatement(deleteSalleQuery)) {
            deleteSalleStatement.setInt(1, idSalle);
            deleteSalleStatement.executeUpdate();
            System.out.println("Salle supprimée avec succès !");
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de la salle : " + e.getMessage());
            throw e;
=======
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
>>>>>>> aea46390f69b2bb6a9b587a90aaa9dc13d0bcff7
        }
    }

    @Override
    public List<Salle> afficher() throws SQLException {
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

<<<<<<< HEAD
        ResultSet rs = statement.executeQuery(req);
        while (rs.next()) {
            Salle salle = new Salle(rs.getInt("id_Salle"), rs.getString("nom"), rs.getString("adresse"), rs.getString("details"), rs.getString("num_tel"), rs.getString("email"), rs.getString("url_photo"));
            salle.setId_Salle(rs.getInt("id_Salle"));
            salle.setNom(rs.getString("nom"));
            salle.setAdresse(rs.getString("adresse"));
            salle.setDetails(rs.getString("details"));
            salle.setNum_tel(rs.getString("num_tel"));
            salle.setEmail(rs.getString("email"));
            salle.setUrl_photo(rs.getString("url_photo"));

=======
                EventService eventService = new EventService();
                salle.setEvents(eventService.getEventsBySalleId(salle.getId_Salle()));
>>>>>>> aea46390f69b2bb6a9b587a90aaa9dc13d0bcff7

                salles.add(salle);
            }
        }
        return salles;
    }

<<<<<<< HEAD
    public List<Salle> getAllSalles(String search) {
=======
    public List<Salle> getAllSalles(String search) throws SQLException {
>>>>>>> aea46390f69b2bb6a9b587a90aaa9dc13d0bcff7
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

<<<<<<< HEAD
            while (rs.next()) {
                int id = rs.getInt("id_Salle");
                String nom = rs.getString("nom");
                String adresse = rs.getString("adresse");
                String details = rs.getString("details");
                String numTel = rs.getString("num_tel");
                String email = rs.getString("email");

                String urlPhoto = rs.getString("url_photo");

                System.out.println("✅ Salle récupérée - ID: " + id + ", Nom: " + nom + ", Adresse: " + adresse);

                Salle salle = new Salle(id, nom, adresse, details, numTel, email, urlPhoto);
                salles.add(salle);
=======
                    Salle salle = new Salle(id, nom, adresse, details, numTel, email, urlPhoto);
                    salle.setIdResponsable(responsableId);
                    EventService eventService = new EventService();
                    salle.setEvents(eventService.getEventsBySalleId(id));
                    salles.add(salle);
                }
>>>>>>> aea46390f69b2bb6a9b587a90aaa9dc13d0bcff7
            }
        }
        return salles;
    }

    public List<Salle> searchSalles(String searchText) throws SQLException {
        return getAllSalles(searchText);
    }

<<<<<<< HEAD


    public boolean salleExiste(int salleId) throws SQLException {
        String query = "SELECT COUNT(*) FROM Salle WHERE id_salle = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, salleId);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    public Salle getSalleById(int salleId) throws SQLException {
        String query = "SELECT * FROM Salle WHERE id_salle = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, salleId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Salle(
                        rs.getInt("id_Salle"),
                        rs.getString("nom"),
                        rs.getString("adresse"),
                        rs.getString("détails"),
                        rs.getString("num_tel"),
                        rs.getString("email"),
                        rs.getString("url_photo")

                 );
            }
        }
        return null;
    }

    public boolean isResponsableDejaAffecte(int idResponsable) throws SQLException {
        String query = "SELECT COUNT(*) FROM user WHERE id_User = ? AND id_Salle IS NOT NULL";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, idResponsable);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
=======
    public boolean isResponsableDejaAffecte(int responsableId, int i) throws SQLException {
        String query = "SELECT COUNT(*) FROM salle WHERE responsableId = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, responsableId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
>>>>>>> aea46390f69b2bb6a9b587a90aaa9dc13d0bcff7
            }
        }
        return false;
    }

<<<<<<< HEAD
    public void dissocierUtilisateursSalle(int idSalle) throws SQLException {
        String req = "UPDATE user SET id_Salle = NULL WHERE id_Salle = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(req)) {
            pstmt.setInt(1, idSalle);
            pstmt.executeUpdate();
            System.out.println("✅ Utilisateurs dissociés de la salle !");
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la dissociation des utilisateurs : " + e.getMessage());
            throw e;
        }
    }
    // Retourne null si aucune salle n'est trouvée

    public int getIdSalleForCurrentUser() {
        User currentUser = AuthToken.getCurrentUser();  // Récupère l'utilisateur connecté
        if (currentUser == null) {
            System.out.println("Aucun utilisateur connecté !");
            return -1;  // Utilisateur non connecté
        }

        int idSalle = currentUser.getId_Salle();  // Récupère l'ID de la salle
        if (idSalle == 0) {
            System.out.println("L'utilisateur n'a pas de salle associée !");
        } else {
            System.out.println("L'ID de la salle de l'utilisateur connecté est : " + idSalle);
        }

        return idSalle;
    }
    public Salle getSallesById(int idSalle) {
        if (idSalle <= 0) {
            System.out.println("ID Salle invalide : " + idSalle);
            return null;
        }

        // Logique pour récupérer la salle depuis la base de données (exemple)
        Salle salle = findSalleById(idSalle);  // Trouver la salle par son ID dans la base
        if (salle == null) {
            System.out.println("Aucune salle trouvée pour l'ID : " + idSalle);
        } else {
            System.out.println("Salle trouvée : " + salle.getNom());
        }
        return salle;
    }

    // Simulation de la méthode qui cherche la salle dans la base de données
    public Salle findSalleById(int idSalle) {
        // Code pour interroger la base de données et récupérer la salle
        // Retourne un objet Salle si trouvé, sinon retourne null
        return new Salle(idSalle, "Salle Exemple");
    }
}
=======
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
>>>>>>> aea46390f69b2bb6a9b587a90aaa9dc13d0bcff7

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