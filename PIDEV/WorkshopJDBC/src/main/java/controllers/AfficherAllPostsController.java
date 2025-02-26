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
import javafx.scene.layout.*;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.application.Platform;
import services.PostService;
import services.ServiceUser;
import services.ServiceUser;
import entities.User;

import javafx.scene.shape.Circle;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class AfficherAllPostsController {

    @FXML
    private VBox postsBox;

    @FXML
    private ScrollPane scrollPane;


    @FXML
    private WebView postWebView;

    private final PostService postService = new PostService();
    private final ServiceUser userService = new ServiceUser(); // Service pour récupérer les utilisateurs



    @FXML
    private void initialize() {
        afficherTousPosts();

        // Centrer les posts
        Platform.runLater(() -> {
            StackPane.setAlignment(postsBox, Pos.CENTER);
            postsBox.setAlignment(Pos.CENTER);
        });





    }

    private void afficherTousPosts() {
        postsBox.getChildren().clear();
        postsBox.setMaxWidth(Double.MAX_VALUE);

        try {
            List<Post> posts = postService.afficherTous();
            for (Post post : posts) {
                VBox postContainer = new VBox(10);
                postContainer.getStyleClass().add("post-container");

                HBox header = new HBox(10);
                header.getStyleClass().add("post-header");

                // 🔥 Récupérer l'utilisateur du post
                User user = userService.getUserById(post.getUserId());

                // 🔥 Image de profil
                ImageView profilePic = new ImageView();
                profilePic.setFitWidth(40);
                profilePic.setFitHeight(40);
                profilePic.getStyleClass().add("profile-pic");

                if (user.getImageURL() != null && !user.getImageURL().isEmpty()) {
                    try {
                        Image image = new Image(user.getImageURL(), true);
                        profilePic.setImage(image);
                    } catch (Exception e) {
                        System.out.println("⚠️ Erreur de chargement de l'image de profil pour " + user.getUsername());
                    }
                } else {
                    profilePic.setImage(new Image("https://www.w3schools.com/howto/img_avatar.png")); // Image par défaut
                }

                Circle clip = new Circle(20, 20, 20);
                profilePic.setClip(clip);

                Label userName = new Label(user.getUsername());
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








                Label dateLabel = new Label("📅 " + post.getCreatedAt().toString());
                dateLabel.getStyleClass().add("post-date");

                ImageView imageView = new ImageView();
                imageView.setFitWidth(500);
                imageView.setPreserveRatio(true);

                if (post.getImageUrl() != null && !post.getImageUrl().isEmpty()) {
                    try {
                        Image image = new Image(post.getImageUrl(), true);
                        imageView.setImage(image);
                    } catch (Exception e) {
                        System.out.println("⚠️ Erreur de chargement de l'image pour le post ID " + post.getId());
                    }
                }

                HBox actions = new HBox(20);
                actions.getStyleClass().add("action-buttons");

                Button likeButton = new Button("👍 J'aime");
                likeButton.getStyleClass().add("like-button");
                Button commentButton = new Button("💬 Commenter");
                commentButton.getStyleClass().add("comment-button");
                Button shareButton = new Button("🔗 Partager");
                shareButton.getStyleClass().add("share-button");

                commentButton.setOnAction(event -> voirCommentaires(post.getId()));

                actions.getChildren().addAll(likeButton, commentButton, shareButton);

                postContainer.getChildren().addAll(header, titleLabel, postWebView, dateLabel, imageView, actions);
                postsBox.getChildren().add(postContainer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
        postsBox.setPrefWidth(600);
    }



    public class JavaFXConnector {
        public void setHeight(double height) {
            Platform.runLater(() -> postWebView.setPrefHeight(height + 20));
        }
    }

}
