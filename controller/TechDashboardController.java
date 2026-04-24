package com.techfix.controller;

import com.techfix.util.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class TechDashboardController {

    @FXML private ListView<String> deviceListView;
    @FXML private Label fullNameLabel;
    @FXML private Label cityLabel;
    @FXML private Label phoneLabel;
    @FXML private Hyperlink deviceLink;
    @FXML private Label statusLabel;
    @FXML private TextArea problemDescriptionArea;
    
    @FXML private Button startRepairButton; 
    @FXML private Button finishButton;

    private final ObservableList<DeviceRecord> deviceRecords = FXCollections.observableArrayList();
    private final ObservableList<String> deviceDisplayList = FXCollections.observableArrayList();

    private DeviceRecord selectedDevice;

    private final int CURRENT_TECHNICIAN_ID = 2; 

    @FXML
    public void initialize() {
        problemDescriptionArea.setEditable(false);
        
        loadJobsFromDatabase();

        deviceListView.getSelectionModel().selectedIndexProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal.intValue() >= 0 && newVal.intValue() < deviceRecords.size()) {
                selectedDevice = deviceRecords.get(newVal.intValue());
                showDeviceDetails(selectedDevice);
            }
        });
    }

    private void loadJobsFromDatabase() {
        deviceRecords.clear();
        deviceDisplayList.clear();

        String query = "SELECT J.JobID, C.FullName, Ci.CityName, C.Phone, " +
                       "CONCAT(B.BrandName, ' ', D.Model) AS DeviceName, " +
                       "S.StatusName, J.ProblemDescription " +
                       "FROM Maintenance_Job J " +
                       "JOIN Device D ON J.DeviceID = D.DeviceID " +
                       "JOIN Customer C ON D.CustomerID = C.CustomerID " +
                       "JOIN City Ci ON C.CityID = Ci.CityID " +
                       "JOIN Brand B ON D.BrandID = B.BrandID " +
                       "JOIN Status S ON J.StatusID = S.StatusID " +
                       "WHERE J.UserID = ? AND J.StatusID IN (1, 2, 3) " + 
                       "ORDER BY J.DateIn ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
             
            stmt.setInt(1, CURRENT_TECHNICIAN_ID);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                DeviceRecord record = new DeviceRecord(
                        rs.getInt("JobID"),
                        rs.getString("FullName"),
                        rs.getString("CityName"),
                        rs.getString("Phone"),
                        rs.getString("DeviceName"),
                        rs.getString("StatusName"),
                        rs.getString("ProblemDescription")
                );
                deviceRecords.add(record);
                deviceDisplayList.add(record.getDeviceName() + " [" + record.getStatus() + "]");
            }
            
            deviceListView.setItems(deviceDisplayList);

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load tasks: " + e.getMessage());
        }
    }

    private void showDeviceDetails(DeviceRecord device) {
        fullNameLabel.setText(device.getFullName());
        cityLabel.setText(device.getCity());
        phoneLabel.setText(device.getPhone());
        deviceLink.setText(device.getDeviceName());
        statusLabel.setText(device.getStatus());
        problemDescriptionArea.setText(device.getProblemDescription());

        if (device.getStatus().equalsIgnoreCase("Pending")) {
            statusLabel.setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold;");
            startRepairButton.setDisable(false);
            finishButton.setDisable(true);
        } else if (device.getStatus().equalsIgnoreCase("In Progress")) {
            statusLabel.setStyle("-fx-text-fill: #f59e0b; -fx-font-weight: bold;");
            startRepairButton.setDisable(true);
            finishButton.setDisable(false);
        }
    }

    @FXML
    private void handleStartRepair() {
        if (selectedDevice == null) return;
        updateJobStatus(selectedDevice.getJobId(), 2); 
    }

    @FXML
    private void handleFinish() {
        if (selectedDevice == null) return;
        updateJobStatus(selectedDevice.getJobId(), 4);
    }

    private void updateJobStatus(int jobId, int newStatusId) {
        String query = "UPDATE Maintenance_Job SET StatusID = ? WHERE JobID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
             
            stmt.setInt(1, newStatusId);
            stmt.setInt(2, jobId);
            
            if (stmt.executeUpdate() > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Status updated successfully!");
                loadJobsFromDatabase(); 
                clearDetails();
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Could not update status: " + e.getMessage());
        }
    }

    private void clearDetails() {
        fullNameLabel.setText("-");
        cityLabel.setText("-");
        phoneLabel.setText("-");
        deviceLink.setText("-");
        statusLabel.setText("Pending");
        problemDescriptionArea.clear();
        selectedDevice = null;
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static class DeviceRecord {
        private int jobId;
        private String fullName;
        private String city;
        private String phone;
        private String deviceName;
        private String status;
        private String problemDescription;

        public DeviceRecord(int jobId, String fullName, String city, String phone, String deviceName, String status, String problemDescription) {
            this.jobId = jobId;
            this.fullName = fullName;
            this.city = city;
            this.phone = phone;
            this.deviceName = deviceName;
            this.status = status;
            this.problemDescription = problemDescription;
        }

        public int getJobId() { return jobId; }
        public String getFullName() { return fullName; }
        public String getCity() { return city; }
        public String getPhone() { return phone; }
        public String getDeviceName() { return deviceName; }
        public String getStatus() { return status; }
        public String getProblemDescription() { return problemDescription; }
    }
}