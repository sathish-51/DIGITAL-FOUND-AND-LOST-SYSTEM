FindIt

Digital Lost & Found Management System

A Java-based desktop application for efficient, centralized management of lost and found belongings.

"Java" (https://img.shields.io/badge/Java-ED8B00?style=flat-square&logo=openjdk&logoColor=white)
"Spring Boot" (https://img.shields.io/badge/Spring_Boot-6DB33F?style=flat-square&logo=springboot&logoColor=white)
"MySQL" (https://img.shields.io/badge/MySQL-4479A1?style=flat-square&logo=mysql&logoColor=white)
"Maven" (https://img.shields.io/badge/Maven-C71A36?style=flat-square&logo=apachemaven&logoColor=white)
"REST API" (https://img.shields.io/badge/REST-API-005571?style=flat-square)

---

Overview

FindIt is a desktop-based Lost and Found Management System developed using Java Swing, Spring Boot, and MySQL.

The application provides a centralized platform for reporting lost belongings, registering found items, browsing available listings, submitting ownership claims, and monitoring claim statuses.

By replacing fragmented and manual lost-and-found processes with a structured digital solution, FindIt aims to improve accessibility, organization, and claim management.

Problem Statement

Traditional lost-and-found systems face several challenges:

- Lack of a centralized platform for reporting belongings.
- Difficulty identifying and tracing lost items.
- Inefficient ownership claim management.
- Limited visibility into claim statuses.
- Absence of organized notifications and tracking.

FindIt addresses these challenges through a unified desktop application supported by a database-driven backend.

Core Features

Feature| Description
User Login & Profile| Access the application and view profile information
Report Lost Items| Register information about missing belongings
Report Found Items| Record details of discovered items
Browse Listings| Explore reported lost and found belongings
Submit Claims| Submit ownership claims for found items
My Claims| Monitor submitted claims and their statuses
Notifications| Receive notifications related to claim submissions
Dashboard| View statistics for items, claims, and notifications

Technology Stack

Layer| Technologies
Frontend| Java Swing
Backend| Spring Boot
API Communication| REST API, HTTP, JSON
JSON Processing| Jackson
Database| MySQL
Database Access| JDBC, JdbcTemplate
Build Management| Apache Maven
Development Environment| Visual Studio Code
Database Administration| MySQL Workbench

System Architecture

FindIt follows a layered architecture that separates the graphical user interface, application logic, and database operations.

+----------------------------+
|       Java Swing GUI       |
|    Desktop User Interface  |
+----------------------------+
              |
              | HTTP Requests
              v
+----------------------------+
|          REST API          |
|      JSON Communication    |
+----------------------------+
              |
              v
+----------------------------+
|     Spring Boot Backend    |
|      Application Logic     |
+----------------------------+
              |
              v
+----------------------------+
|        JdbcTemplate        |
|      Database Access       |
+----------------------------+
              |
              v
+----------------------------+
|       MySQL Database       |
|       Persistent Data      |
+----------------------------+

Architecture Workflow

1. The user interacts with the Java Swing desktop interface.
2. The frontend sends requests to the Spring Boot backend through REST APIs.
3. The backend processes the requests using application logic.
4. JdbcTemplate performs the required database operations.
5. MySQL stores and retrieves application information.
6. The backend returns responses to the frontend for display.

Claim Management Workflow

The system follows a five-stage claim workflow:

Report Lost / Found Item
           |
           v
     Browse Listings
           |
           v
      Submit Claim
           |
           v
   Generate Notification
           |
           v
    Track Claim Status

This workflow allows users to report belongings, discover available items, submit ownership claims, and monitor claim-related information.

Application Modules

1. User Management

Provides login functionality and access to user profile information.

2. Lost Item Reporting

Allows users to create lost-item reports and make missing belongings visible through centralized listings.

3. Found Item Reporting

Enables users to register discovered belongings so that potential owners can locate them.

4. Item Listings

Displays reported lost and found items within the application.

5. Claim Management

Allows users to submit claims for found items and view their claim details and statuses.

6. Notification Management

Provides notifications associated with claim submissions.

7. Dashboard

Displays an overview of application activity, including items, claims, and notifications.

Development Environment

The project uses the following development tools:

- Java Development Kit (JDK)
- Apache Maven
- Visual Studio Code
- MySQL Server
- MySQL Workbench
- Terminal / Command Prompt

Getting Started

Prerequisites

Ensure Java, Maven, and MySQL are installed and configured.

1. Clone the Repository

git clone https://github.com/YOUR_USERNAME/FindIt.git
cd FindIt

Replace "YOUR_USERNAME" with the repository owner's GitHub username.

2. Configure MySQL

Create the required database and configure the backend's database connection settings.

3. Build the Project

For the Maven-based backend:

mvn clean install

4. Launch the Application

Start the Spring Boot backend and run the Java Swing desktop application using the appropriate project entry points.

Note: The exact database schema, configuration properties, module paths, and startup commands should be documented after reviewing the source code.

Project Objectives

The primary objectives of FindIt are to:

- Develop a centralized digital lost-and-found platform.
- Simplify the reporting of missing and discovered belongings.
- Improve access to lost-and-found listings.
- Provide an organized ownership claim process.
- Enable claim tracking and submission notifications.
- Maintain application information using a relational database.

Future Scope

Potential future improvements include:

- AI-assisted matching of lost and found items.
- Image-based item identification.
- Email and SMS notifications.
- QR-code-based item identification.
- Mobile application support.
- Advanced search and filtering.
- Enhanced ownership verification.

These are proposed enhancements, not features of the current implementation.

Project Contributors

Contributor| Register Number
B Sathish| 2025503063
A Antony Christo| 2025503505
N Sanjay| 2025503057
M Vishvananth| 2025503035
S Guru Prasath| 2025503515

Project Information

Project Name: FindIt – Digital Lost & Found System
Project Type: Java Mini Project
Application Type: Desktop Application
Primary Language: Java
Database: MySQL

Conclusion

FindIt demonstrates the integration of a Java Swing desktop interface with a Spring Boot REST API and MySQL database.

The application provides an organized approach to managing lost and found belongings through item reporting, centralized listings, ownership claims, notifications, and claim tracking.

---

FindIt — Connecting Lost Belongings with Their Rightful Owners.
