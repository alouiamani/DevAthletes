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
import java.sql.SQLException;
import java.util.List;

public class ListeDesSallesUser extends ProfileMembreController {

    @FXML
    private TextField searchField;
    @FXML
    private VBox sallesContainer;

    private SalleService salleService = new SalleService();
    private List<Salle> allSalles; // Liste pour stocker toutes les salles
    private PauseTransition pause = new PauseTransition(Duration.millis(200));

    public void initialize() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            pause.setOnFinished(event -> handleSearch());
            pause.playFromStart();
        });
        handleSearch();
    }

    @FXML
    private void handleSearch() {
        String search = searchField.getText();
        sallesContainer.getChildren().clear();

        try {
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
        } catch (SQLException e) {
            e.printStackTrace();
            // Optionally, display an error message to the user
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Erreur lors de la récupération des salles");
            alert.setContentText("Une erreur s'est produite lors de la récupération des salles : " + e.getMessage());
            alert.showAndWait();
        }
    }

    public void refreshList() {
        handleSearch();
    }
}