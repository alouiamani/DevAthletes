package controllers;

import entities.Produit;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import java.io.File;

public class ProductListCell extends ListCell<Produit> {
    private final HBox content;
    private final VBox imageContainer;
    private final VBox detailsContainer;
    private final HBox actionsContainer;
    private final ImageView imageView;
    private final Label nameLabel;
    private final Label priceLabel;
    private final Label stockLabel;
    private final Label categoryLabel;
    private final Button addToCartButton;
    private final StackPane imagePlaceholder;

    public ProductListCell() {
        // Create image placeholder
        Rectangle placeholder = new Rectangle(100, 100);
        placeholder.setFill(Color.LIGHTGRAY);
        placeholder.setStroke(Color.GRAY);
        Label noImageLabel = new Label("No Image");
        noImageLabel.setTextFill(Color.GRAY);
        
        imagePlaceholder = new StackPane(placeholder, noImageLabel);

        // Setup image view
        imageView = new ImageView();
        imageView.setFitHeight(100);
        imageView.setFitWidth(100);
        imageView.setPreserveRatio(true);

        // Create image container
        imageContainer = new VBox(10);
        imageContainer.setAlignment(Pos.CENTER);
        imageContainer.setPrefWidth(120);

        // Create labels with styling
        nameLabel = new Label();
        nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        priceLabel = new Label();
        priceLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #2196F3;");
        
        stockLabel = new Label();
        stockLabel.setStyle("-fx-font-size: 14px;");
        
        categoryLabel = new Label();
        categoryLabel.setStyle("-fx-font-size: 14px; -fx-font-style: italic;");

        // Create details container
        detailsContainer = new VBox(5);
        detailsContainer.setAlignment(Pos.CENTER_LEFT);
        detailsContainer.getChildren().addAll(nameLabel, priceLabel, stockLabel, categoryLabel);
        HBox.setHgrow(detailsContainer, Priority.ALWAYS);

        // Adjust container spacing and alignment
        detailsContainer.setSpacing(10);  // Increase spacing between elements
        detailsContainer.setPadding(new Insets(0, 20, 0, 20));  // Add padding

        // Create cart button
        addToCartButton = new Button();
        try {
            Image cartImage = new Image(getClass().getResourceAsStream("/icons/cart2.png"));
            ImageView cartIcon = new ImageView(cartImage);
            cartIcon.setFitHeight(24);
            cartIcon.setFitWidth(24);
            addToCartButton.setGraphic(cartIcon);
            addToCartButton.setText("");
        } catch (Exception e) {
            addToCartButton.setText("+");
        }

        // Style the button
        addToCartButton.getStyleClass().add("cart-button");
        addToCartButton.setStyle(
            "-fx-background-color: #4CAF50;" +
            "-fx-text-fill: white;" +
            "-fx-padding: 10 20;" +
            "-fx-cursor: hand;" +
            "-fx-background-radius: 5;"
        );

        // Create actions container
        actionsContainer = new HBox(10);
        actionsContainer.setAlignment(Pos.CENTER);
        actionsContainer.getChildren().add(addToCartButton);

        // Main container
        content = new HBox(20);  // Increase spacing between main elements
        content.setAlignment(Pos.CENTER_LEFT);
        content.setPadding(new Insets(15));  // Increase padding
        content.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: #e0e0e0;" +
            "-fx-border-radius: 8;" +  // Slightly rounded corners
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);"  // Subtle shadow
        );
        content.getChildren().addAll(imageContainer, detailsContainer, actionsContainer);

        // Add hover effect to the entire cell
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
    protected void updateItem(Produit produit, boolean empty) {
        super.updateItem(produit, empty);

        if (empty || produit == null) {
            setGraphic(null);
        } else {
            // Update labels with product information
            nameLabel.setText(produit.getNom_p().toUpperCase());  // Product name in uppercase
            nameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
            
            priceLabel.setText(String.format("Prix: %.2f DT", produit.getPrix_p()));
            priceLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #2196F3; -fx-font-weight: bold;");
            
            // Add stock status indicator with clearer format
            int stock = produit.getStock_p();
            if (stock > 10) {
                stockLabel.setText("En stock (" + stock + " unités)");
                stockLabel.setStyle("-fx-text-fill: #4CAF50; -fx-font-size: 14px; -fx-font-weight: bold;");
            } else if (stock > 0) {
                stockLabel.setText("Stock limité (" + stock + " unités)");
                stockLabel.setStyle("-fx-text-fill: #FFA000; -fx-font-size: 14px; -fx-font-weight: bold;");
            } else {
                stockLabel.setText("Rupture de stock");
                stockLabel.setStyle("-fx-text-fill: #F44336; -fx-font-size: 14px; -fx-font-weight: bold;");
            }
            
            categoryLabel.setText("Catégorie: " + produit.getCategorie_p());
            categoryLabel.setStyle("-fx-font-size: 14px; -fx-font-style: italic; -fx-text-fill: #757575;");

            // Handle image loading
            imageContainer.getChildren().clear();
            if (produit.getImage_path() != null && !produit.getImage_path().isEmpty()) {
                try {
                    File imageFile = new File(produit.getImage_path());
                    if (imageFile.exists()) {
                        Image productImage = new Image(imageFile.toURI().toString());
                        imageView.setImage(productImage);
                        imageContainer.getChildren().add(imageView);
                    } else {
                        imageContainer.getChildren().add(imagePlaceholder);
                    }
                } catch (Exception e) {
                    imageContainer.getChildren().add(imagePlaceholder);
                }
            } else {
                imageContainer.getChildren().add(imagePlaceholder);
            }

            // Add cart button action
            addToCartButton.setOnAction(e -> {
                if (getListView().getScene().getWindow() instanceof Stage stage) {
                    listProduitFront controller = (listProduitFront) stage.getScene().getUserData();
                    if (controller != null) {
                        controller.ajouterAuPanier(produit);
                    }
                }
            });

            setGraphic(content);
        }
    }
} 