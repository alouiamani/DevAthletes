package controllers;

import entities.Post;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.application.Platform;
import services.PostService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class AfficherMyPostsController {

    @FXML
    private VBox postsBox;

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private WebView postWebView;

    private final PostService postService = new PostService();

    @FXML
    private void initialize() {
        int currentUserId = 1;  // À remplacer par l'ID réel après login
        afficherPosts(currentUserId);

        // Centrer postsBox dans ScrollPane
        Platform.runLater(() -> {
            StackPane.setAlignment(postsBox, Pos.CENTER);
            postsBox.setAlignment(Pos.CENTER);
        });
    }

    private void afficherPosts(int userId) {
        postsBox.getChildren().clear();
        postsBox.setMaxWidth(Double.MAX_VALUE);

        try {
            List<Post> posts = postService.afficherByUserId(userId);
            for (Post post : posts) {
                VBox postContainer = new VBox(10);
                postContainer.getStyleClass().add("post-container");

                // ✅ HEADER : Image profil + Nom utilisateur
                HBox header = new HBox(10);
                header.getStyleClass().add("post-header");

                ImageView profilePic = new ImageView();
                profilePic.setFitWidth(40);
                profilePic.setFitHeight(40);
                profilePic.getStyleClass().add("profile-pic");
                // ✅ Appliquer un "clip" rond
                Circle clip = new Circle(20, 20, 20); // (x, y, rayon)
                profilePic.setClip(clip);

                if (post.getUserImageUrl() != null && !post.getUserImageUrl().isEmpty()) {
                    try {
                        Image userImage = new Image(post.getUserImageUrl(), true);
                        profilePic.setImage(userImage);
                    } catch (Exception e) {
                        System.out.println("⚠️ Erreur chargement image user pour ID " + post.getUserId());
                        profilePic.setImage(new Image("https://www.w3schools.com/howto/img_avatar.png"));
                    }
                } else {
                    profilePic.setImage(new Image("https://www.w3schools.com/howto/img_avatar.png"));
                }

                Label userName = new Label("Utilisateur " + post.getUserId());
                userName.getStyleClass().add("username");

                header.getChildren().addAll(profilePic, userName);


                Label titleLabel = new Label(post.getTitle());
                titleLabel.getStyleClass().add("post-title");

                // 🆕 WebView pour afficher le contenu formaté
                WebView postWebView = new WebView();
                postWebView.getEngine().loadContent(post.getContent(), "text/html");
                postWebView.setPrefHeight(100); // Hauteur du WebView


                // Récupérer la couleur du titre
                String bgColor = titleLabel.getStyle().contains("-fx-background-color") ?
                        titleLabel.getStyle().split(":")[1].trim().replace(";", "") :
                        "#1e1e1e"; // Couleur par défaut (blanc)

// Charger le contenu HTML avec le même background color
                postWebView.getEngine().loadContent(
                        "<html><head><style>" +
                                "body { background-color: " + bgColor + "; color: white; font-family: Arial, sans-serif; }" +
                                "</style></head><body>" +
                                post.getContent() +
                                "</body></html>", "text/html"
                );








                //Label contentLabel = new Label(post.getContent());
                //contentLabel.getStyleClass().add("post-content");

                Label dateLabel = new Label("📅 " + post.getCreatedAt().toString());
                dateLabel.getStyleClass().add("post-date");

                // ✅ IMAGE DU POST
                ImageView imageView = new ImageView();
                imageView.setFitWidth(500);
                imageView.setPreserveRatio(true);

                if (post.getImageUrl() != null && !post.getImageUrl().isEmpty()) {
                    try {
                        Image postImage = new Image(post.getImageUrl(), true);
                        imageView.setImage(postImage);
                    } catch (Exception e) {
                        System.out.println("⚠️ Erreur chargement image post ID " + post.getId());
                    }
                }

                // ✅ BOUTONS ACTIONS
                HBox actions = new HBox(20);
                actions.getStyleClass().add("action-buttons");

                Button likeButton = new Button("👍 J'aime");
                likeButton.getStyleClass().add("like-button");

                Button commentButton = new Button("💬 Commenter");
                commentButton.getStyleClass().add("comment-button");

                Button deleteButton = new Button("❌ Supprimer");
                deleteButton.getStyleClass().add("delete-button");

                deleteButton.setOnAction(event -> confirmerSuppression(post, userId));
                commentButton.setOnAction(event -> voirCommentaires(post.getId()));

                actions.getChildren().addAll(likeButton, commentButton, deleteButton);

                // ✅ AJOUTER TOUS LES ÉLÉMENTS AU POST CONTAINER
                //postContainer.getChildren().addAll(header, titleLabel, contentLabel, dateLabel, imageView, actions);
                //postsBox.getChildren().add(postContainer);
                postContainer.getChildren().addAll(header, titleLabel, postWebView, dateLabel, imageView, actions);
                postsBox.getChildren().add(postContainer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void confirmerSuppression(Post post, int userId) {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmation de suppression");
        confirmationAlert.setHeaderText("Êtes-vous sûr de vouloir supprimer ce post ?");
        confirmationAlert.setContentText("Cette action est irréversible.");

        ButtonType okButton = new ButtonType("OK");
        ButtonType cancelButton = new ButtonType("Annuler");
        confirmationAlert.getButtonTypes().setAll(okButton, cancelButton);

        confirmationAlert.showAndWait().ifPresent(response -> {
            if (response == okButton) {
                try {
                    postService.supprimer(post.getId());
                    afficherPosts(userId);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @FXML
    private void voirCommentaires(int postId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherCommentaires.fxml"));
            Parent root = loader.load();

            AfficherCommentairesController controller = loader.getController();
            controller.setPostId(postId);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Commentaires du Post " + postId);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateWidth(double newWidth) {
        postsBox.setPrefWidth(600);  // Garde une largeur limitée
    }

    public class JavaFXConnector {
        public void setHeight(double height) {
            Platform.runLater(() -> postWebView.setPrefHeight(height + 20));
        }
    }
}
