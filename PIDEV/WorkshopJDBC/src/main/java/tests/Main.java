package tests;

import entities.Comment;
import entities.Like;
import entities.Post;
import entities.User;
import services.CommentService;
import services.IService;
import services.LikeService;
import services.PostService;
import services.ServiceUser;
import utils.MyDatabase;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public class Main {
    public static void main(String[] args) {
        try {
            // Vérifier la connexion à la base de données
            Connection connection = MyDatabase.getInstance().getConnection();
            if (connection != null) {
                System.out.println("✅ Connexion réussie !");
            } else {
                System.out.println("❌ Échec de la connexion.");
                return;
            }

            // Création des services
            ServiceUser userService = new ServiceUser();
            IService<Post> postService = new PostService();
            IService<Comment> commentService = new CommentService();
            LikeService likeService = new LikeService();

            System.out.println("\n--- 1️⃣ Ajout des entités ---");

            // Ajouter un utilisateur
            User user = new User("John", "Doe","password123", "john.doe@example.com", "Sportif");
            userService.ajouter(user);

// Récupérer l'utilisateur depuis la base de données pour obtenir son ID
            Optional<User> savedUser = userService.authentifier("john.doe@example.com", "password123");
            if (savedUser.isPresent()) {
                user = savedUser.get(); // Met à jour l'utilisateur avec son ID réel
            } else {
                System.out.println("❌ Impossible de récupérer l'utilisateur après l'insertion.");
                return;
            }


            // Authentifier un utilisateur
            Optional<User> authenticatedUser = userService.authentifier("john.doe@example.com", "password123");
            if (authenticatedUser.isPresent()) {
                System.out.println("✅ Authentification réussie : " + authenticatedUser.get());
            } else {
                System.out.println("❌ Échec de l'authentification");
            }

            // Ajouter un post
            Post post = new Post(0, user.getId_User(), "Mon Premier Post", "Ceci est le contenu du post.", "https://www.w3schools.com/w3images/lights.jpg", null);
            postService.ajouter(post);
            System.out.println("Post ajouté : " + post);

            // Ajouter un commentaire
            Comment comment = new Comment(0, post.getId(), user.getId_User(), "Super post !", null);
            commentService.ajouter(comment);
            System.out.println("Commentaire ajouté : " + comment);

            // Ajouter un like
            Like like = new Like(post.getId(), user.getId_User(), new Timestamp(System.currentTimeMillis()));
            likeService.ajouter(like);
            System.out.println("Like ajouté pour le post " + post.getId());

            System.out.println("\n--- 2️⃣ Affichage des entités ---");

            // Afficher les utilisateurs
            List<User> users = userService.afficher();
            users.forEach(System.out::println);

            // Afficher les posts
            List<Post> posts = postService.afficher();
            posts.forEach(System.out::println);

            // Afficher les commentaires
            List<Comment> comments = commentService.afficher();
            comments.forEach(System.out::println);

            // Afficher les likes
            List<Like> likes = likeService.afficher();
            likes.forEach(System.out::println);

            System.out.println("\n--- 3️⃣ Modification des entités ---");

            // Modifier l'utilisateur
            //user.setNom("JohnUpdated");
            //user.setEmail("johnupdated@example.com");
            //userService.modifier(user);

            // Modifier le post
            post.setTitle("Titre modifié");
            post.setContent("Contenu mis à jour.");
            postService.modifier(post);

            // Modifier le commentaire
            comment.setContent("Commentaire mis à jour.");
            commentService.modifier(comment);

            System.out.println("\n--- 4️⃣ Affichage après modification ---");
            userService.afficher().forEach(System.out::println);
            postService.afficher().forEach(System.out::println);
            commentService.afficher().forEach(System.out::println);

            System.out.println("\n--- 5️⃣ Suppression des entités ---");

            // Supprimer le like
            likeService.supprimer(like.getId());
            System.out.println("Like supprimé.");

            // Supprimer le commentaire
            commentService.supprimer(comment.getId());
            System.out.println("Commentaire supprimé.");

            // Supprimer le post
            //postService.supprimer(post.getId());
            //System.out.println("Post supprimé.");

            // Supprimer l'utilisateur
           //// userService.supprimer(user.getId_User());
            //System.out.println("Utilisateur supprimé.");

            System.out.println("\n--- 6️⃣ Vérification après suppression ---");
            System.out.println("Utilisateurs restants : " + userService.afficher().size());
            System.out.println("Posts restants : " + postService.afficher().size());
            System.out.println("Commentaires restants : " + commentService.afficher().size());
            System.out.println("Likes restants : " + likeService.afficher().size());

        } catch (SQLException e) {
            System.err.println("❌ Erreur SQL : " + e.getMessage());
        } catch (Exception e) {
            System.err.println("❌ Erreur inattendue : " + e.getMessage());
        }
    }
}
