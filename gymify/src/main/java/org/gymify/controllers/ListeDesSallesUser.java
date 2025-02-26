package org.gymify.controllers;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.TextField;
import javafx.util.Duration;
import org.gymify.entities.Salle;
import org.gymify.services.SalleService;


import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class ListeDesSallesUser {

    @FXML
    private TextField searchField;
    @FXML
    private VBox sallesContainer;

    private SalleService salleService = new SalleService();
    private List<Salle> allSalles; // Liste pour stocker toutes les salles
    private PauseTransition pause = new PauseTransition(Duration.millis(200));
    // Cette méthode sera appelée pour afficher toutes les salles disponibles
    public void initialize() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            pause.setOnFinished(event -> handleSearch());
            pause.playFromStart();
        });
        handleSearch();
    }

    // Méthode pour afficher la liste des salles
    @FXML
    private void handleSearch() {
        String search = searchField.getText();
        sallesContainer.getChildren().clear();

        List<Salle> salles = salleService.getAllSalles(search);
        for (Salle salle : salles) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/SalleCardUser.fxml"));
                Parent salleCard = loader.load();
                SalleCardUserController controller = loader.getController();
                controller.setSalleData(salle, this);
                sallesContainer.getChildren().add(salleCard);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void refreshList() {
        handleSearch();
    }

}
