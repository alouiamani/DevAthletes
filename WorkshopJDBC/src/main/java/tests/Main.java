package tests;

import entities.Comment;
import entities.Post;
import entities.User;
import services.CommentService;
import services.IService;
import services.PostService;
import services.UserService;
import utils.MyDatabase;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            // Vérifier la connexion à la base de données
            Connection connection = MyDatabase.getInstance().getConnection();
            if (connection != null) {
                System.out.println("✅ Connexion réussie !");
            } else {
                System.out.println("❌ Échec de la connexion.");
                return; // Stopper le programme si la connexion échoue
            }

            // Création des services
            IService<User> userService = new UserService();
            IService<Post> postService = new PostService();
            IService<Comment> commentService = new CommentService();

            System.out.println("\n--- 1️⃣ Ajout des entités ---");

            // Ajouter un utilisateur
            User user = new User(0, "JohnDoe", "johndoe@example.com", "password123");
            userService.ajouter(user);
            System.out.println("Utilisateur ajouté avec ID : " + user.getId());


            // Ajouter un post
            Post post = new Post(0, user.getId(), "My First Post", "This is the content of the post.", null);
            postService.ajouter(post);

            // Ajouter un commentaire
            Comment comment = new Comment(0, post.getId(), user.getId(), "Great post!", null);
            commentService.ajouter(comment);

            System.out.println("\n--- 2️⃣ Affichage des entités ---");

            // Afficher tous les utilisateurs
            List<User> users = userService.afficher();
            System.out.println("Utilisateurs :");
            users.forEach(u -> System.out.println("- " + u.getUsername()));

            // Afficher tous les posts
            List<Post> posts = postService.afficher();
            System.out.println("\nPosts :");
            posts.forEach(p -> System.out.println("- " + p.getTitle()));

            // Afficher tous les commentaires
            List<Comment> comments = commentService.afficher();
            System.out.println("\nCommentaires :");
            comments.forEach(c -> System.out.println("- " + c.getContent()));

            System.out.println("\n--- 3️⃣ Modification des entités ---");

            // Modifier l'utilisateur
            user.setUsername("JohnUpdated");
            user.setEmail("johnupdated@example.com");
            userService.modifier(user);

            // Modifier le post
            post.setTitle("Updated Post Title");
            post.setContent("Updated content for the post.");
            postService.modifier(post);

            // Modifier le commentaire
            comment.setContent("Updated comment content.");
            commentService.modifier(comment);

            System.out.println("\n--- 4️⃣ Affichage après modification ---");

            users = userService.afficher();
            users.forEach(u -> System.out.println("Utilisateur : " + u.getUsername()));

            posts = postService.afficher();
            posts.forEach(p -> System.out.println("Post : " + p.getTitle()));

            comments = commentService.afficher();
            comments.forEach(c -> System.out.println("Commentaire : " + c.getContent()));

            System.out.println("\n--- 5️⃣ Suppression des entités ---");

            // Supprimer un commentaire
            commentService.supprimer(comment.getId());

            // Supprimer un post
            postService.supprimer(post.getId());

            // Supprimer un utilisateur
            userService.supprimer(user.getId());

            System.out.println("\n--- 6️⃣ Affichage après suppression ---");

            users = userService.afficher();
            System.out.println("Utilisateurs restants : " + users.size());

            posts = postService.afficher();
            System.out.println("Posts restants : " + posts.size());

            comments = commentService.afficher();
            System.out.println("Commentaires restants : " + comments.size());

            System.out.println("\n--- 7️⃣ Test de suppression en cascade ---");

            // Ajouter un utilisateur avec post et commentaire
            user = new User(0, "JaneDoe", "janedoe@example.com", "password456");
            userService.ajouter(user);

            post = new Post(0, user.getId(), "Post by Jane", "Content of Jane's post.", null);
            postService.ajouter(post);

            comment = new Comment(0, post.getId(), user.getId(), "Great post by Jane!", null);
            commentService.ajouter(comment);

            // Suppression en cascade : suppression de l'utilisateur
            userService.supprimer(user.getId());

            // Vérifier si les posts et commentaires sont bien supprimés
            posts = postService.afficher();
            comments = commentService.afficher();

            System.out.println("Posts restants après suppression en cascade : " + posts.size());
            System.out.println("Commentaires restants après suppression en cascade : " + comments.size());

        } catch (SQLException e) {
            System.err.println("❌ Erreur SQL : " + e.getMessage());
        } catch (Exception e) {
            System.err.println("❌ Erreur inattendue : " + e.getMessage());
        }
    }
}
