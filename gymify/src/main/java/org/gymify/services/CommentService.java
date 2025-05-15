package org.gymify.services;

import org.gymify.entities.Comment;

import java.io.InputStreamReader;
import java.io.OutputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.gymify.entities.User;

public class CommentService implements IServicePost<Comment> {
    private Connection connection;

    public CommentService() {
        try {
            // Connexion à la base de données
            this.connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/project_dev", "root", "");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void ajouter(Comment comment) throws SQLException {
        String query = "INSERT INTO Comment (postId, id_User, content, createdAt) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, comment.getPostId());
            stmt.setInt(2, comment.getId_User());
            stmt.setString(3, comment.getContent());
            stmt.setTimestamp(4, comment.getCreatedAt());
            stmt.executeUpdate();
            // Récupérer l'ID généré pour le commentaire
            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                comment.setId(generatedKeys.getInt(1));
            }
        }
    }

    @Override
    public void modifier(Comment comment) throws SQLException {
        String req = "UPDATE comment SET content = ?, createdAt = CURRENT_TIMESTAMP WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(req)) {
            stmt.setString(1, comment.getContent());
            stmt.setInt(2, comment.getId());
            stmt.executeUpdate();
        }
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String query = "DELETE FROM Comment WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    @Override
    public List<Comment> afficher() throws SQLException {
        List<Comment> comments = new ArrayList<>();
        String query = "SELECT * FROM Comment";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                comments.add(new Comment(
                        rs.getInt("id"),
                        rs.getInt("postId"),
                        rs.getInt("id_User"),
                        rs.getString("content"),
                        rs.getTimestamp("createdAt"),
                        null // Pas d'image ici car c'est la liste générale
                ));
            }
        }
        return comments;
    }

    // 🔥 **Méthode pour récupérer les commentaires avec les images de profil**
    public List<Comment> afficherParPostId(int postId) throws SQLException {
        List<Comment> comments = new ArrayList<>();

        // Jointure avec la table `user` pour récupérer l'image de profil
        String query = "SELECT c.id, c.postId, c.id_User, c.content, c.createdAt, u.imageURL " +
                "FROM Comment c " +
                "JOIN user u ON c.id_User = u.id_User " +
                "WHERE c.postId = ? " +  // ✅ Ajout de l'espace avant ORDER BY
                "ORDER BY c.createdAt DESC";  // 💥 Ajout du tri ici;

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, postId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    comments.add(new Comment(
                            rs.getInt("id"),
                            rs.getInt("postId"),
                            rs.getInt("id_User"),
                            rs.getString("content"),
                            rs.getTimestamp("createdAt"),
                            rs.getString("imageURL")
                            // 🔥 Ajout de l'URL de l'image
                    ));
                }
            }
        }
        return comments;
    }

    // 🔥 **Méthode pour compter les commentaires d'un post**
    public int countComments(int postId) throws SQLException {
        String query = "SELECT COUNT(*) FROM Comment WHERE postId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, postId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1); // Retourne le nombre de commentaires
                }
            }
        }
        return 0; // Retourne 0 si aucun commentaire
    }

    // 🔥 **Méthode pour récupérer un commentaire par ID**
    public Comment getCommentById(int id) throws SQLException {
        String query = "SELECT id, postId, id_User, content, createdAt FROM Comment WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Comment(
                            rs.getInt("id"),
                            rs.getInt("postId"),
                            rs.getInt("id_User"),
                            rs.getString("content"),
                            rs.getTimestamp("createdAt"),
                            null // Pas d'image ici
                    );
                }
            }
        }
        return null; // Retourne null si aucun commentaire trouvé
    }



    private static final String PERSPECTIVE_API_KEY = "AIzaSyC2Zd-KpL_6-cXQZYSzU1JBt487zigQ3yE";

    // Ajouter une méthode pour analyser un commentaire avec Perspective API
    public float analyzeCommentToxicity(String commentText) throws Exception {
        String apiUrl = "https://commentanalyzer.googleapis.com/v1alpha1/comments:analyze?key=" + PERSPECTIVE_API_KEY;
        URL url = new URL(apiUrl);

        // Préparer la connexion HTTP
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");

        connection.setDoOutput(true);

        String jsonInputString = "{"
                + "\"comment\": {\"text\": \"" + commentText + "\"},"
                + "\"languages\": [\"fr\"],"
                + "\"requestedAttributes\": {\"TOXICITY\": {}}"
                + "}";

        try (OutputStream os = connection.getOutputStream()) {
            os.write(jsonInputString.getBytes("UTF-8"));
        }

        InputStreamReader inputReader = new InputStreamReader(connection.getInputStream(), "UTF-8");
        JsonObject response = JsonParser.parseReader(inputReader).getAsJsonObject();
        inputReader.close();

        return response.getAsJsonObject("attributeScores")
                .getAsJsonObject("TOXICITY")
                .getAsJsonObject("summaryScore")
                .get("value").getAsFloat();
    }


    // Méthode pour récupérer l'email de l'utilisateur
    public String getUserEmail(int userId) {
        // Implémenter cette méthode pour obtenir l'email de l'utilisateur à partir de la base de données
        ServiceUser userService = new ServiceUser();
        User user = userService.getUserById(userId);
        return user != null ? user.getEmail() : "ranym.mejri@istic.ucar.tn";
    }
}