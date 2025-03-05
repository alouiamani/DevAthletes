package controllers;

import entities.Payment;
import entities.Commande;
import entities.Cart;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import services.PaymentService;
import services.SMSService;
import services.CartService;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Locale;

public class CheckoutDialogController {
    @FXML private Label totalItemsLabel;
    @FXML private Label subtotalLabel;
    @FXML private Label taxLabel;
    @FXML private Label totalLabel;
    @FXML private RadioButton cardPayment;
    @FXML private RadioButton paypalPayment;
    @FXML private VBox cardDetailsPane;
    @FXML private TextField cardNumber;
    @FXML private TextField expiryDate;
    @FXML private TextField cvv;
    @FXML private TextField cardHolderName;

    private final PaymentService paymentService = new PaymentService();
    private final SMSService smsService = new SMSService();
    private final CartService cartService = new CartService();
    private float amount;
    private int commandeId;
    private boolean paymentSuccess = false;

    public void initialize() {
        // Add listeners for payment method selection
        cardPayment.selectedProperty().addListener((obs, oldVal, newVal) -> {
            cardDetailsPane.setVisible(newVal);
            cardDetailsPane.setManaged(newVal);
        });

        // Add input validation
        cardNumber.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                cardNumber.setText(newVal.replaceAll("[^\\d]", ""));
            }
            if (cardNumber.getText().length() > 16) {
                cardNumber.setText(cardNumber.getText().substring(0, 16));
            }
        });

        cvv.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                cvv.setText(newVal.replaceAll("[^\\d]", ""));
            }
            if (cvv.getText().length() > 3) {
                cvv.setText(cvv.getText().substring(0, 3));
            }
        });
    }

    public void setOrderDetails(int commandeId, float amount, int itemCount) {
        this.commandeId = commandeId;
        this.amount = amount;
        
        // Create custom currency formatter for DT
        DecimalFormat df = new DecimalFormat("#,##0.00 'DT'");
        
        totalItemsLabel.setText(String.valueOf(itemCount));
        subtotalLabel.setText(df.format(amount / 1.19f));
        taxLabel.setText(df.format(amount - (amount / 1.19f)));
        totalLabel.setText(df.format(amount));
    }

    @FXML
    private void handlePayment() {
        if (!validateInput()) {
            return;
        }

        try {
            Cart cart = cartService.getCurrentCart();
            if (cart.getItems().isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Attention", "Votre panier est vide");
                return;
            }

            // Create order first
            Commande commande = cartService.createOrder();
            if (commande == null) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Échec de la création de la commande");
                return;
            }

            // Process payment
            String paymentMethod = cardPayment.isSelected() ? "CARD" : "PAYPAL";
            Payment payment = paymentService.processPayment(commande.getId_c(), amount, paymentMethod);
            
            if ("COMPLETED".equals(payment.getStatus())) {
                cartService.finalizeOrder(commande.getId_c());
                
                // Send SMS confirmation
                smsService.sendOrderConfirmation(commande, amount);
                
                showSuccessAlert(payment);
                paymentSuccess = true;
                closeDialog();
            } else {
                cartService.cancelOrder(commande.getId_c());
                showErrorAlert("Paiement échoué. Veuillez réessayer.");
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Échec du traitement du paiement: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        closeDialog();
    }

    private boolean validateInput() {
        if (cardPayment.isSelected()) {
            if (cardNumber.getText().isEmpty() || cardNumber.getText().length() < 16) {
                showErrorAlert("Veuillez saisir un numéro de carte valide");
                return false;
            }
            if (expiryDate.getText().isEmpty() || !expiryDate.getText().matches("\\d{2}/\\d{2}")) {
                showErrorAlert("Veuillez saisir une date d'expiration valide (MM/AA)");
                return false;
            }
            if (cvv.getText().isEmpty() || cvv.getText().length() < 3) {
                showErrorAlert("Veuillez saisir un CVV valide");
                return false;
            }
            if (cardHolderName.getText().isEmpty()) {
                showErrorAlert("Veuillez saisir le nom du titulaire de la carte");
                return false;
            }
        }
        return true;
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccessAlert(Payment payment) {
        showAlert(Alert.AlertType.INFORMATION, "Paiement Réussi", 
                 "Paiement effectué avec succès!\nID de transaction: " + payment.getTransactionId());
    }

    private void showErrorAlert(String message) {
        showAlert(Alert.AlertType.ERROR, "Erreur", message);
    }

    private void closeDialog() {
        ((Stage) cardNumber.getScene().getWindow()).close();
    }

    public boolean isPaymentSuccessful() {
        return paymentSuccess;
    }
} 