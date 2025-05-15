package org.gymify.controllers;

import com.ibm.cloud.sdk.core.security.IamAuthenticator;
import com.ibm.watson.speech_to_text.v1.SpeechToText;
import com.ibm.watson.speech_to_text.v1.model.RecognizeOptions;
import com.ibm.watson.speech_to_text.v1.model.SpeechRecognitionResults;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.scene.web.HTMLEditor;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.gymify.services.PostService;
import org.gymify.entities.Post;
import javafx.scene.input.MouseEvent;
import org.gymify.entities.User;
import org.gymify.utils.AuthToken;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class AjouterPostController {
    @FXML
    private Label imageUrlErrorLabel; // Ajoute un Label pour afficher l'erreur de l'URL de l'image
    @FXML
    private ProgressIndicator loadingSpinner;
    @FXML
    private StackPane rootPane;
    @FXML
    private Button uploadAudioButton;

    private static final String API_KEY = "JR-LCWjnds3scsjrtCtn2wCNlyR2GWGAQq_hJ1D6Fhh-";
    private static final String SERVICE_URL = "https://api.au-syd.speech-to-text.watson.cloud.ibm.com/instances/760398a7-3622-4319-83f8-415080a4ee25";



    @FXML
    private TextField imageUrlField;

    @FXML
    private TextArea newPost;

    @FXML
    private Button submitButton;

    @FXML
    private TextField titleField;

    @FXML
    private HTMLEditor newPostEditor;

    @FXML
    private Pane uploadButton;

    @FXML
    private ImageView imagePreview;
    @FXML
    private Label titleErrorLabel;
    @FXML
    private Label contentErrorLabel;

    @FXML
    private TextArea hiddenTextArea; // Ajoute un TextArea invisible dans ton FXML
    private File selectedFile; // Stocke l'image sélectionnée localement

    @FXML
    public void initialize() {
        // Vérification du titre
        titleField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.trim().isEmpty()) {
                titleErrorLabel.setText(""); // Effacer le message d'erreur si le champ est vide
                titleField.setStyle("-fx-border-color: #333;"); // Retirer le cadre rouge si vide
            }else if (newValue.trim().length() < 4) {
                titleErrorLabel.setText("Le titre doit contenir au moins 4 caractères !");
                // Ajouter un cadre rouge autour du champ
                titleField.setStyle("-fx-border-color: D84040; -fx-border-width: 2px;");
            } else {
                titleErrorLabel.setText(""); // Efface le message si la condition est remplie
                // Retirer le cadre rouge
                titleField.setStyle("-fx-border-color: #333;");
            }
        });
      // Vérification du contenu
        // Vérification du contenu
        newPostEditor.setOnKeyReleased(event -> {
            String content = newPostEditor.getHtmlText().trim();
            if (content.length() < 6) {
                contentErrorLabel.setText("Le contenu doit contenir au moins 6 caractères !");
                contentErrorLabel.setStyle("-fx-text-fill: D84040;"); // Red color
                newPostEditor.setStyle("-fx-border-color: D84040; -fx-border-width: 2px;");
            } else {
                contentErrorLabel.setText("");
                contentErrorLabel.setStyle("-fx-text-fill: #333;");
                newPostEditor.setStyle("-fx-border-color: #333;");
            }
        });

        // Validation de l'URL de l'image
        // Validation de l'URL de l'image
        imageUrlField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.trim().isEmpty()) {
                // Condition pour vérifier si l'URL contient "file:/"
                if (newValue.startsWith("file:/")) {
                    // Si c'est un "file://", on remplace par "C:/"
                    String updatedUrl = "C:/" + newValue.substring(7); // Remplace file:/ par C:/
                    imageUrlField.setText(updatedUrl); // Met à jour le texte dans le champ
                    imageUrlErrorLabel.setText(""); // Efface le message d'erreur
                    imageUrlField.setStyle("-fx-border-color: #333;"); // Restaure le style normal
                } else if (!PostService.isValidUrl(newValue)) {
                    // Vérification pour d'autres URL invalides
                    imageUrlErrorLabel.setText("URL de l'image invalide !");
                    imageUrlField.setStyle("-fx-border-color: D84040; -fx-border-width: 2px;");
                } else {
                    imageUrlErrorLabel.setText(""); // Efface le message si l'URL est valide
                    imageUrlField.setStyle("-fx-border-color: #333;");
                }
            } else {
                imageUrlErrorLabel.setText(""); // Efface le message si le champ est vide
                imageUrlField.setStyle("-fx-border-color: #333;");
            }
        });


    }


    @FXML
    private void handleUploadAudio() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Audio Files", "*.mp3", "*.wav"));
        File audioFile = fileChooser.showOpenDialog(uploadAudioButton.getScene().getWindow());

        if (audioFile != null) {
            // Afficher le spinner
            loadingSpinner.setVisible(true);

            // Créer une nouvelle tâche pour la transcription
            new Thread(() -> {
                try {
                    String transcribedText = transcribeAudio(audioFile);

                    // Une fois la transcription terminée, mettre à jour l'interface graphique
                    Platform.runLater(() -> {
                        loadingSpinner.setVisible(false); // Cacher le spinner
                        newPostEditor.setHtmlText(transcribedText); // Ajouter le texte dans le HTMLEditor
                    });

                } catch (IOException e) {
                    // Gérer les erreurs dans un thread séparé
                    Platform.runLater(() -> {
                        loadingSpinner.setVisible(false); // Cacher le spinner en cas d'erreur
                        showStyledAlert(Alert.AlertType.ERROR, "Error", "Impossible d'envoyer l'audio à IBM.");
                    });
                }
            }).start(); // Lancer la tâche dans un thread séparé
        } else {
            showStyledAlert(Alert.AlertType.WARNING, "Avertissement", "Aucun fichier audio sélectionné.");
        }
    }


    private String transcribeAudio(File audioFile) throws IOException {
        IamAuthenticator authenticator = new IamAuthenticator(API_KEY);
        SpeechToText speechToText = new SpeechToText(authenticator);
        speechToText.setServiceUrl(SERVICE_URL);

        RecognizeOptions options = new RecognizeOptions.Builder()
                .audio(new FileInputStream(audioFile))
                .contentType("audio/mp3")
                .model("fr-FR_BroadbandModel")
                .build();

        SpeechRecognitionResults transcript = speechToText.recognize(options).execute().getResult();
        return transcript.getResults().get(0).getAlternatives().get(0).getTranscript();
    }



    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }




    @FXML
    void uploadImage( MouseEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif"));
        selectedFile = fileChooser.showOpenDialog(uploadButton.getScene().getWindow());

        if (selectedFile != null && selectedFile.exists()) {
            try {
                File destDir = new File("uploads");
                if (!destDir.exists()) destDir.mkdir();

                File destFile = new File(destDir, selectedFile.getName());
                Files.copy(selectedFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                Image image = new Image(destFile.toURI().toString());
                imagePreview.setImage(image);
                imageUrlField.setText(destFile.toURI().toString());
            } catch (Exception e) {
                showStyledAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger l'image.");
                e.printStackTrace();
            }
        } else {
            showStyledAlert(Alert.AlertType.WARNING, "Avertissement", "Aucune image sélectionnée.");
        }
    }


    @FXML
    void submitPost(ActionEvent event) {
        System.out.println("Bouton cliqué !");
        System.out.println("🔍 Image URL avant sauvegarde: " + selectedFile);


        String title = titleField.getText().trim();
        String content = newPostEditor.getHtmlText().trim();
        String imageUrl = imageUrlField.getText().trim();

        if (title.isEmpty() || content.isEmpty()) {
            showStyledAlert(Alert.AlertType.ERROR, "Erreur", "Tous les champs sont obligatoires.");
            return;
        }
        if (content == null || content.trim().isEmpty() || removeHtmlTagsAndSpaces(content).isEmpty()) {
            showStyledAlert(Alert.AlertType.ERROR, "Erreur", "Le contenu est obligatoire.");
            return;
        }


        // Vérifier si une image locale a été sélectionnée
        if (selectedFile != null && selectedFile.exists()) {
            imageUrl = selectedFile.toURI().toString(); // Convertir en URL
        } else if (!imageUrl.isEmpty() && imageUrl.startsWith("http")) {
            // Si une URL web est fournie, on la garde
            if (!PostService.isValidUrl(imageUrl)) {
                showStyledAlert(Alert.AlertType.ERROR, "Erreur", "L'URL de l'image est invalide.");
                return;
            }
        } else {
            // Si aucune image n'est fournie, on ne la prend pas en compte
            imageUrl = "https://example.com/default.jpg";  // URL par défaut ou une image générique

        }

        // Vérification console
        System.out.println("🔍 Image URL utilisée : " + imageUrl);

        // Affichage de l'image dans l'ImageView
        imagePreview.setImage(new Image(imageUrl));

        // Nettoyage de l'URL avant stockage
        if (imageUrl.startsWith("file:/")) {
            imageUrl = imageUrl.replace("file:/", "").replace("%20", " "); // Supprime "file:/" et remplace "%20" par " "
        }
        // Création du Post
        Post post = new Post();
        post.setTitle(title);
        post.setContent(content);
        post.setImageUrl(imageUrl.isEmpty() ? "https://example.com/default.jpg" : imageUrl);
        
        // Récupérer l'utilisateur authentifié
        User currentUser = AuthToken.getCurrentUser();
        if (currentUser == null) {
            showStyledAlert(Alert.AlertType.ERROR, "Erreur", "Vous devez être connecté pour publier un post.");
            return;
        }
        post.setId_User(currentUser.getId());
        post.setCreatedAt(java.sql.Timestamp.valueOf(LocalDateTime.now()));

        // Enregistrement du post
        PostService postService = new PostService();
        try {
            postService.ajouter(post);

            // Affiche uniquement l'alerte de succès
            showStyledAlert(Alert.AlertType.INFORMATION, "Succès", "Post ajouté avec succès !");

        } catch (SQLException e) {
            showStyledAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur s'est produite.");
            e.printStackTrace();
        }
    }

    /**
     * Affiche une alerte stylisée avec une icône et du CSS.
     */
    private void showStyledAlert(Alert.AlertType type, String title, String message) {
        JFXDialogLayout content = new JFXDialogLayout();

        // Define the icon based on the alert type
        ImageView icon = new ImageView();
        if (type == Alert.AlertType.INFORMATION) {
            icon.setImage(new Image(getClass().getResourceAsStream("/assets/sucess(2).png")));
            icon.setFitWidth(30); // Set the width of the icon
            icon.setFitHeight(30); // Set the height of the icon
            content.setStyle("-fx-background-color: #e0f7fa; -fx-border-color: #00838f; -fx-border-width: 3px; -fx-background-radius: 15px;");
        } else if (type == Alert.AlertType.ERROR) {
            icon.setImage(new Image(getClass().getResourceAsStream("/assets/xerror.png")));
            icon.setFitWidth(30);
            icon.setFitHeight(30);
            content.setStyle("-fx-background-color: #fce4ec; -fx-border-color: #d32f2f; -fx-border-width: 3px; -fx-background-radius: 15px;");
        }
        else if (type == Alert.AlertType.WARNING) {
            icon.setImage(new Image(getClass().getResourceAsStream("/assets/warning.png")));
            icon.setFitWidth(30);
            icon.setFitHeight(30);
            content.setStyle("-fx-background-color: rgba(241,223,170,0.91); -fx-border-color: rgba(228,161,1,0.91); -fx-border-width: 3px; -fx-background-radius: 15px;");
        }


        // Add the icon to the alert header
        HBox headerBox = new HBox(icon, new Text(title));
        headerBox.setSpacing(10);
        content.setHeading(headerBox);

        // Increase the size of the message text
        Text messageText = new Text(message);
        messageText.setStyle("-fx-font-size: 14px;"); // Increase the text size here
        content.setBody(messageText);

        // Create the close button
        JFXButton closeButton = new JFXButton("Fermer");
        closeButton.setStyle("-fx-background-color: #00796b; -fx-text-fill: white; -fx-border-radius: 5px;");

        // Create the dialog
        JFXDialog dialog = new JFXDialog(rootPane, content, JFXDialog.DialogTransition.CENTER);

        // Set the action to close the dialog
        closeButton.setOnAction(event -> dialog.close());

        // Add the close button to the dialog actions
        content.setActions(closeButton);

        // Show the dialog
        dialog.show();

    }


    private String removeHtmlTagsAndSpaces(String content) {
        // Supprime les balises HTML
        String textWithoutHtml = content.replaceAll("<[^>]*>", "");

        // Remplace les entités HTML comme &nbsp; par un espace vide
        textWithoutHtml = textWithoutHtml.replaceAll("&nbsp;", " ");

        // Supprime les espaces, retours à la ligne, et tabulations
        return textWithoutHtml.replaceAll("\\s+", "").trim();
    }
}