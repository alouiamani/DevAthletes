package controllers;

import entities.Cart;
import entities.Commande;
import entities.Produit;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import services.CartService;
import java.io.IOException;
import java.sql.SQLException;

public class CartController {
    @FXML
    private ListView<Produit> cartListView;
    @FXML
    private Label totalLabel;
    @FXML
    private Button checkoutButton;
    
    private final CartService cartService = new CartService();

    @FXML
    public void initialize() {
        updateCartView();
        
        // Set custom cell factory
        cartListView.setCellFactory(lv -> new CartItemCell());
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
            dialogStage.showAndWait();

            // Process result
            if (checkoutController.isPaymentSuccessful()) {
                cartService.finalizeOrder(commande.getId_c());
                showAlert(Alert.AlertType.INFORMATION, "Success", "Order placed successfully!");
                navigateToOrders();
            } else {
                cartService.cancelOrder(commande.getId_c());
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

    // Custom ListCell for cart items
    private class CartItemCell extends ListCell<Produit> {
        private final HBox content;
        private final Label nameLabel;
        private final Label priceLabel;
        private final Spinner<Integer> quantitySpinner;
        private final Button removeButton;

        public CartItemCell() {
            nameLabel = new Label();
            priceLabel = new Label();
            quantitySpinner = new Spinner<>();
            quantitySpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 999, 1));
            removeButton = new Button("Remove");
            
            content = new HBox(10);
            content.getChildren().addAll(nameLabel, priceLabel, quantitySpinner, removeButton);

            removeButton.setOnAction(e -> {
                Produit item = getItem();
                if (item != null) {
                    cartService.removeFromCart(item);
                    updateCartView();
                }
            });

            quantitySpinner.valueProperty().addListener((obs, oldVal, newVal) -> {
                Produit item = getItem();
                if (item != null) {
                    try {
                        cartService.updateQuantity(item, newVal);
                        updateCartView();
                    } catch (IllegalArgumentException ex) {
                        showAlert(Alert.AlertType.WARNING, "Warning", ex.getMessage());
                        quantitySpinner.getValueFactory().setValue(oldVal);
                    }
                }
            });
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
                setGraphic(content);
            }
        }
    }
} 