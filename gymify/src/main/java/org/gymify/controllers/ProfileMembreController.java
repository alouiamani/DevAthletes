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
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.gymify.entities.User;
import org.gymify.utils.AuthToken;
import org.gymify.services.SalleService;
import org.gymify.entities.Salle;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class ProfileMembreController {

    @FXML private Button EditId;
    @FXML private Label emailid;
    @FXML private Label usernameid;
    @FXML private ImageView profileImage;
    @FXML private VBox sallesContainer;

    private User loggedInUser;
    private static final Logger LOGGER = Logger.getLogger(ProfileMembreController.class.getName());

    @FXML
    public void initialize() {
        User user = AuthToken.getCurrentUser();
        if (user != null) {
            setUser(user);
        } else {
            LOGGER.warning("⚠ Aucun utilisateur connecté !");
        }
    }

    public void setUser(User user) {
        if (user == null) {
            LOGGER.severe("❌ Erreur : Aucun utilisateur reçu !");
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

            String imageURL = loggedInUser.getImage_url();
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
                if (loader.getLocation() == null) {
                    LOGGER.severe("Cannot find SalleCardUser.fxml. Ensure the file exists in src/main/resources/");
                    continue;
                }
                Parent salleCard = loader.load();
                SalleCardUserController controller = loader.getController();
                controller.setSalleData(salle, this); // Pass 'this' (ProfileMembreController instance)
                sallesContainer.getChildren().add(salleCard);
            } catch (IOException e) {
                LOGGER.severe("Erreur lors du chargement de la carte pour la salle ID " + salle.getId() + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
        if (salles.isEmpty()) {
            Label noSallesLabel = new Label("Aucune salle disponible.");
            sallesContainer.getChildren().add(noSallesLabel);
        }
    }

    @FXML
    void editProfile(ActionEvent event) {
        if (loggedInUser == null) {
            LOGGER.warning("⚠ Aucun utilisateur connecté pour l'édition !");
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
            LOGGER.severe("Erreur lors de l'ouverture de la fenêtre d'édition de profil : " + e.getMessage());
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
            LOGGER.severe("Erreur lors de l'ouverture de la fenêtre de gestion des réclamations : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void ouvrirBlogs(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Dashboard");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            LOGGER.severe("Erreur lors de l'ouverture du dashboard : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void onLogoutButtonClick(ActionEvent event) {
        try {
            AuthToken.logout(); // Clear the current user
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Connexion");
            stage.setScene(new Scene(root));
            stage.show();

            // Close the current window
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            LOGGER.severe("Erreur lors de la déconnexion : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void editPersonalInfo(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/EditPersonalInfo.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Modifier les informations personnelles");
            stage.show();
        } catch (IOException e) {
            LOGGER.severe("Erreur lors du chargement de la page EditPersonalInfo.fxml : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void ouvrirSalle(ActionEvent event) {
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
            LOGGER.severe("Erreur lors de l'ouverture de la fenêtre de gestion des participations : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void PlanningForYou(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/planningperso.fxml"));
            Parent root = loader.load();

            // Obtenir la scène actuelle à partir de l'événement
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();

            // Définir la nouvelle scène
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("planning"); // Titre de la nouvelle fenêtre
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
            System.err.println("Erreur lors du chargement de la page EditPersonalInfo.fxml");
        } try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/planningperso.fxml"));
            Parent root = loader.load();

            // Obtenir la scène actuelle à partir de l'événement
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();

            // Définir la nouvelle scène
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("planning"); // Titre de la nouvelle fenêtre
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
            System.err.println("Erreur lors du chargement de la page EditPersonalInfo.fxml");
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

    @FXML
    private void ouvrirProduit() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ProductCatalogSportif.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Boutique des Produits");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Impossible d'ouvrir la boutique des produits: " + e.getMessage());
            alert.showAndWait();
        }
    }
}