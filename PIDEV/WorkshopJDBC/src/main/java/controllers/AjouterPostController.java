package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.web.HTMLEditor;
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
    private HTMLEditor newPostEditor;
    @FXML
    void submitPost(ActionEvent event) {
        // Vérification des champs
        String title = titleField.getText().trim();
        String content = newPostEditor.getHtmlText().trim();
        String imageUrl = imageUrlField.getText().trim();

        if (title.isEmpty() || content.isEmpty()) {
            showStyledAlert(Alert.AlertType.ERROR, "Erreur", "tous les champs sont obligatoires.");
            return;
        }

        // Création de l'objet Post
        Post post = new Post();
        post.setTitle(title);
        post.setContent(content);
        post.setImageUrl(imageUrl.isEmpty() ? "https://example.com/default.jpg" : imageUrl); // Image par défaut si vide
        post.setUserId(1);  // Remplacer par l'ID de l'utilisateur connecté
        post.setCreatedAt(java.sql.Timestamp.valueOf(LocalDateTime.now()));

        // Instance du service PostService
        PostService postService = new PostService();

        try {
            // Ajouter le post via le service
            postService.ajouter(post);

            // Afficher un message de succès
            showStyledAlert(Alert.AlertType.INFORMATION, "Succès", "Post ajouté avec succès !");

            // Rediriger vers la page d'accueil du blog
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/HomeBlog.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) submitButton.getScene().getWindow();  // Obtenir l'instance de Stage
            stage.setScene(scene);  // Changer la scène

        } catch (SQLException | IOException e) {
            showStyledAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur s'est produite lors de l'ajout du post.");
            e.printStackTrace();
        }
    }

    /**
     * Affiche une alerte stylisée avec une icône et du CSS.
     */
    private void showStyledAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        // Ajouter une icône selon le type d'alerte
        String iconPath = "";
        if (type == Alert.AlertType.INFORMATION) {
            iconPath = "/assets/sucess (2).png";  // Chemin de ton icône de succès
        } else if (type == Alert.AlertType.ERROR) {
            iconPath = "/assets/error icon.png";  // Chemin de ton icône d'erreur
        }

        if (!iconPath.isEmpty()) {
            ImageView icon = new ImageView(new Image(getClass().getResourceAsStream(iconPath)));
            icon.setFitHeight(50);
            icon.setFitWidth(50);
            alert.setGraphic(icon);
        }

        // Appliquer le style CSS
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/Style.css").toExternalForm());
        dialogPane.getStyleClass().add("custom-alert");

        alert.show();
    }
}
