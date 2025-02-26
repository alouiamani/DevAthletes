package org.gymify.controllers;


import com.mysql.cj.x.protobuf.MysqlxDatatypes;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import org.gymify.entities.Entraineur;
import org.gymify.entities.User;
import org.gymify.services.ServiceUser;
import org.gymify.utils.AuthToken;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Date;
import java.util.List;
import java.util.ArrayList;

import static org.gymify.utils.AuthToken.logout;


public class DashboardResponsableSalleController {

    @FXML private TextField AddEmailFx, AddFirstNameFx, AddLastNameFx, searchUserField, EditNomId, EditPrenomId, EditEmailId;
    @FXML private PasswordField AddPsswdFx, EditPasswdId;
    @FXML private ChoiceBox<String> AddRoleFx, AddSpecialiteFx, EditRoleId, EditSpecialId;
    @FXML private DatePicker AddBirthFx, EditBirthId;
    @FXML private VBox VBoxId;
    @FXML private AnchorPane addUserPane, homePane, listUsersPane, EditUserPane;
    @FXML private Label welcomeLabel;
    @FXML private ImageView profileImage;

    private User utilisateurSelectionne;
    private final ServiceUser serviceUser = new ServiceUser();

    public void initialize() {
        User currentUser = AuthToken.getCurrentUser();
        if (currentUser != null) {
            welcomeLabel.setText(" " + currentUser.getNom());
            setUserProfileImage(currentUser.getImageURL());
        } else {
            showAlert(Alert.AlertType.ERROR, "❌ Erreur", "Utilisateur non connecté.");
            logout();
            return;
        }

        AddRoleFx.getItems().addAll("Entraîneur", "Sportif");
        AddSpecialiteFx.getItems().addAll("Fitness", "Yoga", "Boxe", "Musculation");
        AddSpecialiteFx.setDisable(true);

        AddRoleFx.setOnAction(event -> AddSpecialiteFx.setDisable(!"Entraîneur".equals(AddRoleFx.getValue())));

        listUsersInVBox();
    }

    @FXML private void onHomeButtonClick(ActionEvent event) { showPane(homePane); }
    @FXML private void onListUsersButtonClick(ActionEvent event) { showPane(listUsersPane); }
    @FXML private void onAddUserButtonClick(ActionEvent event) { showPane(addUserPane); }
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

    @FXML
    private void onLogoutButtonClick(ActionEvent event) {
        AuthToken.setCurrentUser(null);
        logout();
        showAlert(Alert.AlertType.INFORMATION, "Déconnexion", "Vous êtes déconnecté.");
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/Login.fxml"));
            ((Node) event.getSource()).getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void listUsersInVBox() {
        VBoxId.getChildren().clear();

        try {
            List<User> users = serviceUser.afficherPourResponsableAvecStream();

            if (users.isEmpty()) {
                System.out.println("⚠️ Aucun utilisateur trouvé !");
            } else {
                users.forEach(user -> VBoxId.getChildren().add(creerHBoxUtilisateur(user)));
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur SQL : " + e.getMessage());
        }
    }
    @FXML private TextField searchRoleField;
    @FXML void onSearchByRole(ActionEvent event) {
        String roleRecherche = searchRoleField.getText().trim();

        if (VBoxId == null) {
            System.out.println("❌ VBoxId est NULL ! Vérifie ton FXML.");
            return;
        }

        if (roleRecherche.isEmpty()) {
            listUsersInVBox(); // Recharge tous les utilisateurs si le champ est vide
            return;
        }

        try {
            List<User> filteredUsers = serviceUser.rechercherParRole(roleRecherche);

            VBoxId.getChildren().clear();

            if (filteredUsers.isEmpty()) {
                System.out.println("⚠️ Aucun utilisateur trouvé avec le rôle : " + roleRecherche);
            } else {
                filteredUsers.forEach(user -> VBoxId.getChildren().add(creerHBoxUtilisateur(user)));
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur SQL lors de la recherche : " + e.getMessage());
        }
    }



    private HBox creerHBoxUtilisateur(User user) {
        HBox hbox = new HBox(15);
        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.setPadding(new Insets(8, 10, 13, 3));
        hbox.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #ddd; "
                + "-fx-border-width: 1; -fx-border-radius: 8; -fx-background-radius: 8;");


        Label nameLabel = new Label(user.getNom());
        Label lastNameLabel = new Label(user.getPrenom());
        Label emailLabel = new Label(user.getEmail());
        Label roleLabel = new Label(user.getRole());
        for (Label label : new Label[]{nameLabel, lastNameLabel, emailLabel, roleLabel}) {
            label.setStyle("-fx-font-size: 9px; -fx-font-weight: bold; -fx-text-fill: #333;");
        }
        Button editButton = createImageButton("/Image/gear.png", event -> modifierUtilisateur(user));
        Button deleteButton = createImageButton("/Image/delete.png", event -> supprimerUtilisateur(user));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        hbox.getChildren().addAll(nameLabel, lastNameLabel, emailLabel, roleLabel, spacer, editButton, deleteButton);
        return hbox;
    }

    private Button createImageButton(String imagePath, javafx.event.EventHandler<ActionEvent> eventHandler) {
        Button button = new Button();
        ImageView icon = new ImageView(new Image(getClass().getResource(imagePath).toExternalForm()));
        icon.setFitWidth(20);
        icon.setFitHeight(20);
        button.setGraphic(icon);
        button.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
        button.setOnAction(eventHandler);
        return button;
    }

    private void modifierUtilisateur(User user) {
        utilisateurSelectionne = user;
        EditNomId.setText(user.getNom());
        EditPrenomId.setText(user.getPrenom());
        EditEmailId.setText(user.getEmail());
        EditRoleId.setValue(user.getRole());

        if (user instanceof Entraineur) {
            EditSpecialId.setValue(((Entraineur) user).getSpecialite());
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
        EditUserPane.setVisible(false);
        paneToShow.setVisible(true);
    }
}
