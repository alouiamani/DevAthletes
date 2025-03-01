package org.gymify.services;

import org.gymify.entities.Salle;
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
    public void ajouter(Salle salle) throws SQLException {

        String req = "INSERT INTO salle (nom, adresse, details, num_tel, email,url_photo) VALUES ('"
                + salle.getNom() + "', '"
                + salle.getAdresse() + "', '"
                + salle.getDetails() + "', '"
                + salle.getNum_tel() + "', '"
                + salle.getEmail() + "', '" + salle.getUrl_photo() + "')";

        Statement statement = connection.createStatement();
        statement.executeUpdate(req);
        System.out.println("Salle ajoutée avec succès !");


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
        preparedStatement.setString(6, salle.getUrl_photo()); // Éviter les null
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
    public void supprimer(int id) throws SQLException {
        String req = "DELETE FROM salle WHERE id_Salle=?";
        PreparedStatement preparedStatement = connection.prepareStatement(req);

        preparedStatement.setInt(1, id);
        preparedStatement.executeUpdate();
        System.out.println(" Salle supprimé avec succès !");

    }


    @Override
    public List<Salle> afficher() throws SQLException {
        List<Salle> salles = new ArrayList<>();
        String req = "SELECT * FROM salle";
        Statement statement = connection.createStatement();

        ResultSet rs = statement.executeQuery(req);
        while (rs.next()) {
            Salle salle = new Salle(rs.getInt("id_salle"), rs.getString("nom"), rs.getString("adresse"), rs.getString("details"), rs.getString("num_tel"), rs.getString("email"), rs.getString("url_photo"));
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
                int id = rs.getInt("id_salle");
                String nom = rs.getString("nom");
                String adresse = rs.getString("adresse");
                String details = rs.getString("details");
                String numTel = rs.getString("num_tel");
                String email = rs.getString("email");

                String urlPhoto = rs.getString("url_photo");

                System.out.println("✅ Salle récupérée - ID: " + id + ", Nom: " + nom + ", Adresse: " + adresse);

                Salle salle = new Salle(id, nom, adresse,details, numTel, email,  urlPhoto);
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
}


