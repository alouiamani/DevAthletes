package org.gymify.controllers;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.gymify.entities.CaptchaGenerator;
import org.gymify.entities.Objectifs;
import org.gymify.entities.User;
import org.gymify.entities.infoSportif;
import org.gymify.services.ServiceUser;
import org.gymify.services.infoSpotifService;
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
    @FXML
    private ImageView captchaImageView;
    @FXML
    private TextField captchaInputField;

    private String correctCaptcha;
    @FXML
    public void initialize() {
        generateNewCaptcha();
    }
    private final ServiceUser serviceUser = new ServiceUser() {};

    /**
     * 🔹 Action du bouton LOGIN
     */
    public void generateNewCaptcha() {
        try {
            correctCaptcha = CaptchaGenerator.generateCaptchaText();
            captchaImageView.setImage(CaptchaGenerator.getCaptchaImage(correctCaptcha));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * 🔹 Action du bouton LOGIN
     */
    @FXML
    void LoginButtonOnAction(ActionEvent event) throws SQLException {
        String email = emailTextField.getText().trim();
        String password = passwordTextField.getText().trim();
        String userCaptcha = captchaInputField.getText().trim();

        if (email.isEmpty() || password.isEmpty() || userCaptcha.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "⚠️ Champs vides", "Veuillez remplir tous les champs.");
            return;
        }

        if (!userCaptcha.equals(correctCaptcha)) {
            showAlert(Alert.AlertType.ERROR, "❌ Erreur Captcha", "Le captcha est incorrect.");
            generateNewCaptcha(); // Rafraîchir le captcha
            return;
        }

        Optional<User> userOpt = serviceUser.authentifier(email, password);

        if (userOpt.isPresent()) {
            User loggedInUser = userOpt.get();
            AuthToken.setCurrentUser(loggedInUser);

            // Afficher un message avec les informations de l'utilisateur


            switch (loggedInUser.getRole().trim().toLowerCase()) {
                case "admin" -> ouvrirInterface("AdminDash.fxml", "Interface Admin", event);
                case "responsable_salle" -> ouvrirInterface("DashboardReasponsable.fxml", " Interface Responsable", event);
                case "sportif" -> {
                    ajouterInfoSportif(loggedInUser);
                    ouvrirInterface("ProfileMembre.fxml", " Interface Membre", event);
                }
                case "entraineur" -> ouvrirInterface("InterfaceEntraineur.fxml", " Interface Entraîneur", event);
                default -> showAlert(Alert.AlertType.ERROR, " Erreur", "Rôle inconnu : " + loggedInUser.getRole());
            }
        } else {
            showAlert(Alert.AlertType.ERROR, " Erreur", "Email ou mot de passe incorrect.");
        }
    }


    @FXML
    void SignUpButtonOnAction(ActionEvent event) {
        ouvrirInterface("Signup.fxml", "📝 Inscription", event);
    }

    /**
     * 🔹 Gestion du bouton "Mot de passe oublié"
     */
    @FXML
    void ForgetPsswdButtonOnAction(ActionEvent event) {
        ouvrirInterface("ForgetPassword.fxml", "🔑 Réinitialisation du mot de passe", event);
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
    /**
     * 🔹 Ajouter un infoSportif pour un utilisateur sportif s'il n'existe pas déjà
     */
    private void ajouterInfoSportif(User user) {
        infoSpotifService infoSportifService = new infoSpotifService();

        // Vérifier si un infoSportif existe déjà pour cet utilisateur
        boolean infoSportifExists = infoSportifService.existsByUserId(user.getId());

        if (!infoSportifExists) {
            // Créer un nouvel infoSportif avec des valeurs par défaut
            infoSportif newInfoSportif = new infoSportif();
            newInfoSportif.setPoids(0); // Valeur par défaut
            newInfoSportif.setTaille(0); // Valeur par défaut
            newInfoSportif.setAge(0); // Valeur par défaut
            newInfoSportif.setSexe("Non spécifié"); // Valeur par défaut
            newInfoSportif.setObjectifs(Objectifs.PERTE_PROIDS); // Valeur par défaut
            newInfoSportif.setUser(user); // Associer l'utilisateur

            // Ajouter le nouvel infoSportif à la base de données
            infoSportifService.Add(newInfoSportif);
            System.out.println("✅ infoSportif ajouté pour l'utilisateur : " + user.getEmail());
        } else {
            System.out.println("ℹ️ infoSportif existe déjà pour l'utilisateur : " + user.getEmail());
        }
    }
}
