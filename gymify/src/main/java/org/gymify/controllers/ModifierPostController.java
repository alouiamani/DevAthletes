package org.gymify.controllers;
import java.io.FileInputStream;
import java.io.IOException;

import com.ibm.cloud.sdk.core.security.IamAuthenticator;
import com.ibm.watson.speech_to_text.v1.SpeechToText;
import com.ibm.watson.speech_to_text.v1.model.RecognizeOptions;
import com.ibm.watson.speech_to_text.v1.model.SpeechRecognitionResults;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import org.gymify.entities.Post;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.scene.web.HTMLEditor;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.gymify.services.PostService;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;


public class ModifierPostController {
    @FXML
    private Label contentErrorLabel;

    @FXML
    private Label imageUrlErrorLabel; // Ajoute un Label pour afficher l'erreur de l'URL de l'image
    @FXML
    private ProgressIndicator loadingSpinner;

    @FXML
    private Button uploadAudioButton;

    private static final String API_KEY = "JR-LCWjnds3scsjrtCtn2wCNlyR2GWGAQq_hJ1D6Fhh-";
    private static final String SERVICE_URL = "https://api.au-syd.speech-to-text.watson.cloud.ibm.com/instances/760398a7-3622-4319-83f8-415080a4ee25";

    @FXML
private TextArea hiddenTextArea; // Ajoute un TextArea invisible dans ton FXML
private File selectedFile; // Stocke l'image sélectionnée localement

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
    private Button saveButton;

    @FXML
    private ImageView imagePreview;

    @FXML
    private StackPane rootPane;



    @FXML
    private Pane uploadButton; // Bouton pour uploader une image

    @FXML
    private Label titleErrorLabel;

    private Post post;
    private AfficherMyPostsController parentController;

    public void initData(Post post, AfficherMyPostsController parentController) {
        this.post = post;
        this.parentController = parentController;
        titleField.setText(post.getTitle());
        newPostEditor.setHtmlText(post.getContent());

        if (post.getImageUrl() != null && !post.getImageUrl().isEmpty()) {
            if (post.getImageUrl().startsWith("http")) {
                // Si l'image est une URL web
                imagePreview.setImage(new Image(post.getImageUrl()));
                imageUrlField.setText(post.getImageUrl()); // Affiche l'URL web
            } else {
                // Sinon, c'est une image locale
                File imageFile = new File(post.getImageUrl());
                if (imageFile.exists()) {
                    imagePreview.setImage(new Image(imageFile.toURI().toString())); // Charge l'image locale
                    imageUrlField.setText(imageFile.getAbsolutePath()); // Affiche le chemin sans "file://"
                }
            }
        }
    }



    @FXML
    public void initialize() {
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
        // Validation de l'URL de l'image
        imageUrlField.textProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("Changement détecté dans l'URL : " + newValue); // Ajout d'une ligne de débogage

            if (!newValue.trim().isEmpty() && !PostService.isValidUrl(newValue)) {
                imageUrlErrorLabel.setText("URL de l'image invalide !");
                imageUrlField.setStyle("-fx-border-color: D84040; -fx-border-width: 2px;");
            } else {
                imageUrlErrorLabel.setText(""); // Efface le message si l'URL est valide
                imageUrlField.setStyle("-fx-border-color: #333;");
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



    }

    @FXML
    void uploadImage(MouseEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif"));
        selectedFile = fileChooser.showOpenDialog(uploadButton.getScene().getWindow());

        if (selectedFile != null && selectedFile.exists()) {
            try {
                File destDir = new File("uploads");
                if (!destDir.exists() && !destDir.mkdirs()) {
                    throw new IOException("Impossible de créer le dossier uploads");
                }

                File destFile = new File(destDir, selectedFile.getName());
                Files.copy(selectedFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                String imagePath = destFile.toURI().toString();
                imagePreview.setImage(new Image(imagePath));
                imageUrlField.setText(destFile.getAbsolutePath()); // Stocker le chemin sans file://
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Impossible de charger l'image.");
                e.printStackTrace();
            }
        }
    }




    @FXML
    void modifierPost() {
        String newTitle = titleField.getText().trim();
        String newContent = newPostEditor.getHtmlText().trim();
        String imageUrl = imageUrlField.getText().trim();

        if (newTitle.length() < 4) {
            showAlert(Alert.AlertType.ERROR, "Error", "champs obligatoires");
            return;
        }

        boolean isModified = !newTitle.equals(post.getTitle()) ||
                !newContent.equals(post.getContent()) ||
                (imageUrl != null && !imageUrl.equals(post.getImageUrl()));

        if (!isModified) {
            showAlert(Alert.AlertType.WARNING, "Aucun changement", "Vous n'avez effectué aucun changement.");
            closeAfterDelay();
            return;
        }
        if (imageUrl.isEmpty()) {
            imageUrl = null; // Assure que l'image sera supprimée du post
            imagePreview.setImage(null); // Supprime l'aperçu de l'image dans l'UI
        }


        if (selectedFile != null && selectedFile.exists()) {
            try {
                File destDir = new File("uploads");
                if (!destDir.exists() && !destDir.mkdirs()) {
                    throw new IOException("Impossible de créer le dossier uploads");
                }

                File destFile = new File(destDir, selectedFile.getName());
                Files.copy(selectedFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                imageUrl = destFile.getAbsolutePath(); // Stocker le chemin sans file://

            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Une erreur s'est produite lors de la copie de l'image.");
                return;
            }
        } else if (!imageUrl.startsWith("http")) {
            File imageFile = new File(imageUrl.replace("file:/", ""));
            if (!imageFile.exists()) { // Correction ici
                showAlert(Alert.AlertType.ERROR, "Error", "L'image sélectionnée n'existe pas.");
                return;
            }
            imageUrl = imageFile.getAbsolutePath();
        }

        post.setTitle(newTitle);
        post.setContent(newContent);
        post.setImageUrl(imageUrl);


        try {
            new PostService().modifier(post);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Post modifié avec succès !");
            closeAfterDelay();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Une erreur s'est produite lors de la modification du post.");
        }
    }

    private void closeAfterDelay() {
        PauseTransition pause = new PauseTransition(Duration.seconds(2));
        pause.setOnFinished(e -> {
            if (parentController != null) {
                parentController.afficherPosts(post.getId_User());
            }
            Stage stage = (Stage) saveButton.getScene().getWindow();
            if (stage != null) {
                stage.close();
            }
        });
        pause.play();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
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
                        showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'envoyer l'audio à IBM.");
                    });
                }
            }).start(); // Lancer la tâche dans un thread séparé
        } else {
            showAlert(Alert.AlertType.WARNING, "Avertissement", "Aucun fichier audio sélectionné.");
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



    private String removeHtmlTagsAndSpaces(String content) {
        // Supprime les balises HTML
        String textWithoutHtml = content.replaceAll("<[^>]*>", "");

        // Remplace les entités HTML comme &nbsp; par un espace vide
        textWithoutHtml = textWithoutHtml.replaceAll("&nbsp;", " ");

        // Supprime les espaces, retours à la ligne, et tabulations
        return textWithoutHtml.replaceAll("\\s+", "").trim();
    }


}
