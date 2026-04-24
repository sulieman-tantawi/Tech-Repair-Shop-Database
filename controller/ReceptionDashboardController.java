package com.techfix.controller;

import com.techfix.util.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ReceptionDashboardController {

    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField phoneField;
    @FXML private TextField addressField;
    @FXML private ComboBox<String> cityComboBox;

    @FXML private Button addButton;
    @FXML private Button updateButton;
    @FXML private Button deleteButton;
    @FXML private Button addDeviceButton;

    @FXML private TextField searchField;
    @FXML private ComboBox<String> filterCityComboBox;
    @FXML private Button searchButton;
    @FXML private Button clearButton;

    @FXML private TableView<CustomerRow> customerTable;
    @FXML private TableColumn<CustomerRow, Integer> idColumn;
    @FXML private TableColumn<CustomerRow, String> nameColumn;
    @FXML private TableColumn<CustomerRow, String> devicesColumn;

    private final ObservableList<CustomerRow> customerList = FXCollections.observableArrayList();
    private int selectedCustomerId = 0;

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        devicesColumn.setCellValueFactory(new PropertyValueFactory<>("devices"));

        loadCitiesToComboBoxes();
        
        loadCustomerData();

        customerTable.setOnMouseClicked(event -> {
            CustomerRow selectedCustomer = customerTable.getSelectionModel().getSelectedItem();
            if (selectedCustomer != null) {
                fillFormForUpdate(selectedCustomer);
            }
        });
        
        searchField.textProperty().addListener((observable, oldValue, newValue) -> handleSearch());
        filterCityComboBox.valueProperty().addListener((observable, oldValue, newValue) -> handleSearch());
        
        customerTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        idColumn.setMaxWidth(1f * Integer.MAX_VALUE * 10); 
        nameColumn.setMaxWidth(1f * Integer.MAX_VALUE * 30); 
        devicesColumn.setMaxWidth(1f * Integer.MAX_VALUE * 60);
        
        idColumn.setReorderable(false);
        nameColumn.setReorderable(false);
        devicesColumn.setReorderable(false);
    }

    private void loadCitiesToComboBoxes() {
        ObservableList<String> citiesList = FXCollections.observableArrayList();
        ObservableList<String> filterList = FXCollections.observableArrayList();
        
        filterList.add("All"); 

        String query = "SELECT CityName FROM City ORDER BY CityID ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String cityName = rs.getString("CityName");
                citiesList.add(cityName);
                filterList.add(cityName);
            }

            cityComboBox.setItems(citiesList);
            filterCityComboBox.setItems(filterList);
            filterCityComboBox.getSelectionModel().selectFirst();

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load cities: " + e.getMessage());
        }
    }

    private void loadCustomerData() {
        customerList.clear();
        
        String query = "SELECT c.CustomerID, c.FirstName, c.LastName, c.Phone, c.Address, ci.CityName AS City, " +
                       "IFNULL(GROUP_CONCAT(CONCAT(b.BrandName, ' (', dt.TypeName, ')') SEPARATOR '\\n'), 'No devices yet') AS Devices " +
                       "FROM Customer c " +
                       "LEFT JOIN City ci ON c.CityID = ci.CityID " +
                       "LEFT JOIN Device d ON c.CustomerID = d.CustomerID " +
                       "LEFT JOIN Brand b ON d.BrandID = b.BrandID " +
                       "LEFT JOIN Device_Type dt ON d.TypeID = dt.TypeID " +
                       "GROUP BY c.CustomerID";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                customerList.add(new CustomerRow(
                        rs.getInt("CustomerID"),
                        rs.getString("FirstName"),
                        rs.getString("LastName"),
                        rs.getString("Phone"),
                        rs.getString("Address"),
                        rs.getString("City"),
                        rs.getString("Devices")
                ));
            }
            customerTable.setItems(customerList);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
        }
    }

    @FXML
    private void handleAdd() {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String phone = phoneField.getText().trim();
        String address = addressField.getText().trim();
        String city = cityComboBox.getValue();

        if (firstName.isEmpty() || lastName.isEmpty() || phone.isEmpty() || address.isEmpty() || city == null) {
            showAlert(Alert.AlertType.WARNING, "Missing Data", "Please fill all fields.");
            return;
        }

        String fullName = firstName + " " + lastName; 

        String query = "INSERT INTO Customer (FullName, FirstName, LastName, Phone, Address, CityID) VALUES (?, ?, ?, ?, ?, (SELECT CityID FROM City WHERE CityName = ?))";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
             
            stmt.setString(1, fullName);  
            stmt.setString(2, firstName);
            stmt.setString(3, lastName);
            stmt.setString(4, phone);
            stmt.setString(5, address);
            stmt.setString(6, city);

            if (stmt.executeUpdate() > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Customer added successfully!");
                clearCustomerFields();
                loadCustomerData();
            }
        } catch (java.sql.SQLIntegrityConstraintViolationException e) {
            showAlert(Alert.AlertType.ERROR, "Duplicate Data", "This Phone Number is already registered!");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    @FXML
    private void handleUpdate() {
        if (selectedCustomerId == 0) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a customer to update.");
            return;
        }

        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String phone = phoneField.getText().trim();
        String address = addressField.getText().trim();
        String city = cityComboBox.getValue();

        String fullName = firstName + " " + lastName;

        String query = "UPDATE Customer SET FullName=?, FirstName=?, LastName=?, Phone=?, Address=?, CityID=(SELECT CityID FROM City WHERE CityName=?) WHERE CustomerID=?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
             
            stmt.setString(1, fullName);
            stmt.setString(2, firstName);
            stmt.setString(3, lastName);
            stmt.setString(4, phone);
            stmt.setString(5, address);
            stmt.setString(6, city);
            stmt.setInt(7, selectedCustomerId);

            if (stmt.executeUpdate() > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Customer updated successfully!");
                clearCustomerFields();
                loadCustomerData();
            }
        } catch (java.sql.SQLIntegrityConstraintViolationException e) {
            showAlert(Alert.AlertType.ERROR, "Duplicate Data", "This Phone Number is already used by another customer!");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    @FXML
    private void handleDelete() {
        if (selectedCustomerId == 0) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a customer to delete.");
            return;
        }

        String query = "DELETE FROM Customer WHERE CustomerID=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, selectedCustomerId);
            if (stmt.executeUpdate() > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Customer deleted successfully!");
                clearCustomerFields();
                loadCustomerData();
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Cannot delete customer. Please remove their devices first.");
        }
    }

    @FXML
    private void handleAddDevice() {
        if (selectedCustomerId == 0) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a customer first.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/DeviceDialog.fxml"));
            Parent root = loader.load();

            DeviceDialogController controller = loader.getController();
            controller.setCustomerId(selectedCustomerId, phoneField.getText());

            Stage stage = new Stage();
            stage.setTitle("TechFix - Add Device");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            loadCustomerData();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Could not load Add Device window.");
        }
    }

    @FXML
    private void handleSearch() {
        String searchText = searchField.getText().trim().toLowerCase();
        String selectedCity = filterCityComboBox.getValue();

        ObservableList<CustomerRow> filteredList = FXCollections.observableArrayList();
        for (CustomerRow customer : customerList) {
            boolean matchesSearch = searchText.isEmpty()
                    || customer.getFullName().toLowerCase().contains(searchText)
                    || customer.getPhone().toLowerCase().contains(searchText);

            boolean matchesCity = selectedCity == null || selectedCity.equals("All")
                    || customer.getCity().equalsIgnoreCase(selectedCity);

            if (matchesSearch && matchesCity) {
                filteredList.add(customer);
            }
        }
        customerTable.setItems(filteredList);
    }

    @FXML
    private void handleClear() {
        clearCustomerFields();
    }

    private void fillFormForUpdate(CustomerRow customer) {
        selectedCustomerId = customer.getId();
        firstNameField.setText(customer.getFirstName());
        lastNameField.setText(customer.getLastName());
        phoneField.setText(customer.getPhone());
        addressField.setText(customer.getAddress());
        cityComboBox.setValue(customer.getCity());
    }

    private void clearCustomerFields() {
        firstNameField.clear();
        lastNameField.clear();
        phoneField.clear();
        addressField.clear();
        cityComboBox.setValue(null);
        searchField.clear();
        filterCityComboBox.setValue("All");
        selectedCustomerId = 0;
        customerTable.getSelectionModel().clearSelection();
        customerTable.setItems(customerList);
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static class CustomerRow {
        private int id;
        private String firstName;
        private String lastName;
        private String fullName;
        private String phone;
        private String address;
        private String city;
        private String devices;

        public CustomerRow(int id, String firstName, String lastName, String phone, String address, String city, String devices) {
            this.id = id;
            this.firstName = firstName;
            this.lastName = lastName;
            this.fullName = firstName + " " + lastName;
            this.phone = phone;
            this.address = address;
            this.city = city;
            this.devices = devices;
        }

        public int getId() { return id; }
        public String getFirstName() { return firstName; }
        public String getLastName() { return lastName; }
        public String getFullName() { return fullName; }
        public String getPhone() { return phone; }
        public String getAddress() { return address; }
        public String getCity() { return city; }
        public String getDevices() { return devices; }
    }
}