package org.gymify.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.gymify.entities.User;
import org.gymify.utils.AuthToken;

import java.io.IOException;

import static org.gymify.utils.AuthToken.logout;

public class InterfaceEntraineurController {

    @FXML
    private Button EditId;

    @FXML
    private Label emailid;

    @FXML
    private AnchorPane infouserid;

    @FXML
    private ImageView profileImage;

    @FXML
    private Label usernameid;

    private User loggedInUser; // Stocke l'entraîneur connecté

    /**
     * Initialisation du contrôleur.
     * Récupère automatiquement l'entraîneur connecté via AuthToken.
     */
    @FXML
    public void initialize() {
        User user = AuthToken.getCurrentUser();
        if (user != null) {
            setUser(user);
        } else {
            System.out.println("⚠ Aucun entraîneur connecté !");
        }
    }

    /**
     * Met à jour les informations du profil avec l'entraîneur connecté.
     */
    public void setUser(User user) {
        if (user == null) {
            System.out.println("❌ Erreur : Aucun utilisateur reçu !");
            return;
        }
        this.loggedInUser = user;

        // Debugging
        System.out.println("✅ Entraîneur connecté : " + user.getNom() + " - " + user.getEmail());

        updateUI();
    }
    @FXML
    private void onLogoutButtonClick(ActionEvent event) {
        AuthToken.setCurrentUser(null);
        logout();

        try {
            Parent root = FXMLLoader.load(getClass().getResource("/Login.fxml"));
            ((Node) event.getSource()).getScene().setRoot(root);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }


    /**
     * Mise à jour de l'interface avec les infos de l'entraîneur connecté.
     */
    private void updateUI() {
        if (profileImage == null) {
            System.out.println("⚠ profileImage est NULL, vérifie le fichier FXML !");
            return;
        }

        if (loggedInUser != null) {
            usernameid.setText(loggedInUser.getNom());
            emailid.setText(loggedInUser.getEmail());

            String imageUrl = loggedInUser.getImageURL();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                try {
                    profileImage.setImage(new Image(imageUrl, true));
                } catch (Exception e) {
                    profileImage.setImage(new Image(getClass().getResource("/images/icons8-user-32.png").toString(), true));
                }
            } else {
                profileImage.setImage(new Image(getClass().getResource("/images/icons8-user-32.png").toString(), true));
            }
        }
    }

    /**
     * Ouvre l'interface de modification du profil.
     * Vérifie si un utilisateur est bien connecté avant.
     */
    @FXML
    void editProfile(ActionEvent event) {
        if (loggedInUser == null) {
            System.out.println("⚠ Aucun entraîneur connecté pour l'édition !");
            return;
        }

        System.out.println("✏️ Edition du profil de : " + loggedInUser.getNom());

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/EditUser.fxml"));
            Parent root = loader.load();

            // Passer l'utilisateur à l'interface de modification
            EditUserControllers editController = loader.getController();
            editController.setUser(loggedInUser);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Modifier Profil");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("❌ Erreur lors de l'ouverture de l'éditeur de profil.");
        }
    }

    public void showPlanning(ActionEvent event) {
        try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/plannings.fxml"));
        Parent root = loader.load();

        // Récupérer le contrôleur et passer l'ID de l'utilisateur
        planningController planningController = loader.getController();
        planningController.setUser(loggedInUser); // Supposons que getId() existe

        // Changer la scène
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Planning");
        stage.show();
    } catch (IOException e) {
        e.printStackTrace();
        System.out.println("❌ Erreur lors de l'ouverture de la page de planning.");
    }

    }
}
