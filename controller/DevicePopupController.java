package com.techfix.controller;

import com.techfix.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class DevicePopupController {

    @FXML private Label lblBrand;
    @FXML private Label lblSerial;
    @FXML private Label lblPassword;
    @FXML private Label lblAccessories;

    public void loadDeviceDataFromDB(int deviceId) {
        String query = "SELECT B.BrandName, D.Model, D.SerialNo, D.Color, D.Accessories, D.DevicePIN " +
                       "FROM Device D " +
                       "JOIN Brand B ON D.BrandID = B.BrandID " +
                       "WHERE D.DeviceID = ?";
        
        try (Connection conn = DBConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(query)) {
             
            stmt.setInt(1, deviceId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                lblBrand.setText(rs.getString("BrandName") + " " + rs.getString("Model"));
                lblSerial.setText(rs.getString("SerialNo"));
                
                String pin = rs.getString("DevicePIN");
                if (pin == null || pin.trim().isEmpty()) {
                    lblPassword.setText("No PIN"); 
                } else {
                    lblPassword.setText(pin); 
                }
                
                String color = rs.getString("Color");
                String acc = rs.getString("Accessories");

                lblAccessories.setWrapText(true);
                lblAccessories.setMinHeight(javafx.scene.layout.Region.USE_PREF_SIZE);
                lblAccessories.setPrefHeight(javafx.scene.layout.Region.USE_COMPUTED_SIZE);

                lblAccessories.setText("Color: " + color + "\nItems: " + acc);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleClosePopup(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }
}