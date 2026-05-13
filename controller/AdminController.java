package com.techfix.controller;

import com.techfix.model.User;
import com.techfix.util.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.text.Text;
import java.io.File;
import java.io.PrintWriter;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class AdminController implements Initializable {

    @FXML private Label totalEmployeesLabel;
    @FXML private Label totalIncomeLabel;
    @FXML private Label activeRepairsLabel;
    
    @FXML private TextField nameField;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField emailField;
    @FXML private ComboBox<String> roleComboBox;

    @FXML private TextField searchField;
    @FXML private ComboBox<String> filterComboBox;

    @FXML private TableView<User> employeeTable;
    @FXML private TableColumn<User, Integer> colId;
    @FXML private TableColumn<User, String> colName;
    @FXML private TableColumn<User, String> colRole;
    @FXML private TableColumn<User, String> colUsername;
    @FXML private TableColumn<User, String> colEmail;
    @FXML private TableColumn<User, String> colHireDate;
    
    @FXML private TabPane mainTabPane;
    @FXML private Tab dashboardTab;
    @FXML private Tab reportsTab;
    
    @FXML private PieChart statusPieChart;
    @FXML private BarChart<String, Number> revenueBarChart;
    @FXML private LineChart<String, Number> revenueLineChart;
    @FXML private PieChart brandPieChart;
    
    @FXML private TextField invNameField;
    @FXML private ComboBox<String> invTypeComboBox;
    @FXML private TextField invPriceField;
    @FXML private TextField invStockField;
    @FXML private TextField invSearchField;

    @FXML private TableView<InventoryItem> inventoryTable;
    @FXML private TableColumn<InventoryItem, Integer> colInvId;
    @FXML private TableColumn<InventoryItem, String> colInvName;
    @FXML private TableColumn<InventoryItem, String> colInvType;
    @FXML private TableColumn<InventoryItem, Double> colInvPrice;
    @FXML private TableColumn<InventoryItem, Integer> colInvStock;
    @FXML private TableColumn<InventoryItem, String> colInvSuppliers;

    private ObservableList<User> userList = FXCollections.observableArrayList();
    private ObservableList<InventoryItem> inventoryMasterList = FXCollections.observableArrayList(); 
    
    private int selectedUserId = 0;
    private int selectedInvId = 0;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        roleComboBox.getItems().addAll("Admin", "Technician", "Receptionist");
        roleComboBox.setButtonCell(new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(roleComboBox.getPromptText());
                } else {
                    setText(item);
                }
            }
        });
        filterComboBox.getItems().addAll("All", "Admin", "Technician", "Receptionist");
        filterComboBox.setValue("All");

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colHireDate.setCellValueFactory(new PropertyValueFactory<>("hireDate"));

        colId.setPrefWidth(60);
        colName.setPrefWidth(120);
        colUsername.setPrefWidth(85);
        colRole.setPrefWidth(85);
        colEmail.setPrefWidth(200);
        colHireDate.setPrefWidth(90);

        employeeTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        colId.setStyle("-fx-alignment: CENTER;");
        colUsername.setStyle("-fx-alignment: CENTER;");
        colRole.setStyle("-fx-alignment: CENTER;");
        colEmail.setStyle("-fx-alignment: CENTER;");
        colHireDate.setStyle("-fx-alignment: CENTER;");
        colName.setStyle("-fx-alignment: CENTER;"); 

        colId.setResizable(false); colName.setResizable(false);
        colUsername.setResizable(false); colRole.setResizable(false);
        colEmail.setResizable(false); colHireDate.setResizable(false);
        
        colId.setReorderable(false); colName.setReorderable(false);
        colUsername.setReorderable(false); colRole.setReorderable(false);
        colEmail.setReorderable(false); colHireDate.setReorderable(false);

        colName.setCellFactory(tc -> {
            TableCell<User, String> cell = new TableCell<>() {
                private javafx.scene.text.Text text = new javafx.scene.text.Text();
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setGraphic(null);
                    } else {
                        text.setText(item);
                        text.wrappingWidthProperty().bind(colName.widthProperty().subtract(10));
                        text.setTextAlignment(javafx.scene.text.TextAlignment.CENTER); 
                        setGraphic(text);
                        setAlignment(javafx.geometry.Pos.CENTER); 
                    }
                }
            };
            return cell;
        });
        
        invTypeComboBox.getItems().addAll("Part", "Service");

        colInvId.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("id"));
        colInvName.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("name"));
        colInvType.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("type"));
        colInvPrice.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("price"));
        colInvStock.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("stock"));
        colInvSuppliers.setCellValueFactory(new PropertyValueFactory<>("suppliers"));
        
        colInvSuppliers.setCellFactory(column -> {
            return new TableCell<InventoryItem, String>() {
                private javafx.scene.text.Text text = new javafx.scene.text.Text();

                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setGraphic(null);
                        setText(null);
                    } else {
                        text.setText(item);
                        text.wrappingWidthProperty().bind(column.widthProperty().subtract(10));
                        setGraphic(text);
                    }
                }
            };
        });
        
        colInvName.setCellFactory(column -> {
            return new TableCell<InventoryItem, String>() {
                private javafx.scene.text.Text text = new javafx.scene.text.Text();

                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setGraphic(null);
                        setText(null);
                    } else {
                        text.setText(item);
                        text.wrappingWidthProperty().bind(column.widthProperty().subtract(10));
                        text.setTextAlignment(javafx.scene.text.TextAlignment.CENTER); 
                        setGraphic(text);
                        setAlignment(javafx.geometry.Pos.CENTER); 
                    }
                }
            };
        });

        javafx.collections.transformation.FilteredList<InventoryItem> filteredInvData = new javafx.collections.transformation.FilteredList<>(inventoryMasterList, b -> true);

        invSearchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredInvData.setPredicate(item -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                
                if (item.getName().toLowerCase().contains(lowerCaseFilter)) return true;
                if (String.valueOf(item.getId()).contains(lowerCaseFilter)) return true;
                if (item.getType().toLowerCase().contains(lowerCaseFilter)) return true;
                
                return false; 
            });
        });

        javafx.collections.transformation.SortedList<InventoryItem> sortedInvData = new javafx.collections.transformation.SortedList<>(filteredInvData);
        sortedInvData.comparatorProperty().bind(inventoryTable.comparatorProperty());
        inventoryTable.setItems(sortedInvData);

        loadUserData();
        loadSystemReports();
        loadDashboardStats();
        loadInventoryData();

        employeeTable.setOnMouseClicked(event -> {
            User selectedUser = employeeTable.getSelectionModel().getSelectedItem();
            if (selectedUser != null) {
                fillFormForUpdate(selectedUser);
            }
        });

        searchField.textProperty().addListener((observable, oldValue, newValue) -> updateTableData());
        filterComboBox.valueProperty().addListener((observable, oldValue, newValue) -> updateTableData());
        
        inventoryTable.setOnMouseClicked(event -> {
            InventoryItem selected = inventoryTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                selectedInvId = selected.getId();
                invNameField.setText(selected.getName());
                invTypeComboBox.setValue(selected.getType());
                invPriceField.setText(String.valueOf(selected.getPrice()));
                
                if (selected.getType().equals("Part")) {
                    invStockField.setText(String.valueOf(selected.getStock()));
                    invStockField.setDisable(false);
                } else {
                    invStockField.setText("0");
                    invStockField.setDisable(true);
                }
            }
        });

        invTypeComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if ("Service".equals(newVal)) {
                invStockField.setText("0");
                invStockField.setDisable(true);
            } else {
                invStockField.setDisable(false);
            }
        });
    }

    @FXML
    private void handleAddButton() {
        String fullName = nameField.getText().trim();
        String user = usernameField.getText().trim();
        String pass = passwordField.getText();
        String hashedPassword = hashPassword(pass);
        String roleName = roleComboBox.getValue();
        String emailPrefix = emailField.getText().trim();

        if (fullName.isEmpty() || user.isEmpty() || pass.isEmpty() || roleName == null || emailPrefix.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Missing Data", "Please fill in all fields!");
            return;
        }

        String fullEmail = emailPrefix + "@tf.com";

        try (Connection conn = DBConnection.getConnection()) {
            if (isDuplicate(conn, "FullName", fullName, 0)) {
                showAlert(Alert.AlertType.ERROR, "Duplicate Data", "This Employee Name already exists in the system!");
                return;
            }
            if (isDuplicate(conn, "Username", user, 0)) {
                showAlert(Alert.AlertType.ERROR, "Duplicate Data", "This Username is already taken, please choose another one!");
                return;
            }
            if (isDuplicate(conn, "Email", fullEmail, 0)) {
                showAlert(Alert.AlertType.ERROR, "Duplicate Data", "This Email is already in use, please change the prefix!");
                return;
            }
            
            String[] nameParts = fullName.split(" ", 2);
            String firstName = nameParts[0];
            String lastName = (nameParts.length > 1) ? nameParts[1] : "";
            int roleId = getRoleId(roleName);
            Date hireDate = Date.valueOf(LocalDate.now());

            String query = "INSERT INTO `User` (Username, Password, FullName, FirstName, LastName, HireDate, RoleID, Email) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, user);
                stmt.setString(2, hashedPassword);
                stmt.setString(3, fullName);
                stmt.setString(4, firstName);
                stmt.setString(5, lastName);
                stmt.setDate(6, hireDate);
                stmt.setInt(7, roleId);
                stmt.setString(8, fullEmail); 
                
                if (stmt.executeUpdate() > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "User added successfully!");
                    clearForm();
                    loadUserData(); 
                    loadDashboardStats(); 
                }
            }
        } catch (Exception e) { 
            showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
        }
    }

    @FXML
    private void handleUpdateButton() {
        if (selectedUserId == 0) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select a user from the table to update!");
            return;
        }

        String fullName = nameField.getText().trim();
        String user = usernameField.getText().trim();
        String pass = passwordField.getText();
        String hashedPassword = hashPassword(pass);
        String roleName = roleComboBox.getValue();
        String emailPrefix = emailField.getText().trim();

        if (fullName.isEmpty() || user.isEmpty() || pass.isEmpty() || roleName == null || emailPrefix.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Missing Data", "Please fill in all fields!");
            return;
        }

        String fullEmail = emailPrefix + "@tf.com";

        try (Connection conn = DBConnection.getConnection()) {
            if (isDuplicate(conn, "FullName", fullName, selectedUserId)) {
                showAlert(Alert.AlertType.ERROR, "Duplicate Data", "This Employee Name already exists for another user!");
                return;
            }
            if (isDuplicate(conn, "Username", user, selectedUserId)) {
                showAlert(Alert.AlertType.ERROR, "Duplicate Data", "This Username is already taken by another user!");
                return;
            }
            if (isDuplicate(conn, "Email", fullEmail, selectedUserId)) {
                showAlert(Alert.AlertType.ERROR, "Duplicate Data", "This Email is already used by another user!");
                return;
            }

            String[] nameParts = fullName.split(" ", 2);
            String firstName = nameParts[0];
            String lastName = (nameParts.length > 1) ? nameParts[1] : "";
            int roleId = getRoleId(roleName);

            String query = "UPDATE `User` SET Username=?, Password=?, FullName=?, FirstName=?, LastName=?, RoleID=?, Email=? WHERE UserID=?";

            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, user);
                stmt.setString(2, hashedPassword);
                stmt.setString(3, fullName);
                stmt.setString(4, firstName);
                stmt.setString(5, lastName);
                stmt.setInt(6, roleId);
                stmt.setString(7, fullEmail);
                stmt.setInt(8, selectedUserId);

                if (stmt.executeUpdate() > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "User updated successfully!");
                    clearForm();
                    loadUserData(); 
                    loadDashboardStats(); 
                }
            }
        } catch (Exception e) { 
            showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    @FXML
    private void handleDeleteButton() {
        if (selectedUserId == 0) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select a user from the table to delete!");
            return;
        }

        String query = "UPDATE `User` SET IsActive = false WHERE UserID=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, selectedUserId);
            if (stmt.executeUpdate() > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "User archived successfully! (Soft Delete)");
                clearForm();
                loadUserData();
                loadDashboardStats(); 
            }
        } catch (Exception e) { 
            e.printStackTrace(); 
            showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    private void loadUserData() {
        userList.clear();
        String query = "SELECT UserID, FullName, Username, RoleID, Email, HireDate FROM `User` WHERE IsActive = true";   
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
             
            while (rs.next()) {
                int id = rs.getInt("UserID");
                String name = rs.getString("FullName");
                String username = rs.getString("Username");
                int roleId = rs.getInt("RoleID");
                String email = rs.getString("Email");
                
                Date dbDate = rs.getDate("HireDate");
                String hireDateStr = (dbDate != null) ? dbDate.toString() : "N/A";
                
                String roleName = getRoleName(roleId);
                userList.add(new User(id, name, username, roleName, email, hireDateStr));
            }
            employeeTable.setItems(userList);
        } catch (Exception e) { 
            e.printStackTrace(); 
            System.out.println("❌ Error loading data: " + e.getMessage());
        }
    }

    private void updateTableData() {
        String keyword = searchField.getText().toLowerCase().trim();
        String filterRole = filterComboBox.getValue();

        ObservableList<User> filteredList = FXCollections.observableArrayList();
        for (User user : userList) {
            boolean matchesSearch = keyword.isEmpty() || 
                                    user.getFullName().toLowerCase().contains(keyword) || 
                                    user.getUsername().toLowerCase().contains(keyword);
            
            boolean matchesFilter = filterRole == null || filterRole.equals("All") || 
                                    user.getRole().equals(filterRole);

            if (matchesSearch && matchesFilter) {
                filteredList.add(user);
            }
        }
        employeeTable.setItems(filteredList);
    }

    private void fillFormForUpdate(User user) {
        selectedUserId = user.getId();
        nameField.setText(user.getFullName());
        usernameField.setText(user.getUsername());
        roleComboBox.setValue(user.getRole());
        passwordField.clear(); 
        
        if(user.getEmail() != null && user.getEmail().contains("@")) {
            emailField.setText(user.getEmail().split("@")[0]);
        } else {
            emailField.clear();
        }
    }

    @FXML
    private void clearForm() {
        nameField.clear();
        usernameField.clear();
        passwordField.clear();
        emailField.clear();
        
        roleComboBox.setValue(null); 
        roleComboBox.getSelectionModel().clearSelection();
        
        searchField.clear();
        filterComboBox.setValue("All");
        selectedUserId = 0; 
        employeeTable.getSelectionModel().clearSelection();
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
    
    private boolean isDuplicate(Connection conn, String columnName, String value, int excludeUserId) throws Exception {
        String query = "SELECT COUNT(*) FROM `User` WHERE " + columnName + " = ? AND UserID != ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, value);
            stmt.setInt(2, excludeUserId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    private int getRoleId(String roleName) {
        if (roleName == null) return 3;
        switch (roleName) {
            case "Admin": return 1;
            case "Technician": return 2;
            case "Receptionist": return 3;
            default: return 3;
        }
    }

    private String getRoleName(int roleId) {
        switch (roleId) {
            case 1: return "Admin";
            case 2: return "Technician";
            case 3: return "Receptionist";
            case 4: return "Manager";
            default: return "Unknown";
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void loadDashboardStats() {
        String countUsersQuery = "SELECT COUNT(*) FROM `User` WHERE IsActive = true"; 
        String sumIncomeQuery = "SELECT COALESCE(SUM(Amount), 0) FROM `Payment`";
        String activeRepairsQuery = "SELECT COUNT(*) FROM `Maintenance_Job` WHERE StatusID IN (1, 2, 3)"; 

        try (Connection conn = DBConnection.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(countUsersQuery);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    if (totalEmployeesLabel != null) {
                        totalEmployeesLabel.setText(String.valueOf(rs.getInt(1)));
                    }
                }
            }

            try (PreparedStatement stmt = conn.prepareStatement(sumIncomeQuery);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    double income = rs.getDouble(1);
                    if (totalIncomeLabel != null) {
                        totalIncomeLabel.setText("$" + String.format("%.2f", income));
                    }
                }
            } catch (Exception ex) {
                if (totalIncomeLabel != null) totalIncomeLabel.setText("$0.00");
            }

            try (PreparedStatement stmt = conn.prepareStatement(activeRepairsQuery);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    if (activeRepairsLabel != null) {
                        activeRepairsLabel.setText(String.valueOf(rs.getInt(1)));
                    }
                }
            } catch (Exception ex) {
                 if (activeRepairsLabel != null) activeRepairsLabel.setText("0");
            }
        } catch (Exception e) {
            System.out.println("Error loading dashboard stats: " + e.getMessage());
        }
    }
    
    public void setLoggedInRole(int roleId) {
        if (roleId == 1) { 
            mainTabPane.getTabs().remove(dashboardTab);
            mainTabPane.getTabs().remove(reportsTab);
        }
    }
    
    public void loadSystemReports() {
        loadStatusPieChart();
        loadRevenueBarChart();
        loadRevenueLineChart();
        loadBrandPieChart();
    }
    
    @FXML
    private void handleInvAdd() {
        String name = invNameField.getText().trim();
        String type = invTypeComboBox.getValue();
        String priceStr = invPriceField.getText().trim();
        String stockStr = invStockField.getText().trim();

        if (name.isEmpty() || type == null || priceStr.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Missing Data", "Please fill all fields.");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            double price = Double.parseDouble(priceStr);
            int stock = stockStr.isEmpty() ? 0 : Integer.parseInt(stockStr);

            String insertItem = "INSERT INTO item (Name, Price) VALUES (?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(insertItem, java.sql.Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, name);
                stmt.setDouble(2, price);
                stmt.executeUpdate();

                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    int newItemId = rs.getInt(1);

                    if (type.equals("Part")) {
                        String insertPart = "INSERT INTO part (ItemID, StockQuantity) VALUES (?, ?)";
                        try (PreparedStatement pst = conn.prepareStatement(insertPart)) {
                            pst.setInt(1, newItemId);
                            pst.setInt(2, stock);
                            pst.executeUpdate();
                        }
                    } else {
                        String insertService = "INSERT INTO service (ItemID) VALUES (?)";
                        try (PreparedStatement pst = conn.prepareStatement(insertService)) {
                            pst.setInt(1, newItemId);
                            pst.executeUpdate();
                        }
                    }
                }
            }
            showAlert(Alert.AlertType.INFORMATION, "Success", "Item added successfully!");
            clearInvForm();
            loadInventoryData();

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Price and Stock must be valid numbers!");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
        }
    }

    @FXML
    private void handleInvUpdate() {
        if (selectedInvId == 0) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select an item from the table first.");
            return;
        }

        String name = invNameField.getText().trim();
        String type = invTypeComboBox.getValue();
        String priceStr = invPriceField.getText().trim();
        String stockStr = invStockField.getText().trim();

        try (Connection conn = DBConnection.getConnection()) {
            double price = Double.parseDouble(priceStr);
            int stock = stockStr.isEmpty() ? 0 : Integer.parseInt(stockStr);

            String updateItem = "UPDATE item SET Name=?, Price=? WHERE ItemID=?";
            try (PreparedStatement stmt = conn.prepareStatement(updateItem)) {
                stmt.setString(1, name);
                stmt.setDouble(2, price);
                stmt.setInt(3, selectedInvId);
                stmt.executeUpdate();
            }

            if (type.equals("Part")) {
                String updatePart = "UPDATE part SET StockQuantity=? WHERE ItemID=?";
                try (PreparedStatement pst = conn.prepareStatement(updatePart)) {
                    pst.setInt(1, stock);
                    pst.setInt(2, selectedInvId);
                    pst.executeUpdate();
                }
            }
            
            showAlert(Alert.AlertType.INFORMATION, "Success", "Item updated successfully!");
            clearInvForm();
            loadInventoryData();

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    @FXML
    private void handleInvDelete() {
        if (selectedInvId == 0) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select an item to delete.");
            return;
        }

        String query = "DELETE FROM item WHERE ItemID=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, selectedInvId);
            if (stmt.executeUpdate() > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Item deleted successfully!");
                clearInvForm();
                loadInventoryData();
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Cannot delete item. It might be linked to existing jobs.");
        }
    }
    
    @FXML
    private void handleExportInventory() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Inventory Report");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files (*.csv)", "*.csv"));
        fileChooser.setInitialFileName("Inventory_Report_" + java.time.LocalDate.now() + ".csv");
        
        Stage stage = (Stage) inventoryTable.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try (PrintWriter writer = new PrintWriter(file, "UTF-8")) {
                writer.println("ID,Item Name,Type,Price ($),Stock,Suppliers");

                for (InventoryItem item : inventoryTable.getItems()) {
                    writer.println(
                        item.getId() + "," + 
                        "\"" + item.getName() + "\"," +
                        item.getType() + "," + 
                        item.getPrice() + "," + 
                        item.getStock() + "," +
                        "\"" + item.getSuppliers() + "\"" 
                    );
                }
                
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Export Success");
                alert.setHeaderText(null);
                alert.setContentText("Inventory data exported successfully to:\n" + file.getAbsolutePath());
                alert.showAndWait();

            } catch (Exception e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Export Error");
                alert.setContentText("Could not export data: " + e.getMessage());
                alert.showAndWait();
            }
        }
    }

    @FXML
    private void clearInvForm() {
        invNameField.clear();
        invPriceField.clear();
        invStockField.clear();
        invTypeComboBox.getSelectionModel().clearSelection();
        invStockField.setDisable(false);
        selectedInvId = 0;
        inventoryTable.getSelectionModel().clearSelection();
    }

    private void loadStatusPieChart() {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        String query = "SELECT s.StatusName, COUNT(m.JobID) AS TotalCount FROM maintenance_job m JOIN status s ON m.StatusID = s.StatusID GROUP BY s.StatusName";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                pieChartData.add(new PieChart.Data(rs.getString("StatusName") + " (" + rs.getInt("TotalCount") + ")", rs.getInt("TotalCount")));
            }
            statusPieChart.setData(pieChartData);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void loadRevenueBarChart() {
        revenueBarChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Income");
        String query = "SELECT Method, SUM(Amount) AS TotalRevenue FROM payment GROUP BY Method";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                series.getData().add(new XYChart.Data<>(rs.getString("Method"), rs.getDouble("TotalRevenue")));
            }
            revenueBarChart.getData().add(series);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void loadRevenueLineChart() {
        revenueLineChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Revenue Transactions");

        javafx.scene.chart.CategoryAxis xAxis = (javafx.scene.chart.CategoryAxis) revenueLineChart.getXAxis();
        xAxis.setTickLabelRotation(0); 

        String query = "SELECT PaymentDate, Amount FROM payment ORDER BY PaymentDate ASC LIMIT 12";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            int counter = 1;
            while (rs.next()) {
                String fullDate = rs.getString("PaymentDate"); 
                double amount = rs.getDouble("Amount");

                String shortDate = fullDate;
                
                if (fullDate != null && fullDate.length() >= 10) {
                    shortDate = fullDate.substring(5, 10);
                }

                String hiddenSpaces = new String(new char[counter]).replace("\0", "\u200B");
                
                String xAxisLabel = shortDate + hiddenSpaces;

                XYChart.Data<String, Number> dataPoint = new XYChart.Data<>(xAxisLabel, amount);
                
                dataPoint.setExtraValue(fullDate);

                series.getData().add(dataPoint);
                counter++;
            }
            
            revenueLineChart.getData().add(series);

            for (XYChart.Data<String, Number> dataPoint : series.getData()) {
                javafx.scene.Node node = dataPoint.getNode();
                if (node != null) {
                    String fullDate = (String) dataPoint.getExtraValue();
                    javafx.scene.control.Tooltip tooltip = new javafx.scene.control.Tooltip("Transaction: " + fullDate + "\nPaid: $" + dataPoint.getYValue());
                    tooltip.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-color: #2c3e50; -fx-text-fill: white;");
                    javafx.scene.control.Tooltip.install(node, tooltip);

                    node.setOnMouseEntered(e -> node.setStyle("-fx-cursor: hand; -fx-scale-x: 1.8; -fx-scale-y: 1.8;"));
                    node.setOnMouseExited(e -> node.setStyle("-fx-scale-x: 1; -fx-scale-y: 1;"));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadBrandPieChart() {
        javafx.collections.ObservableList<javafx.scene.chart.PieChart.Data> pieChartData = javafx.collections.FXCollections.observableArrayList();
        
        String query = "SELECT b.BrandName, COUNT(m.JobID) AS BrandCount " +
                       "FROM maintenance_job m " +
                       "JOIN device d ON m.DeviceID = d.DeviceID " +
                       "JOIN brand b ON d.BrandID = b.BrandID " +
                       "GROUP BY b.BrandName " +
                       "ORDER BY BrandCount DESC LIMIT 5";

        try (java.sql.Connection conn = DBConnection.getConnection(); 
             java.sql.PreparedStatement stmt = conn.prepareStatement(query); 
             java.sql.ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String brand = rs.getString("BrandName");
                int count = rs.getInt("BrandCount");
                
                pieChartData.add(new javafx.scene.chart.PieChart.Data(brand + " (" + count + ")", count));
            }
            
            brandPieChart.setData(pieChartData);

            for (javafx.scene.chart.PieChart.Data data : brandPieChart.getData()) {
                javafx.scene.Node node = data.getNode();
                if (node != null) {
                    javafx.scene.control.Tooltip tooltip = new javafx.scene.control.Tooltip("Brand: " + data.getName() + "\nRepaired: " + (int)data.getPieValue() + " Devices");
                    tooltip.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-color: #2c3e50; -fx-text-fill: white;");
                    javafx.scene.control.Tooltip.install(node, tooltip);

                    node.setOnMouseEntered(e -> {
                        node.setStyle("-fx-cursor: hand; -fx-opacity: 0.8; -fx-scale-x: 1.03; -fx-scale-y: 1.03;");
                    });
                    
                    node.setOnMouseExited(e -> {
                        node.setStyle("-fx-opacity: 1; -fx-scale-x: 1; -fx-scale-y: 1;");
                    });
                }
            }
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
    }
    
    private void loadInventoryData() {
        inventoryMasterList.clear(); 
        
        String query = "SELECT i.ItemID, i.Name, i.Price, " +
                       "CASE WHEN p.ItemID IS NOT NULL THEN 'Part' ELSE 'Service' END AS ItemType, " +
                       "IFNULL(p.StockQuantity, 0) AS Stock, " +
                       "IFNULL(GROUP_CONCAT(s.SupplierName SEPARATOR ', '), 'No Supplier') AS Suppliers " +
                       "FROM item i " +
                       "LEFT JOIN part p ON i.ItemID = p.ItemID " +
                       "LEFT JOIN supplier_part sp ON p.ItemID = sp.ItemID " +
                       "LEFT JOIN supplier s ON sp.SupplierID = s.SupplierID " +
                       "GROUP BY i.ItemID, i.Name, i.Price, ItemType, Stock";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                inventoryMasterList.add(new InventoryItem(
                    rs.getInt("ItemID"),
                    rs.getString("Name"),
                    rs.getString("ItemType"),
                    rs.getDouble("Price"),
                    rs.getInt("Stock"),
                    rs.getString("Suppliers")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static class InventoryItem {
        private int id;
        private String name;
        private String type;
        private double price;
        private int stock;
        private String suppliers; 

        public InventoryItem(int id, String name, String type, double price, int stock, String suppliers) {
            this.id = id; this.name = name; this.type = type; 
            this.price = price; this.stock = stock; this.suppliers = suppliers;
        }

        public int getId() { return id; }
        public String getName() { return name; }
        public String getType() { return type; }
        public double getPrice() { return price; }
        public int getStock() { return stock; }
        public String getSuppliers() { return suppliers; }
    }
}