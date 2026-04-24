package com.techfix.controller;

import com.techfix.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField passwordTextField;
    
    @FXML
    private CheckBox showPasswordCheckBox;

    @FXML
    public void initialize() {
        if (passwordTextField != null && passwordField != null) {
            passwordTextField.textProperty().bindBidirectional(passwordField.textProperty());
        }
    }

    @FXML
    private void togglePassword() {
        if (showPasswordCheckBox.isSelected()) {
            passwordTextField.setVisible(true);
            passwordField.setVisible(false);
        } else {
            passwordTextField.setVisible(false);
            passwordField.setVisible(true);
        }
    }

    private String hashPassword(String password) {
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception ex) {
            throw new RuntimeException("Error hashing password", ex);
        }
    }

    @FXML
    private void handleLoginButton() {
        String user = usernameField.getText().trim();
        String pass = passwordField.getText();

        if (user.isEmpty() || pass.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Missing Data", "Please enter both username and password!");
            return;
        }

        String hashedPassword = hashPassword(pass);

        String query = "SELECT RoleID, FullName FROM `User` WHERE Username = ? AND Password = ? AND IsActive = true";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
             
            stmt.setString(1, user);
            stmt.setString(2, hashedPassword);
            
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int roleId = rs.getInt("RoleID");
                String fullName = rs.getString("FullName");

                if (roleId == 1 || roleId == 4) { 
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AdminDashboard.fxml"));
                        Parent root = loader.load();

                        AdminController adminController = loader.getController();
                        adminController.setLoggedInRole(roleId); 

                        Stage currentStage = (Stage) usernameField.getScene().getWindow();
                        currentStage.close();

                        Stage adminStage = new Stage();
                        adminStage.setTitle("TechFix - Management Portal");
                        adminStage.setScene(new Scene(root));
                        
                        adminStage.setResizable(false);

                        try {
                            adminStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/logo.png")));
                        } catch (Exception e) { }

                        adminStage.show();

                        Platform.runLater(() -> {
                            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
                            double centerX = (screenBounds.getWidth() - adminStage.getWidth()) / 2;
                            double centerY = (screenBounds.getHeight() - adminStage.getHeight()) / 2;
                            
                            adminStage.setX(centerX);
                            adminStage.setY(centerY);
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                        showAlert(Alert.AlertType.ERROR, "System Error", "Cannot load Admin Dashboard: " + e.getMessage());
                    }
                } 
                else if (roleId == 3) {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ReceptionDashboard.fxml"));
                        Parent root = loader.load();
                        
                        Stage currentStage = (Stage) usernameField.getScene().getWindow();
                        currentStage.close();

                        Stage receptionStage = new Stage();
                        receptionStage.setTitle("TechFix - Reception Dashboard");
                        receptionStage.setScene(new Scene(root));
                        
                        receptionStage.setResizable(false);
                        
                        try {
                            receptionStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/logo.png")));
                        } catch (Exception e) { }

                        receptionStage.show();
                        
                        Platform.runLater(() -> {
                            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
                            double centerX = (screenBounds.getWidth() - receptionStage.getWidth()) / 2;
                            double centerY = (screenBounds.getHeight() - receptionStage.getHeight()) / 2;
                            
                            receptionStage.setX(centerX);
                            receptionStage.setY(centerY);
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                        showAlert(Alert.AlertType.ERROR, "System Error", "Cannot load Reception Dashboard: " + e.getMessage());
                    }
                } 
                // 👈 التعديل هون: فتحنا الباب للفني (Sami Yousef)
                else if (roleId == 2) {
                    try {
                        // ملاحظة: تأكد إن ملف TechDashboard.fxml موجود جوا مجلد fxml
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/TechDashboard.fxml"));
                        Parent root = loader.load();
                        
                        Stage currentStage = (Stage) usernameField.getScene().getWindow();
                        currentStage.close();

                        Stage techStage = new Stage();
                        techStage.setTitle("TechFix - Technician Dashboard");
                        techStage.setScene(new Scene(root));
                        
                        techStage.setResizable(false);
                        
                        try {
                            techStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/logo.png")));
                        } catch (Exception e) { }

                        techStage.show();
                        
                        Platform.runLater(() -> {
                            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
                            double centerX = (screenBounds.getWidth() - techStage.getWidth()) / 2;
                            double centerY = (screenBounds.getHeight() - techStage.getHeight()) / 2;
                            
                            techStage.setX(centerX);
                            techStage.setY(centerY);
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                        showAlert(Alert.AlertType.ERROR, "System Error", "Cannot load Technician Dashboard: " + e.getMessage());
                    }
                }
                else {
                    showAlert(Alert.AlertType.WARNING, "Access Denied", "Welcome " + fullName + ",\nYou are not authorized to access the system yet.");
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid username/password or account is archived!");
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Database connection failed: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        try {
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/logo.png")));
        } catch (Exception e) {  }

        alert.showAndWait();
    }
}