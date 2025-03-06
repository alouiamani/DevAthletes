package org.gymify.controllers;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.gymify.entities.EmailSender;
import org.gymify.entities.Entraineur;
import org.gymify.entities.User;
import org.gymify.services.ActivityService;
import org.gymify.services.ServiceUser;
import org.gymify.services.StatistiquesService;
import org.gymify.utils.AuthToken;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Date;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static org.gymify.utils.AuthToken.logout;


public class DashboardAdminController  {
    @FXML private LineChart<String, Number> lineChart;
    @FXML private CategoryAxis xAxis;
    @FXML private NumberAxis yAxis;
    @FXML private TextField searchRoleField;
    @FXML
    private Label totalUsersLabel;
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
    @FXML
    private Label activityCountLabel;
    private StatistiquesService statistiquesService=new StatistiquesService() {};
    private User utilisateurSelectionne;
    private final ServiceUser serviceUser = new ServiceUser() {

    };

    public void initialize() {
            try {
                profileImage.setImage(new Image(getClass().getResource("/images/user.png").toExternalForm()));
            } catch (Exception e) {
                System.out.println("Erreur chargement image profil: " + e.getMessage());
            }
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
        AddRoleFx.getItems().addAll("Entraîneur", "Responsable_salle", "Sportif");
        AddSpecialiteFx.getItems().addAll("Fitness", "Yoga", "Boxe", "Musculation");
        AddSpecialiteFx.setDisable(true);
        EditRoleId.getItems().addAll("Entraîneur", "Sportif");
        EditSpecialId.getItems().addAll("Fitness", "Yoga", "Boxe", "Musculation");
        EditSpecialId.setDisable(true);

        AddRoleFx.setOnAction(event -> AddSpecialiteFx.setDisable(!"Entraîneur".equals(AddRoleFx.getValue())));

        EditRoleId.setOnAction(event -> EditSpecialId.setDisable(!"Entraîneur".equals(EditRoleId.getValue())));

        int totalUsers = serviceUser.getTotalUsers(); // Récupérer le nombre total

        totalUsersLabel.setText("" + totalUsers);
        afficherCourbeStatistiques();
        // Charger la liste des utilisateurs
        listUsersInVBox();
//        ActivityService activityService = new ActivityService();
//        int activityCount = activityService.getActivityCount();
//
//        // Mettre à jour le texte du label avec le nombre d'activités
//        activityCountLabel.setText(String.valueOf(activityCount));
    }
    @FXML
    private void onListGymButtonClick(ActionEvent event) {
        // method implementation
    }
    @FXML private void handleCancelEdit(ActionEvent event)  {showPane(listUsersPane);}
    @FXML private void handleCancelAdd(ActionEvent event)  {showPane(listUsersPane);}
    @FXML private void onHomeButtonClick(ActionEvent event) { showPane(homePane); }
    @FXML private void onListUsersButtonClick(ActionEvent event) { showPane(listUsersPane); }
    @FXML
    private void onReclamationsButtonClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ReclamationsAdmin.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("📩 Gérer mes Réclamations");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();

        }
    }
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
            User newUser;
            if ("Entraîneur".equals(role)) {
                newUser = new Entraineur(nom, prenom, email, password, dateNaissance, "", specialite);
            } else {
                newUser = new User(nom, prenom, email, password, role, dateNaissance, "");
            }

            serviceUser.ajouter(newUser);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Utilisateur ajouté avec succès !");

            // 📧 ENVOI DE L'EMAIL AUTOMATIQUE
            EmailSender.envoyerEmailInscription(email, nom, password, role);

            listUsersInVBox();
            showPane(listUsersPane);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur SQL", "Impossible d'ajouter l'utilisateur.");
        }
    }
    private HBox creerHBoxUtilisateur(User user) {
        // Création de la HBox principale avec une marge plus grande
        HBox hbox = new HBox(20);
        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.setPadding(new Insets(15));
        hbox.getStyleClass().add("user-hbox"); // Ajout d'une classe CSS pour le style

        // Utilisation d'un GridPane pour organiser les informations de manière plus nette
        GridPane infoGrid = new GridPane();
        infoGrid.setHgap(15);
        infoGrid.setVgap(8);
        infoGrid.setAlignment(Pos.CENTER_LEFT);

        // Labels pour les informations de l'utilisateur, ajout d'une classe CSS pour un style uniforme
        Label nameLabel = new Label(user.getNom());
        nameLabel.getStyleClass().add("user-label");

        Label lastNameLabel = new Label(user.getPrenom());
        lastNameLabel.getStyleClass().add("user-label");

        Label emailLabel = new Label(user.getEmail());
        emailLabel.getStyleClass().add("user-label");

        Label roleLabel = new Label(user.getRole());
        roleLabel.getStyleClass().add("user-label");

        // Ajout des labels et des titres dans le GridPane avec une meilleure disposition
        infoGrid.add(new Label("Nom:"), 0, 0);
        infoGrid.add(nameLabel, 1, 0);
        infoGrid.add(new Label("Prénom:"), 0, 1);
        infoGrid.add(lastNameLabel, 1, 1);
        infoGrid.add(new Label("Email:"), 0, 2);
        infoGrid.add(emailLabel, 1, 2);
        infoGrid.add(new Label("Rôle:"), 0, 3);
        infoGrid.add(roleLabel, 1, 3);

        // Personnalisation des boutons
        Button editButton = createImageButton("/images/gear.png", event -> modifierUtilisateur(user));
        Button deleteButton = createImageButton("/images/delete.png", event -> supprimerUtilisateur(user));

        // Ajout d'un effet visuel pour les boutons
        addButtonHoverEffect(editButton);
        addButtonHoverEffect(deleteButton);

        // Spacer pour pousser les boutons à droite et garder un espacement dynamique
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Ajout des éléments à la HBox
        hbox.getChildren().addAll(infoGrid, spacer, editButton, deleteButton);

        return hbox;
    }

    // Méthode pour créer un bouton avec une image et un effet de survol
    private Button createImageButton(String imagePath, javafx.event.EventHandler<ActionEvent> eventHandler) {
        Button button = new Button();
        ImageView icon = new ImageView(new Image(getClass().getResource(imagePath).toExternalForm()));
        icon.setFitWidth(42);  // Taille de l'icône légèrement plus grande pour plus de visibilité
        icon.setFitHeight(42);
        button.setGraphic(icon);
        button.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
        button.setOnAction(eventHandler);
        return button;
    }

    // Ajout d'un effet de survol pour les boutons, avec un léger agrandissement et un changement de couleur
    private void addButtonHoverEffect(Button button) {
        button.setOnMouseEntered(e -> {
            button.setScaleX(1.1);
            button.setScaleY(1.1);
            button.setStyle("-fx-background-color: #f0f0f0;");  // Couleur de fond claire lors du survol
        });
        button.setOnMouseExited(e -> {
            button.setScaleX(1.0);
            button.setScaleY(1.0);
            button.setStyle("-fx-background-color: transparent;");
        });
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
        profileImage.setImage((imageURL != null && !imageURL.isEmpty()) ? new Image(imageURL) : new Image("/images/icons8-user-32.png"));
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

    public void ActivityPage(MouseEvent mouseEvent) {
        try {
            // Charger la nouvelle page
            Parent root = FXMLLoader.load(getClass().getResource("/addActivity.fxml"));

            // Récupérer la scène actuelle
            Stage stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);

            // Changer la scène
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    void onSearchByRole(ActionEvent event) {
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
    @FXML
    private void ajouterSpecialite() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Nouvelle spécialité");
        dialog.setHeaderText("Ajouter une nouvelle spécialité");
        dialog.setContentText("Nom de la spécialité :");

        dialog.showAndWait().ifPresent(nouvelleSpecialite -> {
            nouvelleSpecialite = nouvelleSpecialite.trim();
            if (!nouvelleSpecialite.isEmpty() && !AddSpecialiteFx.getItems().contains(nouvelleSpecialite)) {
                AddSpecialiteFx.getItems().add(nouvelleSpecialite);
                AddSpecialiteFx.setValue(nouvelleSpecialite); // Sélectionner la nouvelle spécialité
            } else {
                showAlert(Alert.AlertType.WARNING, "Erreur", "La spécialité existe déjà ou est vide !");
            }
        });
    }
    private void afficherCourbeStatistiques() {
        System.out.println("📊 Début affichage courbe");
        Map<String, Integer> stats = statistiquesService.getNombreUtilisateursParRole();

        if (stats.isEmpty()) {
            System.out.println("❌ Aucune donnée reçue pour la courbe !");
            return;
        }

        System.out.println("📊 Données reçues : " + stats);  // Vérification console

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Utilisateurs par rôle");

        for (Map.Entry<String, Integer> entry : stats.entrySet()) {
            XYChart.Data<String, Number> dataPoint = new XYChart.Data<>(entry.getKey(), entry.getValue());
            series.getData().add(dataPoint);
        }

        // Nettoyer l'ancien contenu et ajouter les nouvelles données
        lineChart.getData().clear();
        lineChart.getData().add(series);

        // Mise à jour des labels
        xAxis.setLabel("Rôle");
        yAxis.setLabel("Nombre d'utilisateurs");
    }
}
