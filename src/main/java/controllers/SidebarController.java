package controllers;

import entities.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import utils.AuthToken;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SidebarController implements Initializable {
    @FXML
    private Label userNameLabel;
    @FXML
    private Label userRoleLabel;
    @FXML
    private VBox adminButtons;
    @FXML
    private VBox userButtons;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        updateUserInfo();
        setupVisibility();
    }

    private void setupVisibility() {
        User currentUser = AuthToken.getCurrentUser();
        boolean isAdmin = currentUser != null && "admin".equals(currentUser.getRole());
        
        adminButtons.setVisible(isAdmin);
        adminButtons.setManaged(isAdmin);
        userButtons.setVisible(!isAdmin);
        userButtons.setManaged(!isAdmin);
    }

    public void updateUserInfo() {
        User currentUser = AuthToken.getCurrentUser();
        if (currentUser != null) {
            userNameLabel.setText(currentUser.getNom() + " " + currentUser.getPrenom());
            userRoleLabel.setText(currentUser.getRole());
        }
    }

    @FXML
    private void navigateToProductsAdmin() {
        navigateTo("/listProduit.fxml");
    }

    @FXML
    private void navigateToOrdersAdmin() {
        navigateTo("/listCommande.fxml");
    }

    @FXML
    private void navigateToProducts() {
        navigateTo("/listProduitFront.fxml");
    }

    @FXML
    private void navigateToCart() {
        navigateTo("/Cart.fxml");
    }

    @FXML
    private void navigateToOrders() {
        navigateTo("/listCommandeFront.fxml");
    }

    @FXML
    private void handleLogout() {
        AuthToken.clearCurrentUser();
        navigateTo("/Login.fxml");
    }

    private void navigateTo(String fxml) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxml));
            Stage stage = (Stage) userNameLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
} 