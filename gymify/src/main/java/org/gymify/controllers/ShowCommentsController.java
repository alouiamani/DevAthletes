package org.gymify.controllers;

import org.gymify.entities.Comment;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.gymify.services.CommentService;
import org.gymify.services.RanymEmailService;

import org.gymify.services.ServiceUser;
import org.gymify.entities.User;

import java.text.SimpleDateFormat;
import java.sql.SQLException;
import java.util.List;

public class ShowCommentsController {

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

    private int editingCommentId = -1;
    private AfficherAllPostsController afficherAllPostsController;

    public void setAfficherAllPostsController(AfficherAllPostsController controller) {
        this.afficherAllPostsController = controller;
    }

    private int currentUserId;
    public void setCurrentUserId(int id_User) {
        this.currentUserId = id_User;
        System.out.println("Utilisateur connecté ID: " + currentUserId);
    }

    private final RanymEmailService emailService = new RanymEmailService();
    private final ServiceUser serviceUser = new ServiceUser();
    private final CommentService commentService = new CommentService();
    private int postId;




    public void setPostId(int postId) {
        this.postId = postId;
        try {
            loadComments();  // Charger les commentaires dès que le postId est défini
            updateCommentCount();  // Mettre à jour le nombre de commentaires
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void loadComments() throws SQLException {
        commentContainer.getChildren().clear();
        commentContainer.requestLayout();
        List<Comment> comments = commentService.afficherParPostId(postId);
        System.out.println("Chargement des commentaires...");

        int totalComments = comments.size();
        commentCountLabel.setText("💬 " + totalComments + " Commentaires");

        for (Comment comment : comments) {
            HBox commentBox = new HBox(10);
            commentBox.setStyle("-fx-padding: 10; -fx-background-color: #3A3B3C; -fx-background-radius: 10;");

            ImageView profileImage = new ImageView();
            profileImage.setFitWidth(40);
            profileImage.setFitHeight(40);

            // Charger l'image de profil
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

            profileImage.setClip(new javafx.scene.shape.Circle(20, 20, 20));

            VBox textContainer = new VBox();
            User user = serviceUser.getUserById(comment.getId_User());
            String username = (user != null) ? user.getUsername() : "Utilisateur inconnu";

            Label userName = new Label(username);
            userName.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

            Label commentText = new Label();
            commentText.setStyle("-fx-text-fill: white;");

            // Formater la date de création
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy 'at' HH:mm");
            String formattedDate = dateFormat.format(comment.getCreatedAt());
            Label dateLabel = new Label(formattedDate);
            dateLabel.setStyle("-fx-text-fill: #B0B3B8; -fx-font-size: 12px;");

            // Analyser la toxicité du commentaire
            float toxicityScore = 0;
            try {
                toxicityScore = commentService.analyzeCommentToxicity(comment.getContent());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            // Si la toxicité est entre 0.4 et 0.6, afficher "Contenu sensible"
            if (toxicityScore > 0.4 && toxicityScore <= 0.6) {
                commentText.setText("Contenu sensible à afficher");
                commentText.setStyle("-fx-text-fill: orange;");
            } else if (toxicityScore > 0.6) {
                commentText.setText("Contenu supprimé pour toxicité excessive");
                commentText.setStyle("-fx-text-fill: red;");
                commentBox.setVisible(false);  // Masquer si trop toxique
            } else {
                commentText.setText(comment.getContent());
            }

            // Icône pour modifier un commentaire
            ImageView editIcon = new ImageView(new Image("assets/edit_icon.png"));
            editIcon.setFitWidth(20);
            editIcon.setFitHeight(20);
            editIcon.setStyle("-fx-cursor: hand;");

            // Ensure toxicityScore is final or effectively final for use in lambda
            final float toxicityScoreFinal = toxicityScore;

            editIcon.setOnMouseClicked(event -> {
                if (toxicityScoreFinal <= 0.4) {
                    commentField.setText(comment.getContent());
                } else if (toxicityScoreFinal > 0.4 && toxicityScoreFinal <= 0.6) {
                    // Permettre la modification du commentaire même si c'est sensible
                    commentField.setText(comment.getContent());
                    successMessage.setText("⚠️ Votre commentaire est sensible, mais il peut être modifié.");
                    successMessage.setOpacity(1.0);
                }
                editingCommentId = comment.getId();
            });


            // Icône de suppression
            ImageView trashIcon = new ImageView(new Image("assets/trash_icon.png"));
            trashIcon.setFitWidth(20);
            trashIcon.setFitHeight(20);
            trashIcon.setStyle("-fx-cursor: hand;");

            trashIcon.setOnMouseClicked(event -> {
                try {
                    if (toxicityScoreFinal <= 0.4) {
                        commentService.supprimer(comment.getId());
                        loadComments();
                        updateCommentCount();

                        if (afficherAllPostsController != null) {
                            afficherAllPostsController.afficherTousPosts();
                        }

                        Platform.runLater(() -> {
                            successMessage.setOpacity(1.0);
                            successMessage.setText("✅ Commentaire supprimé avec succès !");
                        });

                        new Thread(() -> {
                            try {
                                Thread.sleep(2000);
                                Platform.runLater(() -> successMessage.setOpacity(0));
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }).start();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });

            // 🔥 Vérifier si l'utilisateur actuel est le propriétaire du commentaire
            int currentUserId = 1; // Remplace ça par l'ID de l'utilisateur connecté
            if (comment.getId_User() == currentUserId) {
                HBox commentHeader = new HBox(10, userName, editIcon ,trashIcon);
                textContainer.getChildren().addAll(commentHeader, dateLabel, commentText);
            } else {
                textContainer.getChildren().addAll(userName, dateLabel, commentText);
            }

            commentBox.getChildren().addAll(profileImage, textContainer);

            commentContainer.getChildren().add(commentBox);
        }
        // Forcer le rafraîchissement de l'interface après le chargement des commentaires
        Platform.runLater(() -> {
            commentContainer.requestLayout();
            commentContainer.setVisible(true);
        });

    }



    @FXML
    private void handleSendComment() {
        String content = commentField.getText().trim();
        if (!content.isEmpty()) {
            try {
                String message = "";

                // Analyser la toxicité du commentaire
                float toxicityScore = commentService.analyzeCommentToxicity(content);

                // Si la toxicité est trop élevée (supérieure à 0.6), ne pas ajouter le commentaire
                if (toxicityScore > 0.6) {
                    message = "❌ Commentaire supprimé pour toxicité excessive.";

                    // Si le commentaire est trop toxique, ne pas l'ajouter
                    if (editingCommentId != -1) {
                        commentService.supprimer(editingCommentId); // Supprimer l'ancien commentaire
                        loadComments();
                        updateCommentCount();
                        editingCommentId = -1;  // Réinitialisation de l'ID du commentaire en cours d'édition
                    }

                    // Envoi d'un e-mail pour avertir l'utilisateur
                    String subject = "⚠️ Commentaire supprimé pour non-respect des règles";
                    String emailMessage = "Votre commentaire a été jugé toxique et a été supprimé.";
                    String userEmail = commentService.getUserEmail(currentUserId);
                    RanymEmailService.sendEmail(userEmail, subject, emailMessage);

                } else if (toxicityScore > 0.4) {
                    message = "⚠️ Votre commentaire contient un contenu sensible. Veuillez le modifier si nécessaire.";

                    // Ajouter le commentaire réel à la base de données sans modification
                    if (editingCommentId != -1) {
                        // Modifier le commentaire existant avec le nouveau contenu
                        Comment updatedComment = new Comment(
                                editingCommentId, postId, currentUserId, content,
                                new java.sql.Timestamp(System.currentTimeMillis()), null
                        );
                        commentService.modifier(updatedComment);  // Mise à jour dans la base de données
                        message = "⚠️ Commentaire modifié avec succès !";
                        editingCommentId = -1;  // Réinitialisation de l'ID du commentaire en cours d'édition
                    } else {
                        // Ajouter un nouveau commentaire si aucun commentaire n'est en cours d'édition
                        Comment newComment = new Comment(0, postId, currentUserId, content,
                                new java.sql.Timestamp(System.currentTimeMillis()), null);
                        commentService.ajouter(newComment);  // Ajout à la base de données
                        message = "✅ Commentaire ajouté avec succès !";
                    }

                    // Envoi d'un e-mail pour avertir l'utilisateur
                    String subject = "⚠️ Votre commentaire a été modéré";
                    String emailMessage = "Votre commentaire contient un contenu sensible. Veuillez le modifier si nécessaire.";
                    String userEmail = commentService.getUserEmail(currentUserId);
                    emailService.sendEmail(userEmail, subject, emailMessage);

                    // Réinitialiser le champ de commentaire après la modération
                    commentField.clear();
                    loadComments();  // Recharger les commentaires
                    updateCommentCount();  // Mettre à jour le compteur de commentaires
                    successMessage.setText(message);
                    successMessage.setOpacity(1.0);

                    // Réduire la visibilité du message de succès après quelques secondes
                    new Thread(() -> {
                        try {
                            Thread.sleep(500);
                            Platform.runLater(() -> successMessage.setOpacity(0));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }).start();

                } else {
                    // Si nous éditons un commentaire existant
                    if (editingCommentId != -1) {
                        Comment updatedComment = new Comment(
                                editingCommentId, postId, currentUserId, content,
                                new java.sql.Timestamp(System.currentTimeMillis()), null
                        );
                        commentService.modifier(updatedComment);  // Mise à jour du commentaire
                        editingCommentId = -1;  // Réinitialisation de l'ID du commentaire en cours d'édition
                        message = "✏️ Commentaire modifié avec succès !";
                    } else {
                        // Ajouter un nouveau commentaire avec le contenu réel
                        Comment newComment = new Comment(0, postId, currentUserId, content,
                                new java.sql.Timestamp(System.currentTimeMillis()), null);
                        commentService.ajouter(newComment);  // Assurez-vous que cette méthode fonctionne
                        message = "✅ Commentaire ajouté avec succès !";
                    }
                }

                // Effacer le champ de commentaire et mettre à jour l'interface utilisateur
                commentField.clear();
                loadComments();
                updateCommentCount();

                if (afficherAllPostsController != null) {
                    afficherAllPostsController.afficherTousPosts();
                }

                successMessage.setText(message);
                successMessage.setOpacity(1.0);

                // Réduire la visibilité du message de succès après quelques secondes
                new Thread(() -> {
                    try {
                        Thread.sleep(3000);
                        Platform.runLater(() -> successMessage.setOpacity(0));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();

            } catch (SQLException e) {
                e.printStackTrace();
            } catch (Exception e) {
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

        userProfileImage.setClip(new javafx.scene.shape.Circle(20, 20, 20));
    }

    @FXML
    private void handleClose(ActionEvent event) {
        commentContainer.getParent().setVisible(false);
    }
}
