package com.techfix.controller;

import com.techfix.util.DBConnection;
import com.techfix.util.InvoiceManager; 
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CheckoutController {

    @FXML private TableView<ReadyJob> readyTable;
    @FXML private TableColumn<ReadyJob, Integer> colJobId;
    @FXML private TableColumn<ReadyJob, String> colCustomer;
    @FXML private TableColumn<ReadyJob, String> colPhone;
    @FXML private TableColumn<ReadyJob, String> colDevice;
    @FXML private TableColumn<ReadyJob, Double> colCost;

    @FXML private ComboBox<String> paymentMethodCombo;
    @FXML private Label selectedJobLabel;

    private final ObservableList<ReadyJob> readyJobsList = FXCollections.observableArrayList();
    private ReadyJob selectedJob = null;

    @FXML
    public void initialize() {
        colJobId.setCellValueFactory(new PropertyValueFactory<>("jobId"));
        colCustomer.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colDevice.setCellValueFactory(new PropertyValueFactory<>("device"));
        colCost.setCellValueFactory(new PropertyValueFactory<>("totalCost"));

        paymentMethodCombo.getItems().addAll("Cash", "Visa", "MasterCard");
        paymentMethodCombo.setValue("Cash");

        readyTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedJob = newVal;
                selectedJobLabel.setText("Selected: " + newVal.getCustomerName() + " | Total: $" + String.format("%.2f", newVal.getTotalCost()));
            }
        });

        loadReadyJobs();
    }

    private void loadReadyJobs() {
        readyJobsList.clear();
        String query = "SELECT j.JobID, c.FullName, c.Phone, CONCAT(b.BrandName, ' ', d.Model) AS Device, j.TotalCost " +
                       "FROM maintenance_job j " +
                       "JOIN device d ON j.DeviceID = d.DeviceID " +
                       "JOIN customer c ON d.CustomerID = c.CustomerID " +
                       "JOIN brand b ON d.BrandID = b.BrandID " +
                       "WHERE j.StatusID = 4"; 

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                readyJobsList.add(new ReadyJob(
                        rs.getInt("JobID"), rs.getString("FullName"), rs.getString("Phone"),
                        rs.getString("Device"), rs.getDouble("TotalCost")
                ));
            }
            readyTable.setItems(readyJobsList);
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    private void handlePayAndDeliver(ActionEvent event) {
        if (selectedJob == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a job from the table first.");
            return;
        }

        String method = paymentMethodCombo.getValue();

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                String insertPayment = "INSERT INTO payment (JobID, Amount, PaymentDate, Method) VALUES (?, ?, CURRENT_DATE, ?)";
                try (PreparedStatement payStmt = conn.prepareStatement(insertPayment)) {
                    payStmt.setInt(1, selectedJob.getJobId());      
                    payStmt.setDouble(2, selectedJob.getTotalCost());
                    payStmt.setString(3, method);
                    payStmt.executeUpdate();
                }

                String updateJob = "UPDATE maintenance_job SET StatusID = 5, DateOut = CURRENT_DATE WHERE JobID = ?";
                try (PreparedStatement jobStmt = conn.prepareStatement(updateJob)) {
                    jobStmt.setInt(1, selectedJob.getJobId());
                    jobStmt.executeUpdate();
                }

                conn.commit(); 

                try {
                    InvoiceManager invoice = new InvoiceManager();
                    invoice.generatePDF(selectedJob.getJobId());
                } catch (Exception e) {
                    System.out.println("Error generating PDF: " + e.getMessage());
                }

                showAlert(Alert.AlertType.INFORMATION, "Success", "Payment recorded successfully! The invoice has been generated.");
                selectedJob = null;
                selectedJobLabel.setText("Selected: None");
                loadReadyJobs();

            } catch (Exception e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type); alert.setTitle(title); alert.setHeaderText(null); alert.setContentText(msg); alert.showAndWait();
    }

    public static class ReadyJob {
        private int jobId; private String customerName; private String phone; private String device; private double totalCost;
        public ReadyJob(int jobId, String customerName, String phone, String device, double totalCost) {
            this.jobId = jobId; this.customerName = customerName; this.phone = phone; this.device = device; this.totalCost = totalCost;
        }
        public int getJobId() { return jobId; } public String getCustomerName() { return customerName; }
        public String getPhone() { return phone; } public String getDevice() { return device; } public double getTotalCost() { return totalCost; }
    }
}