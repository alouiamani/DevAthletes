package controllers;

import entities.Cart;
import entities.Commande;
import entities.Produit;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import services.CartService;
import services.SMSService;
import java.io.IOException;
import java.sql.SQLException;
import java.net.URL;
import java.util.ResourceBundle;

public class CartController implements Initializable {
    @FXML
    private ListView<Produit> cartListView;
    @FXML
    private Label totalLabel;
    @FXML
    private Button checkoutButton;
    
    private final CartService cartService = new CartService();
    private final SMSService smsService = new SMSService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        updateCartView();
        
        cartListView.setCellFactory(lv -> new ListCell<Produit>() {
            private final HBox content = new HBox(10);
            private final Label nameLabel = new Label();
            private final Label priceLabel = new Label();
            private final Spinner<Integer> quantitySpinner = new Spinner<>(1, 100, 1);
            private final Button removeButton = new Button();

            {
                try {
                    Image trashImage = new Image(getClass().getResourceAsStream("/icons/poubelle.png"));
                    ImageView trashIcon = new ImageView(trashImage);
                    trashIcon.setFitHeight(16);
                    trashIcon.setFitWidth(16);
                    removeButton.setGraphic(trashIcon);
                    removeButton.setText("");
                    
                    removeButton.setStyle(
                        "-fx-background-color: transparent;" +
                        "-fx-padding: 5px;" +
                        "-fx-cursor: hand;"
                    );
                    
                    removeButton.setOnMouseEntered(e -> 
                        removeButton.setStyle(
                            "-fx-background-color: #ffebee;" +
                            "-fx-padding: 5px;" +
                            "-fx-cursor: hand;"
                        )
                    );
                    removeButton.setOnMouseExited(e -> 
                        removeButton.setStyle(
                            "-fx-background-color: transparent;" +
                            "-fx-padding: 5px;" +
                            "-fx-cursor: hand;"
                        )
                    );
                } catch (Exception e) {
                    removeButton.setText("X");
                }

                // Add quantity spinner listener
                quantitySpinner.valueProperty().addListener((obs, oldVal, newVal) -> {
                    Produit item = getItem();
                    if (item != null) {
                        try {
                            cartService.updateQuantity(item, newVal);
                            CartController.this.updateCartView();
                        } catch (IllegalArgumentException ex) {
                            // Create alert directly instead of using showAlert
                            Alert alert = new Alert(Alert.AlertType.WARNING);
                            alert.setTitle("Warning");
                            alert.setHeaderText(null);
                            alert.setContentText(ex.getMessage());
                            alert.showAndWait();
                            quantitySpinner.getValueFactory().setValue(oldVal);
                        }
                    }
                });

                content.setAlignment(Pos.CENTER_LEFT);
                content.getChildren().addAll(nameLabel, priceLabel, quantitySpinner, removeButton);
            }

            @Override
            protected void updateItem(Produit produit, boolean empty) {
                super.updateItem(produit, empty);

                if (empty || produit == null) {
                    setGraphic(null);
                } else {
                    nameLabel.setText(produit.getNom_p());
                    priceLabel.setText(String.format("%.2f dt", produit.getPrix_p()));
                    quantitySpinner.getValueFactory().setValue(
                        cartService.getCurrentCart().getItems().get(produit)
                    );
                    
                    removeButton.setOnAction(e -> {
                        cartService.removeFromCart(produit);
                        CartController.this.updateCartView();
                    });

                    setGraphic(content);
                }
            }
        });
    }

    private void updateCartView() {
        cartListView.getItems().clear();
        cartListView.getItems().addAll(cartService.getCurrentCart().getItems().keySet());
        totalLabel.setText(String.format("Total: %.2f dt", cartService.getCurrentCart().getTotal()));
    }

    @FXML
    private void handleCheckout() {
        try {
            Cart cart = cartService.getCurrentCart();
            if (cart.getItems().isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Warning", "Your cart is empty");
                return;
            }

            // Create order first
            Commande commande = cartService.createOrder();
            if (commande == null) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to create order");
                return;
            }

            // Load the checkout dialog
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CheckoutDialog.fxml"));
            Parent root = loader.load();
            CheckoutDialogController checkoutController = loader.getController();
            
            // Set order details with the actual commande_id
            int itemCount = cart.getItems().values().stream().mapToInt(Integer::intValue).sum();
            float total = cart.getTotal();
            checkoutController.setOrderDetails(commande.getId_c(), total, itemCount);

            // Show dialog
            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initStyle(StageStyle.UNDECORATED);
            dialogStage.setScene(new Scene(root));
            
            // Wait for dialog to close before proceeding
            dialogStage.showAndWait();

            // Process result
            if (checkoutController.isPaymentSuccessful()) {
                // Only finalize if payment was successful
                cartService.finalizeOrder(commande.getId_c());
                
                // Send SMS confirmation here
                smsService.sendOrderConfirmation(commande, total);
                
                showAlert(Alert.AlertType.INFORMATION, "Success", "Order placed successfully!");
                navigateToOrders();
            } else {
                // Cancel the order if payment wasn't successful
                cartService.cancelOrder(commande.getId_c());
                return; // Exit without further processing
            }
        } catch (SQLException | IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to process checkout: " + e.getMessage());
        }
    }

    private void navigateToOrders() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/listCommandeFront.fxml"));
            Parent parent = loader.load();
            Stage stage = (Stage) checkoutButton.getScene().getWindow();
            stage.setScene(new Scene(parent));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/listProduitFront.fxml"));
            Parent parent = loader.load();
            Stage stage = (Stage) checkoutButton.getScene().getWindow();
            stage.setScene(new Scene(parent));
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Could not return to products page");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 