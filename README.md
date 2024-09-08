# Donation Platform - Backend

## Description
This project is the backend for a web application that simplifies the process of donating items to trusted institutions. The platform enables users to easily donate unwanted goods and provides administrators with the necessary tools to manage donations and institutions efficiently.

## Technologies Used
* Backend:
    - Java 21
    - Spring Boot
    - Spring Data JPA
    - Hibernate
* Frontend Integration:
    - Thymeleaf
    - HTML, CSS, JavaScript (Frontend developed separately)
* Database:
    - MySQL (mysql:5.7)
* Containerization:
    - Docker (data base)

## Key Features
* **User Management:**
    - Secure user registration and login.
    - Profile management for updating user information.
* **Donation Management:**
    - Listing of available items for donation.
    - Real-time tracking of donation status (submitted, collected, donated).
    - View donation history.
* **Administrator Panel:**
    - Comprehensive management of trusted institutions (add, edit, delete).
    - User management functionalities (approve/deny users, view user details).
    - Donation oversight and status updates.

## Getting Started
These instructions are provided if you'd like to set up and run the application locally.

**Prerequisites:**
- Java JDK 21
- Maven
- Docker (if running the database in a container)
- MySQL (mysql:5.7)

**Steps:**
1. **Clone the repository:**
   ```bash
   git clone https://github.com/AnielskieOczko/oddam_w_dobre_rece
2. **Configure the database connection:**
  - Update the application.properties file with your database credentials and connection settings.
3. **Build the project:**
    ```bash
    mvn clean install
4. **Run the application:**
   ```bash
   mvn spring-boot:run
  - The application will start on port 8080 by default

## License
This project is licensed under the MIT License.
## Screenshots
TODO
