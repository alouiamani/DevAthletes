package controllers;

import entities.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
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

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        updateUserInfo();
    }

    public void updateUserInfo() {
        User currentUser = AuthToken.getCurrentUser();
        if (currentUser != null) {
            userNameLabel.setText(currentUser.getNom() + " " + currentUser.getPrenom());
            userRoleLabel.setText(currentUser.getRole());
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        AuthToken.clearCurrentUser();
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/Login.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading Login.fxml: " + e.getMessage());
        }
    }
} 