package org.gymify.services;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class EmailService {
    public static void envoyerEmail(String email, String sujet, String message) {
        // Configure email properties
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com"); // Example for Gmail
        props.put("mail.smtp.port", "587");

        // Replace with your email credentials
        final String username = "your-email@gmail.com";
        final String password = "your-password";

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(username));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            msg.setSubject(sujet);
            msg.setText(message);
            Transport.send(msg);
            System.out.println("Email envoyé avec succès à " + email);
        } catch (MessagingException e) {
            System.err.println("Erreur lors de l'envoi de l'email : " + e.getMessage());
            e.printStackTrace();
        }
    }
}