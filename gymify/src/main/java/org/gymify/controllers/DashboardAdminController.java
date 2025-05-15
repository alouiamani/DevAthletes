package org.gymify.controllers;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Dialog;
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
import java.net.MalformedURLException;
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
    @FXML private AnchorPane addUserPane, homePane, listUsersPane, manageClaimsPane, EditUserPane, produitPane, commandePane;
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
    @FXML private TableView<org.gymify.entities.Produit> produitTable;
    @FXML private TableColumn<org.gymify.entities.Produit, Integer> colIdP;
    @FXML private TableColumn<org.gymify.entities.Produit, String> colNomP;
    @FXML private TableColumn<org.gymify.entities.Produit, Double> colPrixP;
    @FXML private TableColumn<org.gymify.entities.Produit, Integer> colStockP;
    @FXML private TableColumn<org.gymify.entities.Produit, String> colCategorieP;
    @FXML private TableColumn<org.gymify.entities.Produit, String> colImagePath;
    @FXML private TableColumn<org.gymify.entities.Produit, java.util.Date> colUpdatedAt;
    @FXML private Button addProduitBtn, editProduitBtn, deleteProduitBtn;
    private final org.gymify.services.ProduitService produitService = new org.gymify.services.ProduitService();
    @FXML private TableView<org.gymify.entities.Commande> commandeTable;
    @FXML private TableColumn<org.gymify.entities.Commande, Integer> colIdC;
    @FXML private TableColumn<org.gymify.entities.Commande, Double> colTotalC;
    @FXML private TableColumn<org.gymify.entities.Commande, java.util.Date> colDateC;
    @FXML private TableColumn<org.gymify.entities.Commande, String> colStatutC;
    @FXML private TableColumn<org.gymify.entities.Commande, Integer> colUserId;
    @FXML private TableColumn<org.gymify.entities.Commande, String> colPhoneNumber;
    @FXML private Button deleteCommandeBtn;
    @FXML private Button editCommandeStatusBtn;
    private final org.gymify.services.CommandeService commandeService = new org.gymify.services.CommandeService();
    @FXML private TextField searchProduitField, searchCommandeField;
    private javafx.collections.ObservableList<org.gymify.entities.Produit> allProduits = javafx.collections.FXCollections.observableArrayList();
    private javafx.collections.ObservableList<org.gymify.entities.Commande> allCommandes = javafx.collections.FXCollections.observableArrayList();

    public void initialize() {
        // Load the profile image
        try {
            // Use the correct relative path
            URL imageUrl = getClass().getResource("/images/profile-user.png");
            System.out.println("Image URL: " + imageUrl); // Debugging: Check the URL
            if (imageUrl != null) {
                profileImage.setImage(new Image(imageUrl.toExternalForm()));
            } else {
                System.out.println("Image not found: /images/profile-user.png");
            }
        } catch (Exception e) {
            System.out.println("Erreur chargement image profil: " + e.getMessage());
        }

        // Rest of your initialization code
        User currentUser = AuthToken.getCurrentUser();
        if (currentUser != null) {
            welcomeLabel.setText(" " + currentUser.getNom());
            if (currentUser.getImage_url() != null) {
                try {
                    // Convert file path to URL
                    File file = new File(currentUser.getImage_url());
                    if (file.exists()) {
                        profileImage.setImage(new Image(file.toURI().toURL().toExternalForm()));
                    } else {
                        System.out.println("Image file does not exist: " + currentUser.getImage_url());
                    }
                } catch (MalformedURLException e) {
                    System.out.println("Invalid image path: " + currentUser.getImage_url());
                }
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "❌ Erreur", "Utilisateur non connecté.");
            logout();
            return;
        }

        // Initialiser les rôles disponibles
        AddRoleFx.getItems().addAll("Admin", "responsable_salle", "Entraîneur", "Sportif");
        EditRoleId.getItems().addAll("Admin", "responsable_salle", "Entraîneur", "Sportif");

        // Initialiser les spécialités (uniquement pour les entraîneurs)
        AddSpecialiteFx.getItems().addAll("Fitness", "Yoga", "Boxe", "Musculation");
        AddSpecialiteFx.setDisable(true);
        EditSpecialId.getItems().addAll("Fitness", "Yoga", "Boxe", "Musculation");

        // Activer/désactiver la spécialité en fonction du rôle
        AddRoleFx.setOnAction(event -> {
            if ("Entraîneur".equals(AddRoleFx.getValue())) {
                AddSpecialiteFx.setDisable(false);
            } else {
                AddSpecialiteFx.setDisable(true);
                AddSpecialiteFx.setValue(null);
            }
        });

        AddRoleFx.setOnAction(event -> {
            if ("Entraîneur".equals(EditRoleId.getValue())) {
                EditSpecialId.setDisable(false);
            } else {
                EditSpecialId.setValue(null);
                EditSpecialId.setDisable(true);
            }
        });

        int totalUsers = serviceUser.getTotalUsers(); // Récupérer le nombre total
        totalUsersLabel.setText("" + totalUsers);

        afficherCourbeStatistiques();
        listUsersInVBox();
        setupProduitTable();
        addProduitBtn.setOnAction(this::handleAddProduit);
        editProduitBtn.setOnAction(this::handleEditProduit);
        deleteProduitBtn.setOnAction(this::handleDeleteProduit);
        setupCommandeTable();
        deleteCommandeBtn.setOnAction(this::handleDeleteCommande);
        editCommandeStatusBtn.setOnAction(this::handleEditCommandeStatus);

        // Live search for products
        if (searchProduitField != null) {
            searchProduitField.textProperty().addListener((obs, oldVal, newVal) -> {
                String filter = newVal.toLowerCase();
                produitTable.setItems(allProduits.filtered(p ->
                        p.getNom_p().toLowerCase().contains(filter) ||
                                p.getCategorie_p().toLowerCase().contains(filter) ||
                                String.valueOf(p.getPrix_p()).contains(filter) ||
                                String.valueOf(p.getStock_p()).contains(filter)
                ));
            });
        }
        // Live search for commandes
        if (searchCommandeField != null) {
            searchCommandeField.textProperty().addListener((obs, oldVal, newVal) -> {
                String filter = newVal.toLowerCase();
                commandeTable.setItems(allCommandes.filtered(c ->
                        String.valueOf(c.getId_c()).contains(filter) ||
                                String.valueOf(c.getTotal_c()).contains(filter) ||
                                (c.getStatut_c() != null && c.getStatut_c().toLowerCase().contains(filter)) ||
                                (c.getPhone_number() != null && c.getPhone_number().toLowerCase().contains(filter))
                ));
            });
        }
    }
    @FXML
    private void onListGymButtonClick(ActionEvent event) {
        try {
            // Load the FXML file for the new scene
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListeDesSalleAdmin.fxml"));

            // Create the new scene
            Parent root = loader.load();

            // Get the current stage (window)
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Set the new scene
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            // Handle the error if the FXML file can't be loaded
        }

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
        if (VBoxId == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "VBoxId n'est pas initialisé. Vérifiez le fichier FXML.");
            return;
        }

        VBoxId.getChildren().clear();
        try {
            List<User> users = serviceUser.afficher();
            System.out.println("📋 Nombre d'utilisateurs récupérés : " + users.size());
            if (users.isEmpty()) {
                showAlert(Alert.AlertType.INFORMATION, "Aucun utilisateur", "Aucun utilisateur n'a été trouvé dans la base de données.");
                VBoxId.getChildren().add(new Label("Aucun utilisateur disponible."));
                return;
            }

            for (User user : users) {
                System.out.println("Ajout de l'utilisateur : " + user.getNom() + " (" + user.getEmail() + ")");
                HBox userBox = creerHBoxUtilisateur(user);
                VBoxId.getChildren().add(userBox);
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur SQL : " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Erreur SQL", "Impossible de charger les utilisateurs : " + e.getMessage());
        }
    }

    @FXML

    private void SaveAddUser(ActionEvent event) {
        System.out.println("📥 Début de SaveAddUser...");
        String nom = AddFirstNameFx.getText().trim();
        String prenom = AddLastNameFx.getText().trim();
        String email = AddEmailFx.getText().trim();
        String password = AddPsswdFx.getText().trim();
        String role = AddRoleFx.getValue();
        String specialite = AddSpecialiteFx.getValue();
        Date dateNaissance = (AddBirthFx.getValue() != null) ? Date.valueOf(AddBirthFx.getValue()) : null;

        System.out.println("📋 Données saisies : nom=" + nom + ", prenom=" + prenom + ", email=" + email + ", role=" + role + ", specialite=" + specialite + ", dateNaissance=" + dateNaissance);

        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || password.isEmpty() || role == null) {
            System.out.println("⚠️ Validation échouée : champs requis manquants.");
            showAlert(Alert.AlertType.WARNING, "Erreur", "Veuillez remplir tous les champs requis !");
            return;
        }

        if ("Entraîneur".equals(role) && (specialite == null || specialite.isEmpty())) {
            System.out.println("⚠️ Validation échouée : spécialité requise pour Entraîneur.");
            showAlert(Alert.AlertType.WARNING, "Erreur", "Veuillez sélectionner une spécialité !");
            return;
        }

        if (serviceUser.emailExiste(email)) {
            System.out.println("⚠️ Validation échouée : email déjà utilisé.");
            showAlert(Alert.AlertType.WARNING, "Erreur", "Cet email est déjà utilisé !");
            return;
        }

        boolean success = false;
        try {
            System.out.println("🚀 Création de l'utilisateur...");
            User newUser;
            if ("Entraîneur".equals(role)) {
                newUser = new Entraineur(nom, prenom, email, password, dateNaissance, role, specialite);
            } else {
                newUser = new User(nom, prenom, email, password, role, dateNaissance, "");
            }

            System.out.println("📡 Appel de serviceUser.ajouter...");
            serviceUser.ajouter(newUser);
            System.out.println("✅ Utilisateur ajouté avec succès !");

            System.out.println("📧 Envoi de l'email...");
            EmailSender.envoyerEmailInscription(email, nom, password, role);
            System.out.println("✅ Email envoyé.");

            success = true;
        } catch (SQLException e) {
            System.err.println("❌ Erreur SQL dans SaveAddUser : " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Erreur SQL", "Impossible d'ajouter l'utilisateur : " + e.getMessage());
        } catch (Exception e) {
            System.err.println("❌ Erreur inattendue dans SaveAddUser : " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur inattendue s'est produite : " + e.getMessage());
        }

        System.out.println("🔄 Actualisation de la liste des utilisateurs...");
        listUsersInVBox();
        System.out.println("🔄 Retour au panneau listUsersPane...");
        showPane(listUsersPane);

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Utilisateur ajouté avec succès !");
        }
    }

    private HBox creerHBoxUtilisateur(User user) {
        HBox hbox = new HBox(15); // Espacement entre les éléments
        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.setPadding(new Insets(10, 12, 18, 3)); // (top, right, bottom, left)

        hbox.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #ddd; "
                + "-fx-border-width: 1; -fx-border-radius: 8; -fx-background-radius: 8;");

        // Labels stylisés
        Label nameLabel = new Label(user.getNom());
        Label lastNameLabel = new Label(user.getPrenom());
        Label emailLabel = new Label(user.getEmail());
        Label roleLabel = new Label(user.getRole());

        for (Label label : new Label[]{nameLabel, lastNameLabel, emailLabel, roleLabel}) {
            label.setStyle("-fx-font-size: 9px; -fx-font-weight: bold; -fx-text-fill: #333;");
        }

        // Bouton Modifier (Edit)
        Button editButton = new Button();
        ImageView editIcon = new ImageView(new Image(getClass().getResource("/images/gear.png").toExternalForm()));
        editIcon.setFitWidth(20);
        editIcon.setFitHeight(20);
        editButton.setGraphic(editIcon);
        editButton.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
        editButton.setOnAction(event -> modifierUtilisateur(user));
        addHoverEffect(editButton);

        // Bouton Supprimer (Delete)
        Button deleteButton = new Button();
        ImageView deleteIcon = new ImageView(new Image(getClass().getResource("/images/delete.png").toExternalForm()));
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
                    serviceUser.supprimer(user.getId());
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
        utilisateurSelectionne.setDate_naissance((EditBirthId.getValue() != null) ? Date.valueOf(EditBirthId.getValue()) : null);

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
        produitPane.setVisible(false);
        commandePane.setVisible(false);
        if (paneToShow == produitPane) {
            loadProduitsInTable();
        }
        if (paneToShow == commandePane) {
            loadCommandesInTable();
        }
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
        } else {
            try {
                List<User> filteredUsers = serviceUser.afficher().stream()
                        .filter(user -> user.getRole().toLowerCase().contains(roleRecherche.toLowerCase()))
                        .collect(Collectors.toList());
                VBoxId.getChildren().clear(); // Clear existing users
                for (User user : filteredUsers) {
                    HBox userBox = creerHBoxUtilisateur(user);
                    VBoxId.getChildren().add(userBox); // Add filtered users
                }
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur SQL", "Impossible de rechercher les utilisateurs.");
            }
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

    @FXML
    private void onProduitButtonClick(ActionEvent event) {
        showPane(produitPane);
    }

    @FXML
    private void onCommandeButtonClick(ActionEvent event) {
        showPane(commandePane);
    }

    private void setupProduitTable() {
        colIdP.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getId_p()).asObject());
        colNomP.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNom_p()));
        colPrixP.setCellValueFactory(cellData -> new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().getPrix_p()).asObject());
        colStockP.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getStock_p()).asObject());
        colCategorieP.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getCategorie_p()));
        colImagePath.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getImage_path()));
        colImagePath.setCellFactory(col -> new javafx.scene.control.TableCell<>() {
            private final ImageView imageView = new ImageView();
            {
                imageView.setFitHeight(40);
                imageView.setFitWidth(60);
                imageView.setPreserveRatio(true);
            }
            @Override
            protected void updateItem(String imagePath, boolean empty) {
                super.updateItem(imagePath, empty);
                if (empty || imagePath == null || imagePath.isEmpty()) {
                    setGraphic(null);
                } else {
                    try {
                        Image img = new Image(new java.io.File(imagePath).toURI().toString(), 60, 40, true, true);
                        imageView.setImage(img);
                        setGraphic(imageView);
                    } catch (Exception e) {
                        setGraphic(null);
                    }
                }
            }
        });
        colUpdatedAt.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getUpdated_at()));
    }

    private void loadProduitsInTable() {
        try {
            java.util.List<org.gymify.entities.Produit> produits = produitService.getAllProduits();
            allProduits.setAll(produits);
            produitTable.setItems(allProduits);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger les produits: " + e.getMessage());
        }
    }

    @FXML
    private void handleAddProduit(ActionEvent event) {
        Dialog<org.gymify.entities.Produit> dialog = new Dialog<>();
        dialog.setTitle("Ajouter un produit");
        dialog.setHeaderText("Entrez les informations du produit");

        Label nomLabel = new Label("Nom:");
        TextField nomField = new TextField();
        Label prixLabel = new Label("Prix:");
        TextField prixField = new TextField();
        Label stockLabel = new Label("Stock:");
        TextField stockField = new TextField();
        Label categorieLabel = new Label("Catégorie:");
        TextField categorieField = new TextField();
        Label imageLabel = new Label("Image:");
        TextField imageField = new TextField();
        imageField.setEditable(false);
        Button browseBtn = new Button("Choisir...");
        browseBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Choisir une image");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
            );
            java.io.File file = fileChooser.showOpenDialog(dialog.getDialogPane().getScene().getWindow());
            if (file != null) {
                imageField.setText(file.getAbsolutePath());
            }
        });

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(nomLabel, 0, 0); grid.add(nomField, 1, 0);
        grid.add(prixLabel, 0, 1); grid.add(prixField, 1, 1);
        grid.add(stockLabel, 0, 2); grid.add(stockField, 1, 2);
        grid.add(categorieLabel, 0, 3); grid.add(categorieField, 1, 3);
        grid.add(imageLabel, 0, 4); grid.add(imageField, 1, 4); grid.add(browseBtn, 2, 4);
        dialog.getDialogPane().setContent(grid);
        ButtonType saveButtonType = new ButtonType("Ajouter", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    String nom = nomField.getText().trim();
                    double prix = Double.parseDouble(prixField.getText().trim());
                    int stock = Integer.parseInt(stockField.getText().trim());
                    String categorie = categorieField.getText().trim();
                    String image = imageField.getText().trim();
                    java.util.Date now = new java.util.Date();
                    return new org.gymify.entities.Produit(0, nom, prix, stock, categorie, image, now);
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Champs invalides: " + e.getMessage());
                }
            }
            return null;
        });
        dialog.showAndWait().ifPresent(produit -> {
            try {
                produitService.addProduit(produit);
                loadProduitsInTable();
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Produit ajouté avec succès.");
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ajouter le produit: " + e.getMessage());
            }
        });
    }
    @FXML
    private void handleEditProduit(ActionEvent event) {
        org.gymify.entities.Produit selected = produitTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Aucun produit sélectionné", "Veuillez sélectionner un produit à modifier.");
            return;
        }
        Dialog<org.gymify.entities.Produit> dialog = new Dialog<>();
        dialog.setTitle("Modifier le produit");
        dialog.setHeaderText("Modifiez les informations du produit");

        Label nomLabel = new Label("Nom:");
        TextField nomField = new TextField(selected.getNom_p());
        Label prixLabel = new Label("Prix:");
        TextField prixField = new TextField(String.valueOf(selected.getPrix_p()));
        Label stockLabel = new Label("Stock:");
        TextField stockField = new TextField(String.valueOf(selected.getStock_p()));
        Label categorieLabel = new Label("Catégorie:");
        TextField categorieField = new TextField(selected.getCategorie_p());
        Label imageLabel = new Label("Image:");
        TextField imageField = new TextField(selected.getImage_path());
        imageField.setEditable(false);
        Button browseBtn = new Button("Choisir...");
        browseBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Choisir une image");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
            );
            java.io.File file = fileChooser.showOpenDialog(dialog.getDialogPane().getScene().getWindow());
            if (file != null) {
                imageField.setText(file.getAbsolutePath());
            }
        });

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(nomLabel, 0, 0); grid.add(nomField, 1, 0);
        grid.add(prixLabel, 0, 1); grid.add(prixField, 1, 1);
        grid.add(stockLabel, 0, 2); grid.add(stockField, 1, 2);
        grid.add(categorieLabel, 0, 3); grid.add(categorieField, 1, 3);
        grid.add(imageLabel, 0, 4); grid.add(imageField, 1, 4); grid.add(browseBtn, 2, 4);
        dialog.getDialogPane().setContent(grid);
        ButtonType saveButtonType = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    String nom = nomField.getText().trim();
                    double prix = Double.parseDouble(prixField.getText().trim());
                    int stock = Integer.parseInt(stockField.getText().trim());
                    String categorie = categorieField.getText().trim();
                    String image = imageField.getText().trim();
                    java.util.Date now = new java.util.Date();
                    return new org.gymify.entities.Produit(selected.getId_p(), nom, prix, stock, categorie, image, now);
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Champs invalides: " + e.getMessage());
                }
            }
            return null;
        });
        dialog.showAndWait().ifPresent(produit -> {
            try {
                produitService.updateProduit(produit);
                loadProduitsInTable();
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Produit modifié avec succès.");
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de modifier le produit: " + e.getMessage());
            }
        });
    }
    @FXML
    private void handleDeleteProduit(ActionEvent event) {
        org.gymify.entities.Produit selected = produitTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Aucun produit sélectionné", "Veuillez sélectionner un produit à supprimer.");
            return;
        }
        try {
            // Delete all related order lines first
            try (java.sql.Connection con = org.gymify.utils.gymifyDataBase.getInstance().getConnection();
                 java.sql.PreparedStatement ps = con.prepareStatement("DELETE FROM ligne_commande WHERE produit_id = ?")) {
                ps.setInt(1, selected.getId_p());
                ps.executeUpdate();
            }
            // Now delete the product
            produitService.deleteProduit(selected.getId_p());
            loadProduitsInTable();
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Produit supprimé avec succès.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de supprimer le produit: " + e.getMessage());
        }
    }

    private boolean isProductReferencedInLigneCommande(int produitId) {
        try (java.sql.Connection con = org.gymify.utils.gymifyDataBase.getInstance().getConnection();
             java.sql.PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM ligne_commande WHERE produit_id = ?")) {
            ps.setInt(1, produitId);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (Exception e) {
            // Optionally log error
        }
        return false;
    }

    private void setupCommandeTable() {
        colIdC.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getId_c()).asObject());
        colTotalC.setCellValueFactory(cellData -> new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().getTotal_c()).asObject());
        colDateC.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getDate_c()));
        colStatutC.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStatut_c()));
        colUserId.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getUser_id()));
        colPhoneNumber.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getPhone_number()));
    }

    private void loadCommandesInTable() {
        try {
            java.util.List<org.gymify.entities.Commande> commandes = commandeService.getAllCommandes();
            allCommandes.setAll(commandes);
            commandeTable.setItems(allCommandes);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger les commandes: " + e.getMessage());
        }
    }

    @FXML
    private void handleDeleteCommande(ActionEvent event) {
        org.gymify.entities.Commande selected = commandeTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Aucune commande sélectionnée", "Veuillez sélectionner une commande à supprimer.");
            return;
        }
        try {
            commandeService.deleteCommande(selected.getId_c());
            loadCommandesInTable();
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Commande supprimée avec succès.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de supprimer la commande: " + e.getMessage());
        }
    }

    @FXML
    private void handleEditCommandeStatus(ActionEvent event) {
        org.gymify.entities.Commande selected = commandeTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Aucune commande sélectionnée", "Veuillez sélectionner une commande à modifier.");
            return;
        }
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Modifier le statut de la commande");
        dialog.setHeaderText("Choisissez le nouveau statut");
        ChoiceBox<String> statusChoice = new ChoiceBox<>();
        statusChoice.getItems().addAll("En cours", "Validée", "Annulée");
        statusChoice.setValue(selected.getStatut_c() != null ? selected.getStatut_c() : "En cours");
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Statut:"), 0, 0);
        grid.add(statusChoice, 1, 0);
        dialog.getDialogPane().setContent(grid);
        ButtonType saveButtonType = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return statusChoice.getValue();
            }
            return null;
        });
        dialog.showAndWait().ifPresent(newStatus -> {
            try {
                selected.setStatut_c(newStatus);
                commandeService.updateCommande(selected);
                loadCommandesInTable();
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Statut de la commande mis à jour.");
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de mettre à jour le statut: " + e.getMessage());
            }
        });
    }
}
