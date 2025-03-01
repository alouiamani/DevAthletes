package entities;

import java.util.HashMap;
import java.util.Map;

public class Cart {
    private Map<Produit, Integer> items; // Product and quantity
    private float total;

    public Cart() {
        this.items = new HashMap<>();
        this.total = 0f;
    }

    public Map<Produit, Integer> getItems() {
        return items;
    }

    public float getTotal() {
        calculateTotal();
        return total;
    }

    public void addItem(Produit produit, int quantity) {
        items.merge(produit, quantity, Integer::sum);
        calculateTotal();
    }

    public void removeItem(Produit produit) {
        items.remove(produit);
        calculateTotal();
    }

    public void updateQuantity(Produit produit, int quantity) {
        if (quantity <= 0) {
            items.remove(produit);
        } else {
            items.put(produit, quantity);
        }
        calculateTotal();
    }

    public void clear() {
        items.clear();
        total = 0f;
    }

    private void calculateTotal() {
        total = (float) items.entrySet().stream()
            .mapToDouble(entry -> entry.getKey().getPrix_p() * entry.getValue())
            .sum();
    }
} 