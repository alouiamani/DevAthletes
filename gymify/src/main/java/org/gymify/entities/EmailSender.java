package org.gymify.entities;

import jakarta.mail.*;
import jakarta.mail.internet.*;

import java.util.Properties;

public class EmailSender {

    private static final String EMAIL = System.getenv("GMAIL_USERNAME");  // Email depuis variables d’environnement
    private static final String PASSWORD = System.getenv("GMAIL_PASSWORD"); // Mot de passe ou App Password

    private static Session getSession() {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        return Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EMAIL, PASSWORD);
            }
        });
    }

    public static boolean sendEmail(String to, String subject, String content) {
        if (EMAIL == null || PASSWORD == null || EMAIL.isEmpty() || PASSWORD.isEmpty()) {
            System.out.println("Erreur : Les informations de connexion Gmail ne sont pas définies !");
            return false;
        }

        try {
            Message message = new MimeMessage(getSession());
            message.setFrom(new InternetAddress(EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(content);

            Transport.send(message);
            System.out.println("Email envoyé avec succès à " + to);
            return true;
        } catch (MessagingException e) {
            System.out.println("Erreur lors de l'envoi de l'email : " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static void envoyerEmailInscription(String email, String nom, String password, String role) {
        String subject = "Bienvenue sur Gimify!";
        String content = "Bonjour " + nom + ",\n\n" +
                "Bienvenue sur Gimify ! Vous êtes inscrit en tant que " + role + ".\n" +
                "Vos identifiants :\n" +
                "Email : " + email + "\n" +
                "Mot de passe : " + password + "\n\n" +
                "Cordialement,\n" +
                "L'équipe Gimify.";

        sendEmail(email, subject, content);
    }
}