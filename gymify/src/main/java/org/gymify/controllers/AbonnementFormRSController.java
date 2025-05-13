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

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class AbonnementFormRSController {

    @FXML private ChoiceBox<type_Abonnement> typeAbonnementChoiceBox;
    @FXML private ChoiceBox<Activité> typeActiviteChoiceBox;
    @FXML private TextField tarifTextField;
    @FXML private Label errorTypeAbonnement, errorTypeActivite, errorTarif;
    @FXML private Button handleAbonnement, btnAnnuler;

    private Abonnement isModification;
    private Salle salle;
    private final AbonnementService abonnementService = new AbonnementService();
    private final ActivityService activityService = new ActivityService();
    private ObservableList<Activité> activities = FXCollections.observableArrayList();
    private int responsableId;
    private boolean isFormModified;

    public void setResponsableId(int responsableId) {
        this.responsableId = responsableId;
        try {
            this.salle = abonnementService.getSalleByResponsableId(responsableId);
            if (this.salle == null) {
                showAlert(Alert.AlertType.ERROR, "Error", "No gym assigned to this manager.");
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to retrieve assigned gym: " + e.getMessage());
        }
    }

    @FXML
    public void initialize() {
        hideErrors();
        addRealTimeValidation();
        isFormModified = false;

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

    private void addRealTimeValidation() {
        typeAbonnementChoiceBox.valueProperty().addListener((obs, oldValue, newValue) -> {
            isFormModified = true;
            if (newValue == null) {
                errorTypeAbonnement.setText("Subscription type is required");
                errorTypeAbonnement.setVisible(true);
            } else {
                errorTypeAbonnement.setVisible(false);
            }
        });

        typeActiviteChoiceBox.valueProperty().addListener((obs, oldValue, newValue) -> {
            isFormModified = true;
            if (newValue == null) {
                errorTypeActivite.setText("Activity is required");
                errorTypeActivite.setVisible(true);
            } else {
                errorTypeActivite.setVisible(false);
            }
        });

        tarifTextField.textProperty().addListener((obs, oldValue, newValue) -> {
            isFormModified = true;
            if (newValue.trim().isEmpty()) {
                errorTarif.setText("Price is required");
                errorTarif.setVisible(true);
            } else {
                try {
                    double tarif = Double.parseDouble(newValue);
                    if (tarif <= 0) {
                        errorTarif.setText("Price must be positive");
                        errorTarif.setVisible(true);
                    } else {
                        errorTarif.setVisible(false);
                    }
                } catch (NumberFormatException e) {
                    errorTarif.setText("Invalid price format");
                    errorTarif.setVisible(true);
                }
            }
        });
    }

    private void hideErrors() {
        errorTypeAbonnement.setVisible(false);
        errorTypeActivite.setVisible(false);
        errorTarif.setVisible(false);
    }

    @FXML
    private void handleAbonnementSelection(ActionEvent actionEvent) {
        try {
            if (!validateFields()) return;

            // Show confirmation dialog
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Confirm Save");
            confirmation.setHeaderText("Save Subscription");
            confirmation.setContentText("Are you sure you want to save this subscription?");
            if (!confirmation.showAndWait().filter(ButtonType.OK::equals).isPresent()) {
                return;
            }

            type_Abonnement selectedType = typeAbonnementChoiceBox.getValue();
            Activité selectedActivite = typeActiviteChoiceBox.getValue();
            double tarif = Double.parseDouble(tarifTextField.getText());

            if (salle == null) {
                showAlert(Alert.AlertType.ERROR, "Error", "No gym associated with this subscription.");
                return;
            }

            Abonnement abonnement = new Abonnement(
                    selectedType,
                    salle,
                    tarif,
                    selectedActivite,
                    selectedActivite.getType().toString()
            );

            if (isModification == null) {
                abonnementService.ajouter(abonnement, salle.getId());
                showAlert(Alert.AlertType.INFORMATION, "Success", "Subscription added successfully!");
            } else {
                abonnement.setId_Abonnement(isModification.getId_Abonnement());
                abonnementService.modifier(abonnement);
                showAlert(Alert.AlertType.INFORMATION, "Success", "Subscription updated successfully!");
            }

            isFormModified = false;
            loadSubscriptionList(actionEvent);

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "SQL Error", "An error occurred: " + e.getMessage());
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Unable to load the subscriptions page.");
        }
    }

    private boolean validateFields() {
        boolean hasErrors = false;

        if (typeAbonnementChoiceBox.getValue() == null) {
            errorTypeAbonnement.setText("Subscription type is required");
            errorTypeAbonnement.setVisible(true);
            hasErrors = true;
        } else {
            errorTypeAbonnement.setVisible(false);
        }

        if (typeActiviteChoiceBox.getValue() == null) {
            errorTypeActivite.setText("Activity is required");
            errorTypeActivite.setVisible(true);
            hasErrors = true;
        } else {
            errorTypeActivite.setVisible(false);
        }

        String tarifText = tarifTextField.getText().trim();
        if (tarifText.isEmpty()) {
            errorTarif.setText("Price is required");
            errorTarif.setVisible(true);
            hasErrors = true;
        } else {
            try {
                double tarif = Double.parseDouble(tarifText);
                if (tarif <= 0) {
                    errorTarif.setText("Price must be positive");
                    errorTarif.setVisible(true);
                    hasErrors = true;
                } else {
                    errorTarif.setVisible(false);
                }
            } catch (NumberFormatException e) {
                errorTarif.setText("Invalid price format");
                errorTarif.setVisible(true);
                hasErrors = true;
            }
        }

        return !hasErrors;
    }

    public void preFillForm(Abonnement abonnement) {
        this.isModification = abonnement;
        typeAbonnementChoiceBox.setValue(abonnement.getType_Abonnement());
        tarifTextField.setText(String.valueOf(abonnement.getTarif()));

        Optional<Activité> activiteOptional = activities.stream()
                .filter(a -> a.getId() == (abonnement.getActivite() != null ? abonnement.getActivite().getId() : -1))
                .findFirst();

        activiteOptional.ifPresent(typeActiviteChoiceBox::setValue);
        this.salle = abonnement.getSalle();
        isFormModified = false;
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void btnAnnuler(ActionEvent actionEvent) {
        clearFields();
        isFormModified = false;
    }

    private void clearFields() {
        typeAbonnementChoiceBox.setValue(null);
        typeActiviteChoiceBox.setValue(null);
        tarifTextField.clear();
    }

    @FXML
    private void goBack(ActionEvent actionEvent) {
        if (isFormModified) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Unsaved Changes");
            alert.setHeaderText("You have unsaved changes.");
            alert.setContentText("Do you want to discard changes and go back?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isEmpty() || result.get() != ButtonType.OK) {
                return;
            }
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListeAbonnementRS.fxml"));
            Parent root = loader.load();
            ListeAbonnementRSController listeController = loader.getController();
            listeController.setResponsableId(responsableId);
            listeController.loadFilteredAbonnements();
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Unable to return to the previous screen.");
        }
    }

    private void loadSubscriptionList(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListeAbonnementRS.fxml"));
        Parent root = loader.load();
        ListeAbonnementRSController listeController = loader.getController();
        listeController.setResponsableId(responsableId);
        listeController.loadFilteredAbonnements();
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    public void retournerEnArriere(ActionEvent actionEvent) {
        if (isFormModified) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Unsaved Changes");
            alert.setHeaderText("You have unsaved changes.");
            alert.setContentText("Do you want to discard changes and go back?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isEmpty() || result.get() != ButtonType.OK) {
                return;
            }
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListeAbonnementRS.fxml"));
            Parent root = loader.load();
            ListeAbonnementRSController listeController = loader.getController();
            listeController.setResponsableId(responsableId);
            listeController.loadFilteredAbonnements();
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Unable to return to the previous screen.");
        }
    }
}
