package org.gymify.controllers;

import javafx.animation.PauseTransition;
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
import javafx.util.Duration;
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
import java.util.Optional;
import java.util.stream.Collectors;

public class showController {
    @FXML
    public StackPane contentArea;
    @FXML
    private Label planningTitle;
    @FXML
    private ListView<Cours> listView;
    private Planning selectedPlanning;
    private CoursService coursService = new CoursService();
    private User user;
    @FXML
    private GridPane calendarGrid;
    @FXML
    private TextField rechercheCours;

    private LocalDate selectedDate; // Variable pour stocker la date sélectionnée

    public void setPlanningData(Planning planning, User user) {
        if (planning != null) {
            this.user = user;
            this.selectedPlanning = planning;
            planningTitle.setText(planning.getTitle());
            System.out.println("Le planning n'est pas null");
            afficherCalendrier(); // Afficher le calendrier avec les dates
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
        calendarGrid.getColumnConstraints().clear(); // Supprimer les contraintes de colonnes existantes

        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
        int column = 0; // Colonne initiale
        int row = 1; // Ligne unique pour les dates

        // Ajouter des colonnes dynamiquement
        for (int i = 0; i <= daysBetween; i++) {
            calendarGrid.getColumnConstraints().add(new ColumnConstraints(80)); // Largeur de chaque colonne
        }

        for (int i = 0; i <= daysBetween; i++) {
            LocalDate currentDate = startDate.plusDays(i);
            String dayOfWeek = currentDate.getDayOfWeek().toString().substring(0, 3); // Ex: "Mon", "Tue"
            Label dayLabel = new Label(dayOfWeek + " " + currentDate.getDayOfMonth() + "/" + currentDate.getMonthValue());
            dayLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #333; -fx-border-color: #ccc; -fx-border-width: 1px; -fx-padding: 5px;");

            // Vérifier si la date actuelle correspond à un cours
            boolean isCoursDate = coursList.stream()
                    .anyMatch(cours -> {
                        java.util.Date coursUtilDate = new java.util.Date(cours.getDateDebut().getTime());
                        LocalDate coursDate = coursUtilDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                        return coursDate.isEqual(currentDate);
                    });

            // Appliquer un style différent si c'est une date de cours
            if (isCoursDate) {
                dayLabel.getStyleClass().add("course-date"); // Ajouter la classe CSS pour les dates de cours
            } else {
                dayLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #333; -fx-background-color: #f4f4f4; -fx-border-color: #ccc; -fx-border-width: 1px; -fx-padding: 5px;");
            }

            // Ajouter un gestionnaire d'événements pour le clic sur la date
            dayLabel.setOnMouseClicked(event -> {
                // Réinitialiser le style de tous les labels (sauf ceux avec la classe CSS course-date)
                calendarGrid.getChildren().forEach(node -> {
                    if (node instanceof Label) {
                        Label label = (Label) node;
                        if (!label.getStyleClass().contains("course-date")) {
                            label.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #333; -fx-background-color: #f4f4f4; -fx-border-color: #ccc; -fx-border-width: 1px; -fx-padding: 5px;");
                        }
                    }
                });

                // Appliquer un style différent au label cliqué
                dayLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #fff; -fx-background-color: #073a4d; -fx-border-color: #010917; -fx-border-width: 1px; -fx-padding: 5px;");

                // Mettre à jour la date sélectionnée
                selectedDate = currentDate;

                // Filtrer et afficher les cours
                String searchText = rechercheCours.getText().trim().toLowerCase();
                filterAndDisplayCoursesByDate(selectedDate, searchText);
            });

            // Ajouter le label à la grille
            calendarGrid.add(dayLabel, column, row);

            // Passer à la colonne suivante
            column++;
        }

        // Ajuster la largeur de la GridPane
        calendarGrid.setPrefWidth(daysBetween * 80); // 80 = largeur estimée par colonne
    }

    private void filterAndDisplayCoursesByDate(LocalDate selectedDate, String searchText) {
        if (selectedDate != null) {
            List<Cours> allCourses = coursService.getCoursByPlanning(selectedPlanning);
            List<Cours> filteredCourses = allCourses.stream()
                    .filter(cours -> {
                        Date dateDebut = cours.getDateDebut();
                        if (dateDebut != null) {
                            // Convertir java.sql.Date à java.util.Date
                            Date utilDate = new Date(dateDebut.getTime());
                            // Convertir java.util.Date à LocalDate
                            Instant instant = utilDate.toInstant();
                            LocalDate dateDebutLocalDate = instant.atZone(ZoneId.systemDefault()).toLocalDate();

                            // Vérifier si la date du cours correspond à la date sélectionnée
                            boolean matchesDate = dateDebutLocalDate.isEqual(selectedDate);

                            // Vérifier si le texte de recherche correspond au titre ou à la description du cours
                            boolean matchesText = searchText.isEmpty()
                                    || cours.getTitle().toLowerCase().contains(searchText)
                                    || cours.getDescription().toLowerCase().contains(searchText);

                            return matchesDate && matchesText;
                        }
                        return false;
                    })
                    .collect(Collectors.toList());

            // Mettre à jour la ListView avec les cours filtrés
            listView.getItems().setAll(filteredCourses);
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
                controller.setPlanningSelect(this.selectedPlanning, user); // Vérifiez que popupController n'est pas null
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

    public void initialize() {
        // Charger le fichier CSS
        listView.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

        // Ajouter un écouteur pour le champ de recherche
        rechercheCours.textProperty().addListener((observable, oldValue, newValue) -> {
            if (selectedDate != null) {
                String searchText = newValue.trim().toLowerCase();
                filterAndDisplayCoursesByDate(selectedDate, searchText);
            }
        });

        // Configurer la ListView
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
                            editButton.setOnAction(event -> {
                                // Afficher la popup d'édition
                                showEditCoursePopup(item);
                            });

                            // Bouton de suppression
                            ImageView deleteIcon = new ImageView(new Image("/images/supprimer.png"));
                            deleteIcon.setFitWidth(20);
                            deleteIcon.setFitHeight(20);
                            Button deleteButton = new Button();
                            deleteButton.setGraphic(deleteIcon);
                            deleteButton.getStyleClass().add("button");
                            deleteButton.setOnAction(event -> {
                                // Afficher une popup de confirmation
                                boolean confirmed = showConfirmationDialog("Confirmer la suppression", "Êtes-vous sûr de vouloir supprimer ce cours ?");

                                if (confirmed) {
                                    // Supprimer le cours
                                    coursService.Delete(item);
                                    refreshCourseList();
                                } else {
                                    showAlert("Erreur", "La suppression du cours a échoué.");
                                }
                            });

                            // Agencer la date et les boutons
                            HBox dateBox = new HBox(10);
                            Region spacer1 = new Region();
                            spacer1.setPrefWidth(50);
                            Region spacer2 = new Region();
                            spacer2.setPrefWidth(10);
                            dateBox.getChildren().addAll(startDateText, spacer1, editButton, spacer2, deleteButton);
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

    private void showEditCoursePopup(Cours cours) {
        try {
            // Charger le fichier FXML de la popup d'édition
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/editCours.fxml"));
            Parent root = loader.load();

            // Récupérer le contrôleur de la popup
            editCoursController editController = loader.getController();
            if (editController != null) {
                // Passer les données du cours à éditer
                editController.setCoursData(cours);
            } else {
                System.out.println("Erreur : Le contrôleur de la popup d'édition est null.");
            }

            // Créer une nouvelle scène avec la popup
            Scene scene = new Scene(root);

            // Créer un nouveau Stage pour la popup
            Stage popupStage = new Stage();
            popupStage.setTitle("Éditer le cours");
            popupStage.setScene(scene);
            popupStage.initModality(Modality.APPLICATION_MODAL);

            // Afficher la popup
            popupStage.showAndWait();

            // Rafraîchir la liste des cours après édition
            refreshCourseList();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean showConfirmationDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    // Méthode pour rafraîchir la liste des cours
    private void refreshCourseList() {
        if (selectedDate != null) {
            String searchText = rechercheCours.getText().trim().toLowerCase();
            filterAndDisplayCoursesByDate(selectedDate, searchText);
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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