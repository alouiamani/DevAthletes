package Gimify.Pi.Controllers;

import Gimify.Pi.Service.ServiceUser;
import Gimify.Pi.entities.Entraineur;
import Gimify.Pi.entities.User;
import Gimify.Pi.utils.AuthToken;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static Gimify.Pi.utils.AuthToken.logout;

public class DashboardAdminController  {

    @FXML private TextField AddEmailFx, AddFirstNameFx, AddLastNameFx, searchUserField;
    @FXML private PasswordField AddPsswdFx;
    @FXML private ChoiceBox<String> AddRoleFx, AddSpecialiteFx;
    @FXML private DatePicker AddBirthFx;
    @FXML private VBox VBoxId;
    @FXML private AnchorPane addUserPane, homePane, listUsersPane, manageClaimsPane, EditUserPane;
    @FXML private Label welcomeLabel;
    @FXML private ImageView profileImage;
    @FXML private TextField EditEmailId, EditNomId, EditPrenomId;
    @FXML private PasswordField EditPasswdId;
    @FXML private ChoiceBox<String> EditRoleId, EditSpecialId;
    @FXML private DatePicker EditBirthId;
    @FXML private Button SaveEdit;

    private User utilisateurSelectionne;
    private final ServiceUser serviceUser = new ServiceUser() {

    };

    public void initialize() {
        // Charger l'image de profil par défaut
        if (profileImage != null) {
            try {
                profileImage.setImage(new Image(getClass().getResource("/Image/user.png").toExternalForm()));
            } catch (Exception e) {
                System.out.println("Erreur chargement image profil: " + e.getMessage());
            }
        }

        // Récupérer l'utilisateur connecté
        User currentUser = AuthToken.getCurrentUser();
        if (currentUser != null) {
            welcomeLabel.setText(" " + currentUser.getNom());
            if (currentUser.getImageURL() != null) {
                profileImage.setImage(new Image(currentUser.getImageURL()));
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "❌ Erreur", "Utilisateur non connecté.");
            logout();
            return;
        }

        // Initialiser les rôles disponibles
        AddRoleFx.getItems().addAll("Admin", "Responsable de salle", "Entraîneur", "Sportif");

        // Initialiser les spécialités (uniquement pour les entraîneurs)
        AddSpecialiteFx.getItems().addAll("Fitness", "Yoga", "Boxe", "Musculation");
        AddSpecialiteFx.setDisable(true);

        // Activer/désactiver la spécialité en fonction du rôle
        AddRoleFx.setOnAction(event -> {
            if ("Entraîneur".equals(AddRoleFx.getValue())) {
                AddSpecialiteFx.setDisable(false);
            } else {
                AddSpecialiteFx.setDisable(true);
                AddSpecialiteFx.setValue(null);
            }
        });

        // Charger la liste des utilisateurs
        listUsersInVBox();
    }



    @FXML private void onHomeButtonClick(ActionEvent event) { showPane(homePane); }
    @FXML private void onListUsersButtonClick(ActionEvent event) { showPane(listUsersPane); }
    @FXML private void onReclamationsButtonClick(ActionEvent event) { showPane(manageClaimsPane); }
    @FXML private void onAddUserButtonClick(ActionEvent event) { showPane(addUserPane); }
    @FXML
    private void onDeleteUser(ActionEvent event) {
        System.out.println("Suppression de l'utilisateur...");
        // Ajoute ici le code pour supprimer un utilisateur
    }
    @FXML
    private void onLogoutButtonClick(ActionEvent event) {
        AuthToken.setCurrentUser(null);
        logout();
        showAlert(Alert.AlertType.INFORMATION, "Déconnexion", "Vous êtes déconnecté.");

        try {
            Parent root = FXMLLoader.load(getClass().getResource("/Login.fxml"));
            ((Node) event.getSource()).getScene().setRoot(root);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }


    private void listUsersInVBox() {
        VBoxId.getChildren().clear();
        try {
            List<User> users = serviceUser.afficher();
            for (User user : users) {
                HBox userBox = creerHBoxUtilisateur(user);
                VBoxId.getChildren().add(userBox);
            }
        } catch (SQLException e) {
            System.out.println("Erreur chargement utilisateurs : " + e.getMessage());
        }
    }

    @FXML
    private void SaveAddUser(ActionEvent event) {
        String nom = AddFirstNameFx.getText().trim();
        String prenom = AddLastNameFx.getText().trim();
        String email = AddEmailFx.getText().trim();
        String password = AddPsswdFx.getText().trim();
        String role = AddRoleFx.getValue();
        String specialite = AddSpecialiteFx.getValue();
        Date dateNaissance = (AddBirthFx.getValue() != null) ? Date.valueOf(AddBirthFx.getValue()) : null;

        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || password.isEmpty() || role == null) {
            showAlert(Alert.AlertType.WARNING, "Erreur", "Veuillez remplir tous les champs requis !");
            return;
        }

        if ("Entraîneur".equals(role) && (specialite == null || specialite.isEmpty())) {
            showAlert(Alert.AlertType.WARNING, "Erreur", "Veuillez sélectionner une spécialité !");
            return;
        }

        try {
            User newUser = "Entraîneur".equals(role) ?
                    new Entraineur(nom, prenom, email, password, dateNaissance, "", specialite) :
                    new User(nom, prenom, email, password, role, dateNaissance, "");

            serviceUser.ajouter(newUser);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Utilisateur ajouté avec succès !");
            listUsersInVBox();
            showPane(listUsersPane);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur SQL", "Impossible d'ajouter l'utilisateur.");
        }
    }

    private HBox creerHBoxUtilisateur(User user) {
        HBox hbox = new HBox(15); // Espacement entre les éléments
        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.setPadding(new Insets(11, 15, 20, 5)); // (top, right, bottom, left)

        hbox.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #ddd; "
                + "-fx-border-width: 1; -fx-border-radius: 8; -fx-background-radius: 8;");

        // Labels stylisés
        Label nameLabel = new Label(user.getNom());
        Label lastNameLabel = new Label(user.getPrenom());
        Label emailLabel = new Label(user.getEmail());
        Label roleLabel = new Label(user.getRole());

        for (Label label : new Label[]{nameLabel, lastNameLabel, emailLabel, roleLabel}) {
            label.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #333;");
        }

        // Bouton Modifier (Edit)
        Button editButton = new Button();
        ImageView editIcon = new ImageView(new Image(getClass().getResource("/Image/gear.png").toExternalForm()));
        editIcon.setFitWidth(20);
        editIcon.setFitHeight(20);
        editButton.setGraphic(editIcon);
        editButton.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
        editButton.setOnAction(event -> modifierUtilisateur(user));
        addHoverEffect(editButton);

        // Bouton Supprimer (Delete)
        Button deleteButton = new Button();
        ImageView deleteIcon = new ImageView(new Image(getClass().getResource("/Image/delete.png").toExternalForm()));
        deleteIcon.setFitWidth(20);
        deleteIcon.setFitHeight(20);
        deleteButton.setGraphic(deleteIcon);
        deleteButton.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
        deleteButton.setOnAction(event -> supprimerUtilisateur(user));
        addHoverEffect(deleteButton);

        // Espacement dynamique pour bien aligner les boutons à droite
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        hbox.getChildren().addAll(nameLabel, lastNameLabel, emailLabel, roleLabel, spacer, editButton, deleteButton);
        return hbox;
    }

    // Effet au survol pour les boutons (agrandissement léger)
    private void addHoverEffect(Button button) {
        button.setOnMouseEntered(e -> button.setScaleX(1.1));
        button.setOnMouseEntered(e -> button.setScaleY(1.1));
        button.setOnMouseExited(e -> button.setScaleX(1.0));
        button.setOnMouseExited(e -> button.setScaleY(1.0));
    }


    private void modifierUtilisateur(User user) {
        utilisateurSelectionne = user;
        EditNomId.setText(user.getNom());
        EditPrenomId.setText(user.getPrenom());
        EditEmailId.setText(user.getEmail());
        EditRoleId.setValue(user.getRole());

        if (user instanceof Entraineur) {
            EditSpecialId.setValue(((Entraineur) user).getSpecialite());
        } else {
            EditSpecialId.setValue(null);
        }

        showPane(EditUserPane);
    }

    private void supprimerUtilisateur(User user) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION, "Voulez-vous vraiment supprimer cet utilisateur ?", ButtonType.YES, ButtonType.NO);
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                try {
                    serviceUser.supprimer(user.getId_User());
                    showAlert(Alert.AlertType.INFORMATION, "Succès", "Utilisateur supprimé avec succès !");
                    listUsersInVBox();
                } catch (SQLException e) {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de supprimer l'utilisateur.");
                }
            }
        });
    }
    @FXML
    private void saveEditUser(ActionEvent event) {
        if (utilisateurSelectionne == null) return;

        utilisateurSelectionne.setNom(EditNomId.getText().trim());
        utilisateurSelectionne.setPrenom(EditPrenomId.getText().trim());
        utilisateurSelectionne.setEmail(EditEmailId.getText().trim());
        utilisateurSelectionne.setRole(EditRoleId.getValue());
        utilisateurSelectionne.setDateNaissance((EditBirthId.getValue() != null) ? Date.valueOf(EditBirthId.getValue()) : null);
        if (!EditPasswdId.getText().isEmpty()) {
            utilisateurSelectionne.setPassword(EditPasswdId.getText().trim());
        }
        if (utilisateurSelectionne instanceof Entraineur) {
            ((Entraineur) utilisateurSelectionne).setSpecialite(EditSpecialId.getValue());
        }

        try {
            serviceUser.modifier(utilisateurSelectionne);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Utilisateur modifié avec succès !");
            listUsersInVBox();
            showPane(listUsersPane);
            utilisateurSelectionne = null;
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur SQL", "Impossible de modifier l'utilisateur.");
        }
    }
    private void setUserProfileImage(String imageURL) {
        profileImage.setImage((imageURL != null && !imageURL.isEmpty()) ? new Image(imageURL) : new Image("/Image/icons8-user-32.png"));
    }
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showPane(AnchorPane paneToShow) {
        homePane.setVisible(false);
        listUsersPane.setVisible(false);
        addUserPane.setVisible(false);
        manageClaimsPane.setVisible(false);
        EditUserPane.setVisible(false);
        paneToShow.setVisible(true);
    }

}
