package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.stage.Stage;


import java.io.IOException;


public class HomeBlogController {
    @FXML
    private Button btnMyPosts;  // Declare the btnMyPosts button


    @FXML
    private ImageView audience;

    @FXML
    private Button btnAddPost;

    @FXML
    private Button btnCustomers;

    @FXML
    private Button btnMenus;

    @FXML
    private Button btnOverview;

    @FXML
    private Button btnPackages;

    @FXML
    private Button btnSettings;

    @FXML
    private Button btnSignout;

    @FXML
    private Label date;

    @FXML
    private ImageView imgLike;

    @FXML
    private ImageView imgPost;

    @FXML
    private ImageView imgReaction;

    @FXML
    private ImageView imgVerified;

    @FXML
    private HBox likeContainer;

    @FXML
    private VBox pnItems;

    @FXML
    private Label usernamee;

    @FXML
    void handleAddPost(ActionEvent event) {
        try {
            // Charger le fichier FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterPost.fxml"));
            Parent root = loader.load();

            // Créer une nouvelle scène
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Ajouter un Post");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML

    void handleClicks(ActionEvent event) {
        if (event.getSource() == btnCustomers) {
            // Do something for btnCustomers if needed
        } else if (event.getSource() == btnMenus) {
            // Do something for btnMenus if needed
        } else if (event.getSource() == btnOverview) {
            // Do something for btnOverview if needed
        } else if (event.getSource() == btnPackages) {
            // Do something for btnPackages if needed
        } else if (event.getSource() == btnSettings) {
            // Do something for btnSettings if needed
        } else if (event.getSource() == btnSignout) {
            // Do something for btnSignout if needed
        } else if (event.getSource() == btnAddPost) {
            handleAddPost(event);
        } else if (event.getSource() == btnMyPosts) {
            try {
                // Charger le fichier FXML pour AfficherMyPostsController
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherMyPosts.fxml"));
                Parent root = loader.load();

                // Créer une nouvelle scène
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Afficher mes posts");
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @FXML
    void onLikeContainerMouseReleased(MouseEvent event) {

    }

    @FXML
    void onLikeContainerPressed(MouseEvent event) {

    }

    @FXML
    void onReactionImgPressed(MouseEvent event) {

    }

}
