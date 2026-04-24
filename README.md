# TechFix: Advanced Repair Shop Management System 🛠️💻
An enterprise-grade desktop application and relational database solution designed to manage the daily operations, inventory, and financial analytics of a large-scale electronics maintenance center. 

## 🚀 Key Features

* **Interactive GUI (JavaFX):** A robust and user-friendly interface built with JavaFX, featuring organized MVC (Model-View-Controller) architecture.
* **Soft Delete Architecture:** Implemented an `IsActive` flag for User and Employee records to prevent orphaned records and preserve historical financial data without hard-deleting records.
* **Automated Financial Triggers:** Engineered a MySQL `AFTER INSERT` trigger to dynamically calculate and update the total cost of a maintenance job in real-time as parts and services are added.
* **Strict Normalization (3NF):** The database schema consists of 17 fully normalized tables up to the Third Normal Form (3NF), eliminating data redundancy.
* **Secure Authentication:** Implemented SHA-256 cryptographic hashing to ensure secure system authentication and protect sensitive user credentials.

## 🛠️ Technology Stack

* **Language:** Java 21
* **GUI Framework:** JavaFX
* **RDBMS:** MySQL 8.0
* **Architecture:** MVC (Model, View, Controller)
* **Security:** SHA-256 Hashing

## 📂 Repository Structure

* `src/controller/`: Contains the Java classes handling the application's business logic and UI events.
* `src/model/`: Contains the data models and database connectivity logic (JDBC).
* `src/resources/`: Contains the FXML files for the UI layout and visual assets.
* `TechFix_Final_DB.sql`: The complete database dump containing the schema, automated triggers, constraints, and mock data.

## ⚙️ How to Run

1. **Set up the Database:**
   * Ensure you have MySQL Server installed.
   * Create a new database named `techfix_db`.
   * Import the provided SQL dump: 
     ```bash
     mysql -u root -p techfix_db < TechFix_Final_DB.sql
     ```
2. **Run the Application:**
   * Clone this repository and open it in your preferred Java IDE (e.g., IntelliJ IDEA, Eclipse).
   * Ensure the MySQL JDBC Driver is added to your project dependencies.
   * Run the main application class located in the `techfixapp` package.
