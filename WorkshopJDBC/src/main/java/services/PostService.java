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
        // Étape 1 : Vérifier ce que Java envoie à la base
        System.out.println("🔍 Tentative d'insertion du post : " + post);
        System.out.println("🖼 URL de l'image avant insertion : " + post.getImageUrl());

        // Étape 2 : Vérifier si imageUrl est NULL avant l’insertion
        if (post.getImageUrl() == null || post.getImageUrl().isEmpty()) {
            System.out.println("⚠️ Attention : imageUrl est NULL ou vide !");
        }

        // Construire la requête SQL
        String query = "INSERT INTO Post (userId, title, content, image_url, createdAt) VALUES (?, ?, ?, ?, ?)";

        // Étape 3 : Vérifier si la requête d’insertion est bien construite
        System.out.println("💾 Requête SQL : INSERT INTO Post (userId, title, content, image_url, createdAt) VALUES ("
                + post.getUserId() + ", '" + post.getTitle() + "', '" + post.getContent() + "', '"
                + post.getImageUrl() + "', '" + post.getCreatedAt() + "')");

        // Étape 4 : Forcer une valeur par défaut pour imageUrl si elle est vide ou NULL
        if (post.getImageUrl() == null || post.getImageUrl().isEmpty()) {
            post.setImageUrl("https://example.com/default.jpg");
        }

        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, post.getUserId());
            stmt.setString(2, post.getTitle());
            stmt.setString(3, post.getContent());
            stmt.setString(4, post.getImageUrl()); // Nouveau paramètre pour l'image
            stmt.setTimestamp(5, post.getCreatedAt());

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
        String query = "UPDATE Post SET title = ?, content = ?, image_url = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, post.getTitle());
            stmt.setString(2, post.getContent());
            stmt.setString(3, post.getImageUrl()); // Nouveau paramètre
            stmt.setInt(4, post.getId());
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
                    Post post = new Post(
                            rs.getInt("id"),
                            rs.getInt("userId"),
                            rs.getString("title"),
                            rs.getString("content"),
                            rs.getString("image_url"), // Nouvelle colonne
                            rs.getTimestamp("createdAt")
                    );
                    posts.add(post);
                }
            }
            return posts;
        }
    }

