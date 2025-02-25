package controllers;

import entities.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import services.ServiceUser;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;

public class SignUpController {
    @FXML
    private TextField nomTextField;
    @FXML
    private TextField prenomTextField;
    @FXML
    private TextField emailTextField;
    @FXML
    private PasswordField passwordTextField;
    @FXML
    private DatePicker dateNaissancePicker;

    private final ServiceUser serviceUser = new ServiceUser();

    @FXML
    void signUpButtonOnAction(ActionEvent event) {
        if (validateInputs()) {
            try {
                User newUser = new User(
                    nomTextField.getText().trim(),
                    prenomTextField.getText().trim(),
                    emailTextField.getText().trim(),
                    passwordTextField.getText().trim(),
                    "user",
                    java.sql.Date.valueOf(dateNaissancePicker.getValue()),
                    null
                );

                int result = serviceUser.ajouter(newUser);
                if (result > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Account created successfully!");
                    switchToLogin(event);
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Could not create account. Please try again.");
                }
            } catch (SQLException e) {
                if (e.getMessage().contains("Duplicate entry")) {
                    showAlert(Alert.AlertType.ERROR, "Error", "This email is already registered.");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Database error: " + e.getMessage());
                }
                e.printStackTrace();
            }
        }
    }

    @FXML
    void loginLinkOnAction(ActionEvent event) {
        switchToLogin(event);
    }

    private boolean validateInputs() {
        if (nomTextField.getText().trim().isEmpty() ||
            prenomTextField.getText().trim().isEmpty() ||
            emailTextField.getText().trim().isEmpty() ||
            passwordTextField.getText().trim().isEmpty() ||
            dateNaissancePicker.getValue() == null) {
            
            showAlert(Alert.AlertType.WARNING, "Missing Information", "Please fill all fields");
            return false;
        }
        return true;
    }

    private void switchToLogin(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/Login.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Could not load login page: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 