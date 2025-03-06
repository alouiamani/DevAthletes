package org.gymify.services;

<<<<<<< HEAD
import org.gymify.entities.Salle;

=======
>>>>>>> aea46390f69b2bb6a9b587a90aaa9dc13d0bcff7
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;
import java.util.Properties;

public class EmailService {

    private static final String USERNAME = "amani.aloui@esprit.tn";  // Ton adresse email
    private static final String PASSWORD = "X(382817226370ah+";        // Ton mot de passe

<<<<<<< HEAD
    public static void envoyerEmail(String destinataire, String sujet, Salle salle, boolean isAjout) {
=======
    public static void envoyerEmail(String destinataire, String sujet, String messageText) {
>>>>>>> aea46390f69b2bb6a9b587a90aaa9dc13d0bcff7
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
<<<<<<< HEAD

=======
>>>>>>> aea46390f69b2bb6a9b587a90aaa9dc13d0bcff7
            // Créer une partie HTML pour le contenu de l'email
            MimeMultipart multipart = new MimeMultipart("related");

            // Partie texte HTML
            MimeBodyPart htmlPart = new MimeBodyPart();
<<<<<<< HEAD
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
=======
            String htmlContent = "<html><body>"
                    + "<h2>Bonjour, " + messageText + "</h2>"
                    + "<p>Nous vous informons que vous avez été affecté(e) à une nouvelle salle. Voici les détails :</p>"
                    + "<p><strong>Nom de la salle :</strong> " + messageText + "</p>"
                    + "<p><strong>Adresse :</strong> " + messageText + "</p>"
                    + "<br><img src=\"cid:logo\">"
                    + "<p>Cordialement, l'équipe de gestion des salles</p>"
                    + "</body></html>";
>>>>>>> aea46390f69b2bb6a9b587a90aaa9dc13d0bcff7
            htmlPart.setContent(htmlContent, "text/html");

            // Partie logo
            MimeBodyPart imagePart = new MimeBodyPart();
<<<<<<< HEAD
            FileDataSource fds = new FileDataSource("C:\\Users\\39\\Documents\\GitHub\\DevAthletes\\DevAthletes\\gymify\\src\\main\\resources\\images\\logo.png");  // Spécifie le chemin de ton logo
            imagePart.setDataHandler(new DataHandler(fds));
=======
            // No need to cast to DataSource
            FileDataSource fds = new FileDataSource("/logo.png");  // Spécifie le chemin de ton logo
            imagePart.setDataHandler(new DataHandler(fds)); // Directly use fds
>>>>>>> aea46390f69b2bb6a9b587a90aaa9dc13d0bcff7
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
<<<<<<< HEAD
            System.err.println("Erreur lors de l'envoi de l'email : " + e.getMessage());
        }
    }}
=======
            System.err.println("Error while sending email: " + e.getMessage());
        }
    }
}
>>>>>>> aea46390f69b2bb6a9b587a90aaa9dc13d0bcff7
