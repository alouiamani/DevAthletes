package Gimify.Pi.Controllers;

import Gimify.Pi.Service.ServiceUser;
import Gimify.Pi.entities.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class SignupControllers {

    @FXML
    private PasswordField ConfPasswdField;

    @FXML
    private TextField EmailField;

    @FXML
    private TextField LastNameTextField; // Correction du nom

    @FXML
    private PasswordField PasswdField;

    @FXML
    private Button RegisterButton;

    @FXML
    private TextField UserNameTextField;

    private final ServiceUser serviceUser = new ServiceUser();

    @FXML
    void ButtonRegisterOnAction(ActionEvent event) {
        System.out.println("➡ Bouton Inscription cliqué !");

        String nom = UserNameTextField.getText().trim();
        String prenom = LastNameTextField.getText().trim();
        String email = EmailField.getText().trim();
        String password = PasswdField.getText().trim();
        String confirmPassword = ConfPasswdField.getText().trim();

        // ✅ Vérification des champs vides
        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "⚠️ Champs manquants", "Veuillez remplir tous les champs.");
            return;
        }

        // ✅ Vérification du format email
        if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
            showAlert(Alert.AlertType.ERROR, "❌ Email invalide", "Veuillez entrer une adresse email valide.");
            return;
        }

        // ✅ Vérification de la correspondance des mots de passe
        if (!password.equals(confirmPassword)) {
            showAlert(Alert.AlertType.ERROR, "❌ Mot de passe", "Les mots de passe ne correspondent pas.");
            return;
        }

        // ✅ Création de l'utilisateur avec le rôle "sportif"
        User newUser = new User(nom, prenom, password, email, "sportif");
        System.out.println("Tentative d'inscription de : " + email);

        try {
            boolean success = serviceUser.inscrire(newUser);
            if (success) {
                System.out.println("✅ Inscription réussie !");
                showAlert(Alert.AlertType.INFORMATION, "✅ Succès", "Inscription réussie ! Vous pouvez vous connecter.");

                // ✅ Redirection vers la page de connexion
                ouvrirInterface("Login.fxml", "🔑 Connexion");
            } else {
                System.out.println("❌ Échec de l'inscription !");
                showAlert(Alert.AlertType.ERROR, "❌ Erreur", "Cet email est déjà utilisé. Essayez un autre.");
            }
        } catch (Exception e) {
            System.out.println("❌ Erreur lors de l'inscription : " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "❌ Erreur", "Problème avec la base de données.");
            e.printStackTrace();
        }
    }

    /**
     * 🔹 Méthode pour ouvrir une interface FXML
     */
    private void ouvrirInterface(String fxmlPath, String title) {
        try {
            System.out.println("➡ Ouverture de l'interface : " + fxmlPath);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/" + fxmlPath)); // Correction du chemin
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.show();

            // 🔹 Fermer la fenêtre actuelle
            Stage signUpStage = (Stage) RegisterButton.getScene().getWindow();
            signUpStage.close();

        } catch (IOException e) {
            System.out.println("❌ Erreur d'ouverture de l'interface : " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "❌ Erreur", "Impossible d'ouvrir l'interface : " + fxmlPath);
            e.printStackTrace();
        }
    }

    /**
     * 🔹 Affichage d'une alerte
     */
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    public void setUserData(User user) {
        if (user != null) {
            System.out.println("Utilisateur connecté lors de l'inscription : " + user.getEmail());
        }
    }
}
