package org.gymify.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import org.gymify.entities.Commande;
import org.gymify.entities.Produit;
import org.gymify.services.CommandeService;
import org.gymify.services.ProduitService;
import org.gymify.utils.AuthToken;

import java.net.URL;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class ProductCatalogSportifController implements Initializable {
    @FXML private TilePane productGrid;
    @FXML private TableView<CartItem> cartTable;
    @FXML private TableColumn<CartItem, ImageView> cartColImage;
    @FXML private TableColumn<CartItem, String> cartColName;
    @FXML private TableColumn<CartItem, Double> cartColPrice;
    @FXML private TableColumn<CartItem, Integer> cartColQuantity;
    @FXML private TableColumn<CartItem, Double> cartColTotal;
    @FXML private TableColumn<CartItem, Void> cartColRemove;
    @FXML private Label cartTotalLabel;
    @FXML private Button validateCartBtn;
    @FXML private TableView<CommandeRow> myCommandesTable;
    @FXML private TableColumn<CommandeRow, Integer> cmdColId;
    @FXML private TableColumn<CommandeRow, String> cmdColDate;
    @FXML private TableColumn<CommandeRow, Double> cmdColTotal;
    @FXML private TableColumn<CommandeRow, String> cmdColStatus;
    @FXML private TableColumn<CommandeRow, Void> cmdColCancel;

    private final ProduitService produitService = new ProduitService();
    private final CommandeService commandeService = new CommandeService();
    private final ObservableList<CartItem> cartItems = FXCollections.observableArrayList();
    private final ObservableList<CommandeRow> commandeRows = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadProducts();
        setupCartTable();
        setupCommandesTable();
        validateCartBtn.setOnAction(e -> handleValidateCart());
        cartTable.setItems(cartItems);
        myCommandesTable.setItems(commandeRows);
        updateCartTotal();
        loadUserCommandes();
    }

    private void loadProducts() {
        productGrid.getChildren().clear();
        try {
            List<Produit> produits = produitService.getAllProduits();
            for (Produit produit : produits) {
                VBox card = createProductCard(produit);
                productGrid.getChildren().add(card);
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger les produits: " + e.getMessage());
        }
    }

    private VBox createProductCard(Produit produit) {
        VBox card = new VBox(8);
        card.setAlignment(Pos.CENTER);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-padding: 10; -fx-effect: dropshadow(gaussian, #b0b0b0, 6, 0, 0, 2);");
        ImageView imageView = new ImageView();
        try {
            imageView.setImage(new Image(new java.io.File(produit.getImage_path()).toURI().toString(), 80, 80, true, true));
        } catch (Exception e) {
            // ignore
        }
        imageView.setFitHeight(80);
        imageView.setFitWidth(80);
        imageView.setPreserveRatio(true);
        Label name = new Label(produit.getNom_p());
        name.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        Label price = new Label(String.format("%.2f DT", produit.getPrix_p()));
        price.setStyle("-fx-text-fill: #4db178; -fx-font-size: 13px;");
        Label cat = new Label(produit.getCategorie_p());
        Spinner<Integer> qtySpinner = new Spinner<>(1, produit.getStock_p(), 1);
        qtySpinner.setEditable(true);
        Button addBtn = new Button("Ajouter au panier");
        addBtn.setStyle("-fx-background-color: #2196f3; -fx-text-fill: white; -fx-background-radius: 8;");
        addBtn.setOnAction(e -> addToCart(produit, qtySpinner.getValue()));
        card.getChildren().addAll(imageView, name, price, cat, qtySpinner, addBtn);
        return card;
    }

    private void addToCart(Produit produit, int quantity) {
        for (CartItem item : cartItems) {
            if (item.getProduit().getId_p() == produit.getId_p()) {
                item.setQuantity(item.getQuantity() + quantity);
                cartTable.refresh();
                updateCartTotal();
                return;
            }
        }
        cartItems.add(new CartItem(produit, quantity));
        updateCartTotal();
    }

    private void setupCartTable() {
        cartColImage.setCellValueFactory(data -> data.getValue().imageViewProperty());
        cartColName.setCellValueFactory(data -> data.getValue().nameProperty());
        cartColPrice.setCellValueFactory(data -> data.getValue().priceProperty().asObject());
        cartColQuantity.setCellValueFactory(data -> data.getValue().quantityProperty().asObject());
        cartColTotal.setCellValueFactory(data -> data.getValue().totalProperty().asObject());
        cartColRemove.setCellFactory(getRemoveCartButtonFactory());
    }

    private Callback<TableColumn<CartItem, Void>, TableCell<CartItem, Void>> getRemoveCartButtonFactory() {
        return col -> new TableCell<>() {
            private final Button btn = new Button("✖");
            {
                btn.setStyle("-fx-background-color: #e44f25; -fx-text-fill: white; -fx-background-radius: 8;");
                btn.setOnAction(e -> {
                    CartItem item = getTableView().getItems().get(getIndex());
                    cartItems.remove(item);
                    updateCartTotal();
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        };
    }

    private void updateCartTotal() {
        double total = cartItems.stream().mapToDouble(CartItem::getTotal).sum();
        cartTotalLabel.setText(String.format("%.2f DT", total));
    }

    private void handleValidateCart() {
        if (cartItems.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Panier vide", "Ajoutez des produits au panier avant de valider.");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Valider cette commande ?", ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText(null);
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES) {
            try {
                double total = cartItems.stream().mapToDouble(CartItem::getTotal).sum();
                Commande commande = new Commande(0, total, new java.util.Date(), "En cours", AuthToken.getCurrentUser().getId(), null);
                commandeService.addCommande(commande);
                // Get the last inserted commande (assume it's the one just added)
                List<Commande> allCmds = commandeService.getAllCommandes();
                Commande lastCmd = allCmds.stream().max(Comparator.comparingInt(Commande::getId_c)).orElse(null);
                if (lastCmd != null) {
                    // Insert order lines (ligne_commande)
                    try (java.sql.Connection con = org.gymify.utils.gymifyDataBase.getInstance().getConnection()) {
                        String sql = "INSERT INTO ligne_commande (commande_id, produit_id, quantite_lc, prix_lc) VALUES (?, ?, ?, ?)";
                        for (CartItem item : cartItems) {
                            try (java.sql.PreparedStatement ps = con.prepareStatement(sql)) {
                                ps.setInt(1, lastCmd.getId_c());
                                ps.setInt(2, item.getProduit().getId_p());
                                ps.setInt(3, item.getQuantity());
                                ps.setDouble(4, item.getProduit().getPrix_p());
                                ps.executeUpdate();
                            }
                        }
                    }
                }
                cartItems.clear();
                updateCartTotal();
                loadUserCommandes();
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Commande validée avec succès !");
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de valider la commande: " + e.getMessage());
            }
        }
    }

    private void setupCommandesTable() {
        cmdColId.setCellValueFactory(data -> data.getValue().idProperty().asObject());
        cmdColDate.setCellValueFactory(data -> data.getValue().dateProperty());
        cmdColTotal.setCellValueFactory(data -> data.getValue().totalProperty().asObject());
        cmdColStatus.setCellValueFactory(data -> data.getValue().statusProperty());
        cmdColCancel.setCellFactory(getCancelCommandeButtonFactory());
    }

    private Callback<TableColumn<CommandeRow, Void>, TableCell<CommandeRow, Void>> getCancelCommandeButtonFactory() {
        return col -> new TableCell<>() {
            private final Button btn = new Button("Annuler");
            {
                btn.setStyle("-fx-background-color: #e44f25; -fx-text-fill: white; -fx-background-radius: 8;");
                btn.setOnAction(e -> {
                    CommandeRow row = getTableView().getItems().get(getIndex());
                    if (!"En cours".equals(row.getStatus())) {
                        showAlert(Alert.AlertType.WARNING, "Non autorisé", "Seules les commandes 'En cours' peuvent être annulées.");
                        return;
                    }
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Annuler cette commande ?", ButtonType.YES, ButtonType.NO);
                    confirm.setTitle("Confirmation");
                    confirm.setHeaderText(null);
                    Optional<ButtonType> result = confirm.showAndWait();
                    if (result.isPresent() && result.get() == ButtonType.YES) {
                        try {
                            Commande cmd = row.getCommande();
                            cmd.setStatut_c("Annulée");
                            commandeService.updateCommande(cmd);
                            loadUserCommandes();
                            showAlert(Alert.AlertType.INFORMATION, "Succès", "Commande annulée.");
                        } catch (Exception ex) {
                            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'annuler la commande: " + ex.getMessage());
                        }
                    }
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                CommandeRow row = empty ? null : getTableView().getItems().get(getIndex());
                setGraphic((empty || row == null || !"En cours".equals(row.getStatus())) ? null : btn);
            }
        };
    }

    private void loadUserCommandes() {
        commandeRows.clear();
        try {
            int userId = AuthToken.getCurrentUser().getId();
            List<Commande> all = commandeService.getAllCommandes();
            List<Commande> userCmds = all.stream().filter(c -> c.getUser_id() != null && c.getUser_id() == userId).collect(Collectors.toList());
            for (Commande c : userCmds) {
                commandeRows.add(new CommandeRow(c));
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger les commandes: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // --- Helper classes for cart and commandes ---
    public static class CartItem {
        private final Produit produit;
        private final ImageView imageView;
        private final javafx.beans.property.SimpleStringProperty name;
        private final javafx.beans.property.SimpleDoubleProperty price;
        private final javafx.beans.property.SimpleIntegerProperty quantity;
        private final javafx.beans.property.SimpleDoubleProperty total;
        public CartItem(Produit produit, int quantity) {
            this.produit = produit;
            this.imageView = new ImageView();
            try {
                this.imageView.setImage(new Image(new java.io.File(produit.getImage_path()).toURI().toString(), 40, 40, true, true));
            } catch (Exception e) {}
            this.imageView.setFitHeight(40);
            this.imageView.setFitWidth(40);
            this.imageView.setPreserveRatio(true);
            this.name = new javafx.beans.property.SimpleStringProperty(produit.getNom_p());
            this.price = new javafx.beans.property.SimpleDoubleProperty(produit.getPrix_p());
            this.quantity = new javafx.beans.property.SimpleIntegerProperty(quantity);
            this.total = new javafx.beans.property.SimpleDoubleProperty(quantity * produit.getPrix_p());
            this.quantity.addListener((obs, oldVal, newVal) -> this.total.set(newVal.intValue() * produit.getPrix_p()));
        }
        public Produit getProduit() { return produit; }
        public javafx.beans.property.SimpleObjectProperty<ImageView> imageViewProperty() { return new javafx.beans.property.SimpleObjectProperty<>(imageView); }
        public javafx.beans.property.SimpleStringProperty nameProperty() { return name; }
        public javafx.beans.property.SimpleDoubleProperty priceProperty() { return price; }
        public javafx.beans.property.SimpleIntegerProperty quantityProperty() { return quantity; }
        public javafx.beans.property.SimpleDoubleProperty totalProperty() { return total; }
        public int getQuantity() { return quantity.get(); }
        public void setQuantity(int q) { this.quantity.set(q); }
        public double getTotal() { return total.get(); }
    }

    public static class CommandeRow {
        private final Commande commande;
        private final javafx.beans.property.SimpleIntegerProperty id;
        private final javafx.beans.property.SimpleStringProperty date;
        private final javafx.beans.property.SimpleDoubleProperty total;
        private final javafx.beans.property.SimpleStringProperty status;
        public CommandeRow(Commande c) {
            this.commande = c;
            this.id = new javafx.beans.property.SimpleIntegerProperty(c.getId_c());
            this.date = new javafx.beans.property.SimpleStringProperty(c.getDate_c() != null ? c.getDate_c().toString() : "");
            this.total = new javafx.beans.property.SimpleDoubleProperty(c.getTotal_c());
            this.status = new javafx.beans.property.SimpleStringProperty(c.getStatut_c());
        }
        public Commande getCommande() { return commande; }
        public javafx.beans.property.SimpleIntegerProperty idProperty() { return id; }
        public javafx.beans.property.SimpleStringProperty dateProperty() { return date; }
        public javafx.beans.property.SimpleDoubleProperty totalProperty() { return total; }
        public javafx.beans.property.SimpleStringProperty statusProperty() { return status; }
        public String getStatus() { return status.get(); }
    }
} 