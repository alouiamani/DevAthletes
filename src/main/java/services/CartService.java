package services;

import entities.Cart;
import entities.Commande;
import entities.Produit;
import utils.AuthToken;
import utils.MyDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CartService {
    private static Cart currentCart = new Cart();
    private final ServiceCommande serviceCommande = new ServiceCommande();
    private final ServiceProduit serviceProduit = new ServiceProduit();
    private final Connection connection;

    public CartService() {
        this.connection = MyDatabase.getInstance().getConnection();
    }

    public Cart getCurrentCart() {
        return currentCart;
    }

    public void addToCart(Produit produit, int quantity) throws IllegalArgumentException {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (quantity > produit.getStock_p()) {
            throw new IllegalArgumentException("Not enough stock available");
        }
        currentCart.addItem(produit, quantity);
    }

    public void removeFromCart(Produit produit) {
        currentCart.removeItem(produit);
    }

    public void updateQuantity(Produit produit, int quantity) throws IllegalArgumentException {
        if (quantity > produit.getStock_p()) {
            throw new IllegalArgumentException("Not enough stock available");
        }
        currentCart.updateQuantity(produit, quantity);
    }

    public void clearCart() {
        currentCart.clear();
    }

    public Commande createOrder() throws SQLException {
        if (currentCart.getItems().isEmpty()) {
            throw new IllegalStateException("Cart is empty");
        }

        // Create new order with the correct total from cart
        Commande commande = new Commande(currentCart.getTotal(), "EN COURS"); // Change initial status to EN COURS
        commande.setUser_id(AuthToken.getCurrentUser().getId_User());
        
        // Add order to database
        int commandeId = serviceCommande.ajouter(commande);
        commande.setId_c(commandeId);
        
        return commande;
    }

    public void finalizeOrder(int commandeId) throws SQLException {
        // Add order items and update stock
        for (var entry : currentCart.getItems().entrySet()) {
            Produit produit = entry.getKey();
            int quantity = entry.getValue();
            
            // Add order-product relationship
            serviceCommande.ajouterCommandeProduit(commandeId, produit.getId_p(), quantity);
            
            // Update product stock
            produit.setStock_p(produit.getStock_p() - quantity);
            serviceProduit.modifier(produit);
        }
        
        // Clear cart after successful checkout
        clearCart();
    }

    public void cancelOrder(int commandeId) {
        try {
            // Delete the order and its related records
            String sql = "DELETE FROM commande WHERE id_c = ?";
            try (PreparedStatement pst = connection.prepareStatement(sql)) {
                pst.setInt(1, commandeId);
                pst.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
} 