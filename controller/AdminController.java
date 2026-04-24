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
import javafx.scene.text.Text;

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

    private ObservableList<User> userList = FXCollections.observableArrayList();
    private int selectedUserId = 0;

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

        loadUserData();
        loadDashboardStats();

        employeeTable.setOnMouseClicked(event -> {
            User selectedUser = employeeTable.getSelectionModel().getSelectedItem();
            if (selectedUser != null) {
                fillFormForUpdate(selectedUser);
            }
        });

        searchField.textProperty().addListener((observable, oldValue, newValue) -> updateTableData());
        filterComboBox.valueProperty().addListener((observable, oldValue, newValue) -> updateTableData());
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
    
    //SHA-256
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
}