package controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

public class Controller implements Initializable {

    @FXML
    private ImageView Exit;

    @FXML
    private Label Menu;

    @FXML
    private Label MenuClose;

    @FXML
    private BorderPane mainContent; // Zone où le contenu change

    @FXML
    private AnchorPane slider;

    @FXML
    private JFXButton btnBlogs; // Bouton Blogs
    @FXML
    private JFXButton btnAllBlogs;
    @FXML
    private HBox hboxMenu; // Assure-toi que cette variable est bien liée au FXML


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        System.out.println("✅ Initialisation du contrôleur...");

        // Gestion de la fermeture de l'application
        Exit.setOnMouseClicked(event -> System.exit(0));

        // Gestion du menu latéral
        setupSideMenu();

        // Ajout de l'événement pour le bouton Blogs et all blogs
        btnBlogs.setOnAction(event -> loadBlogContent("/AfficherMyPosts.fxml"));
        btnAllBlogs.setOnAction(event -> loadBlogContent("/AfficherAllPosts.fxml"));


    }

    /**
     * Configure les animations du menu latéral.
     */
    private void setupSideMenu() {
        slider.setTranslateX(-176);

        Menu.setOnMouseClicked(event -> {
            TranslateTransition slide = new TranslateTransition(Duration.seconds(0.4), slider);
            slide.setToX(0);
            slide.play();

            slide.setOnFinished(e -> {
                Menu.setVisible(false);
                MenuClose.setVisible(true);
            });
        });

        MenuClose.setOnMouseClicked(event -> {
            TranslateTransition slide = new TranslateTransition(Duration.seconds(0.4), slider);
            slide.setToX(-176);
            slide.play();

            slide.setOnFinished(e -> {
                Menu.setVisible(true);
                MenuClose.setVisible(false);
            });
        });
    }

    /**
     * Charge le contenu de la page "AfficherMyPosts.fxml" dans la zone principale.
     */
    /**
     * Charge dynamiquement un fichier FXML dans la zone principale.
     */
    private void loadBlogContent(String fxmlFile) {
        System.out.println("📌 Chargement du contenu : " + fxmlFile);

        try {
            URL fxmlLocation = getClass().getResource(fxmlFile);
            if (fxmlLocation == null) {
                throw new IOException("❌ Fichier FXML introuvable : " + fxmlFile);
            }

            System.out.println("🔎 Fichier FXML trouvé à : " + fxmlLocation);

            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            AnchorPane view = loader.load();

            mainContent.setCenter(view);
            System.out.println("✅ Contenu chargé avec succès : " + fxmlFile);
        } catch (IOException e) {
            System.out.println("❌ Erreur lors du chargement !");
            e.printStackTrace();
            showAlert("Erreur de chargement", "Impossible de charger l'interface demandée.");
        }
    }


    /**
     * Affiche une alerte en cas d'erreur.
     */
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
