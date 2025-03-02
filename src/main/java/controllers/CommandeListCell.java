package controllers;

import entities.Commande;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class CommandeListCell extends ListCell<Commande> {
    private final HBox content;
    private final VBox detailsContainer;
    private final Label totalLabel;
    private final Label statusLabel;
    private final Label dateLabel;
    private final Label idLabel;

    public CommandeListCell() {
        content = new HBox(20);
        detailsContainer = new VBox(8);
        
        // Create and style labels
        totalLabel = new Label();
        totalLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        statusLabel = new Label();
        statusLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        
        dateLabel = new Label();
        dateLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #757575;");
        
        idLabel = new Label();
        idLabel.setStyle("-fx-font-size: 14px; -fx-font-style: italic; -fx-text-fill: #757575;");

        // Setup containers
        detailsContainer.getChildren().addAll(totalLabel, statusLabel, dateLabel, idLabel);
        content.getChildren().add(detailsContainer);
        
        // Style the content container
        content.setAlignment(Pos.CENTER_LEFT);
        content.setPadding(new Insets(15));
        content.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: #e0e0e0;" +
            "-fx-border-radius: 8;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);"
        );

        // Add hover effect
        content.setOnMouseEntered(e -> 
            content.setStyle(
                "-fx-background-color: #f8f9fa;" +
                "-fx-border-color: #2196F3;" +
                "-fx-border-radius: 8;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 8, 0, 0, 3);"
            )
        );
        content.setOnMouseExited(e -> 
            content.setStyle(
                "-fx-background-color: white;" +
                "-fx-border-color: #e0e0e0;" +
                "-fx-border-radius: 8;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);"
            )
        );
    }

    @Override
    protected void updateItem(Commande commande, boolean empty) {
        super.updateItem(commande, empty);

        if (empty || commande == null) {
            setGraphic(null);
        } else {
            // Set order total
            totalLabel.setText(String.format("Total: %.2f DT", commande.getTotal_c()));
            
            // Set and style status
            String status = commande.getStatut_c();
            statusLabel.setText("Statut: " + status);
            switch (status.toLowerCase()) {
                case "completed":
                    statusLabel.setTextFill(Color.web("#4CAF50")); // Green
                    break;
                case "en cours":
                    statusLabel.setTextFill(Color.web("#2196F3")); // Blue
                    break;
                case "en attente":
                    statusLabel.setTextFill(Color.web("#FFA000")); // Orange
                    break;
                default:
                    statusLabel.setTextFill(Color.web("#757575")); // Gray
            }
            
            // Set date and ID
            dateLabel.setText("Date: " + commande.getDateC());
            idLabel.setText("Commande #" + commande.getId_c());

            setGraphic(content);
        }
    }
} 