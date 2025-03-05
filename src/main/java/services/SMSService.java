/**package services;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import entities.Commande;
import utils.TwilioConfig;
import java.time.format.DateTimeFormatter;
import java.text.DecimalFormat;

public class SMSService {
    private static boolean isInitialized = false;
    
    private void initTwilio() {
        if (!isInitialized) {
            try {
                Twilio.init(TwilioConfig.ACCOUNT_SID, TwilioConfig.AUTH_TOKEN);
                // Test the credentials
                Message.creator(
                    new PhoneNumber(TwilioConfig.YOUR_NUMBER),
                    new PhoneNumber(TwilioConfig.TWILIO_NUMBER),
                    "Initializing Twilio connection..."
                ).create();
                isInitialized = true;
                System.out.println("Twilio initialized successfully!");
            } catch (Exception e) {
                System.err.println("Failed to initialize Twilio: " + e.getMessage());
                if (e.getMessage().contains("Authenticate")) {
                    System.err.println("Please check your Twilio credentials in TwilioConfig.java");
                    System.err.println("Make sure to copy the AUTH TOKEN directly from Twilio Console");
                }
                throw e;
            }
        }
    }
    
    public boolean testConnection() {
        try {
            initTwilio();
            return true;
        } catch (Exception e) {
            System.err.println("Twilio connection test failed!");
            e.printStackTrace();
            return false;
        }
    }
    
    public void sendOrderConfirmation(Commande commande, double total) {
        try {
            if (!testConnection()) {
                throw new RuntimeException("Échec de la connexion Twilio");
            }
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            String orderDate = commande.getDateC().toLocalDateTime().format(formatter);
            
            DecimalFormat df = new DecimalFormat("0.00");
            String formattedTotal = df.format(total);
            
            String messageBody = String.format(
                "Merci pour votre commande!\n" +
                "Commande N°%d\n" +
                "Date: %s\n" +
                "Montant: %s DT\n" +
                "Statut: CONFIRMÉE",
                commande.getId_c(),
                orderDate,
                formattedTotal
            );

            Message message = Message.creator(
                new PhoneNumber(TwilioConfig.YOUR_NUMBER),
                new PhoneNumber(TwilioConfig.TWILIO_NUMBER),
                messageBody
            ).create();

            System.out.println("SMS envoyé avec succès! SID: " + message.getSid());
            
        } catch (Exception e) {
            System.err.println("Échec de l'envoi du SMS: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 