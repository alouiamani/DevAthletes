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
import javafx.geometry.Insets;
import javafx.scene.paint.Color;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Locale;

public class CheckoutDialogController {
    @FXML private VBox mainContainer;
    @FXML private Label totalItemsLabel;
    @FXML private Label subtotalLabel;
    @FXML private Label taxLabel;
    @FXML private Label totalLabel;
    @FXML private VBox paymentMethodsContainer;
    @FXML private VBox cardDetailsPane;
    
    // Card Details Fields
    @FXML private TextField cardNumber;
    @FXML private TextField expiryDate;
    @FXML private TextField cvv;
    @FXML private TextField cardHolderName;
    
    // Payment Method Toggle Group
    private ToggleGroup paymentMethods;
    private RadioButton cardPayment;
    private RadioButton cashOnDelivery;
    
    private final PaymentService paymentService = new PaymentService();
    private final SMSService smsService = new SMSService();
    private final CartService cartService = new CartService();
    private float amount;
    private int commandeId;
    private boolean paymentSuccess = false;

    public void initialize() {
        setupUI();
        setupValidation();
        setupPaymentMethods();
    }

    private void setupUI() {
        // Style main container
        mainContainer.setStyle("-fx-background-color: white; -fx-padding: 20;");
        mainContainer.setSpacing(15);

        // Style labels
        String labelStyle = "-fx-font-size: 14px; -fx-text-fill: #2c3e50;";
        totalItemsLabel.setStyle(labelStyle);
        subtotalLabel.setStyle(labelStyle);
        taxLabel.setStyle(labelStyle);
        totalLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        // Style card details pane
        cardDetailsPane.setStyle("-fx-spacing: 10; -fx-padding: 15; -fx-background-color: #f8f9fa; -fx-background-radius: 5;");
        
        // Style text fields
        String textFieldStyle = "-fx-pref-height: 35; -fx-font-size: 14;";
        cardNumber.setStyle(textFieldStyle);
        expiryDate.setStyle(textFieldStyle);
        cvv.setStyle(textFieldStyle);
        cardHolderName.setStyle(textFieldStyle);

        // Add field prompts
        cardNumber.setPromptText("1234 5678 9012 3456");
        expiryDate.setPromptText("MM/YY");
        cvv.setPromptText("123");
        cardHolderName.setPromptText("NOM DU TITULAIRE");
    }

    private void setupPaymentMethods() {
        paymentMethods = new ToggleGroup();

        // Create payment method options
        cardPayment = createPaymentOption("Carte Bancaire", "💳");
        cashOnDelivery = createPaymentOption("Paiement à la livraison", "🚚");

        // Add to toggle group
        cardPayment.setToggleGroup(paymentMethods);
        cashOnDelivery.setToggleGroup(paymentMethods);

        // Default selection
        cardPayment.setSelected(true);

        // Add to container
        paymentMethodsContainer.getChildren().addAll(cardPayment, cashOnDelivery);

        // Listen for changes
        paymentMethods.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            cardDetailsPane.setVisible(newVal == cardPayment);
            cardDetailsPane.setManaged(newVal == cardPayment);
        });
    }

    private RadioButton createPaymentOption(String text, String emoji) {
        RadioButton rb = new RadioButton(emoji + " " + text);
        rb.setStyle("-fx-font-size: 14px; -fx-padding: 10 0;");
        return rb;
    }

    private void setupValidation() {
        // Card number validation (numbers only, max 16 digits)
        cardNumber.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                cardNumber.setText(newVal.replaceAll("[^\\d]", ""));
            }
            if (cardNumber.getText().length() > 16) {
                cardNumber.setText(cardNumber.getText().substring(0, 16));
            }
        });

        // Expiry date formatting (MM/YY)
        expiryDate.textProperty().addListener((obs, oldVal, newVal) -> {
            // Don't process if text is being deleted
            if (oldVal.length() > newVal.length()) {
                return;
            }

            // Remove any non-digits
            String cleaned = newVal.replaceAll("[^\\d]", "");
            
            try {
                StringBuilder formatted = new StringBuilder();
                
                // Handle month
                if (cleaned.length() >= 1) {
                    String monthFirst = cleaned.substring(0, 1);
                    // If first digit is > 1, prefix with 0
                    if (Integer.parseInt(monthFirst) > 1) {
                        formatted.append("0").append(monthFirst);
                    } else {
                        formatted.append(monthFirst);
                    }
                }
                
                // Add second month digit
                if (cleaned.length() >= 2) {
                    String monthSecond = cleaned.substring(1, 2);
                    if (formatted.length() == 1 && formatted.charAt(0) == '1' 
                        && Integer.parseInt(monthSecond) > 2) {
                        formatted.append("2");
                    } else {
                        formatted.append(monthSecond);
                    }
                    formatted.append("/");
                }
                
                // Add year digits
                if (cleaned.length() >= 3) {
                    formatted.append(cleaned.substring(2, Math.min(cleaned.length(), 4)));
                }
                
                // Update text field
                expiryDate.setText(formatted.toString());
                expiryDate.positionCaret(formatted.length());
                
            } catch (NumberFormatException e) {
                expiryDate.setText(oldVal);
            }
        });

        // CVV validation (numbers only, max 3 digits)
        cvv.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                cvv.setText(newVal.replaceAll("[^\\d]", ""));
            }
            if (cvv.getText().length() > 3) {
                cvv.setText(cvv.getText().substring(0, 3));
            }
        });

        // Card holder name validation (letters and spaces only)
        cardHolderName.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("[a-zA-Z\\s]*")) {
                cardHolderName.setText(oldVal);
            }
        });
    }

    public void setOrderDetails(int commandeId, float amount, int itemCount) {
        this.commandeId = commandeId;
        this.amount = amount;
        
        DecimalFormat df = new DecimalFormat("#,##0.00 'DT'");
        
        totalItemsLabel.setText(String.valueOf(itemCount) + " article(s)");
        subtotalLabel.setText("Sous-total: " + df.format(amount / 1.19f));
        taxLabel.setText("TVA (19%): " + df.format(amount - (amount / 1.19f)));
        totalLabel.setText("Total: " + df.format(amount));
    }

    @FXML
    private void handlePayment() {
        if (!validatePayment()) {
            return;
        }

        try {
            String paymentMethod = cardPayment.isSelected() ? "CARD" : "CASH_ON_DELIVERY";
            
            // For card payments, validate card details
            if ("CARD".equals(paymentMethod) && !validateCardDetails()) {
                return;
            }

            Payment payment = paymentService.processPayment(commandeId, amount, paymentMethod);
            
            if ("COMPLETED".equals(payment.getStatus())) {
                showSuccessAlert(payment);
                paymentSuccess = true;
                closeDialog();
            } else {
                showErrorAlert("Le paiement a échoué. Veuillez réessayer.");
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", 
                "Échec du traitement du paiement: " + e.getMessage());
        }
    }

    private boolean validatePayment() {
        if (paymentMethods.getSelectedToggle() == null) {
            showErrorAlert("Veuillez sélectionner un mode de paiement");
            return false;
        }
        return true;
    }

    private boolean validateCardDetails() {
        if (cardPayment.isSelected()) {
            if (cardNumber.getText().length() < 16) {
                showErrorAlert("Numéro de carte invalide");
                return false;
            }
            
            if (!validateExpiryDate(expiryDate.getText())) {
                showErrorAlert("Date d'expiration invalide ou expirée");
                return false;
            }
            
            if (cvv.getText().length() < 3) {
                showErrorAlert("CVV invalide");
                return false;
            }
            
            if (cardHolderName.getText().trim().isEmpty()) {
                showErrorAlert("Nom du titulaire requis");
                return false;
            }
        }
        return true;
    }

    @FXML
    private void handleCancel() {
        closeDialog();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        
        // Style the alert
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: white;");
        
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

    private boolean validateExpiryDate(String date) {
        if (!date.matches("\\d{2}/\\d{2}")) {
            return false;
        }
        
        try {
            String[] parts = date.split("/");
            int month = Integer.parseInt(parts[0]);
            int year = Integer.parseInt(parts[1]);
            
            // Get current date components
            java.time.YearMonth currentYearMonth = java.time.YearMonth.now();
            int currentYear = currentYearMonth.getYear() % 100;
            int currentMonth = currentYearMonth.getMonthValue();
            
            // Basic validation
            if (month < 1 || month > 12) {
                return false;
            }
            
            // Convert to full year for comparison
            int fullYear = 2000 + year;
            int fullCurrentYear = 2000 + currentYear;
            
            // Card must not be expired and not more than 10 years in future
            if (fullYear < fullCurrentYear || fullYear > fullCurrentYear + 10) {
                return false;
            }
            
            // If it's current year, check if month has not expired
            if (fullYear == fullCurrentYear && month < currentMonth) {
                return false;
            }
            
            return true;
        } catch (Exception e) {
            return false;
        }
    }
} 