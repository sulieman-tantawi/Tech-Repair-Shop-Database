package com.techfix.controller;

import com.techfix.util.DBConnection;
import com.techfix.model.DeviceModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class TechDashboardController {

    @FXML private ListView<DeviceModel> deviceListView;
    @FXML private Label fullNameLabel;
    @FXML private Label cityLabel;
    @FXML private Label phoneLabel;
    @FXML private Hyperlink deviceLink;
    @FXML private Label statusLabel;
    @FXML private TextArea problemDescriptionArea;
    
    @FXML private Button startRepairButton; 
    @FXML private Button finishButton;

    private final ObservableList<DeviceModel> deviceRecords = FXCollections.observableArrayList();

    private DeviceModel selectedDevice;

    private int currentTechnicianId; 

    @FXML
    public void initialize() {
        problemDescriptionArea.setEditable(false);
        
        deviceListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedDevice = newVal;
                showDeviceDetails(selectedDevice);
            }
        });
    }

    private void loadJobsFromDatabase() {
        deviceRecords.clear();

        String query = "SELECT J.JobID, D.DeviceID, C.FullName, Ci.CityName, C.Phone, " +
               "CONCAT(B.BrandName, ' ', D.Model) AS DeviceName, " +
               "D.DevicePIN, " + // <--- ضفناها هون
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
             
            stmt.setInt(1, currentTechnicianId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
            String pin = rs.getString("DevicePIN");
            if (pin == null || pin.trim().isEmpty()) {
                pin = "No PIN";
            }

            DeviceModel record = new DeviceModel(
                rs.getInt("JobID"),
                rs.getInt("DeviceID"),
                rs.getString("FullName"),
                rs.getString("CityName"),
                rs.getString("Phone"),
                rs.getString("DeviceName"),
                rs.getString("StatusName"),
                rs.getString("ProblemDescription"),
                pin
            );
            deviceRecords.add(record);
        }
            
            deviceListView.setItems(deviceRecords);
            
            deviceListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) { 
                DeviceModel selectedDevice = deviceListView.getSelectionModel().getSelectedItem();
                
                if (selectedDevice != null) {
                    String details = "Problem: " + selectedDevice.getProblemDescription() + "\n" +
                                     "PIN/Passcode: " + selectedDevice.getDevicePin(); 

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Device Details");
                    alert.setHeaderText("Device: " + selectedDevice.getDeviceName());
                    alert.setContentText(details);
                    alert.showAndWait();
                }
            }
        });

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load tasks: " + e.getMessage());
        }
    }
    
    public void setLoggedInTechnician(int techId) {
        this.currentTechnicianId = techId;
        loadJobsFromDatabase();
    }

    private void showDeviceDetails(DeviceModel device) {
        fullNameLabel.setText(device.getFullName());
        cityLabel.setText(device.getCity());
        phoneLabel.setText(device.getPhone());
        deviceLink.setText(device.getDeviceName());
        statusLabel.setText(device.getStatus());
        problemDescriptionArea.setText(device.getProblemDescription());

        if (device.getStatus().equalsIgnoreCase("Pending") || device.getStatus().equalsIgnoreCase("Waiting for Parts")) {
            statusLabel.setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold;");
            startRepairButton.setDisable(false);
            finishButton.setDisable(true);
        } else if (device.getStatus().equalsIgnoreCase("In Progress")) {
            statusLabel.setStyle("-fx-text-fill: #f59e0b; -fx-font-weight: bold;");
            startRepairButton.setDisable(true);
            finishButton.setDisable(false);
        } else {
             statusLabel.setStyle("-fx-text-fill: #000000; -fx-font-weight: bold;"); 
             startRepairButton.setDisable(true);
             finishButton.setDisable(true);
        }
    }

    @FXML
    private void handleStartRepair() {
        if (selectedDevice == null) return;
        updateJobStatus(selectedDevice.getJobId(), 2);
    }

    @FXML
    private void handleFinish() {
        if (selectedDevice == null) {
            showAlert(Alert.AlertType.WARNING, "تنبيه", "Please select an In Progress device first.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/FinishJobDialog.fxml"));
            Parent root = loader.load();

            FinishJobController controller = loader.getController();
            controller.initData(selectedDevice.getJobId());

            Stage stage = new Stage();
            stage.setTitle("Finish Job & Add Materials");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            loadJobsFromDatabase();
            clearDetails();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Could not open Finish Job dialog: " + e.getMessage());
        }
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
        startRepairButton.setDisable(true);
        finishButton.setDisable(true);
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    
    @FXML
    private void handleDeviceClick(ActionEvent event) {
    System.out.println("==== تم الكبس عالرابط بنجاح! ====");
        
        if (selectedDevice == null) {
            showAlert(Alert.AlertType.WARNING, "تنبيه", "الرجاء تحديد جهاز من القائمة أولاً.");
            return;
        }

        try {
            java.net.URL popupUrl = getClass().getResource("/fxml/DevicePopup.fxml"); 
            
            if (popupUrl == null) {
                showAlert(Alert.AlertType.ERROR, "خطأ بالمسار", "مش قادر ألاقي ملف DevicePopup.fxml! تأكد من اسم المجلد اللي هو جواته.");
                return;
            }

            FXMLLoader loader = new FXMLLoader(popupUrl);
            Parent root = loader.load();
            
            DevicePopupController popupController = loader.getController();
            popupController.loadDeviceDataFromDB(selectedDevice.getDeviceId());
            
            Stage popupStage = new Stage();
            popupStage.setScene(new Scene(root));
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setTitle("Device Details");
            popupStage.showAndWait();
            
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "خطأ برمجي", "ما فتحت الشاشة بسبب: " + e.getMessage());
        } finally {
            if (deviceLink != null) {
                deviceLink.setVisited(false);
            }
        }
    }
}