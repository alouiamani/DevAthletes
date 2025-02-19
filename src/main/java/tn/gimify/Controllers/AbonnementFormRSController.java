package tn.gimify.Controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import tn.gimify.entities.Abonnement;
import tn.gimify.entities.Salle;
import tn.gimify.entities.type_Abonnement;
import tn.gimify.services.AbonnementService;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class AbonnementFormRSController {

    @FXML
    private ChoiceBox<type_Abonnement> typeAbonnementChoiceBox;

    @FXML
    private Label nomAbonnementLabel;

    @FXML
    private Label dateDebutLabel;

    @FXML
    private Label dateFinLabel;

    @FXML
    private TextField tarifTextField;

    @FXML
    private TextField idSalleTextField; // Nouveau champ pour l'ID de la salle

    @FXML
    private Button handleAbonnement;

    private LocalDate dateDebut;
    private Abonnement isModification; // Objet pour modification (évite de créer un nouvel abonnement)

    private final AbonnementService abonnementService = new AbonnementService();

    @FXML
    public void initialize() {
        typeAbonnementChoiceBox.setItems(FXCollections.observableArrayList(type_Abonnement.values()));
        typeAbonnementChoiceBox.setValue(type_Abonnement.mois);

        typeAbonnementChoiceBox.setConverter(new StringConverter<type_Abonnement>() {
            @Override
            public String toString(type_Abonnement abonnement) {
                return abonnement.name().charAt(0) + abonnement.name().substring(1).toLowerCase();
            }

            @Override
            public type_Abonnement fromString(String string) {
                return type_Abonnement.valueOf(string.toUpperCase());
            }
        });

        dateDebut = LocalDate.now();
        dateDebutLabel.setText(dateDebut.toString());

        typeAbonnementChoiceBox.setOnAction(event -> updateDetails());

        updateDetails();
    }

    public void initData(Abonnement abonnement) {
        if (abonnement != null) {
            System.out.println("Abonnement reçu : " + abonnement);
            isModification = abonnement;

            if (abonnement.getSalle() != null) {
                System.out.println("Salle trouvée dans initData : " + abonnement.getSalle());
            } else {
                System.out.println("⚠️ ERREUR : L'abonnement n'a pas de salle associée !");
            }

            typeAbonnementChoiceBox.setValue(abonnement.getType_Abonnement());

            if (abonnement.getSalle() != null) {
                idSalleTextField.setText(String.valueOf(abonnement.getSalle().getId_Salle()));
                idSalleTextField.setDisable(true);
            } else {
                idSalleTextField.setText("Aucune salle associée");
                idSalleTextField.setDisable(true);
            }

            if (abonnement.getDate_Début() != null) {
                dateDebutLabel.setText(abonnement.getDate_Début().toLocalDate().toString());
            }

            if (abonnement.getDate_Fin() != null) {
                dateFinLabel.setText(abonnement.getDate_Fin().toLocalDate().toString());
            }

            tarifTextField.setText(String.valueOf(abonnement.getTarif()));

        } else {
            System.out.println("⚠️ ERREUR : initData() a reçu un abonnement NULL !");
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger les données de l'abonnement !");
        }
    }



    private void updateDetails() {
        type_Abonnement selectedType = typeAbonnementChoiceBox.getValue();
        LocalDate dateFin;

        nomAbonnementLabel.setText(selectedType.name().charAt(0) + selectedType.name().substring(1).toLowerCase());

        switch (selectedType) {
            case mois:
                dateFin = dateDebut.plus(1, ChronoUnit.MONTHS);
                break;
            case trimestre:
                dateFin = dateDebut.plus(3, ChronoUnit.MONTHS);
                break;
            case année:
                dateFin = dateDebut.plus(1, ChronoUnit.YEARS);
                break;
            default:
                dateFin = dateDebut;
        }

        dateFinLabel.setText(dateFin.toString());
    }


    @FXML
    private void handleAbonnementSelection(ActionEvent actionEvent) {
        try {
            // Récupération des valeurs de l'interface
            type_Abonnement selectedType = typeAbonnementChoiceBox.getValue();
            LocalDate dateDebutLocal = LocalDate.parse(dateDebutLabel.getText());
            LocalDate dateFinLocal = LocalDate.parse(dateFinLabel.getText());

            double tarif;
            try {
                tarif = Double.parseDouble(tarifTextField.getText());
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur de saisie", "Veuillez entrer un tarif valide.");
                return;
            }

            // Récupération et conservation de l'ID de la salle
            int idSalle;
            if (isModification != null && isModification.getSalle() != null) {
                System.out.println("Salle trouvée : " + isModification.getSalle());
                System.out.println("ID de la salle : " + isModification.getSalle().getId_Salle());
                idSalle = isModification.getSalle().getId_Salle();
            } else {
                System.out.println("ERREUR : isModification ou getSalle() est null !");
                showAlert(Alert.AlertType.ERROR, "Erreur", "Aucune salle associée à cet abonnement.");
                return;
            }

            // Création de l'objet Salle avec l'ID existant
            Salle salle = new Salle();
            salle.setId_Salle(idSalle);

            if (isModification == null) {
                // **Ajout d'un nouvel abonnement**
                Abonnement abonnement = new Abonnement(Date.valueOf(dateDebutLocal), Date.valueOf(dateFinLocal), selectedType, salle, tarif);
                abonnementService.ajouter(abonnement, idSalle);
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Abonnement ajouté avec succès !");
            } else {
                // **Modification d'un abonnement existant**
                int idAbonnement = isModification.getId_Abonnement();
                Abonnement abonnement = new Abonnement(Date.valueOf(dateDebutLocal), Date.valueOf(dateFinLocal), selectedType, salle, tarif);
                abonnement.setId_Abonnement(idAbonnement);
                abonnementService.modifier(abonnement);
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Abonnement modifié avec succès !");
            }

            // **Redirection vers la liste des abonnements**
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListeAbonnementRS.fxml"));
            Parent root = loader.load();
            handleAbonnement.getScene().setRoot(root); // Changer la scène actuelle

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur SQL", "Une erreur est survenue : " + e.getMessage());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur inattendue est survenue : " + e.getMessage());
        }
    }


    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
