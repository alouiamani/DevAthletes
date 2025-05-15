package org.gymify.services;

import org.gymify.utils.gymifyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReactionService {
    private Connection connection;

    public ReactionService() {
        this.connection = gymifyDataBase.getInstance().getConnection(); // Connexion DB
    }


    // Ajouter une réaction (ou la modifier si elle existe)
    // Ajouter ou supprimer une réaction
    public void ajouterReaction(int postId, int id_User, String type) throws SQLException {
        // Vérifier si l'utilisateur a déjà une réaction
        String checkQuery = "SELECT type FROM reactions WHERE postId = ? AND id_User = ?";
        PreparedStatement checkStmt = connection.prepareStatement(checkQuery);
        checkStmt.setInt(1, postId);
        checkStmt.setInt(2, id_User);
        ResultSet rs = checkStmt.executeQuery();

        if (rs.next()) {
            String currentReaction = rs.getString("type");

            if (currentReaction.equals(type)) {
                // 🗑️ Supprimer la réaction si l'utilisateur clique à nouveau sur le même type
                String deleteQuery = "DELETE FROM reactions WHERE postId = ? AND id_User = ?";
                PreparedStatement deleteStmt = connection.prepareStatement(deleteQuery);
                deleteStmt.setInt(1, postId);
                deleteStmt.setInt(2, id_User);
                deleteStmt.executeUpdate();
                System.out.println("🗑️ Réaction supprimée pour l'utilisateur " + id_User);
            } else {
                // 🔄 Mettre à jour si l'utilisateur change de réaction
                String updateQuery = "UPDATE reactions SET type = ? WHERE postId = ? AND id_User = ?";
                PreparedStatement updateStmt = connection.prepareStatement(updateQuery);
                updateStmt.setString(1, type);
                updateStmt.setInt(2, postId);
                updateStmt.setInt(3, id_User);
                updateStmt.executeUpdate();
                System.out.println("🔄 Réaction mise à jour : " + type + " pour l'utilisateur " + id_User);
            }
        } else {
            // ➕ Ajouter une nouvelle réaction
            String insertQuery = "INSERT INTO reactions (postId, id_User, type) VALUES (?, ?, ?)";
            PreparedStatement insertStmt = connection.prepareStatement(insertQuery);
            insertStmt.setInt(1, postId);
            insertStmt.setInt(2, id_User);
            insertStmt.setString(3, type);
            insertStmt.executeUpdate();
            System.out.println("➕ Nouvelle réaction ajoutée : " + type + " pour l'utilisateur " + id_User);
        }
    }


    // Récupérer le nombre de réactions par type
    public int countReactions(int postId, String type) throws SQLException {
        String query = "SELECT COUNT(*) FROM reactions WHERE postId = ? AND type = ?";
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setInt(1, postId);
        stmt.setString(2, type);
        ResultSet rs = stmt.executeQuery();

        int count = 0;
        if (rs.next()) {
            count = rs.getInt(1);
        }

        // ✅ Vérification en console
        System.out.println("🔍 Vérification SQL : Post ID = " + postId + ", Type = " + type);
        System.out.println("➡️ Nombre de réactions trouvées : " + count);

        return count;
    }


    // Vérifier la réaction d'un utilisateur sur un post
    public String getUserReaction(int postId, int id_User) throws SQLException {
        String query = "SELECT type FROM reactions WHERE postId = ? AND id_User = ?";
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setInt(1, postId);
        stmt.setInt(2, id_User);
        ResultSet rs = stmt.executeQuery();
        return rs.next() ? rs.getString("type") : null;
    }


    // Supprimer une réaction
    public void supprimerReaction(int postId, int id_User) throws SQLException {
        String deleteQuery = "DELETE FROM reactions WHERE postId = ? AND id_User = ?";
        PreparedStatement deleteStmt = connection.prepareStatement(deleteQuery);
        deleteStmt.setInt(1, postId);
        deleteStmt.setInt(2, id_User);
        deleteStmt.executeUpdate();
        System.out.println("🗑️ Réaction supprimée pour l'utilisateur " + id_User);
    }

}
