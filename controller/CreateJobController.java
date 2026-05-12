package com.techfix.controller;

import com.techfix.util.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

public class CreateJobController {

    @FXML private TextField txtCustomerName;
    @FXML private ComboBox<ComboItem> comboDevice;
    @FXML private ComboBox<ComboItem> comboTech;
    @FXML private TextArea txtProblem;

    private int customerId;

    public void initData(int customerId, String customerName) {
        this.customerId = customerId;
        this.txtCustomerName.setText(customerName);
        
        loadCustomerDevices();
        loadTechnicians();
    }

    private void loadCustomerDevices() {
        ObservableList<ComboItem> devices = FXCollections.observableArrayList();
        String query = "SELECT D.DeviceID, CONCAT(B.BrandName, ' ', D.Model, ' (', D.SerialNo, ')') AS DeviceName " +
                       "FROM Device D JOIN Brand B ON D.BrandID = B.BrandID WHERE D.CustomerID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                devices.add(new ComboItem(rs.getInt("DeviceID"), rs.getString("DeviceName")));
            }
            comboDevice.setItems(devices);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void loadTechnicians() {
        ObservableList<ComboItem> techs = FXCollections.observableArrayList();
        String query = "SELECT UserID, FullName FROM User WHERE RoleID = 2 AND IsActive = true";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                techs.add(new ComboItem(rs.getInt("UserID"), rs.getString("FullName")));
            }
            comboTech.setItems(techs);
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    private void handleCreate(ActionEvent event) {
        ComboItem selectedDevice = comboDevice.getValue();
        ComboItem selectedTech = comboTech.getValue();
        String problem = txtProblem.getText().trim();

        if (selectedDevice == null || selectedTech == null || problem.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Missing Info", "Please select a device, a technician, and write the problem description.");
            return;
        }

        String query = "INSERT INTO Maintenance_Job (DeviceID, UserID, ProblemDescription, StatusID, DateIn) VALUES (?, ?, ?, 1, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
             
            stmt.setInt(1, selectedDevice.getId());
            stmt.setInt(2, selectedTech.getId());
            stmt.setString(3, problem);
            stmt.setDate(4, Date.valueOf(LocalDate.now()));
            
            if (stmt.executeUpdate() > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Repair Job created and assigned to technician successfully!");
                closeStage(event);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
        }
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        closeStage(event);
    }

    private void closeStage(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type); alert.setTitle(title); alert.setHeaderText(null); alert.setContentText(msg); alert.showAndWait();
    }

    private static class ComboItem {
        private final int id; private final String name;
        public ComboItem(int id, String name) { this.id = id; this.name = name; }
        public int getId() { return id; }
        @Override public String toString() { return name; }
    }
}