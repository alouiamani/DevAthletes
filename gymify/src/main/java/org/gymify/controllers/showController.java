package org.gymify.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
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
import java.time.temporal.ChronoUnit;
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
    @FXML
    private GridPane calendarGrid;


    public void setPlanningData(Planning planning,User user) {
        if (planning != null) {
            this.user=user;
            this.selectedPlanning = planning;
            planningTitle.setText(planning.getTitle());

            System.out.println("Le planning n'est pas null");
            afficherCalendrier();
            // Affiche le titre du planning
        } else {
            System.out.println("Le planning est null");
        }
    }

    private void afficherCalendrier() {
        if (selectedPlanning == null) {
            System.out.println("Erreur : selectedPlanning est null");
            return;
        }

        // Récupérer les cours associés au planning
        List<Cours> coursList = coursService.getCoursByPlanning(selectedPlanning);

        // Conversion des dates de début et de fin en LocalDate
        Date dateDebut = selectedPlanning.getdateDebut();
        Date dateFin = selectedPlanning.getdateFin();

        // Convertir java.sql.Date en java.util.Date
        java.util.Date utilDateDebut = new java.util.Date(dateDebut.getTime());
        java.util.Date utilDateFin = new java.util.Date(dateFin.getTime());

        // Convertir java.util.Date en LocalDate
        LocalDate startDate = utilDateDebut.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate endDate = utilDateFin.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        calendarGrid.getChildren().clear(); // Nettoyer avant d'ajouter

        // Ajouter les jours de la semaine
        String[] joursSemaine = {"Lun", "Mar", "Mer", "Jeu", "Ven", "Sam", "Dim"};
        for (int i = 0; i < 7; i++) {
            Label jourLabel = new Label(joursSemaine[i]);
            jourLabel.setStyle("-fx-font-weight: bold; -fx-alignment: center; -fx-padding: 5px;");
            calendarGrid.add(jourLabel, i, 0);
        }

        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
        int column = 0;
        int row = 1;

        for (int i = 0; i <= daysBetween; i++) {
            LocalDate currentDate = startDate.plusDays(i);
            Label dayLabel = new Label(currentDate.getDayOfMonth() + "/" + currentDate.getMonthValue());

            // Vérifier si la date actuelle correspond à un cours
            boolean isCoursDate = coursList.stream()
                    .anyMatch(cours -> {
                        // Convertir java.sql.Date en java.util.Date
                        java.util.Date coursUtilDate = new java.util.Date(cours.getDateDebut().getTime());
                        LocalDate coursDate = coursUtilDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                        return coursDate.isEqual(currentDate);
                    });

            // Appliquer un style différent si c'est une date de cours
            if (isCoursDate) {
                dayLabel.setStyle("-fx-border-color: black; -fx-padding: 5px; -fx-background-color: #ADD8E6;"); // Bleu clair
            } else {
                dayLabel.setStyle("-fx-border-color: black; -fx-padding: 5px; -fx-background-color: #f4f4f4;"); // Couleur par défaut
            }

            calendarGrid.add(dayLabel, column, row);
            column++;

            if (column > 6) {
                break; // Sortir de la boucle après avoir ajouté les dates sur une seule ligne
            }
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

        // Charger le fichier CSS
        listView.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

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
                            vBox.setSpacing(8);
                            vBox.getStyleClass().add("list-cell");

                            // Titre et description
                            Text titleText = new Text(item.getTitle());
                            titleText.getStyleClass().add("course-title");

                            Text descriptionText = new Text(item.getDescription());
                            descriptionText.getStyleClass().add("course-description");

                            vBox.getChildren().addAll(titleText, descriptionText);

                            // Date de début
                            Date startDate = item.getDateDebut();
                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                            String startDateString = dateFormat.format(startDate);
                            Text startDateText = new Text("Date: " + startDateString);
                            startDateText.getStyleClass().add("course-date");

                            // Bouton d'édition
                            ImageView editIcon = new ImageView(new Image("/images/editer.png"));
                            editIcon.setFitWidth(20);
                            editIcon.setFitHeight(20);
                            Button editButton = new Button();
                            editButton.setGraphic(editIcon);
                            editButton.getStyleClass().add("button");

                            // Bouton de suppression
                            ImageView deleteIcon = new ImageView(new Image("/images/supprimer.png"));
                            deleteIcon.setFitWidth(20);
                            deleteIcon.setFitHeight(20);
                            Button deleteButton = new Button();
                            deleteButton.setGraphic(deleteIcon);
                            deleteButton.getStyleClass().add("button");

                            // Agencer la date et les boutons
                            HBox dateBox = new HBox(10);
                            Region spacer1 = new Region();
                            spacer1.setPrefWidth(50);
                            Region spacer2 = new Region();
                            spacer2.setPrefWidth(10);
                            dateBox.getChildren().addAll(startDateText,spacer1 ,editButton,spacer2 ,deleteButton);
                            vBox.getChildren().add(dateBox);

                            // Heures de début et de fin
                            String startHour = item.getHeureDebut().toString();
                            Text startHourText = new Text("Début: " + startHour);
                            startHourText.getStyleClass().add("course-time");

                            String endHourString = item.getHeureFin().toString();
                            Text endHourText = new Text("Fin: " + endHourString);
                            endHourText.getStyleClass().add("course-time");

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


    public void goBackPage(ActionEvent event) {
        try {
            // Charger la nouvelle page (remplace "previousPage.fxml" par le bon fichier FXML)
            Parent root = FXMLLoader.load(getClass().getResource("/plannings.fxml"));

            // Récupérer la scène actuelle
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}



