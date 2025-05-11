package org.gymify.services;

import org.gymify.entities.Salle;
import org.gymify.entities.User;
import org.gymify.utils.AuthToken;
import org.gymify.utils.gymifyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public  class SalleService implements Iservices<Salle> {
    Connection connection;

    public SalleService() {
        connection = gymifyDataBase.getInstance().getConnection();

    }

    @Override
    public int ajouter(Salle salle) throws SQLException {
        if (salle == null || salle.getNom() == null || salle.getAdresse() == null ) {
            throw new IllegalArgumentException("Tous les champs requis doivent être remplis !");
        }

        String req = "INSERT INTO salle (nom, adresse, details, num_tel, email,url_photo) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(req, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, salle.getNom());
            pstmt.setString(2, salle.getAdresse());
            pstmt.setString(3, salle.getDetails());
            pstmt.setString(4, salle.getNum_tel());
            pstmt.setString(5, salle.getEmail());
            pstmt.setString(6, salle.getUrl_photo());



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
        }
    }


    @Override
    public List<Salle> afficher() throws SQLException {
        List<Salle> salles = new ArrayList<>();
        String req = "SELECT * FROM salle";
        Statement statement = connection.createStatement();

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


            salles.add(salle);
        }


        return salles;
    }

    public List<Salle> getAllSalles(String search) {
        List<Salle> salles = new ArrayList<>();
        String query = "SELECT * FROM salle WHERE nom LIKE ? OR adresse LIKE ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, "%" + search + "%");
            stmt.setString(2, "%" + search + "%");

            System.out.println("🔍 Exécution de la requête: " + stmt.toString()); // Debug

            ResultSet rs = stmt.executeQuery();

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
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return salles;
    }

    // Recherche des salles selon le texte de recherche
    public List<Salle> searchSalles(String searchText) {
        // Récupérer toutes les salles
        List<Salle> allSalles = getAllSalles(searchText);

        // Filtrer les salles en fonction du texte de recherche (ignorant la casse)
        return allSalles.stream()
                .filter(salle -> salle.getNom().toLowerCase().contains(searchText) ||
                        salle.getAdresse().toLowerCase().contains(searchText))
                .collect(Collectors.toList());
    }



    public boolean salleExiste(int salleId) throws SQLException {
        String query = "SELECT COUNT(*) FROM Salle WHERE id_salle = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, salleId);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    public Salle getSalleById(int salleId) throws SQLException {
        String query = "SELECT * FROM Salle WHERE id_Salle = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, salleId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Salle(
                        rs.getInt("id_Salle"),
                        rs.getString("nom"),
                        rs.getString("adresse"),
                        rs.getString("details"),
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
            }
        }
        return false;
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


    // Simulation de la méthode qui cherche la salle dans la base de données
    public Salle findSalleById(int idSalle) {
        // Code pour interroger la base de données et récupérer la salle
        // Retourne un objet Salle si trouvé, sinon retourne null
        return new Salle(idSalle, "Salle Exemple");
    }

    public int getIdSalleByResponsableId(int responsableId) throws SQLException {
        String query = "SELECT id_Salle FROM user WHERE id_User = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, responsableId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id_Salle");
            }
        }
        return -1; // Retourne -1 si aucune salle n'est trouvée
    }
    public Salle getSalleForCurrentResponsable() throws SQLException {
        // Récupérer l'utilisateur connecté
        User currentUser = AuthToken.getCurrentUser();
        if (currentUser == null) {
            System.out.println("Aucun utilisateur connecté !");
            return null; // Retourne null si aucun utilisateur n'est connecté
        }

        // Récupérer l'ID du responsable connecté
        int responsableId = currentUser.getId();

        // Requête SQL pour récupérer l'ID de la salle associée au responsable
        String query = "SELECT id_Salle FROM user WHERE id_User = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, responsableId);
            ResultSet rs = pstmt.executeQuery();

            // Si une salle est trouvée, récupérer ses informations
            if (rs.next()) {
                int salleId = rs.getInt("id_Salle");
                return getSalleById(salleId); // Récupérer les informations de la salle
            } else {
                System.out.println("Aucune salle associée à ce responsable !");
                return null; // Retourne null si aucune salle n'est trouvée
            }
        }
    }

    public boolean getSalleByResponsableId(int responsableId) throws SQLException {
        return  salleExiste(responsableId);
    }
}


