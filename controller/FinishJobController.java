package com.techfix.controller;

import com.techfix.util.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class FinishJobController {

    @FXML private ComboBox<DBItem> itemComboBox;
    @FXML private TextField qtyField;
    @FXML private TableView<CartItem> cartTable;
    @FXML private TableColumn<CartItem, String> colName;
    @FXML private TableColumn<CartItem, String> colType;
    @FXML private TableColumn<CartItem, Double> colPrice;
    @FXML private TableColumn<CartItem, Integer> colQty;
    @FXML private TableColumn<CartItem, Double> colTotal;
    @FXML private Label totalCostLabel;

    private int currentJobId;
    private double finalTotalCost = 0.0;
    
    private final ObservableList<CartItem> cartItems = FXCollections.observableArrayList();

    public void initData(int jobId) {
        this.currentJobId = jobId;
        setupTable();
        loadItemsFromDatabase();
    }

    private void setupTable() {
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("subTotal"));
        cartTable.setItems(cartItems);
    }

    private void loadItemsFromDatabase() {
        ObservableList<DBItem> dbItems = FXCollections.observableArrayList();
        String query = "SELECT i.ItemID, i.Name, i.Price, " +
                       "CASE WHEN p.ItemID IS NOT NULL THEN 'Part' ELSE 'Service' END AS ItemType, " +
                       "IFNULL(p.StockQuantity, 999) AS Stock " +
                       "FROM item i " +
                       "LEFT JOIN part p ON i.ItemID = p.ItemID";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                dbItems.add(new DBItem(
                        rs.getInt("ItemID"),
                        rs.getString("Name"),
                        rs.getDouble("Price"),
                        rs.getString("ItemType"),
                        rs.getInt("Stock")
                ));
            }
            itemComboBox.setItems(dbItems);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddItem() {
        DBItem selectedItem = itemComboBox.getValue();
        String qtyText = qtyField.getText().trim();

        if (selectedItem == null || qtyText.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Error", "Please select an item and enter quantity/hours.");
            return;
        }

        try {
            int qty = Integer.parseInt(qtyText);
            if (qty <= 0) throw new NumberFormatException();

            if (selectedItem.getType().equals("Part") && qty > selectedItem.getStock()) {
                showAlert(Alert.AlertType.WARNING, "Out of Stock", "Not enough stock! Available: " + selectedItem.getStock());
                return;
            }

            CartItem newItem = new CartItem(selectedItem.getId(), selectedItem.getName(), selectedItem.getType(), selectedItem.getPrice(), qty);
            cartItems.add(newItem);
            
            finalTotalCost += newItem.getSubTotal();
            totalCostLabel.setText("$" + String.format("%.2f", finalTotalCost));
            
            qtyField.clear();
            itemComboBox.getSelectionModel().clearSelection();

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Quantity must be a valid positive number.");
        }
    }

    @FXML
    private void handleCompleteJob(ActionEvent event) {
        if (cartItems.isEmpty()) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "No parts or services added. Finish with $0 cost?", ButtonType.YES, ButtonType.NO);
            confirm.showAndWait();
            if (confirm.getResult() == ButtonType.NO) return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            try {
                String insertJobItemQuery = "INSERT INTO job_item (JobID, ItemID, Quantity) VALUES (?, ?, ?)";
                String updateStockQuery = "UPDATE part SET StockQuantity = StockQuantity - ? WHERE ItemID = ?";

                try (PreparedStatement insertStmt = conn.prepareStatement(insertJobItemQuery);
                     PreparedStatement updateStmt = conn.prepareStatement(updateStockQuery)) {
                    
                    for (CartItem item : cartItems) {
                        insertStmt.setInt(1, currentJobId);
                        insertStmt.setInt(2, item.getItemId());
                        insertStmt.setInt(3, item.getQuantity());
                        insertStmt.addBatch();

                        if (item.getType().equals("Part")) {
                            updateStmt.setInt(1, item.getQuantity());
                            updateStmt.setInt(2, item.getItemId());
                            updateStmt.addBatch();
                        }
                    }
                    insertStmt.executeBatch();
                    updateStmt.executeBatch();
                }

                String updateJobQuery = "UPDATE maintenance_job SET StatusID = 4, TotalCost = ?, DateOut = CURRENT_DATE WHERE JobID = ?";
                try (PreparedStatement jobStmt = conn.prepareStatement(updateJobQuery)) {
                    jobStmt.setDouble(1, finalTotalCost);
                    jobStmt.setInt(2, currentJobId);
                    jobStmt.executeUpdate();
                }

                conn.commit();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Job completed successfully!");
                closeStage(event);

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

    private void closeStage(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type); alert.setTitle(title); alert.setHeaderText(null); alert.setContentText(msg); alert.showAndWait();
    }

    public static class DBItem {
        private int id; private String name; private double price; private String type; private int stock;
        public DBItem(int id, String name, double price, String type, int stock) {
            this.id = id; this.name = name; this.price = price; this.type = type; this.stock = stock;
        }
        public int getId() { return id; } public String getName() { return name; }
        public double getPrice() { return price; } public String getType() { return type; } public int getStock() { return stock; }
        @Override public String toString() { return name + " ($" + price + ") [" + type + "]"; }
    }

    public static class CartItem {
        private int itemId; private String name; private String type; private double price; private int quantity; private double subTotal;
        public CartItem(int itemId, String name, String type, double price, int quantity) {
            this.itemId = itemId; this.name = name; this.type = type; this.price = price; this.quantity = quantity; this.subTotal = price * quantity;
        }
        public int getItemId() { return itemId; } public String getName() { return name; }
        public String getType() { return type; } public double getPrice() { return price; }
        public int getQuantity() { return quantity; } public double getSubTotal() { return subTotal; }
    }
}