package com.techfix.controller;

import com.techfix.util.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DeviceDialogController {

    @FXML private TextField customerPhoneField;
    
    @FXML private ComboBox<String> brandComboBox;
    @FXML private ComboBox<String> typeComboBox;
    
    @FXML private TextField modelField;
    @FXML private TextField colorField;
    @FXML private TextField serialNumberField;
    @FXML private TextField accessoriesField;
    @FXML private TextField txtDevicePin;
    

    private int currentCustomerId;

    @FXML
    public void initialize() {
        loadBrandsAndTypes();
    }

    public void setCustomerId(int customerId, String phone) {
        this.currentCustomerId = customerId;
        customerPhoneField.setText(phone);
        customerPhoneField.setEditable(false);
    }

    private void loadBrandsAndTypes() {
        ObservableList<String> brands = FXCollections.observableArrayList();
        ObservableList<String> types = FXCollections.observableArrayList();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT BrandName FROM Brand ORDER BY BrandName");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                brands.add(rs.getString("BrandName"));
            }
            brandComboBox.setItems(brands);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT TypeName FROM Device_Type ORDER BY TypeName");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                types.add(rs.getString("TypeName"));
            }
            typeComboBox.setItems(types);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSave() {
        String brandValue = brandComboBox.getValue();
        String typeValue = typeComboBox.getValue();
        String modelValue = modelField.getText() != null ? modelField.getText().trim() : "";
        String colorValue = colorField.getText() != null ? colorField.getText().trim() : "Unknown";
        String serialValue = serialNumberField.getText() != null ? serialNumberField.getText().trim() : "";
        
        String accValue = accessoriesField.getText() != null && !accessoriesField.getText().trim().isEmpty() ? accessoriesField.getText().trim() : "None";
        
        String pinValue = txtDevicePin.getText() != null ? txtDevicePin.getText().trim() : "";

        if (brandValue == null || typeValue == null || modelValue.isEmpty() || serialValue.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Missing Data", "Please fill all required device fields.");
            return;
        }

        String query = "INSERT INTO Device (CustomerID, SerialNo, Model, Color, Accessories, DevicePIN, BrandID, TypeID) " +
                       "VALUES (?, ?, ?, ?, ?, ?, (SELECT BrandID FROM Brand WHERE BrandName = ?), (SELECT TypeID FROM Device_Type WHERE TypeName = ?))";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
             
            stmt.setInt(1, currentCustomerId);
            stmt.setString(2, serialValue);     
            stmt.setString(3, modelValue);      
            stmt.setString(4, colorValue);      
            stmt.setString(5, accValue);        
            stmt.setString(6, pinValue);        
            stmt.setString(7, brandValue);      
            stmt.setString(8, typeValue);       

            if (stmt.executeUpdate() > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Device added to customer successfully!");
                closeWindow();
            }
        } catch (java.sql.SQLIntegrityConstraintViolationException e) {
            showAlert(Alert.AlertType.ERROR, "Duplicate Serial", "This Serial Number is already registered in the system!");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) customerPhoneField.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}