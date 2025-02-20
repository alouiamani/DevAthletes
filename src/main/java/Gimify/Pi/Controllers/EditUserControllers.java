package Gimify.Pi.Controllers;

import Gimify.Pi.Service.ServiceUser;
import Gimify.Pi.entities.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

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
    private TextField LastnameFx;

    @FXML
    private Label SuccessMessageFx;

    @FXML
    private ImageView profilePreview; // Correction du type

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
            EmailFx.setText(user.getEmail());

            if (user.getDateNaissance() != null) {
                DateBirthFx.setValue(user.getDateNaissance().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            }

            ImageURLFx.setText(user.getImageURL());
            updateImagePreview();
        }
    }

    /**
     * Met à jour l'aperçu de l'image de profil.
     */
    @FXML
    void updateImagePreview() {
        String imageUrl = ImageURLFx.getText();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            try {
                profilePreview.setImage(new Image(imageUrl, true));
            } catch (Exception e) {
                profilePreview.setImage(null);
                System.err.println("Erreur de chargement de l'image: " + e.getMessage());
            }
        } else {
            profilePreview.setImage(null);
        }
    }

    /**
     * Enregistre les modifications apportées au profil de l'utilisateur.
     */
    @FXML
    void saveChanges(ActionEvent event) {
        // Vérification des champs obligatoires
        if (FirstnameFx.getText().isEmpty() || LastnameFx.getText().isEmpty() || EmailFx.getText().isEmpty()) {
            SuccessMessageFx.setText("❌ Tous les champs doivent être remplis.");
            SuccessMessageFx.setStyle("-fx-text-fill: red;");
            return;
        }

        try {
            currentUser.setNom(FirstnameFx.getText());
            currentUser.setPrenom(LastnameFx.getText());
            currentUser.setEmail(EmailFx.getText());

            if (DateBirthFx.getValue() != null) {
                currentUser.setDateNaissance(Date.from(DateBirthFx.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()));
            }

            currentUser.setImageURL(ImageURLFx.getText());

            // Mise à jour dans la base de données
            serviceUser.modifier(currentUser);

            // Affichage du message de succès
            SuccessMessageFx.setText("✅ Profil mis à jour !");
            SuccessMessageFx.setStyle("-fx-text-fill: green;");

            // Ferme la fenêtre après la sauvegarde
            Stage stage = (Stage) EnregistrerButtFx.getScene().getWindow();
            stage.close();

        } catch (SQLException e) {
            SuccessMessageFx.setText("❌ Erreur lors de la mise à jour.");
            SuccessMessageFx.setStyle("-fx-text-fill: red;");
            e.printStackTrace();
        }
    }
}
