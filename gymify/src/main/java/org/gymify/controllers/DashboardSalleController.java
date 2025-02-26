
package org.gymify.controllers;

import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;



import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class DashboardSalleController {

    @FXML
    private Pane cardAlertes;

    @FXML
    private Pane cardEntraineurs;

    @FXML
    private Pane cardMembres;

    @FXML
    private Pane cardRevenus;

    @FXML
    private ImageView iconAlerte;

    @FXML
    private ImageView iconEntraineur;

    @FXML
    private ImageView iconMembres;

    @FXML
    private ImageView iconMoney;

    @FXML
    private Label lblEntraineur;

    @FXML
    private Label lblMembres;

    @FXML
    private Label lblMoney;

    @FXML
    private Label lblReclamations;

    @FXML
    private PieChart pieChartAbonnements;

    @FXML
    private PieChart pieChartRevenus;
    public void initialize() {
        // Charger les icônes
        iconMembres.setImage(new Image(getClass().getResource("/assets/athlete.png").toExternalForm()));
        iconEntraineur.setImage(new Image(getClass().getResource("/assets/coach.png").toExternalForm()));
        iconAlerte.setImage(new Image(getClass().getResource("/assets/alert.png").toExternalForm()));
        iconMoney.setImage(new Image(getClass().getResource("/assets/mny.png").toExternalForm()));
        // Remplir les PieCharts
        pieChartAbonnements.getData().addAll(
                new PieChart.Data("Annuel", 30),
                new PieChart.Data("Mensuel", 50),
                new PieChart.Data("Trimestriel", 20)
        );



        pieChartRevenus.getData().addAll(
                new PieChart.Data("En avance", 6400),
                new PieChart.Data("En cours", 10300),
                new PieChart.Data("Passé", 26500)
        );
    }
}



