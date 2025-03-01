package org.gymify.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.gymify.entities.User;
import org.gymify.utils.AuthToken;

import java.io.IOException;

public class ProfileMembreController {

    @FXML
    private Button EditId;

    @FXML
    private Label emailid;

    @FXML
    private Label usernameid;

    @FXML
    private ImageView profileImage; // Image de profil

    private User loggedInUser; // Stocke l'utilisateur connecté

    /**
     * Initialisation du contrôleur.
     * Récupère automatiquement l'utilisateur connecté via AuthToken.
     */
    @FXML
    public void initialize() {
        User user = AuthToken.getCurrentUser();
        if (user != null) {
            setUser(user);
        } else {
            System.out.println("⚠ Aucun utilisateur connecté !");
        }
    }

    /**
     * Met à jour les informations du profil avec l'utilisateur connecté.
     */
    public void setUser(User user) {
        if (user == null) {
            System.out.println("❌ Erreur : Aucun utilisateur reçu !");
            return;
        }
        this.loggedInUser = user;

        // Debugging
        System.out.println("✅ Utilisateur connecté : " + user.getNom() + " - " + user.getEmail());

        updateUI();
    }

    /**
     * Mise à jour de l'interface avec les infos de l'utilisateur connecté.
     */

    private void updateUI() {
        if (loggedInUser != null) {
            usernameid.setText(loggedInUser.getNom());
            emailid.setText(loggedInUser.getEmail());

            String imageURL = loggedInUser.getImageURL();
            if (imageURL != null && !imageURL.isEmpty()) {

                profileImage.setImage(new Image(imageURL)); // Charge directement l'image depuis l'URL

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
            System.out.println("⚠ Aucun utilisateur connecté pour l'édition !");
            return;
        }

        System.out.println("✏️ Edition du profil de : " + loggedInUser.getNom());

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/EditUser.fxml"));
            Parent root = loader.load();

            // Passer l'utilisateur à l'interface de modification
            EditUserControllers editController = loader.getController();
            editController.setUser(loggedInUser); // Passe l'utilisateur connecté

            // Ouvrir la scène d'édition
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

    @FXML
    private void ouvrirReclamation(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ReclamationSportif.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("📩 Gérer mes Réclamations");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    public void ShowPlanning(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/planningforuser.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Planning");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();

        }

    }




}
