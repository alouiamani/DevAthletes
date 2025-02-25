package controllers;

import entities.Commande;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class CommandeListCell extends ListCell<Commande> {
    private final HBox content;
    private final Label totalLabel;
    private final Label statusLabel;
    private final Label dateLabel;
    private final Circle statusIndicator;
    private final VBox textContent;

    public CommandeListCell() {
        totalLabel = new Label();
        totalLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        
        statusLabel = new Label();
        dateLabel = new Label();
        
        statusIndicator = new Circle(5);
        
        textContent = new VBox(
            totalLabel,
            statusLabel,
            dateLabel
        );
        textContent.setSpacing(5);

        content = new HBox();
        content.setSpacing(15);
        content.setPadding(new Insets(10));
    }

    @Override
    protected void updateItem(Commande commande, boolean empty) {
        super.updateItem(commande, empty);

        if (empty || commande == null) {
            setGraphic(null);
        } else {
            totalLabel.setText(String.format("Total: %.2fdt", commande.getTotal_c()));
            statusLabel.setText("Statut: " + commande.getStatut_c());
            dateLabel.setText("Date: " + commande.getDateC().toString());

            // Set status indicator color based on status
            switch (commande.getStatut_c().toLowerCase()) {
                case "en attente":
                    statusIndicator.setFill(Color.ORANGE);
                    break;
                case "en cours":
                    statusIndicator.setFill(Color.BLUE);
                    break;
                case "terminée":
                    statusIndicator.setFill(Color.GREEN);
                    break;
                case "annulée":
                    statusIndicator.setFill(Color.RED);
                    break;
                default:
                    statusIndicator.setFill(Color.GRAY);
            }

            content.getChildren().setAll(statusIndicator, textContent);
            setGraphic(content);
        }
    }
} 