package org.gymify.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.gymify.entities.Equipe;
import org.gymify.services.EquipeService;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ListeDesEquipesController {

    @FXML
    private VBox vboxEquipes;
    @FXML
    private ScrollPane scrollPaneEquipes;

    private final EquipeService equipeService = new EquipeService();

    public ListeDesEquipesController() throws SQLException {}

    @FXML
    public void initialize() {
        chargerEquipes();
    }

    private void chargerEquipes() {
        vboxEquipes.getChildren().clear();
        try {
            List<Equipe> equipes = equipeService.recuperer();  // Récupérer les équipes depuis la base de données
            for (Equipe equipe : equipes) {
                // Chargement de la carte pour chaque équipe
                URL fxmlURL = getClass().getResource("/CardEquipe.fxml");
                if (fxmlURL == null) {
                    throw new IOException("Le fichier FXML '/CardEquipe.fxml' est introuvable");
                }
                FXMLLoader loader = new FXMLLoader(fxmlURL);
                HBox card = loader.load();  // Charger l'élément graphique
                CardEquipeController controller = loader.getController();
                controller.setEquipe(equipe);  // Passer l'équipe au contrôleur
                // Injecter ce contrôleur pour permettre le rafraîchissement après modification/suppression
                controller.setListeDesEquipesController(this);
                vboxEquipes.getChildren().add(card);
            }
        } catch (IOException | SQLException e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Erreur lors du chargement des équipes", e);
            afficherMessageErreur("Erreur de chargement", "Impossible de charger les équipes.");
        }
    }

    public void rafraichirListe() {
        chargerEquipes();
    }

    private void afficherMessageErreur(String titre, String contenu) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(contenu);
        alert.showAndWait();
    }
}
