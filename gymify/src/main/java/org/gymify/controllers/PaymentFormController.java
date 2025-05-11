package org.gymify.controllers;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;
import org.gymify.entities.Abonnement;
import org.gymify.entities.Salle;
import org.gymify.entities.User;
import org.gymify.services.EmailService;

import java.time.LocalDate;

public class PaymentFormController {
    @FXML private WebView webView;
    @FXML private Label amountLabel;
    @FXML private Label descriptionLabel;

    private static final String STRIPE_PK = "pk_test_51R6XfmCvMipSUHQuBPuvGSEoyT4USxinzYkBENoYz4C2LSYzsB46fd6CgJaXzAtDOUP7prhW651vB3CP6CM9PgvL009edX6abt";
    private Abonnement abonnement;
    private User user;

    public void initialize() {
        Stripe.apiKey = "sk_test_51R6XfmCvMipSUHQuaVylRxEIETrWqy4OtYXPBegkEIcFhkUUkByDt8EixxdDNh1ekcHSdVAUGNRr7BUsfXFEc5VX00CqsNejm0";
    }

    public void setPaymentData(Abonnement abonnement, User user) {
        this.abonnement = abonnement;
        this.user = user;

        amountLabel.setText(String.format("%.2f DT", abonnement.getTarif()));
        descriptionLabel.setText("Abonnement " + abonnement.getType_Abonnement());

        loadStripeForm();
    }

    private void loadStripeForm() {
        String html = String.format("""
        <!DOCTYPE html>
        <html>
        <head>
            <script src="https://js.stripe.com/v3/"></script>
            <style>
                .StripeElement {
                    padding: 10px;
                    border: 1px solid #ccc;
                    border-radius: 4px;
                    margin-bottom: 10px;
                }
                button {
                    padding: 10px 15px;
                    background: #6772E5;
                    color: white;
                    border: none;
                    border-radius: 4px;
                    cursor: pointer;
                }
                #card-errors {
                    color: #ff5252;
                    margin-top: 10px;
                }
            </style>
        </head>
        <body>
            <form id="payment-form">
                <div id="card-element"></div>
                <button id="submit">Payer %.2f DT</button>
                <div id="card-errors" role="alert"></div>
            </form>
            <script>
                const stripe = Stripe('%s');
                const elements = stripe.elements();
                const card = elements.create('card');
                card.mount('#card-element');
                
                const form = document.getElementById('payment-form');
                form.addEventListener('submit', async (e) => {
                    e.preventDefault();
                    document.getElementById('submit').disabled = true;
                    
                    const {error, paymentMethod} = await stripe.createPaymentMethod({
                        type: 'card',
                        card: card
                    });
                    
                    if (error) {
                        document.getElementById('card-errors').textContent = error.message;
                        document.getElementById('submit').disabled = false;
                    } else {
                        window.java.onPaymentSuccess(paymentMethod.id);
                    }
                });
            </script>
        </body>
        </html>
        """, abonnement.getTarif(), STRIPE_PK);

        webView.getEngine().loadContent(html);
        webView.getEngine().setJavaScriptEnabled(true);

        JSObject window = (JSObject) webView.getEngine().executeScript("window");
        window.setMember("java", new JSBridge());
    }

    public class JSBridge {
        public void onPaymentSuccess(String paymentMethodId) {
            Platform.runLater(() -> processPayment(paymentMethodId));
        }
    }

    private void processPayment(String paymentMethodId) {
        new Thread(() -> {
            try {
                // 1. Créer et confirmer le paiement
                PaymentIntent intent = PaymentIntent.create(
                        PaymentIntentCreateParams.builder()
                                .setAmount((long)(abonnement.getTarif() * 100))
                                .setCurrency("eur")
                                .setPaymentMethod(paymentMethodId)
                                .setConfirm(true)
                                .putMetadata("user_id", String.valueOf(user.getId_User()))
                                .putMetadata("abonnement_id", String.valueOf(abonnement.getId_Abonnement()))
                                .build()
                );

                Platform.runLater(() -> {
                    if ("succeeded".equals(intent.getStatus())) {
                        sendConfirmationEmail();
                        showSuccess();
                    } else if ("requires_action".equals(intent.getStatus())) {
                        handle3DSecure(intent.getNextAction().getRedirectToUrl().getUrl());
                    }
                });
            } catch (StripeException e) {
                Platform.runLater(() -> showError("Erreur de paiement", e.getMessage()));
            }
        }).start();
    }

    private void sendConfirmationEmail() {
        String to = user.getEmail();
        String subject = "Confirmation de votre abonnement Gymify";

        // Créer un objet Salle factice pour utiliser votre EmailService existant
        Salle salleFactice = new Salle();
        salleFactice.setNom(abonnement.getSalle().getNom());
        salleFactice.setAdresse("Non applicable");
        salleFactice.setDetails("Abonnement: " + abonnement.getType_Abonnement());
        salleFactice.setNum_tel("Non applicable");
        salleFactice.setEmail("contact@gymify.com");
        salleFactice.setUrl_photo("https://example.com/logo.png");

        // Utiliser votre service email existant
        EmailService.envoyerEmail(
                to,
                subject,
                salleFactice,
                true // isAjout = true pour une nouvelle souscription
        );
    }

    private void handle3DSecure(String redirectUrl) {
        WebView authView = new WebView();
        authView.getEngine().load(redirectUrl);

        Stage stage = new Stage();
        stage.setScene(new Scene(authView, 600, 800));
        stage.setTitle("Authentification 3D Secure");
        stage.show();
    }

    private void showSuccess() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Paiement réussi");
        alert.setHeaderText(null);
        alert.setContentText("Paiement confirmé! Un email de confirmation vous a été envoyé.");
        alert.showAndWait();

        // Fermer la fenêtre de paiement
        ((Stage) webView.getScene().getWindow()).close();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}