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
            // ✅ Validation en temps réel pour chaque champ

            // Vérification du nom
            UserNameTextField.focusedProperty().addListener((obs, oldVal, newVal) -> {
                if (!newVal) { // Lorsque l'utilisateur quitte le champ
                    validerNom();
                }
            });

            // Vérification du prénom
            LastNameTextField.focusedProperty().addListener((obs, oldVal, newVal) -> {
                if (!newVal) {
                    validerPrenom();
                }
            });

            // Vérification de l'email en direct
            EmailField.textProperty().addListener((observable, oldValue, newValue) -> {
                validerEmail(newValue);
            });

            // Vérification du mot de passe
            PasswdField.focusedProperty().addListener((obs, oldVal, newVal) -> {
                if (!newVal) {
                    validerMotDePasse();
                }
            });

            // Vérification de la confirmation du mot de passe
            ConfPasswdField.focusedProperty().addListener((obs, oldVal, newVal) -> {
                if (!newVal) {
                    validerConfirmationMotDePasse();
                }
            });
        }

        private void validerNom() {
            if (UserNameTextField.getText().trim().isEmpty()) {
                errorNom.setText("❌ Ce champ est requis.");
            } else {
                errorNom.setText(""); // Supprimer l'erreur si tout est bon
            }
        }

        private void validerPrenom() {
            if (LastNameTextField.getText().trim().isEmpty()) {
                errorPrenom.setText("❌ Ce champ est requis.");
            } else {
                errorPrenom.setText("");
            }
        }

        private void validerEmail(String email) {
            if (email.trim().isEmpty()) {
                errorEmail.setText("❌ L'email est requis.");
            } else if (!email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z]{2,6}$")) {
                errorEmail.setText("❌ Email invalide. Exemple : exemple@email.com");
            } else if (emailExiste(email)) { // Vérifie si l'email est déjà utilisé
                errorEmail.setText("❌ Cet email est déjà utilisé.");
            } else {
                errorEmail.setText(""); // Supprimer l'erreur si tout est bon
            }
        }

        private void validerMotDePasse() {
            String password = PasswdField.getText().trim();
            if (password.isEmpty()) {
                errorPassword.setText("❌ Le mot de passe est requis.");
            } else if (password.length() < 6) {
                errorPassword.setText("❌ Le mot de passe doit contenir au moins 6 caractères.");
            } else {
                errorPassword.setText("");
            }
        }

        private void validerConfirmationMotDePasse() {
            String password = PasswdField.getText().trim();
            String confirmPassword = ConfPasswdField.getText().trim();
            if (confirmPassword.isEmpty()) {
                errorConfirmPassword.setText("❌ La confirmation du mot de passe est requise.");
            } else if (!password.equals(confirmPassword)) {
                errorConfirmPassword.setText("❌ Les mots de passe ne correspondent pas.");
            } else {
                errorConfirmPassword.setText("");
            }
        }

        @FXML
        void ButtonRegisterOnAction(ActionEvent event) {
            System.out.println("➡ Bouton Inscription cliqué !");

            // Vérifier une dernière fois avant d'envoyer
            validerNom();
            validerPrenom();
            validerEmail(EmailField.getText());
            validerMotDePasse();
            validerConfirmationMotDePasse();

            if (!errorNom.getText().isEmpty() ||
                    !errorPrenom.getText().isEmpty() ||
                    !errorEmail.getText().isEmpty() ||
                    !errorPassword.getText().isEmpty() ||
                    !errorConfirmPassword.getText().isEmpty()) {
                return; // Si une erreur est affichée, on ne continue pas
            }

            // ✅ Création de l'utilisateur
            User newUser = new User(UserNameTextField.getText().trim(),
                    LastNameTextField.getText().trim(),
                    PasswdField.getText().trim(),
                    EmailField.getText().trim(),
                    "sportif");

            try {
                boolean success = serviceUser.inscrire(newUser);
                if (success) {
                    System.out.println("✅ Inscription réussie !");
                    showAlert(Alert.AlertType.INFORMATION, "✅ Succès", "Inscription réussie ! Vous pouvez vous connecter.");
                    ouvrirInterface("Login.fxml", "🔑 Connexion");
                } else {
                    System.out.println("❌ Échec de l'inscription !");
                    showAlert(Alert.AlertType.ERROR, "❌ Erreur", "Une erreur est survenue. Veuillez réessayer.");
                }
            } catch (Exception e) {
                System.out.println("❌ Erreur lors de l'inscription : " + e.getMessage());
                showAlert(Alert.AlertType.ERROR, "❌ Erreur", "Problème avec la base de données.");
                e.printStackTrace();
            }
        }

        private boolean emailExiste(String email) {
            return serviceUser.emailExiste(email);
        }

        private void ouvrirInterface(String fxmlPath, String title) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/" + fxmlPath));
                Parent root = loader.load();
                Stage stage = new Stage();
                stage.setTitle(title);
                stage.setScene(new Scene(root));
                stage.show();
                Stage signUpStage = (Stage) RegisterButton.getScene().getWindow();
                signUpStage.close();
            } catch (IOException e) {
                System.out.println("❌ Erreur d'ouverture de l'interface : " + e.getMessage());
                showAlert(Alert.AlertType.ERROR, "❌ Erreur", "Impossible d'ouvrir l'interface : " + fxmlPath);
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
