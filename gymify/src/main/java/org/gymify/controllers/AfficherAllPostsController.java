package org.gymify.controllers;

import org.gymify.entities.Post;
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
import org.gymify.services.PostService;
import org.gymify.services.ReactionService;
import org.gymify.services.ServiceUser;
import org.gymify.services.ServiceUser;
import org.gymify.entities.User;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;


import javafx.scene.shape.Circle;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class AfficherAllPostsController {


    @FXML private HBox reactionsBox;
    @FXML private ImageView likeIcon, loveIcon, hahaIcon, wowIcon, sadIcon, angryIcon;




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
    private final ServiceUser ServiceUser = new ServiceUser(); // Service pour récupérer les utilisateurs
    private int id_User = ServiceUser.getUserById(1).getId(); // Exemple, remplace 1 par l'ID réel de l'utilisateur connecté




    @FXML
    private void initialize() {
        afficherTousPosts();

        // Centrer les posts
        Platform.runLater(() -> {
            StackPane.setAlignment(postsBox, Pos.CENTER);
            postsBox.setAlignment(Pos.CENTER);
        });





    }

    public void afficherTousPosts() {
        postsBox.getChildren().clear();
        postsBox.setMaxWidth(Double.MAX_VALUE);

        try {
            List<Post> posts = postService.afficherTous();
            for (Post post : posts) {
                VBox postContainer = new VBox(10);
                postContainer.getStyleClass().add("post-container");

                // 🔥 Récupérer l'utilisateur du post
                User user = ServiceUser.getUserById(post.getId_User());

                HBox header = new HBox(10);
                header.getStyleClass().add("post-header");



                // 🔥 Image de profil
                ImageView profilePic = new ImageView();
                profilePic.setFitWidth(40);
                profilePic.setFitHeight(40);
                profilePic.getStyleClass().add("profile-pic");
                Circle clip = new Circle(20, 20, 20);
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

                HBox actions = new HBox(20);
                actions.getStyleClass().add("action-buttons");

                String userReaction = reactionService.getUserReaction(post.getId(), id_User);
                if (userReaction == null) {
                    userReaction = "like";  // Valeur par défaut si aucune réaction trouvée
                }
                // 🔥 Récupérer le nombre total de réactions
                int totalReactions = 0;
                String[] reactionTypes = {"like", "love", "haha", "wow", "sad", "angry"};

                for (String reaction : reactionTypes) {
                    totalReactions += reactionService.countReactions(post.getId(), reaction);
                }

                //Label reactionCountLabel = new Label("💬 " + totalReactions + " réaction(s)");
                reactionButton.setText(getEmoji(userReaction)+ " "+ totalReactions);

                //reactionButton.getStyleClass().add("like-button");


                // 🔥 Boîte de réactions au survol
                HBox reactionsBox = new HBox(10);
                reactionsBox.setVisible(false);
                reactionsBox.getStyleClass().add("reactions-box");

                String[] types = {"like", "love", "haha", "wow", "sad", "angry"};
                String[] emojiPaths = {
                        "/assets/like.png",
                        "/assets/love.png",
                        "/assets/haha.png",
                        "/assets/wow.png",
                        "/assets/sad.png",
                        "/assets/angry.png"
                };

                for (int i = 0; i < types.length; i++) {
                    ImageView emojiView = new ImageView(new Image(emojiPaths[i]));



                    emojiView.setFitWidth(35);
                    emojiView.setFitHeight(35);
                    final String type = types[i];
                    emojiView.setOnMouseClicked(event -> {
                        try {
                            String currentReaction = reactionService.getUserReaction(post.getId(), id_User);

                            if (currentReaction != null && currentReaction.equals(type)) {
                                // 🔥 Si l'utilisateur a déjà mis cette réaction, on la supprime
                                reactionService.supprimerReaction(post.getId(), id_User);
                                Platform.runLater(() -> {
                                    reactionButton.setText("👍 Like");  // Remet le bouton à "Like" par défaut
                                });
                            } else {
                                // ✅ Sinon, on ajoute la nouvelle réaction
                                reactionService.ajouterReaction(post.getId(), id_User, type);
                                Platform.runLater(() -> {
                                    reactionButton.setText(getEmoji(type));  // Met uniquement l'emoji
                                    reactionButton.setStyle("-fx-font-size: 18px; -fx-text-fill: black;");
                                });
                            }

                            // ✅ Masquer la boîte de réactions après la sélection
                            reactionsBox.setVisible(false);
                            afficherTousPosts(); // 🔥 Met à jour l'affichage des posts pour refléter la modification

                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    });




                    reactionsBox.getChildren().add(emojiView);
                }

                reactionButton.setOnMouseEntered(event -> reactionsBox.setVisible(true));
                reactionsBox.setOnMouseExited(event -> reactionsBox.setVisible(false));









                // 🔥 Récupérer le nombre total de commentaires
                int commentCount = postService.countComments(post.getId());
                // 🔴 Mettre à jour le texte du bouton "Commenter" avec le nombre
                Button commentButton = new Button("💬 " + commentCount + " Commenter");
                commentButton.setText( "💬" + postService.countComments(post.getId()) + " Comment");
                commentButton.getStyleClass().add("comment-button");
                Button shareButton = new Button("🔗 Partager");
                shareButton.getStyleClass().add("share-button");


                //Button deleteButton = new Button("❌ Supprimer");
                //deleteButton.getStyleClass().add("delete-button");
                //deleteButton.setOnAction(event -> confirmerSuppression(post, id_User));


                commentButton.setOnAction(event -> voirCommentaires(post.getId()));
                //deleteButton.setOnAction(event -> confirmerSuppression(post, user.getId_User()));


                actions.getChildren().addAll(commentButton,reactionButton,reactionsBox);

                postContainer.getChildren().addAll(header, dateLabel, titleLabel, postWebView,  imageView, actions);
                postsBox.getChildren().add(postContainer);
            }
        } catch (SQLException e) {
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
                    afficherTousPosts();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    @FXML
    private void voirCommentaires(int postId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ShowComments.fxml"));
            Parent root = loader.load();

            ShowCommentsController controller = loader.getController();
            controller.setPostId(postId);


            controller.setAfficherAllPostsController(this);// ✅ Passer la référence de AfficherALLPostsController
            // 🔥 Passer l'ID de l'utilisateur connecté
            controller.setCurrentUserId(id_User);
            // 🔥 Récupérer l'utilisateur connecté et son image de profil
            User userConnected = ServiceUser.getUserById(id_User);
            if (userConnected != null) {
                controller.setUserProfileImage(userConnected.getImage_url());
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
        postsBox.setPrefWidth(600);
    }



    public class JavaFXConnector {
        public void setHeight(double height) {
            Platform.runLater(() -> postWebView.setPrefHeight(height + 20));
        }
    }
    private void afficherPopupReactions(int postId) {
        Stage popup = new Stage();
        popup.setTitle("Réactions");

        HBox reactionsBox = new HBox(10);
        reactionsBox.setAlignment(Pos.CENTER);

        String[] types = {"like", "love", "haha", "wow", "sad", "angry"};
        String[] emojis = {"👍", "❤️", "😂", "😲", "😢", "😡"};

        for (int i = 0; i < types.length; i++) {
            Button btn = new Button(emojis[i]);
            final String type = types[i];
            btn.setOnAction(event -> {
                try {
                    reactionService.ajouterReaction(postId, id_User, type);
                    popup.close();
                    afficherTousPosts();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
            reactionsBox.getChildren().add(btn);
        }

        Scene scene = new Scene(reactionsBox, 300, 100);
        popup.setScene(scene);
        popup.show();
    }

    private String getEmoji(String type) {
        switch (type) {
            case "like": return "👍new";
            case "love": return "❤️";
            case "haha": return "😂";
            case "wow": return "😲";
            case "sad": return "😢";
            case "angry": return "😡";
            default: return "";
        }
    }
}