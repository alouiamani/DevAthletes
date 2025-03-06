package org.gymify.controllers;

import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import org.gymify.entities.User;
import org.gymify.utils.AuthToken;

public class PlanningpersoController implements Initializable {

    @FXML
    private WebView webView; // Assure-toi que le nom est EXACTEMENT celui du FXML

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        User currentUser = AuthToken.getCurrentUser();
        if (currentUser == null) {
            System.err.println("Aucun utilisateur connecté.");
            return;
        }

        int userId = currentUser.getId_User(); // Récupérer l'ID de l'utilisateur
        WebEngine webEngine = webView.getEngine();
        webEngine.setJavaScriptEnabled(true);

        // Charger le fichier HTML
        URL htmlFileUrl = getClass().getResource("/ia.html");
        if (htmlFileUrl == null) {
            System.err.println("❌ Fichier HTML introuvable : ia.html");
            return;
        }

        // Lire le contenu du fichier HTML
        String htmlContent;
        try {
            htmlContent = new String(Files.readAllBytes(Paths.get(htmlFileUrl.toURI())));
        } catch (IOException | URISyntaxException e) {
            System.err.println("❌ Erreur lors de la lecture du fichier HTML : " + e.getMessage());
            return;
        }

        // Injecter l'ID de l'utilisateur dans le fichier HTML
        htmlContent = htmlContent.replace("{{userId}}", String.valueOf(userId));

        // Charger le contenu modifié dans la WebView
        webEngine.loadContent(htmlContent);

        // Gestion des erreurs
        webEngine.getLoadWorker().exceptionProperty().addListener((obs, oldException, newException) -> {
            if (newException != null) {
                System.err.println("❌ Erreur WebView : " + newException.getMessage());
            }
        });

        // Vérification du chargement
        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                System.out.println("✅ Page chargée avec succès !");
            } else if (newState == Worker.State.FAILED) {
                System.err.println("❌ Échec du chargement de la page.");
            }
        });
    }
}
