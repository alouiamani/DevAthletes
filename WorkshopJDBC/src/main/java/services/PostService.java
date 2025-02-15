package services;

import entities.Post;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;



public class PostService implements IService<Post> {
    private Connection connection;

    public PostService() {
        try {
            // Connexion à la base de données
            this.connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/blog", "root", "");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void ajouter(Post post) throws SQLException {
        String query = "INSERT INTO Post (userId, title, content) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, post.getUserId());
            stmt.setString(2, post.getTitle());
            stmt.setString(3, post.getContent());
            stmt.executeUpdate();

            // Récupérer l'ID généré et l'affecter à l'objet Post
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                post.setId(rs.getInt(1));  // Stocke l'ID du post
                System.out.println("✅ Post ajouté avec ID : " + post.getId());
            }
        }
    }


    @Override
    public void modifier(Post post) throws SQLException {
        String query = "UPDATE Post SET title = ?, content = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, post.getTitle());
            stmt.setString(2, post.getContent());
            stmt.setInt(3, post.getId());
            stmt.executeUpdate();
        }
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String query = "DELETE FROM Post WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    @Override
    public List<Post> afficher() throws SQLException {
        List<Post> posts = new ArrayList<>();
        String query = "SELECT * FROM Post";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Post post = new Post(rs.getInt("id"), rs.getInt("userId"), rs.getString("title"), rs.getString("content"), rs.getTimestamp("createdAt"));
                posts.add(post);
            }
        }
        return posts;
    }
}
