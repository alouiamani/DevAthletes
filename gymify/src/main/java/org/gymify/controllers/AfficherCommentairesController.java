package org.gymify.controllers;

import org.gymify.entities.Comment;
import org.gymify.entities.User;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.gymify.services.CommentService;
import org.gymify.services.ServiceUser;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

public class AfficherCommentairesController {
    @FXML
    private ListView<Comment> listViewCommentaires;
    @FXML
    private Button fermerButton;

    private final CommentService commentService = new CommentService();
    private final ServiceUser serviceUser = new ServiceUser();
    private int postId;

    public void setPostId(int postId) {
        this.postId = postId;
        chargerCommentaires();

        // Vérifie si la scène est disponible avant de modifier le titre
        if (listViewCommentaires.getScene() != null) {
            Stage stage = (Stage) listViewCommentaires.getScene().getWindow();
            stage.setTitle("Commentaires du post");
        }
    }

    private void chargerCommentaires() {
        try {
            List<Comment> commentaires = commentService.afficher();
            listViewCommentaires.getItems().clear();

            for (Comment comment : commentaires) {
                if (comment.getPostId() == postId) {
                    listViewCommentaires.getItems().add(comment);
                }
            }

            // Personnalisation des cellules
            listViewCommentaires.setCellFactory(param -> new ListCell<>() {
                private final HBox container = new HBox(10);
                private final ImageView profilePic = new ImageView();
                private final VBox textContainer = new VBox(5);
                private final Text usernameText = new Text();
                private final Text dateText = new Text();
                private final Text commentText = new Text();


                {
                    container.getStyleClass().add("comment-container");

                    profilePic.setFitWidth(45);
                    profilePic.setFitHeight(45);

                    usernameText.getStyleClass().add("username-text");
                    commentText.getStyleClass().add("comment-text");
                    dateText.getStyleClass().add("date-text");

                    textContainer.getChildren().addAll(usernameText, dateText, commentText);
                    container.getChildren().addAll(profilePic, textContainer);
                }

                @Override
                protected void updateItem(Comment comment, boolean empty) {
                    super.updateItem(comment, empty);

                    if (empty || comment == null) {
                        setGraphic(null);
                    } else {

                            // Récupérer l'utilisateur associé au commentaire
                            User user = serviceUser.getUserById(comment.getId_User());

                            // Vérifie si l'utilisateur est trouvé
                            String username = (user != null) ? user.getNom() + " " + user.getPrenom() : "Utilisateur inconnu";
                            usernameText.setText(username);
                            commentText.setText(comment.getContent());

                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM à HH:mm");
                            dateText.setText(dateFormat.format(comment.getCreatedAt()));



                            setGraphic(container); // Définir le graphique de la cellule




                    }
                }
            });

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void fermerFenetre() {
        Stage stage = (Stage) fermerButton.getScene().getWindow();
        stage.close();
    }
}
