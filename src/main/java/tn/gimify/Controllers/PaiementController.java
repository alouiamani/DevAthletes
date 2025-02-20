package tn.gimify.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import tn.gimify.entities.Paiement;
import tn.gimify.services.PaiementService;

import java.sql.SQLException;
import java.time.LocalDate;

public class PaiementController {

    @FXML private TextField numCarteField;
    @FXML private TextField dateExpField;
    @FXML private TextField cvvField;
    @FXML private Label montantLabel;
    @FXML private Button payerButton;
    @FXML private Label messageLabel;

    private PaiementService paiementService = new PaiementService();
    private int idAbonnement;
    private int idUser;
    private double montant;

    public void setPaiementData(int idAbonnement, int idUser, double montant) {
        this.idAbonnement = idAbonnement;
        this.idUser = idUser;
        this.montant = montant;
        montantLabel.setText(montant + " DT");
    }

    @FXML
    private void initialize() {
        payerButton.setOnAction(e -> traiterPaiement());
    }

    private void traiterPaiement() {
        String numCarte = numCarteField.getText();
        String dateExp = dateExpField.getText();
        String cvv = cvvField.getText();

        if (numCarte.isEmpty() || dateExp.isEmpty() || cvv.isEmpty()) {
            messageLabel.setText("Veuillez remplir tous les champs !");
            return;
        }

        if (!numCarte.matches("\\d{16}") || !cvv.matches("\\d{3}")) {
            messageLabel.setText("Carte ou CVV invalide !");
            return;
        }

        try {
            Paiement paiement = new Paiement(idAbonnement, idUser, montant, LocalDate.now(), "Carte bancaire", "Effectué");
            paiementService.ajouterPaiement(paiement);
            messageLabel.setText("Paiement effectué avec succès !");
            messageLabel.setStyle("-fx-text-fill: green;");
        } catch (SQLException e) {
            e.printStackTrace();
            messageLabel.setText("Erreur lors du paiement.");
        }
    }
}
