package org.gymify.controllers;

import org.gymify.entities.Post;
import org.gymify.entities.User;
import javafx.collections.FXCollections;
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
import org.gymify.services.PostService;
import org.gymify.services.ReactionService;
import org.gymify.services.ServiceUser;
import org.gymify.utils.AuthToken;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class AfficherMyPostsController {
    @FXML
    private AnchorPane commentSection; // Section pour afficher les commentaires

    @FXML
    private VBox postsBox;

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private WebView postWebView;
    private final ReactionService reactionService = new ReactionService();
    private final PostService postService = new PostService();
    private final ServiceUser serviceUser = new ServiceUser();

    @FXML
    private void initialize() {
        User currentUser = AuthToken.getCurrentUser();
        if (currentUser != null) {
            afficherPosts(currentUser.getId());
        } else {
            System.out.println("❌ Erreur : Aucun utilisateur connecté");
            // Optionnel : afficher une alerte ou rediriger vers la page de connexion
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur d'authentification");
            alert.setHeaderText(null);
            alert.setContentText("Vous devez être connecté pour voir vos posts.");
            alert.showAndWait();
        }

        // Centrer postsBox dans ScrollPane
        Platform.runLater(() -> {
            StackPane.setAlignment(postsBox, Pos.CENTER);
            postsBox.setAlignment(Pos.CENTER);
        });
    }

    public void afficherPosts(int id_User) {
        postsBox.getChildren().clear();
        postsBox.setMaxWidth(Double.MAX_VALUE);

        try {
            List<Post> posts = postService.afficherByid_User(id_User);
            for (Post post : posts) {
                VBox postContainer = new VBox(10);
                postContainer.getStyleClass().add("post-container");

                User user = serviceUser.getUserById(post.getId_User());

                // ✅ HEADER : Image profil + Nom utilisation
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
                        System.out.println("⚠️ Erreur chargement image user pour ID " + post.getId_User());
                        profilePic.setImage(new Image("https://www.w3schools.com/howto/img_avatar.png"));
                    }
                } else {
                    profilePic.setImage(new Image("https://www.w3schools.com/howto/img_avatar.png"));
                }
                System.out.println("Chargement de l'image: " + post.getImageUrl());


                Label userName = new Label(user.getUsername());
                userName.getStyleClass().add("username");

                //userName.getStyleClass().add("username");

                header.getChildren().addAll(profilePic, userName);

                Label dateLabel = new Label("📅 " + post.getCreatedAt().toString());
                dateLabel.getStyleClass().add("post-date");


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



                // ✅ IMAGE DU POST
                ImageView imageView = new ImageView();
                imageView.setFitWidth(500);
                imageView.setPreserveRatio(true);

                if (post.getImageUrl() != null && !post.getImageUrl().isEmpty()) {
                    try {
                        // Vérifier si l'URL commence par "http://" ou "https://"
                        if (post.getImageUrl().startsWith("http://") || post.getImageUrl().startsWith("https://")) {
                            // Charger l'image distante
                            Image postImage = new Image(post.getImageUrl(), true); // true pour charger de façon asynchrone
                            imageView.setImage(postImage);
                        } else {
                            // Si ce n'est pas une URL web valide, charger comme chemin local
                            String path = post.getImageUrl().replace("\\", "/"); // Remplacer les antislashs par des slashs
                            if (!path.startsWith("file:///")) {
                                path = "file:///" + path; // Ajouter "file:///" au début du chemin
                            }
                            Image postImage = new Image(path); // Charger l'image locale
                            imageView.setImage(postImage);
                        }
                    } catch (Exception e) {
                        System.out.println("⚠️ Erreur chargement image post ID " + post.getId());
                        e.printStackTrace();
                    }
                }


                // 🔥 Bouton "J'aime" avec réactions
                Button reactionButton = new Button("👍 Like");

                // ✅ BOUTONS ACTIONS
                HBox actions = new HBox(20);
                actions.getStyleClass().add("action-buttons");



                Button likeButton = new Button("👍 J'aime");
                //likeButton.getStyleClass().add("like-button");

                // 🔥 Récupérer le nombre total de commentaires
                int commentCount = postService.countComments(post.getId());
                // 🔴 Mettre à jour le texte du bouton "Commenter" avec le nombre
                Button commentButton = new Button();
                commentButton.setText( "💬" + postService.countComments(post.getId()) + " Comment");

                commentButton.getStyleClass().add("comment-button");

                Button editButton = new Button("✏️ Modifier");
                editButton.getStyleClass().add("edit-button");
                editButton.setOnAction(event -> ouvrirModificationPost(post));


                Button deleteButton = new Button("❌ Supprimer");
                deleteButton.getStyleClass().add("delete-button");




                deleteButton.setOnAction(event -> confirmerSuppression(post, id_User));
                commentButton.setOnAction(event -> voirCommentaires(post.getId()));

                actions.getChildren().addAll( commentButton, editButton , deleteButton);

                // ✅ AJOUTER TOUS LES ÉLÉMENTS AU POST CONTAINER
                //postContainer.getChildren().addAll(header, titleLabel, contentLabel, dateLabel, imageView, actions);
                //postsBox.getChildren().add(postContainer);
                postContainer.getChildren().addAll(header, dateLabel, titleLabel, postWebView,  imageView, actions);
                postsBox.getChildren().add(postContainer);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void ouvrirModificationPost(Post post) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierPost.fxml"));
            Parent root = loader.load();

            ModifierPostController controller = loader.getController();
            controller.initData(post, this); // Passer le post à modifier et le contrôleur pour rafraîchir

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Modifier le Post");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    private void confirmerSuppression(Post post, int id_User) {
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
                    afficherPosts(id_User);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @FXML
    private void voirCommentaires(int postId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ShowCommentsMyPosts.fxml"));
            Parent root = loader.load();

            ShowCommentsMyPostsController controller = loader.getController();

            controller.setPostId(postId);
            controller.setAfficherMyPostsController(this); // ✅ Passer la référence de AfficherMyPostsController

            // 🔥 Récupérer l'utilisateur connecté
            int currentUserId = 1; // Remplace 1 par l'ID réel après login
            User userConnected = serviceUser.getUserById(currentUserId);

            if (userConnected != null) {
                controller.setUserProfileImage(userConnected.getImage_url());
            } else {
                System.out.println("⚠️ Aucun utilisateur connecté trouvé !");
            }


            // Afficher les commentaires dans le panneau de droite
            commentSection.getChildren().setAll(root);


            // Rendre la section visible
            commentSection.setVisible(true);
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
