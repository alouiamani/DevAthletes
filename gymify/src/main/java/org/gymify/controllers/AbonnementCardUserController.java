package org.gymify.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.gymify.entities.Abonnement;
import org.gymify.entities.User;

import java.io.IOException;

public class AbonnementCardUserController {

    @FXML private Label typeLabel;
    @FXML private Label tarifLabel;
    @FXML private Button payerButton;

    private Abonnement abonnement;
    private User sportif;

    public void setAbonnementData(Abonnement abonnement) {
        this.abonnement = abonnement;
        typeLabel.setText("Type: " + abonnement.getType_Abonnement());
        tarifLabel.setText("Price: " + abonnement.getTarif() + " DT");
        payerButton.setOnAction(e -> handlePayerButtonClick());
    }

    public void setSportif(User sportif) {
        this.sportif = sportif;
    }

    @FXML
    private void handlePayerButtonClick() {
        // Placeholder for payment logic
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Payment");
        alert.setHeaderText(null);
        alert.setContentText("Payment processing not implemented yet.");
        alert.showAndWait();
    }
}