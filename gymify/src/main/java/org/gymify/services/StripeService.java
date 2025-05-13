//package org.gymify.services;
//
//
//
//import com.stripe.Stripe;
//import com.stripe.exception.StripeException;
//import com.stripe.model.PaymentIntent;
//import java.util.HashMap;
//import java.util.Map;
//
//public class StripeService {
//    private static final String STRIPE_SECRET_KEY = "sk_test_51R6XfmCvMipSUHQuaVylRxEIETrWqy4OtYXPBegkEIcFhkUUkByDt8EixxdDNh1ekcHSdVAUGNRr7BUsfXFEc5VX00CqsNejm0"; // Remplace par ta clé secrète
//
//    public StripeService() {
//        Stripe.apiKey = STRIPE_SECRET_KEY;
//    }
//
//    // Créer un paiement pour un abonnement
//    public String createPaymentIntent(int amount) throws StripeException {
//        Map<String, Object> params = new HashMap<>();
//        params.put("amount", amount * 100); // Stripe utilise les centimes
//        params.put("currency", "eur");
//        params.put("payment_method_types", java.util.Collections.singletonList("card"));
//
//        PaymentIntent intent = PaymentIntent.create(params);
//        return intent.getClientSecret(); // Retourne le client_secret à l'interface
//    }
//}
//
