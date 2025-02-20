package tn.esprit.tests;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
public class MainFX extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
                // Chargement du fichier FXML correspondant à l'interface de l'ajout d'équipe
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ServiceEvent.fxml"));
                try {
                    // Chargement de la scène FXML
                    Parent parent = loader.load();

                    // Création de la scène
                    Scene scene = new Scene(parent);
                    primaryStage.setScene(scene);
                    primaryStage.setTitle("Liste Evennement");

                    // Affichage de la fenêtre
                    primaryStage.show();
                } catch (IOException e) {
                    // Gestion de l'exception si le fichier FXML ne peut pas être chargé
                    e.printStackTrace();
                    throw new RuntimeException("Erreur lors du chargement de l'interface FXML", e);
                }
        }

    }
