package org.gymify.controllers;

import javafx.application.Platform;
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
import org.gymify.entities.Responsable_Salle;
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
            responsableChoiceBox.getItems().add(responsable.getId_User() + " - " + responsable.getNom());
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

    public void chargerSalle(Salle salle) {
        salleAModifier = salle;
        nomFX.setText(salle.getNom());
        adresseFX.setText(salle.getAdresse());
        detailsFX.setText(salle.getDetails());
        numtelFX.setText(salle.getNum_tel());
        emailFX.setText(salle.getEmail());
        url_photoFX.setText(salle.getUrl_photo());

        // Debugging
        System.out.println("Responsable de la salle : " + salle.getIdResponsable());

        // On vide et remplit la liste des responsables
        responsableChoiceBox.getItems().clear();
        try {
            List<User> responsables = userService.afficherPourResponsableAvecStream();
            System.out.println("Responsables récupérés : " + responsables);

            String selectedResponsable = null;

            for (User responsable : responsables) {
                String choix = responsable.getId_User() + " - " + responsable.getNom();
                responsableChoiceBox.getItems().add(choix);

                // Debugging
                System.out.println("Ajouté au ChoiceBox : " + choix);

                // Vérifier si c'est le responsable affecté
                if (responsable.getId_User() == salle.getIdResponsable()) {
                    selectedResponsable = choix;
                }
            }

            // Debugging
            System.out.println("Valeur à sélectionner : " + selectedResponsable);

            if (selectedResponsable != null) {
                responsableChoiceBox.setValue(selectedResponsable);
            }

            // Vérifions si la valeur est bien mise
            System.out.println("Valeur actuelle du ChoiceBox : " + responsableChoiceBox.getValue());

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

        // Vérifier si le responsable est déjà affecté à une salle avec deux paramètres
        boolean responsableDejaAffecte = salleService.isResponsableDejaAffecte(idResponsable, salleAModifier != null ? salleAModifier.getId_Salle() : 0);
        if (responsableDejaAffecte) {
            afficherAlerte("Erreur", "Ce responsable est déjà affecté à une salle.");
            return;
        }

        // Créer une nouvelle salle avec les données du formulaire
        Salle salle = new Salle(nomFX.getText(), adresseFX.getText(), detailsFX.getText(),
                numtelFX.getText(), emailFX.getText(), url_photoFX.getText(), idResponsable);

        try {
            if (salleAModifier == null) {
                // Ajouter la salle si elle n'est pas en modification
                salleService.ajouter(salle);
                afficherAlerte("Succès", "La salle a été ajoutée avec succès !");
            } else {
                // Modifier la salle existante si elle est en modification
                salle.setId_Salle(salleAModifier.getId_Salle());
                salleService.modifier(salle);
                afficherAlerte("Succès", "La salle a été modifiée avec succès !");
            }
            chargerListeSalles();
            // Créer une instance de ServiceUser
            ServiceUser serviceUser = new ServiceUser();

            // Récupérer le responsable avec son ID
            Optional<User> responsableOpt = serviceUser.getUtilisateurById(idResponsable);
            if (responsableOpt.isPresent()) {
                User responsable = responsableOpt.get();  // Récupérer l'utilisateur (responsable)
                String emailResponsable = responsable.getEmail();  // Récupérer l'email du responsable

                String sujet = "Affectation ou modification d'une salle";
                String message = "Bonjour " + responsable.getNom() + ",\n\n" +
                        "Vous avez été affecté(e) à une salle. Détails :\n\n" +
                        "Nom de la salle : " + salle.getNom() + "\n" +
                        "Adresse : " + salle.getAdresse() + "\n\n" +
                        "Cordialement,\nL'équipe de gestion des salles.";

                // Appel du service d'envoi d'email
                EmailService.envoyerEmail(emailResponsable, sujet, message);
            } else {
                afficherAlerte("Erreur", "Responsable non trouvé.");
            }

            // Recharger la liste des salles pour afficher les mises à jour

        } catch (SQLException e) {
            // Gestion des erreurs
            e.printStackTrace();
            afficherAlerte("Erreur", "Une erreur est survenue lors de l'enregistrement.");
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
            errorNumTel.setText("Format du téléphone invalide (ex +216 22 555 555)");
            errorNumTel.setVisible(true);
            return false;
        }
        errorNumTel.setVisible(false);
        return true;
    }

    // Validation de l'email (exemple: a@g.f)
    private boolean validerEmail(String email) {
        Pattern pattern = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$");
        Matcher matcher = pattern.matcher(email);
        if (!matcher.matches()) {
            errorEmail.setText("Format de l'email invalide (ex: a@g.f)");
            errorEmail.setVisible(true);
            return false;
        }
        errorEmail.setVisible(false);
        return true;
    }

    private void cacherErreurs() {
        errorNom.setVisible(false);
        errorAdresse.setVisible(false);
        errorNumTel.setVisible(false);
        errorEmail.setVisible(false);
        errorDetails.setVisible(false);
        errorUrlPhoto.setVisible(false);
    }

    private void ajouterValidationEnTempsReel() {
        ajouterListener(nomFX, errorNom, "Nom obligatoire");
        ajouterListener(adresseFX, errorAdresse, "Adresse obligatoire");
        ajouterListener(numtelFX, errorNumTel, "Numéro de téléphone obligatoire");
        ajouterListener(emailFX, errorEmail, "Email obligatoire");
        ajouterListener(detailsFX, errorDetails, "Détails obligatoires");  // ✅ Spécifique à TextArea
        ajouterListener(url_photoFX, errorUrlPhoto, "URL photo obligatoire");
    }

    private void ajouterListener(TextField champ, Label erreur, String message) {
        champ.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.trim().isEmpty()) {
                erreur.setVisible(false);
            } else {
                erreur.setText(message);
                erreur.setVisible(true);
            }
        });
    }

    private void ajouterListener(TextArea champ, Label erreur, String message) {
        champ.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.trim().isEmpty()) {
                erreur.setVisible(false);
            } else {
                erreur.setText(message);
                erreur.setVisible(true);
            }
        });
    }

    private void chargerListeSalles() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListeDesSalleAdmin.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) nomFX.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            afficherAlerte("Erreur", "Impossible de charger la liste des salles.");
        }
    }

    private void afficherAlerte(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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

    private void chargerCarte() {
        webEngine = mapView.getEngine();
        webEngine.load(getClass().getResource("/map.html").toExternalForm());

        // Attendre que la page soit chargée
        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                System.out.println("Carte chargée avec succès !");
            }
        });

        // Récupérer l'adresse sélectionnée dans le champ adresseFX
        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                webEngine.executeScript("window.javaConnector = { setAddress: function(address) { javafx.scene.web.WebEngine.call('setAddress', address); } }");
            }
        });
        JSObject window = (JSObject) webEngine.executeScript("window");
        window.setMember("javaConnector", this);
    }

    public class JavaConnector {
        public void setAddress(String address) {
            Platform.runLater(() -> adresseFX.setText(address));
        }
    }
}