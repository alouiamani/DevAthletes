package org.gymify.entities;

import jakarta.mail.*;
import jakarta.mail.internet.*;

import java.util.Properties;

public class EmailSender {

    public static void envoyerEmailInscription(String email, String nom, String password, String role) {
        // Paramètres du serveur SMTP de Gmail
        String host = "smtp.gmail.com";
        String fromEmail = System.getenv("GMAIL_USERNAME");  // Récupérer depuis l'environnement
        String fromPassword = System.getenv("GMAIL_PASSWORD");  // Récupérer depuis l'environnement

        // Afficher les valeurs pour vérifier si elles sont bien définies
        System.out.println("GMAIL_USERNAME: " + (fromEmail != null ? fromEmail : "Non défini"));
        System.out.println("GMAIL_PASSWORD: " + (fromPassword != null ? "Défini" : "Non défini"));

        // Vérifier que les variables d'environnement sont correctement définies
        if (fromEmail == null || fromEmail.isEmpty() || fromPassword == null || fromPassword.isEmpty()) {
            System.out.println("❌ Erreur : Les informations de connexion ne sont pas définies ou sont vides.");
            return;
        }

        // Configuration des propriétés SMTP
        Properties properties = System.getProperties();
        properties.setProperty("mail.smtp.host", host);
        properties.setProperty("mail.smtp.auth", "true");
        properties.setProperty("mail.smtp.port", "587"); // Port SMTP pour Gmail
        properties.setProperty("mail.smtp.starttls.enable", "true"); // Activer TLS

        // Créer une session avec authentification
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, fromPassword);
            }
        });

        try {
            // Créer le message
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
            message.setSubject("Welcome to Gimify!");
            message.setText("Hello " + nom + ",\n\n" +
                    "Welcome to Gimify! You have been registered as a " + role + ".\n" +
                    "Your credentials are as follows:\n" +
                    "Email: " + email + "\n" +
                    "Password: " + password + "\n\n" +
                    "Best regards,\n" +
                    "Gimify Team");

            // Tester la connexion SMTP avant d'envoyer
            System.out.println("🔄 Test de connexion SMTP...");
            Transport transport = session.getTransport("smtp");
            transport.connect(host, fromEmail, fromPassword);  // Tester la connexion
            transport.close();
            System.out.println("✅ Connexion SMTP réussie.");

            // Envoyer le message
            Transport.send(message);
            System.out.println("✅ Email envoyé avec succès.");
        } catch (MessagingException e) {
            // Afficher les erreurs détaillées
            System.out.println("❌ Erreur lors de l'envoi de l'email: " + e.getMessage());
            e.printStackTrace(); // Pour plus de détails sur l'erreur
        }
    }
}
