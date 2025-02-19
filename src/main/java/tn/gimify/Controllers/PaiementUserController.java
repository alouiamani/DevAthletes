package tn.gimify.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class PaiementUserController {

    @FXML private TextField nomCarteField;
    @FXML private TextField numeroCarteField;
    @FXML private TextField dateExpirationField;
    @FXML private TextField cvvField;
    @FXML private Button payerButton;

    @FXML
    private void initialize() {
        payerButton.setOnAction(e -> traiterPaiement());
    }

    private void traiterPaiement() {
        String nomCarte = nomCarteField.getText();
        String numeroCarte = numeroCarteField.getText();
        String dateExpiration = dateExpirationField.getText();
        String cvv = cvvField.getText();

        if (nomCarte.isEmpty() || numeroCarte.isEmpty() || dateExpiration.isEmpty() || cvv.isEmpty()) {
            System.out.println("Veuillez remplir tous les champs !");
            return;
        }

        // Simuler un paiement réussi
        System.out.println("Paiement effectué avec succès !");

        // Fermer la fenêtre après paiement
        Stage stage = (Stage) payerButton.getScene().getWindow();
        stage.close();
    }
}
