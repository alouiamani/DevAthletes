package org.gymify.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import netscape.javascript.JSObject;
import org.gymify.entities.Salle;
import org.gymify.entities.User;
import org.gymify.services.EmailService;
import org.gymify.services.SalleService;
import org.gymify.services.ServiceUser;
import javafx.concurrent.Worker;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SalleFormAdminController {

    @FXML private TextField nomFX, adresseFX, numtelFX, emailFX, url_photoFX;
    @FXML private TextArea detailsFX;
    @FXML private Label errorNom, errorAdresse, errorNumTel, errorEmail, errorDetails, errorUrlPhoto;
    @FXML private ChoiceBox<String> responsableChoiceBox;
    @FXML private WebView mapView;
    private WebEngine webEngine;
    private Salle salleAModifier;
    private final SalleService salleService = new SalleService();
    private final ServiceUser userService = new ServiceUser();

    public void initialize() throws SQLException {
        cacherErreurs();
        ajouterValidationEnTempsReel();
        chargerCarte();
        // Vider le ChoiceBox avant d'ajouter de nouveaux éléments
        responsableChoiceBox.getItems().clear();

        // Récupérer tous les responsables de salle avec le rôle "Responsable_salle"
        List<User> responsables = userService.afficherPourResponsableAvecStream();

        // Vérifiez si la liste de responsables n'est pas vide
        for (User responsable : responsables) {
            responsableChoiceBox.getItems().add(responsable.getId() + " - " + responsable.getNom());
        }

        if (responsableChoiceBox.getItems().isEmpty()) {
            System.out.println("Aucun responsable de salle trouvé.");
        }
        WebEngine webEngine = mapView.getEngine();
        webEngine.load(getClass().getResource("/map.html").toExternalForm());

        // Attendre que la page Web soit chargée
        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                JSObject window = (JSObject) webEngine.executeScript("window");
                window.setMember("javaConnector", new JavaConnector());
            }
        });
    }
    private void ajouterValidationEnTempsReel() {
        nomFX.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.trim().isEmpty()) {
                errorNom.setText("Nom obligatoire");
                errorNom.setVisible(true);
            } else {
                errorNom.setVisible(false);
            }
        });

        adresseFX.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.trim().isEmpty()) {
                errorAdresse.setText("Adresse obligatoire");
                errorAdresse.setVisible(true);
            } else {
                errorAdresse.setVisible(false);
            }
        });

        numtelFX.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.trim().isEmpty()) {
                errorNumTel.setText("Numéro de téléphone obligatoire");
                errorNumTel.setVisible(true);
            } else if (!validerTelephone(newValue)) {
                errorNumTel.setText("Numéro de téléphone invalide");
                errorNumTel.setVisible(true);
            } else {
                errorNumTel.setVisible(false);
            }
        });

        emailFX.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.trim().isEmpty()) {
                errorEmail.setText("Email obligatoire");
                errorEmail.setVisible(true);
            } else if (!validerEmail(newValue)) {
                errorEmail.setText("Email invalide");
                errorEmail.setVisible(true);
            } else {
                errorEmail.setVisible(false);
            }
        });

        detailsFX.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.trim().isEmpty()) {
                errorDetails.setText("Détails obligatoires");
                errorDetails.setVisible(true);
            } else {
                errorDetails.setVisible(false);
            }
        });

        url_photoFX.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.trim().isEmpty()) {
                errorUrlPhoto.setText("URL photo obligatoire");
                errorUrlPhoto.setVisible(true);
            } else {
                errorUrlPhoto.setVisible(false);
            }
        });
    }
    @FXML
    private void choisirImage(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"));
        Stage stage = (Stage) nomFX.getScene().getWindow(); // Correctement récupéré
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            url_photoFX.setText(file.toURI().toString());
        }
    }


    public void chargerSalle(Salle salle, ListeDesSalleController parentController) {
        salleAModifier = salle;
        nomFX.setText(salle.getNom());
        adresseFX.setText(salle.getAdresse());
        detailsFX.setText(salle.getDetails());
        numtelFX.setText(salle.getNum_tel());
        emailFX.setText(salle.getEmail());
        url_photoFX.setText(salle.getUrl_photo());

        // Vider et remplir la liste des responsables
        responsableChoiceBox.getItems().clear();
        try {
            List<User> responsables = userService.afficherPourResponsableAvecStream();
            System.out.println("Responsables récupérés : " + responsables);

            String selectedResponsable = null;

            for (User responsable : responsables) {
                String choix = responsable.getId() + " - " + responsable.getNom();
                responsableChoiceBox.getItems().add(choix);

                // Vérifier si c'est le responsable affecté à la salle
                if (responsable.getId_Salle() == salle.getId_Salle()) {
                    selectedResponsable = choix;
                }
            }

            // Sélectionner le responsable associé à la salle
            if (selectedResponsable != null) {
                responsableChoiceBox.setValue(selectedResponsable);
            } else {
                System.out.println("Aucun responsable associé à cette salle.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void btnEnregistrer(ActionEvent actionEvent) throws SQLException {
        if (!validerChamps()) return;

        // Récupérer l'ID du responsable sélectionné dans le ChoiceBox
        String responsableSelectionne = responsableChoiceBox.getValue();
        if (responsableSelectionne == null || responsableSelectionne.trim().isEmpty()) {
            afficherAlerte("Erreur", "Veuillez sélectionner un responsable.");
            return;
        }

        String[] parts = responsableSelectionne.split(" - ");
        if (parts.length < 2) {
            afficherAlerte("Erreur", "Le format du responsable sélectionné est incorrect.");
            return;
        }

        int idResponsable = Integer.parseInt(parts[0]);  // Convertir l'ID du responsable en int

        // Vérifier si la salle est déjà associée à un utilisateur (responsable) via l'ID salle
        if (salleAModifier != null && salleAModifier.getId_Salle() != salleAModifier.getId_Salle()) {
            boolean salleDejaAssociee = userService.isSalleDejaAssociee(salleAModifier.getId_Salle());
            if (salleDejaAssociee) {
                afficherAlerte("Erreur", "Cette salle est déjà associée à un responsable.");
                return;
            }
        }

        // Créer une nouvelle salle avec les données du formulaire
        Salle salle = new Salle(nomFX.getText(), adresseFX.getText(), detailsFX.getText(),
                numtelFX.getText(), emailFX.getText(), url_photoFX.getText() );

        try {
            int idSalle;
            if (salleAModifier == null) {
                // Ajouter la salle si elle n'est pas en modification
                idSalle = salleService.ajouter(salle);
                afficherAlerte("Succès", "La salle a été ajoutée avec succès !");
            } else {
                // Modifier la salle existante si elle est en modification
                salle.setId_Salle(salleAModifier.getId_Salle());
                salleService.modifier(salle);
                idSalle = salleAModifier.getId_Salle();
                afficherAlerte("Succès", "La salle a été modifiée avec succès !");
            }

            // Mettre à jour l'ID de la salle pour le responsable
            userService.mettreAJourIdSalle(idResponsable, idSalle);

            // Envoyer un email au responsable
            Optional<User> responsableOpt = userService.getUtilisateurById(idResponsable);
            if (responsableOpt.isPresent()) {
                User responsable = responsableOpt.get();
                String emailResponsable = responsable.getEmail();

                String sujet = "Affectation ou modification d'une salle";
                String message = "Nom de la salle : " + salle.getNom() + "\n"
                        + "Adresse : " + salle.getAdresse() + "\n"
                        + "Détails : " + salle.getDetails() + "\n"
                        + "Téléphone : " + salle.getNum_tel() + "\n"
                        + "Email : " + salle.getEmail() + "\n"
                        + "Photo : " + salle.getUrl_photo();

                if (salleAModifier == null) {
                    EmailService.envoyerEmail(emailResponsable, sujet, salle, true); // Email pour l'ajout
                } else {
                    EmailService.envoyerEmail(emailResponsable, sujet, salle, false); // Email pour la modification
                }
            } else {
                afficherAlerte("Erreur", "Responsable non trouvé.");
            }

            // Recharger la liste des salles pour afficher les mises à jour
            chargerListeSalles();
        } catch (SQLException e) {
            e.printStackTrace();
            afficherAlerte("Erreur", "Une erreur est survenue lors de l'enregistrement.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean validerChamps() {
        boolean hasErrors = false;

        hasErrors |= verifierChamp(nomFX, errorNom, "Nom obligatoire");
        hasErrors |= verifierChamp(adresseFX, errorAdresse, "Adresse obligatoire");
        hasErrors |= verifierChamp(numtelFX, errorNumTel, "Numéro de téléphone obligatoire");
        hasErrors |= verifierChamp(emailFX, errorEmail, "Email obligatoire");
        hasErrors |= verifierChampTextArea(detailsFX, errorDetails, "Détails obligatoires");
        hasErrors |= verifierChamp(url_photoFX, errorUrlPhoto, "URL photo obligatoire");

        // Validation spécifique pour le téléphone et l'email
        hasErrors |= !validerTelephone(numtelFX.getText());
        hasErrors |= !validerEmail(emailFX.getText());

        return !hasErrors;
    }

    private boolean verifierChamp(TextField champ, Label erreur, String message) {
        if (champ.getText().trim().isEmpty()) {
            erreur.setText(message);
            erreur.setVisible(true);
            return true;
        }
        erreur.setVisible(false);
        return false;
    }

    private boolean verifierChampTextArea(TextArea champ, Label erreur, String message) {
        if (champ.getText().trim().isEmpty()) {
            erreur.setText(message);
            erreur.setVisible(true);
            return true;
        }
        erreur.setVisible(false);
        return false;
    }

    // Validation du numéro de téléphone (format: +216 22 555 555)
    private boolean validerTelephone(String telephone) {
        Pattern pattern = Pattern.compile("^\\+216\\s\\d{2}\\s\\d{3}\\s\\d{3}$");
        Matcher matcher = pattern.matcher(telephone);
        if (!matcher.matches()) {
            errorNumTel.setText("Numéro de téléphone invalide");
            errorNumTel.setVisible(true);
            return false;
        }
        errorNumTel.setVisible(false);
        return true;
    }

    // Validation de l'email (format: exemple@domaine.com)
    private boolean validerEmail(String email) {
        Pattern pattern = Pattern.compile("^[\\w-]+(?:\\.[\\w-]+)*@[\\w-]+(?:\\.[\\w-]+)+$");
        Matcher matcher = pattern.matcher(email);
        if (!matcher.matches()) {
            errorEmail.setText("Email invalide");
            errorEmail.setVisible(true);
            return false;
        }
        errorEmail.setVisible(false);
        return true;
    }

    private void afficherAlerte(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void cacherErreurs() {
        errorNom.setVisible(false);
        errorAdresse.setVisible(false);
        errorNumTel.setVisible(false);
        errorEmail.setVisible(false);
        errorDetails.setVisible(false);
        errorUrlPhoto.setVisible(false);
    }

    // Charger la carte à partir de l'URL définie
    private void chargerCarte() {
        webEngine = mapView.getEngine();
        webEngine.load(getClass().getResource("/map.html").toExternalForm());
    }

    // Classe JavaConnector pour permettre l'interaction entre Java et JavaScript
    public class JavaConnector {
        public void setAddress(String address) {
            adresseFX.setText(address);
        }
    }

    private void chargerListeSalles() throws SQLException, IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListeDesSalleAdmin.fxml"));
        Parent root = loader.load();

        // Récupérer la scène actuelle
        Stage stage = (Stage) nomFX.getScene().getWindow();

        // Définir la nouvelle scène
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void btnAnnuler(ActionEvent actionEvent) {
        viderChamps();
    }

    private void viderChamps() {
        nomFX.clear();
        adresseFX.clear();
        emailFX.clear();
        detailsFX.clear();
        numtelFX.clear();
        url_photoFX.clear();
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
            afficherAlerte("Erreur", "Impossible de retourner à l'interface précédente.");
        }
    }
}
