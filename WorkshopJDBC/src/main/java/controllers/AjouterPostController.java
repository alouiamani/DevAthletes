package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import services.PostService;
import entities.Post;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class AjouterPostController {

    @FXML
    private TextField imageUrlField;

    @FXML
    private TextArea newPost;

    @FXML
    private Button submitButton;

    @FXML
    private TextField titleField;

    @FXML
    void submitPost(ActionEvent event) {
        // Vérification des champs
        String title = titleField.getText().trim();
        String content = newPost.getText().trim();
        String imageUrl = imageUrlField.getText().trim();

        if (title.isEmpty() || content.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setContentText("Le titre et le contenu du post sont obligatoires.");
            alert.show();
            return;
        }

        // Créer un objet Post
        Post post = new Post();
        post.setTitle(title);
        post.setContent(content);
        post.setImageUrl(imageUrl.isEmpty() ? "https://example.com/default.jpg" : imageUrl); // Valeur par défaut si URL vide

        // Ajouter une valeur d'utilisateur (par exemple, userId = 1)
        post.setUserId(1);  // Il faut ajuster cette valeur en fonction de l'utilisateur connecté
        post.setCreatedAt(java.sql.Timestamp.valueOf(LocalDateTime.now()));

        // Instance du service qui gère l'ajout de posts
        PostService postService = new PostService();

        try {
            // Appel à la méthode du service pour ajouter le post
            postService.ajouter(post);

            // Afficher un message de succès
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setContentText("Post ajouté avec succès !");
            alert.show();

            // Changer de scène (par exemple, revenir à la liste des posts)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/HomeBlog.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) submitButton.getScene().getWindow();  // Obtenir l'instance de Stage
            stage.setScene(scene);  // Changer la scène

        } catch (SQLException | IOException e) {
            // Gérer les erreurs
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setContentText("Une erreur s'est produite lors de l'ajout du post. Veuillez réessayer.");
            alert.show();
            e.printStackTrace();
        }
    }
}
