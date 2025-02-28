package org.gymify.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.gymify.entities.User;
import org.gymify.services.ServiceUser;

import java.io.IOException;
public class SignupControllers {

    @FXML
    private PasswordField ConfPasswdField;

    @FXML
    private TextField EmailField;

    @FXML
    private TextField LastNameTextField;

    @FXML
    private PasswordField PasswdField;

    @FXML
    private Button RegisterButton;

    @FXML
    private TextField UserNameTextField;

    @FXML
    private Label errorConfirmPassword;

    @FXML
    private Label errorEmail;

    @FXML
    private Label errorNom;

    @FXML
    private Label errorPassword;

    @FXML
    private Label errorPrenom;

    private final ServiceUser serviceUser = new ServiceUser();

    @FXML
    public void initialize() {
        // ✅ Vérification instantanée de l'email lors du changement de focus
        EmailField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) { // L'utilisateur a quitté le champ email
                validerEmail();
            }
        });
        ConfPasswdField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) { // L'utilisateur a quitté le champ confirmation mot de passe
                validerMotDePasse();
            }
        });
    }
    private void validerMotDePasse() {
        String password = PasswdField.getText();
        String confirmPassword = ConfPasswdField.getText();

        if (confirmPassword.isEmpty()) {
            errorConfirmPassword.setText("❌ La confirmation du mot de passe est requise.");
        } else if (!password.equals(confirmPassword)) {
            errorConfirmPassword.setText("❌ Les mots de passe ne correspondent pas.");
        } else {
            errorConfirmPassword.setText(""); // Supprimer l'erreur si tout est bon
        }
    }
    /**
     * 🔹 Validation de l'email en temps réel
     */
    private void validerEmail() {
        String email = EmailField.getText().trim();

        if (email.isEmpty()) {
            errorEmail.setText("❌ L'email est requis.");
        } else if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            errorEmail.setText("❌ Email invalide. Exemple : exemple@email.com");
        } else if (emailExiste(email)) { // Vérifier si l'email est déjà utilisé
            errorEmail.setText("❌ Cet email est déjà utilisé.");
        } else {
            errorEmail.setText(""); // Supprimer l'erreur si tout est bon
        }
    }

    @FXML
    void ButtonRegisterOnAction(ActionEvent event) {
        System.out.println("➡ Bouton Inscription cliqué !");

        // ✅ Vérifier une dernière fois avant d'envoyer
        validerEmail();
        validerMotDePasse();
        boolean hasError = false;

        // ✅ Récupération des valeurs
        String nom = UserNameTextField.getText();
        String prenom = LastNameTextField.getText();
        String email = EmailField.getText();
        String password = PasswdField.getText();
        String confirmPassword = ConfPasswdField.getText();

        // ✅ Vérification des champs vides
        if (nom.isEmpty()) {
            errorNom.setText("❌ Ce champ est requis.");
            hasError = true;
        }
        if (prenom.isEmpty()) {
            errorPrenom.setText("❌ Ce champ est requis.");
            hasError = true;
        }
        if (!errorEmail.getText().isEmpty()) { // Vérification déjà faite en temps réel
            hasError = true;
        }
        if (password.isEmpty()) {
            errorPassword.setText("❌ Ce champ est requis.");
            hasError = true;
        }
        if (confirmPassword.isEmpty()) {
            errorConfirmPassword.setText("❌ Ce champ est requis.");
            hasError = true;
        } else if (!password.equals(confirmPassword)) {
            errorConfirmPassword.setText("❌ Les mots de passe ne correspondent pas.");
            hasError = true;
        }

        // 🚨 Arrêter l'exécution si une erreur est détectée
        if (hasError) {
            return;
        }

        // ✅ Création de l'utilisateur
        User newUser = new User(nom, prenom, password, email, "sportif");

        try {
            boolean success = serviceUser.inscrire(newUser);
            if (success) {
                System.out.println("✅ Inscription réussie !");
                showAlert(Alert.AlertType.INFORMATION, "✅ Succès", "Inscription réussie ! Vous pouvez vous connecter.");
                ouvrirInterface("Login.fxml", "🔑 Connexion");
            } else {
                showAlert(Alert.AlertType.ERROR, "❌ Erreur", "Cet email est déjà utilisé. Essayez un autre.");
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "❌ Erreur", "Problème avec la base de données.");
            e.printStackTrace();
        }
    }

    /**
     * 🔹 Vérifie si un email est déjà utilisé dans la base de données.
     */
    private boolean emailExiste(String email) {
        return serviceUser.emailExiste(email);
    }

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
}
