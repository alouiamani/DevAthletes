package org.gymify.services;
import org.gymify.entities.Abonnement;
import org.gymify.entities.Salle;
import org.gymify.entities.type_Abonnement;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;
import java.time.LocalDate;
import java.util.Properties;
public class EmailService {

    private static final String USERNAME = "amani.aloui@esprit.tn";  // Ton adresse email
    private static final String PASSWORD = "X(382817226370ah+";        // Ton mot de passe
    private static final String LOGO_PATH = "C:\\Users\\39\\Documents\\GitHub\\DevAthletes\\DevAthletes\\gymify\\src\\main\\resources\\images\\logo.png";  // Path to your logo

    public static void envoyerEmail(String destinataire, String sujet, Salle salle, boolean isAjout) {
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
            String htmlContent = "<html>"
                    + "<head>"
                    + "<style>"
                    + "body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }"
                    + ".email-container { max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 10px; background-color: #f9f9f9; }"
                    + ".email-header { font-size: 24px; color: #0044cc; margin-bottom: 20px; }"
                    + ".email-content { margin-bottom: 20px; }"
                    + ".email-content p { margin: 10px 0; }"
                    + ".email-footer { font-size: 14px; color: #666; text-align: center; margin-top: 20px; }"
                    + ".email-footer img { width: 100px; height: auto; }"
                    + "</style>"
                    + "</head>"
                    + "<body>"
                    + "<div class=\"email-container\">"
                    + "<div class=\"email-header\">Important details from Gymify</div>"
                    + "<div class=\"email-content\">"
                    + "<p>Bonjour,</p>"
                    + (isAjout
                    ? "<p>Nous vous informons que vous avez été affecté(e) à une nouvelle salle. Voici les détails :</p>"
                    : "<p>Nous vous informons que les détails de la salle à laquelle vous êtes affecté(e) ont été modifiés. Voici les nouveaux détails :</p>")
                    + "<p><strong>Nom de la salle :</strong> " + salle.getNom() + "</p>"
                    + "<p><strong>Adresse :</strong> " + salle.getAdresse() + "</p>"
                    + "<p><strong>Détails :</strong> " + salle.getDetails() + "</p>"
                    + "<p><strong>Téléphone :</strong> " + salle.getNum_tel() + "</p>"
                    + "<p><strong>Email :</strong> " + salle.getEmail() + "</p>"
                    + "<p><strong>Photo :</strong> <a href=\"" + salle.getUrl_photo() + "\">Voir la photo</a></p>"
                    + "</div>"
                    + "<div class=\"email-footer\">"
                    + "<p>Cordialement,</p>"
                    + "<p>L'équipe de gestion des salles</p>"
                    + "<img src=\"cid:logo\" alt=\"Logo\">"
                    + "</div>"
                    + "</div>"
                    + "</body>"
                    + "</html>";
            htmlPart.setContent(htmlContent, "text/html");

            // Partie logo
            MimeBodyPart imagePart = new MimeBodyPart();
            FileDataSource fds = new FileDataSource(LOGO_PATH);  // Using the constant LOGO_PATH
            imagePart.setDataHandler(new DataHandler(fds));
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
            System.err.println("Erreur lors de l'envoi de l'email : " + e.getMessage());
        }
    }

    public static void sendSubscriptionConfirmation(String recipient, Abonnement abonnement) {
        new Thread(() -> {
            try {
                Message message = new MimeMessage(createSession());
                message.setFrom(new InternetAddress(USERNAME));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
                message.setSubject("Confirmation d'abonnement - " + abonnement.getType_Abonnement());

                MimeMultipart multipart = new MimeMultipart("related");

                // Partie HTML
                MimeBodyPart htmlPart = new MimeBodyPart();
                String htmlContent = buildSubscriptionEmailContent(abonnement);
                htmlPart.setContent(htmlContent, "text/html; charset=utf-8");

                // Partie image (logo)
                MimeBodyPart imagePart = new MimeBodyPart();
                FileDataSource fds = new FileDataSource(LOGO_PATH);
                imagePart.setDataHandler(new DataHandler(fds));
                imagePart.setHeader("Content-ID", "<logo>");

                multipart.addBodyPart(htmlPart);
                multipart.addBodyPart(imagePart);
                message.setContent(multipart);

                Transport.send(message);
                System.out.println("Email de confirmation envoyé à: " + recipient);
            } catch (Exception e) {
                System.err.println("Erreur lors de l'envoi de l'email: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }

    private static Session createSession() {
        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp-mail.outlook.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        return Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USERNAME, PASSWORD);
            }
        });
    }

    private static String buildSubscriptionEmailContent(Abonnement abonnement) {
        // Calcul de la date de fin basée sur le type d'abonnement
        LocalDate endDate = calculateEndDate(abonnement.getType_Abonnement());

        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<style>" +
                "body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }" +
                ".email-container { max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 10px; }" +
                ".header { color: #2c3e50; font-size: 24px; margin-bottom: 20px; }" +
                ".details { margin: 15px 0; }" +
                ".footer { margin-top: 30px; font-size: 12px; color: #7f8c8d; text-align: center; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='email-container'>" +
                "<div class='header'>Confirmation d'abonnement</div>" +
                "<p>Bonjour,</p>" +
                "<div class='details'>" +
                "<p><strong>Type d'abonnement:</strong> " + abonnement.getType_Abonnement() + "</p>" +
                "<p><strong>Prix:</strong> " + abonnement.getTarif() + " DT</p>" +
                "<p><strong>Durée:</strong> " + getDurationText(abonnement.getType_Abonnement()) + "</p>" +
                "<p><strong>Salle:</strong> " + abonnement.getSalle().getNom() + "</p>" +
                "<p><strong>Date de début:</strong> " + LocalDate.now() + "</p>" +
                "<p><strong>Date de fin:</strong> " + endDate + "</p>" +
                "</div>" +
                "<p>Merci pour votre confiance !</p>" +
                "<div class='footer'>" +
                "<img src='cid:logo' alt='Gymify Logo' width='100'>" +
                "<p>© 2024 Gymify. Tous droits réservés.</p>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";
    }

    private static LocalDate calculateEndDate(type_Abonnement type) {
        LocalDate now = LocalDate.now();
        switch (type) {
            case mois:
                return now.plusMonths(1);
            case trimestre:
                return now.plusMonths(6);
            case année:
                return now.plusYears(1);
            default:
                return now.plusMonths(1);
        }
    }

    private static String getDurationText(type_Abonnement type) {
        switch (type) {
            case mois: return "1 mois";
            case trimestre: return "6 mois";
            case année: return "1 an";
            default: return "1 mois";
        }
    }
}