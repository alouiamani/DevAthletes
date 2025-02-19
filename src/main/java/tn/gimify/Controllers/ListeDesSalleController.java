package tn.gimify.Controllers;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;
import tn.gimify.entities.Salle;
import tn.gimify.services.SalleService;

import java.io.IOException;
import java.util.List;

public class ListeDesSalleController {

    @FXML private VBox sallesContainer;
    @FXML private TextField searchField;

    @FXML private Button ajoutBtn;

    private SalleService salleService = new SalleService();
    private List<Salle> allSalles; // Liste pour stocker toutes les salles
    private PauseTransition pause = new PauseTransition(Duration.millis(300));

    @FXML
    public void initialize() {
        // Ajouter un listener sur le champ de recherche
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            pause.setOnFinished(event -> searchSalles());
            pause.playFromStart();
        });

        ajoutBtn.setOnAction(event -> ouvrirFormulaireAjout());
        searchSalles(); // Charger toutes les salles au démarrage
    }

    // Charger les salles dynamiquement en fonction de la recherche
    private void searchSalles() {
        String search = searchField.getText();
        sallesContainer.getChildren().clear();

        List<Salle> salles = salleService.getAllSalles(search);
        for (Salle salle : salles) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/SalleCard.fxml"));
                Parent salleCard = loader.load();
                SalleCardUserController controller = loader.getController();
                controller.setSalleData(salle, this);
                sallesContainer.getChildren().add(salleCard);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Ouvrir l'interface d'ajout/modification
    private void ouvrirFormulaireAjout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SalleFormAdmin.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Ajouter une salle");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Rafraîchir la liste après modification/suppression
    public void refreshList() {
        searchSalles();
    }
}
