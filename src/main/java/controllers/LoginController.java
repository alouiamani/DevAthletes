package controllers;

import entities.User;
import services.ServiceUser;
import utils.AuthToken;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

public class LoginController {
    @FXML
    private TextField emailTextField;
    @FXML
    private PasswordField passwordTextField;

    private final ServiceUser serviceUser = new ServiceUser();

    @FXML
    void LoginButtonOnAction(ActionEvent event) throws SQLException {
        String email = emailTextField.getText().trim();
        String password = passwordTextField.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "⚠️ Champs vides", "Veuillez entrer un email et un mot de passe.");
            return;
        }

        Optional<User> userOpt = serviceUser.authentifier(email, password);

        if (userOpt.isPresent()) {
            User loggedInUser = userOpt.get();
            AuthToken.setCurrentUser(loggedInUser);

            // Convert role to lowercase for case-insensitive comparison
            String role = loggedInUser.getRole().trim().toLowerCase();
            try {
                switch (role) {
                    case "admin" -> ouvrirInterface("/listProduit.fxml", "Interface Admin", event);
                    case "user", "sportif" -> ouvrirInterface("/listProduitFront.fxml", "Interface Utilisateur", event);
                    default -> showAlert(Alert.AlertType.ERROR, "⚠️ Erreur", 
                        "Rôle non autorisé : " + loggedInUser.getRole() + 
                        "\nSeuls les rôles 'admin', 'user' et 'sportif' sont autorisés.");
                }
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "❌ Erreur", 
                    "Erreur lors de l'ouverture de l'interface : " + e.getMessage());
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "❌ Erreur", "Email ou mot de passe incorrect.");
        }
    }

    @FXML
    void signUpLinkOnAction(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/SignUp.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "❌ Erreur", 
                "Impossible de charger la page d'inscription : " + e.getMessage());
        }
    }

    private void ouvrirInterface(String fxmlFile, String title, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "❌ Erreur", 
                "Impossible d'ouvrir l'interface : " + fxmlFile);
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 