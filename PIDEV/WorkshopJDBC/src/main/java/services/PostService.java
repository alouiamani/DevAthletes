package services;

import entities.Post;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class PostService implements IService<Post> {
    private Connection connection;

    public PostService() {
        try {
            // Connexion à la base de données
            this.connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/project_dev", "root", "");
            System.out.println("✅ Connexion réussie !");
            // Vérifier la base de données utilisée
            System.out.println("📌 Base de données utilisée : " + connection.getCatalog());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void ajouter(Post post) throws SQLException {
        // Vérifier les règles de saisie
        validerPost(post);
        // Vérifier si un post identique existe déjà
        if (existeDeja(post)) {
            throw new SQLException("⚠️ ERREUR : Un post identique existe déjà !");
        }

        System.out.println("🔍 Tentative d'insertion du post : " + post);
        System.out.println("🖼 URL de l'image avant insertion : " + post.getImageUrl());

        // Forcer une valeur par défaut pour imageUrl si elle est vide
        if (post.getImageUrl() == null || post.getImageUrl().isEmpty()) {
            post.setImageUrl("https://example.com/default.jpg");
        }

        // Requête d'insertion
        String query = "INSERT INTO Post (userId, title, content, image_url, createdAt) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, post.getUserId());
            stmt.setString(2, post.getTitle());
            stmt.setString(3, post.getContent());
            stmt.setString(4, post.getImageUrl());
            stmt.setTimestamp(5, post.getCreatedAt());

            stmt.executeUpdate();

            // Récupérer l'ID généré
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                post.setId(rs.getInt(1));
                System.out.println("✅ Post ajouté avec ID : " + post.getId());
            }
        }
    }

    /**
     * Vérifie si un post avec le même titre, contenu et image existe déjà.
     */
    private boolean existeDeja(Post post) throws SQLException {
        String query = "SELECT id FROM Post WHERE title = ? AND content = ? AND image_url = ? LIMIT 1";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, post.getTitle());
            stmt.setString(2, post.getContent());
            stmt.setString(3, post.getImageUrl());
            ResultSet rs = stmt.executeQuery();
            return rs.next(); // Retourne true si un post existe déjà
        }
    }

    /**
     * Vérifie les règles de saisie avant d'ajouter un post.
     */
    private void validerPost(Post post) throws SQLException {
        // Afficher l'URL pour voir si elle est bien récupérée
        System.out.println("🔍 Image URL reçue: " + post.getImageUrl());
        // Vérifier si les champs ne sont pas vides
        if (post.getTitle().trim().isEmpty() || post.getContent().trim().isEmpty()) {
            throw new SQLException("❌ Erreur : Le titre et le contenu sont obligatoires !");
        }

        // Vérifier la longueur du titre et du contenu
        if (post.getTitle().length() < 3 || post.getTitle().length() > 100) {
            throw new SQLException("❌ Erreur : Le titre doit avoir entre 3 et 100 caractères !");
        }
        if (post.getContent().length() < 10 || post.getContent().length() > 1000) {
            throw new SQLException("❌ Erreur : Le contenu doit avoir entre 10 et 1000 caractères !");
        }

        // Vérifier les mots interdits
        String[] motsInterdits = {"spam", "arnaque", "insulte"};
        for (String mot : motsInterdits) {
            if (post.getTitle().toLowerCase().contains(mot) || post.getContent().toLowerCase().contains(mot)) {
                throw new SQLException("❌ Erreur : Votre post contient un mot interdit (" + mot + ")");
            }
        }

        // Vérifier si l'image a une URL valide
        if (!isValidUrl(post.getImageUrl())) {
            throw new SQLException("❌ Erreur : L'URL de l'image est invalide !");
        }
    }

    /**
     * Vérifie si une URL est valide.
     */
    private boolean isValidUrl(String url) {
        if (url == null || url.isEmpty()) return true; // Accepte une URL vide (gérée ailleurs)
        String regexUrl = "^(http|https)://.*\\.(jpg|png|jpeg|gif)(\\?.*)?$";

        return Pattern.matches(regexUrl, url);
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

    /**
     * Récupère les posts d'un utilisateur spécifique par ID.
     */





    public List<Post> afficherByUserId(int userId) throws SQLException {
        List<Post> posts = new ArrayList<>();
        String query = "SELECT p.*, u.imageURL AS user_image_url " +
                "FROM Post p JOIN user u ON p.userId = u.id_User " +  // 🔥 Assurez-vous que les noms de colonnes sont corrects
                "WHERE p.userId = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Post post = new Post();
                post.setId(rs.getInt("id"));
                post.setUserId(rs.getInt("userId"));
                post.setTitle(rs.getString("title"));
                post.setContent(rs.getString("content"));
                post.setCreatedAt(rs.getTimestamp("createdAt"));
                post.setImageUrl(rs.getString("image_url"));
                post.setUserImageUrl(rs.getString("user_image_url")); // ✅ Ajout de l'image du profil

                posts.add(post);
            }
        }
        return posts;
    }



    public List<Post> afficherTous() throws SQLException {
        List<Post> posts = new ArrayList<>();
        String query = "SELECT * FROM post ORDER BY createdAt DESC";

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Post post = new Post(
                        rs.getInt("id"),
                        rs.getInt("userId"),
                        rs.getString("title"),
                        rs.getString("content"),
                        rs.getString("image_url"),
                        rs.getTimestamp("createdAt"));
                posts.add(post);
            }
        }
        return posts;
    }

}
