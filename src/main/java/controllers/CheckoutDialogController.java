package controllers;

import entities.Payment;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import services.PaymentService;

import java.sql.SQLException;
import java.text.NumberFormat;
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
        
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.FRANCE);
        
        totalItemsLabel.setText(String.valueOf(itemCount));
        subtotalLabel.setText(currencyFormat.format(amount / 1.19f));
        taxLabel.setText(currencyFormat.format(amount - (amount / 1.19f)));
        totalLabel.setText(currencyFormat.format(amount));
    }

    @FXML
    private void handlePayment() {
        if (!validateInput()) {
            return;
        }

        try {
            String paymentMethod = cardPayment.isSelected() ? "CARD" : "PAYPAL";
            Payment payment = paymentService.processPayment(commandeId, amount, paymentMethod);
            
            if ("COMPLETED".equals(payment.getStatus())) {
                showSuccessAlert(payment);
                paymentSuccess = true;
                closeDialog();
            } else {
                showErrorAlert("Payment failed. Please try again.");
            }
        } catch (SQLException e) {
            showErrorAlert("Payment processing error: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        closeDialog();
    }

    private boolean validateInput() {
        if (cardPayment.isSelected()) {
            if (cardNumber.getText().isEmpty() || cardNumber.getText().length() < 16) {
                showErrorAlert("Please enter a valid card number");
                return false;
            }
            if (expiryDate.getText().isEmpty() || !expiryDate.getText().matches("\\d{2}/\\d{2}")) {
                showErrorAlert("Please enter a valid expiry date (MM/YY)");
                return false;
            }
            if (cvv.getText().isEmpty() || cvv.getText().length() < 3) {
                showErrorAlert("Please enter a valid CVV");
                return false;
            }
            if (cardHolderName.getText().isEmpty()) {
                showErrorAlert("Please enter the cardholder name");
                return false;
            }
        }
        return true;
    }

    private void showSuccessAlert(Payment payment) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Payment Successful");
        alert.setHeaderText(null);
        alert.setContentText("Payment completed successfully!\nTransaction ID: " + payment.getTransactionId());
        alert.showAndWait();
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void closeDialog() {
        ((Stage) cardNumber.getScene().getWindow()).close();
    }

    public boolean isPaymentSuccessful() {
        return paymentSuccess;
    }
} 