package Gimify.Pi.Controllers;

import java.net.URL;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.ResourceBundle;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.Duration;
import Gimify.Pi.Service.ServiceUser;
import Gimify.Pi.entities.User;

public class ProfileUserController implements Initializable, UserAwareController {

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
    private Button ModifierButtFx;

    @FXML
    private Label SuccessMessageFx;  // Ajout d'un Label pour afficher le message de succès

    private static final ServiceUser serviceUser = new ServiceUser();
    private User currentUser;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Rien ici car on reçoit l'utilisateur via setUser()
    }

    @Override
    public void setUser(User user) {
        if (user == null) {
            showAlert(Alert.AlertType.ERROR, "❌ Erreur", "Impossible de récupérer l'utilisateur connecté.");
            return;
        }

        this.currentUser = user;

        // 🔹 Affichage des données de l'utilisateur
        FirstnameFx.setText(user.getNom());
        LastnameFx.setText(user.getPrenom());
        EmailFx.setText(user.getEmail());

        if (user.getDateNaissance() != null) {
            DateBirthFx.setValue(
                    Instant.ofEpochMilli(user.getDateNaissance().getTime())
                            .atZone(ZoneId.systemDefault()).toLocalDate()
            );
        }

        ImageURLFx.setText(user.getImageURL() == null ? "" : user.getImageURL());

        // Désactiver les champs par défaut
        setFieldsEditable(false);
    }

    // 🔹 Activer l'édition des champs
    @FXML
    private void modifierProfil() {
        setFieldsEditable(true);
    }

    // 🔹 Enregistrer les modifications
    @FXML
    private void enregistrerProfil() {
        try {
            // Mise à jour des informations utilisateur
            currentUser.setNom(FirstnameFx.getText());
            currentUser.setPrenom(LastnameFx.getText());
            currentUser.setEmail(EmailFx.getText());

            if (DateBirthFx.getValue() != null) {
                currentUser.setDateNaissance(
                        Date.from(DateBirthFx.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant())
                );
            }

            currentUser.setImageURL(ImageURLFx.getText());

            // Enregistrement dans la base de données
            serviceUser.modifier(currentUser);

            // Désactiver les champs après enregistrement
            setFieldsEditable(false);

            // Affichage d'un message de succès avec animation
            showAlert(Alert.AlertType.INFORMATION, "✅ Succès", "Profil mis à jour avec succès !");
            animateMessage("✅ Profil mis à jour avec succès !");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "❌ Erreur", "Erreur lors de la mise à jour du profil : " + e.getMessage());
        }
    }

    // 🔹 Activer/désactiver les champs
    private void setFieldsEditable(boolean editable) {
        FirstnameFx.setEditable(editable);
        LastnameFx.setEditable(editable);
        EmailFx.setEditable(editable);
        DateBirthFx.setDisable(!editable);
        ImageURLFx.setEditable(editable);
    }

    // 🔹 Animation pour afficher un message de succès
    private void animateMessage(String message) {
        if (SuccessMessageFx != null) {
            SuccessMessageFx.setText(message);
            SuccessMessageFx.setOpacity(0);

            FadeTransition ft = new FadeTransition(Duration.seconds(3), SuccessMessageFx);
            ft.setFromValue(0.0);
            ft.setToValue(1.0);
            ft.play();
        }
    }

    // 🔹 Affichage d'une alerte
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
