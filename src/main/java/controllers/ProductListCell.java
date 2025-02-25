package controllers;

import entities.Produit;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import java.io.File;

public class ProductListCell extends ListCell<Produit> {
    private final HBox content;
    private final ImageView imageView;
    private final Label nameLabel;
    private final Label priceLabel;
    private final Label stockLabel;
    private final Label categoryLabel;
    private final StackPane imagePlaceholder;
    private final VBox textContent;

    public ProductListCell() {
        // Create image placeholder
        Rectangle placeholder = new Rectangle(80, 80);
        placeholder.setFill(Color.LIGHTGRAY);
        placeholder.setStroke(Color.GRAY);
        Label noImageLabel = new Label("No Image");
        noImageLabel.setTextFill(Color.GRAY);
        
        imagePlaceholder = new StackPane(placeholder, noImageLabel);

        imageView = new ImageView();
        imageView.setFitHeight(80);
        imageView.setFitWidth(80);
        imageView.setPreserveRatio(true);

        nameLabel = new Label();
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        priceLabel = new Label();
        stockLabel = new Label();
        categoryLabel = new Label();

        textContent = new VBox(
            nameLabel,
            priceLabel,
            stockLabel,
            categoryLabel
        );
        textContent.setSpacing(5);

        content = new HBox();
        content.setSpacing(15);
        content.setPadding(new Insets(10));
    }

    @Override
    protected void updateItem(Produit produit, boolean empty) {
        super.updateItem(produit, empty);

        if (empty || produit == null) {
            setGraphic(null);
        } else {
            nameLabel.setText("Nom: " + produit.getNom_p());
            priceLabel.setText(String.format("Prix: %.2fdt", produit.getPrix_p()));
            stockLabel.setText("Stock: " + produit.getStock_p());
            categoryLabel.setText("Catégorie: " + produit.getCategorie_p());

            // Handle image loading
            boolean imageLoaded = false;
            if (produit.getImage_path() != null && !produit.getImage_path().isEmpty()) {
                try {
                    File imageFile = new File(produit.getImage_path());
                    if (imageFile.exists()) {
                        Image productImage = new Image(imageFile.toURI().toString());
                        imageView.setImage(productImage);
                        content.getChildren().setAll(imageView, textContent);
                        imageLoaded = true;
                    }
                } catch (Exception e) {
                    // Image loading failed, will use placeholder
                }
            }

            if (!imageLoaded) {
                content.getChildren().setAll(imagePlaceholder, textContent);
            }

            setGraphic(content);
        }
    }
} 