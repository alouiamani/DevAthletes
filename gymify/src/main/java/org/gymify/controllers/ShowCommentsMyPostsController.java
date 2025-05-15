package org.gymify.controllers;

import org.gymify.entities.Comment;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.gymify.services.CommentService;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.gymify.services.ServiceUser; // ✅ Importer le service utilisateur
import org.gymify.entities.User; // ✅ Importer l'entité User

public class ShowCommentsMyPostsController {


    @FXML
    private Label successMessage;

    @FXML
    private VBox commentContainer;

    @FXML
    private TextField commentField;

    @FXML
    private ImageView userProfileImage;

    @FXML
    private Label commentCountLabel;
    @FXML
    private Button btnClose;

    private AfficherMyPostsController afficherMyPostsController;

    public void setAfficherMyPostsController(AfficherMyPostsController controller) {
        this.afficherMyPostsController = controller;
    }

    private final ServiceUser serviceUser = new ServiceUser(); // ✅ Service pour récupérer les utilisateurs

    private final CommentService commentService = new CommentService();
    private int postId;

    public void setPostId(int postId) {
        this.postId = postId;
        try {
            loadComments();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadComments() throws SQLException {
        commentContainer.getChildren().clear();
        List<Comment> comments = commentService.afficherParPostId(postId); // 🔥 Filtrer par postId


        // 🔥 Mettre à jour le nombre total de commentaires
        int totalComments = comments.size();
        commentCountLabel.setText("💬 " + totalComments + " Commentaires");


        for (Comment comment : comments) {
            HBox commentBox = new HBox(10);
            commentBox.setStyle("-fx-padding: 10; -fx-background-color: #3A3B3C; -fx-background-radius: 10;");

            ImageView profileImage = new ImageView();
            profileImage.setFitWidth(40);
            profileImage.setFitHeight(40);

            // 🔥 Charger l'image de profil
            if (comment.getUserImageUrl() != null && !comment.getUserImageUrl().isEmpty()) {
                try {
                    profileImage.setImage(new Image(comment.getUserImageUrl(), true));
                } catch (Exception e) {
                    System.out.println("⚠️ Erreur chargement image user " + comment.getId_User());
                    profileImage.setImage(new Image("https://www.w3schools.com/howto/img_avatar.png"));
                }
            } else {
                profileImage.setImage(new Image("https://www.w3schools.com/howto/img_avatar.png"));
            }

            // 🔴 Appliquer un clip circulaire
            profileImage.setClip(new javafx.scene.shape.Circle(20, 20, 20));

            VBox textContainer = new VBox();

            // 🔥 Récupérer l'utilisateur et son vrai nom
            User user = serviceUser.getUserById(comment.getId_User());
            String username = (user != null) ? user.getUsername() : "Utilisateur inconnu";

            Label userName = new Label(username); // ✅ Afficher le vrai nom
            userName.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

            Label commentText = new Label(comment.getContent());
            commentText.setStyle("-fx-text-fill: white;");

            // 🔥 Récupérer et formater la date de création
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy 'at' HH:mm");
            String formattedDate = dateFormat.format(comment.getCreatedAt());
            Label dateLabel = new Label(formattedDate);
            dateLabel.setStyle("-fx-text-fill: #B0B3B8; -fx-font-size: 12px;");


            // 🗑️ Ajouter une icône "Trash" pour supprimer le commentaire
            ImageView trashIcon = new ImageView(new Image("assets/trash_icon.png"));
            trashIcon.setFitWidth(20);
            trashIcon.setFitHeight(20);
            trashIcon.setStyle("-fx-cursor: hand;"); // Changer le curseur pour montrer que c'est cliquable

// ✅ Action pour supprimer le commentaire
            trashIcon.setOnMouseClicked(event -> {


                try {
                    commentService.supprimer(comment.getId()); // 🔥 Supprime le commentaire
                    loadComments(); // 🔄 Rafraîchir l'affichage des commentaires
                    updateCommentCount(); // 🔄 Mettre à jour le compteur des commentaires

                    // ✅ Rafraîchir les posts pour mettre à jour le compteur dans AfficherMyPostsController
                    if (afficherMyPostsController != null) {
                        afficherMyPostsController.afficherPosts(1); // Remplace 1 par l'ID réel de l'utilisateur
                    }

                    // ✅ Afficher le message de succès
                    Platform.runLater(() -> {
                        successMessage.setOpacity(1.0);
                        successMessage.setText("✅ Commentaire supprimé avec succès !");
                    });

                    // ⏳ Faire disparaître le message après 2 secondes
                    new Thread(() -> {
                        try {
                            Thread.sleep(2000);
                            Platform.runLater(() -> successMessage.setOpacity(0));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }).start();

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });


            // ✅ TOUS les commentaires auront une icône "Trash"
            HBox commentHeader = new HBox(10, userName, trashIcon);
            textContainer.getChildren().addAll(commentHeader, dateLabel, commentText);





            commentBox.getChildren().addAll(profileImage, textContainer);

            commentContainer.getChildren().add(commentBox);
        }
    }

    @FXML
    private void handleSendComment() {
        String content = commentField.getText().trim();
        if (!content.isEmpty()) {
            try {
                Comment newComment = new Comment(0, 1, postId, content, new java.sql.Timestamp(System.currentTimeMillis()), null);
                commentService.ajouter(newComment);


                // ✅ Vider le champ de texte
                commentField.clear();
                // ✅ Rafraîchir la liste des commentaires
                loadComments();
                // ✅ Mettre à jour le compteur de commentaires

                updateCommentCount(); // 🔄 Mettre à jour le compteur
                // ✅ Rafraîchir l'affichage du post dans AfficherMyPostsController
                if (afficherMyPostsController != null) {
                    afficherMyPostsController.afficherPosts(1); // Remplace 1 par l'ID réel de l'utilisateur

                    // ✅ Afficher un message de succès
                    successMessage.setText("✅ Commentaire ajouté avec succès !");
                    successMessage.setOpacity(1.0);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateCommentCount() {
        try {
            int totalComments = commentService.afficherParPostId(postId).size();
            commentCountLabel.setText("💬 " + totalComments + " Commentaires");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void setUserProfileImage(String imageUrl) {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            try {
                userProfileImage.setImage(new Image(imageUrl, true));
            } catch (Exception e) {
                System.out.println("⚠️ Erreur chargement image user connecté");
                userProfileImage.setImage(new Image("https://www.w3schools.com/howto/img_avatar.png"));
            }
        } else {
            userProfileImage.setImage(new Image("https://www.w3schools.com/howto/img_avatar.png"));
        }

        // 🔴 Appliquer un clip circulaire pour l'image de profil en bas
        userProfileImage.setClip(new javafx.scene.shape.Circle(20, 20, 20));
    }

    @FXML
    private void handleClose(ActionEvent event) {
        // Vérifier si la section des commentaires existe et la cacher au lieu de la supprimer
        commentContainer.getParent().setVisible(false);
    }
}

