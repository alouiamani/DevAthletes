package services;

import entities.Comment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommentService implements IService<Comment> {
    private Connection connection;

    public CommentService() {
        try {
            // Connexion à la base de données
            this.connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/blog", "root", "");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void ajouter(Comment comment) throws SQLException {
        String query = "INSERT INTO Comment (postId, userId, content, createdAt) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, comment.getPostId());
            stmt.setInt(2, comment.getUserId());
            stmt.setString(3, comment.getContent());
            stmt.setTimestamp(4, comment.getCreatedAt());
            stmt.executeUpdate();
        }
    }

    @Override
    public void modifier(Comment comment) throws SQLException {
        String query = "UPDATE Comment SET content = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
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
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Comment comment = new Comment(rs.getInt("id"), rs.getInt("postId"), rs.getInt("userId"), rs.getString("content"), rs.getTimestamp("createdAt"));
                comments.add(comment);
            }
        }
        return comments;
    }

    // Méthode pour compter le nombre de commentaires d'un post
    public int countComments(int postId) throws SQLException {
        String query = "SELECT COUNT(*) FROM Comment WHERE postId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, postId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1); // Retourner le nombre de commentaires
                }
            }
        }
        return 0; // Si aucun commentaire, retourner 0
    }
}
