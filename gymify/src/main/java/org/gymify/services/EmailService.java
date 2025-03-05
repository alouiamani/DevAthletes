package org.gymify.services;

import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;
import java.util.Properties;

public class EmailService {

    private static final String USERNAME = "amani.aloui@esprit.tn";  // Ton adresse email
    private static final String PASSWORD = "X(382817226370ah+";        // Ton mot de passe

    public static void envoyerEmail(String destinataire, String sujet, String messageText) {
        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp-mail.outlook.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        // Créer la session de l'email
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USERNAME, PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(USERNAME));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinataire));
            message.setSubject(sujet);
            // Créer une partie HTML pour le contenu de l'email
            MimeMultipart multipart = new MimeMultipart("related");

            // Partie texte HTML
            MimeBodyPart htmlPart = new MimeBodyPart();
            String htmlContent = "<html><body>"
                    + "<h2>Bonjour, " + messageText + "</h2>"
                    + "<p>Nous vous informons que vous avez été affecté(e) à une nouvelle salle. Voici les détails :</p>"
                    + "<p><strong>Nom de la salle :</strong> " + messageText + "</p>"
                    + "<p><strong>Adresse :</strong> " + messageText + "</p>"
                    + "<br><img src=\"cid:logo\">"
                    + "<p>Cordialement, l'équipe de gestion des salles</p>"
                    + "</body></html>";
            htmlPart.setContent(htmlContent, "text/html");

            // Partie logo
            MimeBodyPart imagePart = new MimeBodyPart();
            // No need to cast to DataSource
            FileDataSource fds = new FileDataSource("/logo.png");  // Spécifie le chemin de ton logo
            imagePart.setDataHandler(new DataHandler(fds)); // Directly use fds
            imagePart.setHeader("Content-ID", "<logo>");

            // Ajouter les parties au multipart
            multipart.addBodyPart(htmlPart);
            multipart.addBodyPart(imagePart);

            // Ajouter le multipart au message
            message.setContent(multipart);

            // Envoyer l'email
            Transport.send(message);
            System.out.println("Email envoyé avec succès.");
        } catch (MessagingException e) {
            e.printStackTrace();
            System.err.println("Error while sending email: " + e.getMessage());
        }
    }
}