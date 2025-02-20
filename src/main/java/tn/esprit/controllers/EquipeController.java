package tn.esprit.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import tn.esprit.entities.Equipe;
import tn.esprit.services.EquipeService;

import java.sql.SQLException;

public class EquipeController {

    @FXML private TextField nameField;
    @FXML private Label nameErrorLabel; // Label pour afficher le message d'erreur
    @FXML private TableView<Equipe> equipeTable;
    @FXML private TableColumn<Equipe, Integer> idColumn;
    @FXML private TableColumn<Equipe, String> nameColumn;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;
    @FXML private Button deleteButton;

    private final EquipeService equipeService = new EquipeService();

    public EquipeController() throws SQLException {
    }

    @FXML
    public void initialize() {
        if (idColumn != null && nameColumn != null && equipeTable != null) {
            idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
            nameColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
            loadEquipes();
        }

        // Masquer le message d'erreur au départ
        nameErrorLabel.setVisible(false);

        // Écouteur pour la validation en temps réel
        nameField.textProperty().addListener((observable, oldValue, newValue) -> {
            validateNameField();
        });
    }

    private boolean validateNameField() {
        String nom = nameField.getText().trim();
        if (nom.isEmpty()) {
            nameErrorLabel.setText("Le nom de l'équipe est obligatoire !");
            nameErrorLabel.setVisible(true);
            return false;
        } else {
            nameErrorLabel.setVisible(false);
            return true;
        }
    }

    @FXML
    private void handleSaveButton(ActionEvent event) {
        if (!validateNameField()) {
            showAlert("Erreur", "Le champ nom est obligatoire.", Alert.AlertType.ERROR);
            return;
        }

        String nom = nameField.getText().trim();
        Equipe equipe = new Equipe();
        equipe.setNom(nom);

        try {
            equipeService.ajouter(equipe);
            showAlert("Succès", "Équipe ajoutée avec succès !", Alert.AlertType.INFORMATION);
            loadEquipes();
            nameField.clear();
            nameErrorLabel.setVisible(false);
        } catch (SQLException e) {
            showAlert("Erreur", "Impossible d'ajouter l'équipe : " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleCancelButton(ActionEvent event) {
        nameField.clear();
        nameErrorLabel.setVisible(false);
    }

    @FXML
    private void handleDeleteButton(ActionEvent event) {
        Equipe selectedEquipe = equipeTable.getSelectionModel().getSelectedItem();
        if (selectedEquipe == null) {
            showAlert("Erreur", "Veuillez sélectionner une équipe à supprimer.", Alert.AlertType.ERROR);
            return;
        }

        try {
            equipeService.supprimer(selectedEquipe.getId());
            showAlert("Succès", "Équipe supprimée avec succès !", Alert.AlertType.INFORMATION);
            loadEquipes();
        } catch (SQLException e) {
            showAlert("Erreur", "Impossible de supprimer l'équipe : " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void loadEquipes() {
        if (equipeTable != null) {
            try {
                equipeTable.getItems().setAll(equipeService.afficher());
            } catch (SQLException e) {
                showAlert("Erreur", "Impossible de charger les équipes : " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
