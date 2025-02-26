package org.gymify.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.gymify.entities.Cours;
import org.gymify.entities.Planning;
import org.gymify.entities.User;
import org.gymify.services.CoursService;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class showController {
    @FXML
    public StackPane contentArea;
    @FXML
    private Label planningTitle;
    @FXML
    private DatePicker datePicker;
    @FXML
    private ListView<Cours> listView;
    private Planning selectedPlanning;
    private CoursService coursService = new CoursService();
    private User user;


    public void setPlanningData(Planning planning,User user) {
        if (planning != null) {
            this.user=user;
            this.selectedPlanning = planning;
            planningTitle.setText(planning.getTitle());
            System.out.println("Le planning n'est pas null");
            // Affiche le titre du planning
        } else {
            System.out.println("Le planning est null");
        }
    }

    public void showAddCoursPage(ActionEvent actionEvent) {
        try {
            // Charger le fichier FXML de la popup
            System.out.println(selectedPlanning);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/addCours.fxml"));

            Parent root = loader.load();
            coursController controller = loader.getController();
            if (controller != null) {
                controller.setPlanningSelect(this.selectedPlanning,user); // Vérifiez que popupController n'est pas null
            } else {
                System.out.println("Controller is null");
            }


            // Créer une nouvelle scène avec la popup
            Scene scene = new Scene(root);

            // Créer un nouveau Stage pour la popup
            Stage popupStage = new Stage();
            popupStage.setTitle("Add Course");
            popupStage.setScene(scene);

            // Rendre la fenêtre modale (bloque l'accès à la fenêtre principale jusqu'à fermeture)
            popupStage.initModality(Modality.APPLICATION_MODAL);

            // Afficher la popup
            popupStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public void filterCoursesByDate(ActionEvent actionEvent) {
        LocalDate selectedDate = datePicker.getValue();
        if (selectedDate != null) {
            List<Cours> allCourses = coursService.Display();
            List<Cours> filteredCourses = allCourses.stream()
                    .filter(cours -> {
                        Date dateDebut = cours.getDateDebut(); // Toujours un java.sql.Date
                        if (dateDebut != null) {
                            // Convertir java.sql.Date à java.util.Date
                            Date utilDate = new Date(dateDebut.getTime());
                            // Convertir java.util.Date à LocalDate
                            Instant instant = utilDate.toInstant();
                            LocalDate dateDebutLocalDate = instant.atZone(ZoneId.systemDefault()).toLocalDate();
                            return dateDebutLocalDate.isEqual(selectedDate);
                        }
                        return false;
                    })
                    .collect(Collectors.toList());

            listView.getItems().setAll(filteredCourses);
        }
    }
    public void initialize() {
        listView.setCellFactory(new Callback<ListView<Cours>, ListCell<Cours>>() {
            @Override
            public ListCell<Cours> call(ListView<Cours> param) {
                return new ListCell<Cours>() {
                    @Override
                    protected void updateItem(Cours item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            // Conteneur principal
                            VBox vBox = new VBox();
                            vBox.setSpacing(5);

                            // Titre et description
                            Text titleText = new Text(item.getTitle());
                            titleText.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
                            Text descriptionText = new Text(item.getDescription());
                            descriptionText.setStyle("-fx-font-size: 12px; -fx-fill: #666;");
                            vBox.getChildren().addAll(titleText, descriptionText);

                            // Date de début
                            Date startDate = item.getDateDebut();
                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                            String startDateString = dateFormat.format(startDate);
                            Text startDateText = new Text("Date: " + startDateString);

                            // Ajouter un bouton avec une image à côté de la date
                            ImageView imageView = new ImageView(new Image("/images/editer.png"));
                            imageView.setFitWidth(16);
                            imageView.setFitHeight(16);
                            Button dateButton = new Button();
                            dateButton.setGraphic(imageView);
                            dateButton.setOnAction(e-> {
                                try {
                                    // Charger le fichier FXML de la popup
                                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/editCours.fxml"));
                                    Parent root = loader.load();

                                    // Obtenir le contrôleur de la popup
                                    editCoursController controller = loader.getController();
                                    if (controller != null) {
                                        // Passer les données du cours au contrôleur de la popup
                                        controller.setCoursData(item);
                                    } else {
                                        System.out.println("Controller is null");
                                    }

                                    // Créer une nouvelle scène avec la popup
                                    Scene scene = new Scene(root);

                                    // Créer un nouveau Stage pour la popup
                                    Stage popupStage = new Stage();
                                    popupStage.setTitle("Edit Course");
                                    popupStage.setScene(scene);

                                    // Rendre la fenêtre modale (bloque l'accès à la fenêtre principale jusqu'à fermeture)
                                    popupStage.initModality(Modality.APPLICATION_MODAL);

                                    // Afficher la popup
                                    popupStage.showAndWait();
                                    listView.getItems().setAll(coursService.Display());

                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                }
                            });
                            // Bouton de suppression avec icône
                            ImageView imageViewd = new ImageView(new Image("/images/supprimer.png"));
                            imageViewd.setFitWidth(16);
                            imageViewd.setFitHeight(16);
                            Button dButton = new Button();
                            dButton.setGraphic(imageViewd); // Remplacez par votre image

                            // Gérer l'événement de suppression
                            dButton.setOnAction(e -> {
                                // Affichage de l'alerte de confirmation
                                Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
                                confirmationAlert.setTitle("Confirmation");
                                confirmationAlert.setHeaderText("Voulez-vous vraiment supprimer ce cours ?");
                                confirmationAlert.setContentText(item.getTitle());

                                confirmationAlert.showAndWait().ifPresent(response -> {
                                    if (response == ButtonType.OK) {
                                        // Supprimer le cours de la base de données
                                        coursService.Delete(item);

                                        // Supprimer l'élément de la ListView
                                        listView.getItems().remove(item);

                                        // Message de succès
                                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                        alert.setTitle("Suppression réussie");
                                        alert.setHeaderText(null);
                                        alert.setContentText("Le cours a été supprimé avec succès.");
                                        alert.showAndWait();
                                    }
                                });
                            });

                            // Agencer la date et les boutons
                            HBox dateBox = new HBox(10); // Espacement de 10 entre la date et les boutons
                            dateBox.getChildren().addAll(startDateText, dateButton, dButton); // Ajouter la date et les boutons dans un HBox

                            vBox.getChildren().add(dateBox);

                            // Heures de début et de fin
                            String startHour = item.getHeureDebut().toString();
                            Text startHourText = new Text("Début: " + startHour);
                            String endHourString = item.getHeureFin().toString();
                            Text endHourText = new Text("Fin: " + endHourString);
                            HBox timeBox = new HBox(10);
                            timeBox.getChildren().addAll(startHourText, endHourText);
                            vBox.getChildren().add(timeBox);

                            // Affichage du contenu de la cellule
                            setGraphic(vBox);
                        }
                    }
                };
            }
        });
    }

    private void handleDateButtonClick(Cours item) {
        // Logique pour gérer l'action lorsque le bouton est cliqué
        System.out.println("Date button clicked for course: " + item.getTitle());
    }







}



