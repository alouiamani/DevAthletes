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

import javafx.scene.layout.VBox;

import javafx.stage.Stage;
import org.gymify.entities.User;
import org.gymify.utils.AuthToken;
import org.gymify.services.SalleService;
import org.gymify.entities.Salle;

import java.io.IOException;
import java.util.List;

import static org.gymify.utils.AuthToken.logout;

public class ProfileMembreController {

    @FXML private Button EditId;
    @FXML private Label emailid;
    @FXML private Label usernameid;
    @FXML private ImageView profileImage;
    @FXML private VBox sallesContainer; // Nouveau VBox pour afficher les salles

    private User loggedInUser;

    @FXML
    public void initialize() {
        User user = AuthToken.getCurrentUser();
        if (user != null) {
            setUser(user);
        } else {
            System.out.println("⚠ Aucun utilisateur connecté !");
        }
    }

    public void setUser(User user) {
        if (user == null) {
            System.out.println("❌ Erreur : Aucun utilisateur reçu !");
            return;
        }
        this.loggedInUser = user;
        updateUI();
        chargerSalles(); // Charger les salles lorsque l'utilisateur est défini
    }

    private void updateUI() {
        if (loggedInUser != null) {
            usernameid.setText(loggedInUser.getNom());
            emailid.setText(loggedInUser.getEmail());

            String imageURL = loggedInUser.getImageURL();
            if (imageURL != null && !imageURL.isEmpty()) {
                profileImage.setImage(new Image(imageURL));
            } else {
                profileImage.setImage(new Image(getClass().getResource("/images/icons8-user-32.png").toString(), true));
            }
        }
    }


    private void chargerSalles() {
        SalleService salleService = new SalleService();
        List<Salle> salles = salleService.getAllSalles("");

        sallesContainer.getChildren().clear(); // Clear existing salles

        for (Salle salle : salles) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/SalleCardUser.fxml"));
                Parent salleCard = loader.load();
                SalleCardUserController controller = loader.getController();
                controller.setSalleData(salle, this); // Pass 'this' (ProfileMembreController instance)
                sallesContainer.getChildren().add(salleCard);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @FXML
    void editProfile(ActionEvent event) {
        if (loggedInUser == null) {
            System.out.println("⚠ Aucun utilisateur connecté pour l'édition !");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/EditUser.fxml"));
            Parent root = loader.load();

            EditUserControllers editController = loader.getController();
            editController.setUser(loggedInUser);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Modifier Profil");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
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


    public void onLogoutButtonClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Profile");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();

        }

    }


    public void editPersonalInfo(ActionEvent event) {
        try {
            // Charger la nouvelle page FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/EditPersonalInfo.fxml"));
            Parent root = loader.load();

            // Obtenir la scène actuelle à partir de l'événement
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();

            // Définir la nouvelle scène
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Edit Personal Info"); // Titre de la nouvelle fenêtre
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement de la page EditPersonalInfo.fxml");
        }
    }
    @FXML
    private void ouvrirSalle(ActionEvent event) {
        // Fait défiler la page vers la section des salles
        sallesContainer.requestFocus();
    }

    @FXML
    private void ouvrirEvent(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListeParticipation.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("📩 Gérer mes Participations");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}




