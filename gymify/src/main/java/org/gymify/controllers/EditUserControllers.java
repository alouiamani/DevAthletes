package org.gymify.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.gymify.entities.User;
import org.gymify.services.ServiceUser;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class EditUserControllers {

    @FXML
    private DatePicker DateBirthFx;

    @FXML
    private TextField EmailFx;

    @FXML
    private Button EnregistrerButtFx;

    @FXML
    private TextField FirstnameFx;

    @FXML
    private TextField ImageURLFx;

    @FXML
    private ImageView profilePreview;

    @FXML
    private Button importImageButton;

    @FXML
    private TextField LastnameFx;

    @FXML
    private Label SuccessMessageFx;
    @FXML
    private Button cancelButton;
    // Correction du type

    private User currentUser;
    private final ServiceUser serviceUser = new ServiceUser() {
        @Override
        public List<User> afficher() throws SQLException {
            return List.of();
        }
    };

    /**
     * Récupère les informations de l'utilisateur et les affiche dans le formulaire.
     */
    public void setUser(User user) {
        if (user != null) {
            this.currentUser = user;
            FirstnameFx.setText(user.getNom());
            LastnameFx.setText(user.getPrenom());

            if (user.getDate_naissance() != null) {
                DateBirthFx.setValue(user.getDate_naissance().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            }

            // Charger l'image actuelle de l'utilisateur
            if (user.getImage_url() != null && !user.getImage_url().isEmpty()) {
                profilePreview.setImage(new Image(user.getImage_url()));
                ImageURLFx.setText(user.getImage_url());
            }
        }
    }

    /**
     * Met à jour l'aperçu de l'image de profil.
     */
    @FXML
    void importerImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner une image");

        // Filtre pour ne sélectionner que les fichiers image
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp")
        );

        // Ouvrir la boîte de dialogue pour choisir une image
        File selectedFile = fileChooser.showOpenDialog(new Stage());

        if (selectedFile != null) {
            String imagePath = selectedFile.toURI().toString(); // Convertir en URL utilisable
            ImageURLFx.setText(imagePath); // Afficher le chemin dans le TextField
            profilePreview.setImage(new Image(imagePath)); // Charger et afficher l'image
        }
    }

    /**
     * Enregistre les modifications apportées au profil de l'utilisateur.
     */
    @FXML
    void saveChanges(ActionEvent event) {
        try {
            currentUser.setNom(FirstnameFx.getText());
            currentUser.setPrenom(LastnameFx.getText());

            if (DateBirthFx.getValue() != null) {
                currentUser.setDate_naissance(Date.from(DateBirthFx.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()));
            }

            if (ImageURLFx.getText() != null && !ImageURLFx.getText().isEmpty()) {
                currentUser.setImage_url(ImageURLFx.getText()); // Mettre à jour l'URL de l'image
            }

            // Mise à jour dans la base de données
            serviceUser.modifier(currentUser);

            // Affichage du message de succès
            SuccessMessageFx.setText("✅ Profil mis à jour !");
            SuccessMessageFx.setStyle("-fx-text-fill: green;");

            // Charger la page du profil après modification
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ProfileMembre.fxml"));
            Parent root = loader.load();
            ProfileMembreController profileController = loader.getController();
            profileController.setUser(currentUser); // Mettre à jour l'affichage du profil

            // Obtenir la scène actuelle et charger le nouveau contenu
            Stage stage = (Stage) EnregistrerButtFx.getScene().getWindow();
            stage.getScene().setRoot(root);

        } catch (SQLException | IOException e) {
            SuccessMessageFx.setText("❌ Erreur lors de la mise à jour.");
            SuccessMessageFx.setStyle("-fx-text-fill: red;");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancel(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/ProfileMembre.fxml"));
        ((Node) event.getSource()).getScene().setRoot(root);
    }
}
