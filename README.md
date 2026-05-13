# TechFix: Advanced Repair Shop Management System 🛠️💻

An enterprise-grade desktop application and relational database solution designed to manage the daily operations, inventory, and financial analytics of a large-scale electronics maintenance center.

## 🚀 Key Features

* **Role-Based Access Control (RBAC):** Secure, multi-tier access tailored for Administrators, Receptionists, and Technicians, ensuring data privacy and workflow integrity.
* **Interactive Analytics Dashboard:** Real-time financial tracking and job status monitoring using dynamic JavaFX charts (Line, Pie, and Bar charts) with interactive tooltips.
* **Automated PDF Invoicing:** Integrated the `iText` library to automatically generate, format, and locally archive professional itemized PDF receipts upon successful checkout.
* **Smart Inventory & Subtyping Design:** Implemented an advanced relational subtyping pattern (`Item` as parent, `Part` and `Service` as children) to track physical stock seamlessly alongside service labor.
* **Soft Delete Architecture:** Implemented an `IsActive` flag for User and Employee records to prevent orphaned records and preserve historical financial data without hard-deleting records.
* **Automated Financial Triggers:** Engineered MySQL `AFTER INSERT` and `AFTER UPDATE` triggers to dynamically calculate and update the total cost of a maintenance job in real-time.
* **Strict Normalization (3NF):** The database schema consists of carefully designed tables normalized up to the Third Normal Form (3NF), eliminating data redundancy and ensuring integrity.
* **Secure Authentication:** Implemented SHA-256 cryptographic hashing to ensure secure system authentication and protect sensitive user credentials.

## 🛠️ Technology Stack

* **Language:** Java 21
* **GUI Framework:** JavaFX
* **RDBMS:** MySQL 8.0
* **Architecture:** MVC (Model, View, Controller) & 3-Tier Architecture
* **Libraries:** `iText` (for PDF generation), JDBC (for database connectivity)
* **Security:** SHA-256 Hashing

## 📸 Screenshots
*(Add your beautiful UI screenshots here to show off the system!)*
* **Admin Dashboard** (Analytics & Revenue)
* **Receptionist View** (Job Creation & Checkout)
* **Technician View** (Workshop Management)
* **Generated PDF Invoice**

## 📂 Repository Structure

* `src/controller/`: Contains the Java classes handling the application's business logic and UI events.
* `src/model/`: Contains the data models, objects, and database connectivity logic.
* `src/util/`: Utility classes including database connection setup and PDF generation managers.
* `src/resources/`: Contains the FXML files for the UI layout and visual assets.
* `TechFix_Final_DB.sql`: The complete database dump containing the schema, automated triggers, constraints, and mocked seeded data.

## ⚙️ How to Run

**1. Set up the Database:**
* Ensure you have MySQL Server installed.
* Create a new database named `techfix_db`.
* Import the provided SQL dump: 
    ```bash
    mysql -u root -p techfix_db < TechFix_Final_DB.sql
    ```

**2. Run the Application:**
* Clone this repository and open it in your preferred Java IDE (e.g., IntelliJ IDEA, Eclipse).
* Ensure the **MySQL JDBC Driver** and **iText PDF** libraries are added to your project dependencies (via Maven/Gradle or external JARs).
* Run the main application class located in the `techfixapp` package.
