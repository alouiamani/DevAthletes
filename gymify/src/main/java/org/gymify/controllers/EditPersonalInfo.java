package org.gymify.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.gymify.entities.User;
import org.gymify.entities.infoSportif;
import org.gymify.entities.Objectifs;
import org.gymify.services.infoSpotifService;
import org.gymify.utils.AuthToken;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EditPersonalInfo {

    @FXML
    private TextField poids, taille, age;

    @FXML
    private RadioButton hommeRadioButton, femmeRadioButton;

    @FXML
    private RadioButton pertePoidsRadioButton, priseMasseRadioButton, enduranceRadioButton;

    @FXML
    private Label SuccessMessageFx, ErrorMessageFx;

    private final infoSpotifService infoSportifService = new infoSpotifService();
    private static final Logger logger = Logger.getLogger(EditPersonalInfo.class.getName());

    @FXML
    public void initialize() {
        // Gestion manuelle des RadioButtons pour le sexe
        hommeRadioButton.setOnAction(event -> {
            if (hommeRadioButton.isSelected()) {
                femmeRadioButton.setSelected(false);
            }
        });

        femmeRadioButton.setOnAction(event -> {
            if (femmeRadioButton.isSelected()) {
                hommeRadioButton.setSelected(false);
            }
        });

        // Gestion manuelle des RadioButtons pour l'objectif
        pertePoidsRadioButton.setOnAction(event -> {
            if (pertePoidsRadioButton.isSelected()) {
                priseMasseRadioButton.setSelected(false);
                enduranceRadioButton.setSelected(false);
            }
        });

        priseMasseRadioButton.setOnAction(event -> {
            if (priseMasseRadioButton.isSelected()) {
                pertePoidsRadioButton.setSelected(false);
                enduranceRadioButton.setSelected(false);
            }
        });

        enduranceRadioButton.setOnAction(event -> {
            if (enduranceRadioButton.isSelected()) {
                pertePoidsRadioButton.setSelected(false);
                priseMasseRadioButton.setSelected(false);
            }
        });

        // Chargement des données utilisateur
        User currentUser = AuthToken.getCurrentUser();
        if (currentUser != null) {
            infoSportif info = infoSportifService.getInfoSportifByUserId(currentUser.getId_User());

            if (info != null) {
                poids.setText(String.valueOf(info.getPoids()));
                taille.setText(String.valueOf(info.getTaille()));
                age.setText(String.valueOf(info.getAge()));

                // Sélection du sexe
                if ("Homme".equalsIgnoreCase(info.getSexe())) {
                    hommeRadioButton.setSelected(true);
                } else if ("Femme".equalsIgnoreCase(info.getSexe())) {
                    femmeRadioButton.setSelected(true);
                }

                // Sélection de l'objectif
                switch (info.getObjectifs()) {
                    case PERTE_PROIDS -> pertePoidsRadioButton.setSelected(true);
                    case PRISE_DE_MASSE -> priseMasseRadioButton.setSelected(true);
                    case ENDURANCE -> enduranceRadioButton.setSelected(true);
                }
            } else {
                SuccessMessageFx.setText("Aucune information trouvée. Veuillez remplir les champs.");
            }
        }
    }

    @FXML
    public void savePersonalInfo() {
        try {
            User currentUser = AuthToken.getCurrentUser();
            if (currentUser == null) {
                ErrorMessageFx.setText("Utilisateur non connecté.");
                return;
            }

            // Vérifier les champs numériques
            float poidsValue = Float.parseFloat(poids.getText());
            float tailleValue = Float.parseFloat(taille.getText());
            int ageValue = Integer.parseInt(age.getText());

            // Vérifier la sélection du sexe
            String selectedSexe = null;
            if (hommeRadioButton.isSelected()) {
                selectedSexe = "Homme";
            } else if (femmeRadioButton.isSelected()) {
                selectedSexe = "Femme";
            } else {
                ErrorMessageFx.setText("Veuillez sélectionner votre sexe.");
                return;
            }

            // Vérifier la sélection de l'objectif
            Objectifs selectedObjectif = null;
            if (pertePoidsRadioButton.isSelected()) {
                selectedObjectif = Objectifs.PERTE_PROIDS;
            } else if (priseMasseRadioButton.isSelected()) {
                selectedObjectif = Objectifs.PRISE_DE_MASSE;
            } else if (enduranceRadioButton.isSelected()) {
                selectedObjectif = Objectifs.ENDURANCE;
            } else {
                ErrorMessageFx.setText("Veuillez sélectionner un objectif.");
                return;
            }

            // Mise à jour des données
            infoSportif existingInfo = infoSportifService.getInfoSportifByUserId(currentUser.getId_User());
            if (existingInfo != null) {
                existingInfo.setPoids(poidsValue);
                existingInfo.setTaille(tailleValue);
                existingInfo.setAge(ageValue);
                existingInfo.setSexe(selectedSexe);
                existingInfo.setObjectifs(selectedObjectif);

                infoSportifService.Update(existingInfo);

                SuccessMessageFx.setText("Informations mises à jour avec succès !");
                ErrorMessageFx.setText("");
            } else {
                ErrorMessageFx.setText("Aucune information trouvée pour l'utilisateur.");
            }
        } catch (NumberFormatException e) {
            ErrorMessageFx.setText("Veuillez entrer des valeurs numériques valides pour le poids, la taille et l'âge.");
        } catch (Exception e) {
            ErrorMessageFx.setText("Erreur lors de la mise à jour des informations.");
            logger.log(Level.SEVERE, "Erreur lors de la mise à jour des informations", e);
        }
    }

    @FXML
    public void cancel() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ProfileMembre.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) poids.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erreur lors du retour à la page précédente", e);
        }
    }
}
