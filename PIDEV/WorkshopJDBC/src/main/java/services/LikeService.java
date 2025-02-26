package services;


import entities.Like;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LikeService implements IService<Like> {
    private Connection connection;

    public LikeService() {
        try {
            this.connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/project_dev", "root", "");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void ajouter(Like like) throws SQLException {
        String query = "INSERT INTO Likes (post_id, user_id, createdAt) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, like.getPostId());
            stmt.setInt(2, like.getUserId());
            stmt.setTimestamp(3, like.getCreatedAt());
            stmt.executeUpdate();
        }
    }

    @Override
    public void modifier(Like like) throws SQLException {
        String query = "UPDATE Likes SET post_id = ?, user_id = ?, createdAt = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, like.getPostId());
            stmt.setInt(2, like.getUserId());
            stmt.setTimestamp(3, like.getCreatedAt());
            stmt.setInt(4, like.getId());
            stmt.executeUpdate();
        }
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String query = "DELETE FROM Likes WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    @Override
    public List<Like> afficher() throws SQLException {
        List<Like> likes = new ArrayList<>();
        String query = "SELECT * FROM Likes";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Like like = new Like(rs.getInt("id"), rs.getInt("post_id"), rs.getInt("user_id"), rs.getTimestamp("createdAt"));
                likes.add(like);
            }
        }
        return likes;
    }

    // Ajouter une méthode pour compter les likes d'un post
    public int countLikes(int postId) throws SQLException {
        String query = "SELECT COUNT(*) FROM Likes WHERE post_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, postId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    // Vérifier si un utilisateur a déjà liké un post
    public boolean userHasLiked(int postId, int userId) throws SQLException {
        String query = "SELECT COUNT(*) FROM Likes WHERE post_id = ? AND user_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, postId);
            stmt.setInt(2, userId);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }
}
