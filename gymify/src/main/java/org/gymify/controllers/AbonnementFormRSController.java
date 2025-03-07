package org.gymify.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.gymify.entities.*;
import org.gymify.services.AbonnementService;
import org.gymify.services.ActivityService;
import org.gymify.utils.AuthToken;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

public class AbonnementFormRSController {

    @FXML
    private ChoiceBox<type_Abonnement> typeAbonnementChoiceBox;
    @FXML
    private ChoiceBox<Activité> typeActiviteChoiceBox;
    @FXML
    private Label dateDebutLabel;
    @FXML
    private Label dateFinLabel;
    @FXML
    private TextField tarifTextField;
    @FXML
    private Button handleAbonnement;

    private LocalDate dateDebut;
    private Abonnement isModification;
    private Salle salle;
    private final AbonnementService abonnementService = new AbonnementService();
    private final ActivityService activityService = new ActivityService();
    private ObservableList<Activité> activities = FXCollections.observableArrayList();
    private int salleId;

    public void setSalleId(int salleId) {
        this.salleId = salleId;
        this.salle = new Salle();
        this.salle.setId_Salle(salleId);
    }

    @FXML
    public void initialize() {
        typeAbonnementChoiceBox.setItems(FXCollections.observableArrayList(type_Abonnement.values()));
        typeAbonnementChoiceBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(type_Abonnement type) {
                return type != null ? type.name() : "";
            }

            @Override
            public type_Abonnement fromString(String string) {
                return type_Abonnement.valueOf(string);
            }
        });

        dateDebut = LocalDate.now();
        dateDebutLabel.setText(dateDebut.toString());

        typeAbonnementChoiceBox.setOnAction(event -> updateDetails());

        List<Activité> allActivities = activityService.getActivities();
        activities.setAll(allActivities);
        typeActiviteChoiceBox.setItems(activities);
        typeActiviteChoiceBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Activité activite) {
                return activite != null ? activite.getNom() + " - " + activite.getType() : "";
            }

            @Override
            public Activité fromString(String string) {
                return null;
            }
        });

        if (!activities.isEmpty()) {
            typeActiviteChoiceBox.getSelectionModel().selectFirst();
        }
    }

    private void updateDetails() {
        type_Abonnement selectedType = typeAbonnementChoiceBox.getValue();
        if (selectedType == null || dateDebut == null) return;

        LocalDate dateFin;
        switch (selectedType) {
            case mois -> dateFin = dateDebut.plusMonths(1);
            case trimestre -> dateFin = dateDebut.plusMonths(3);
            case année -> dateFin = dateDebut.plusYears(1);
            default -> dateFin = dateDebut;
        }

        dateFinLabel.setText(dateFin.toString());
    }

    @FXML
    private void handleAbonnementSelection(ActionEvent actionEvent) {
        try {
            type_Abonnement selectedType = typeAbonnementChoiceBox.getValue();
            if (selectedType == null) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez sélectionner un type d'abonnement.");
                return;
            }


            LocalDate dateDebutLocal = LocalDate.parse(dateDebutLabel.getText());
            LocalDate dateFinLocal = LocalDate.parse(dateFinLabel.getText());
            double tarif = Double.parseDouble(tarifTextField.getText());

            if (salle == null) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Aucune salle associée à cet abonnement.");
                return;
            }

            Activité selectedActivite = typeActiviteChoiceBox.getValue();
            if (selectedActivite == null) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez sélectionner une activité.");
                return;
            }

            Abonnement abonnement = new Abonnement(
                    Date.valueOf(dateDebutLocal),
                    Date.valueOf(dateFinLocal),
                    selectedType,
                    salle,
                    tarif,
                    selectedActivite,                          // Pass Activité object
                    selectedActivite.getType().toString()      // Pass the activity type as String
            );

            if (isModification == null) {
                abonnementService.ajouter(abonnement, salleId);
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Abonnement ajouté avec succès !");
            } else {
                abonnement.setId_Abonnement(isModification.getId_Abonnement());
                abonnementService.modifier(abonnement);
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Abonnement modifié avec succès !");
            }

            // Recharger la liste des abonnements
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListeAbonnementRS.fxml"));
            Parent root = loader.load();
            ListeAbonnementRSController listeController = loader.getController();
            listeController.setSalleId(salleId); // Assurez-vous que cette méthode existe et qu'elle recharge les abonnements
            listeController.loadFilteredAbonnements(); // Recharger les abonnements

            // Récupérer la scène actuelle
            Stage stage = (Stage) handleAbonnement.getScene().getWindow();

            // Définir la nouvelle scène
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur SQL", "Une erreur est survenue : " + e.getMessage());
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger la page.");
        }
    }


    public void preFillForm(Abonnement abonnement) {
        this.isModification = abonnement;

        dateDebut = abonnement.getDate_Début().toLocalDate();
        dateDebutLabel.setText(dateDebut.toString());
        dateFinLabel.setText(abonnement.getDate_Fin().toLocalDate().toString());

        typeAbonnementChoiceBox.setValue(abonnement.getType_Abonnement());
        tarifTextField.setText(String.valueOf(abonnement.getTarif()));

        Optional<Activité> activiteOptional = activities.stream()
                .filter(a -> a.getId() == abonnement.getActivite().getId())
                .findFirst();

        activiteOptional.ifPresent(typeActiviteChoiceBox::setValue);
        this.salleId = abonnement.getSalle().getId_Salle();


    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    @FXML
    private void retournerEnArriere(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListeDesSalleAdmin.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Impossible de retourner à l'interface précédente.","veuiller resseée");
        }
    }
}