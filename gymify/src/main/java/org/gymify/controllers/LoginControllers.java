package org.gymify.controllers;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.gymify.entities.User;
import org.gymify.services.ServiceUser;
import org.gymify.utils.AuthToken;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class LoginControllers {

    @FXML
    private Button forgetPsswdButton, LoginButton, SignUpButton;

    @FXML
    private PasswordField passwordTextField;// Correction de la casse

    @FXML
    private TextField emailTextField;

    private final ServiceUser serviceUser = new ServiceUser() {};

    /**
     * 🔹 Action du bouton LOGIN
     */
    @FXML
    void LoginButtonOnAction(ActionEvent event) throws SQLException {
        String email = emailTextField.getText().trim();
        String password = passwordTextField.getText().trim();

        // Vérification des champs vides
        if (email.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "⚠️ Champs vides", "Veuillez entrer un email et un mot de passe.");
            return;
        }

        Optional<User> userOpt = serviceUser.authentifier(email, password);

        if (userOpt.isPresent()) {
            User loggedInUser = userOpt.get();
            System.out.println("✅ Connexion réussie : " + loggedInUser.getEmail() + " | Rôle : " + loggedInUser.getRole());

            // Stocker l'utilisateur connecté
            AuthToken.setCurrentUser(loggedInUser);

            // Vérifier le rôle et rediriger vers l'interface appropriée
            switch (loggedInUser.getRole().trim().toLowerCase()) {
                case "admin" -> ouvrirInterface("AdminDash.fxml", "🏢 Interface Admin", event);
                case "responsable_salle" -> ouvrirInterface("DashboardReasponsable.fxml", "📋 Interface Responsable", event);
                case "sportif" -> ouvrirInterface("ProfileMembre.fxml", "🏋️ Interface Membre", event);
                case "entraineur" -> ouvrirInterface("InterfaceEntraineur.fxml", "👨‍🏫 Interface Entraîneur", event);
                default -> showAlert(Alert.AlertType.ERROR, "⚠️ Erreur", "Rôle inconnu : " + loggedInUser.getRole());
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "❌ Erreur", "Email ou mot de passe incorrect.");
        }
    }

    /**
     * 🔹 Redirection vers l'inscription
     */
    @FXML
    void SignUpButtonOnAction(ActionEvent event) {
        ouvrirInterface("Signup.fxml", "📝 Inscription", event);
    }

    /**
     * 🔹 Gestion du bouton "Mot de passe oublié"
     */
    @FXML
    void ForgetPsswdButtonOnAction(ActionEvent event) {
        ouvrirInterface("ResetPassword.fxml", "🔑 Réinitialisation du mot de passe", event);
    }

    /**
     * 🔹 Ouvrir une interface spécifique
     */
    private void ouvrirInterface(String fxmlFile, String title, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/" + fxmlFile));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "❌ Erreur", "Impossible d'ouvrir l'interface : " + fxmlFile);
            e.printStackTrace();
        }
    }

    /**
     * 🔹 Affichage d'alertes
     */
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
