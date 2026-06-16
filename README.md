# Library Management System (Java + MySQL)

## Overview

Library Management System is a Java-based desktop application that helps manage books, borrowers, librarians, clerks, loans, and hold requests. The project uses JDBC to connect with a MySQL database and follows object-oriented programming principles.

---

## Features

### User Management

* Librarian management
* Clerk management
* Borrower management
* User authentication and login

### Book Management

* Add new books
* Remove books
* View all books
* Search books by:

  * Title
  * Author
  * Subject

### Loan Management

* Issue books
* Return books
* Loan history tracking
* Fine calculation for overdue books

### Hold Requests

* Place hold requests on books
* Manage hold requests
* Automatic hold request expiration support

### Database Integration

* Persistent storage using MySQL
* Load data from database on startup
* Save changes back to database

---

## Technologies Used

* Java SE
* JDBC (Java Database Connectivity)
* MySQL
* Apache Ant
* VS Code / NetBeans
* Object-Oriented Programming (OOP)

---

## Project Structure

```text
src/
 └── LMS/
      ├── Library.java
      ├── Book.java
      ├── Borrower.java
      ├── Clerk.java
      ├── Librarian.java
      ├── Loan.java
      ├── HoldRequest.java
      ├── Main.java

test/
 └── LMS/
      └── DomainSmokeTest.java

config/
docs/
images/
libs/
```

---

## Database Setup

### Create Database

```sql
CREATE DATABASE lms;
USE lms;
```

### Create Tables

Run the provided SQL schema script to create:

* PERSON
* BOOK
* STAFF
* CLERK
* LIBRARIAN
* BORROWER
* BORROWED_BOOK
* LOAN
* ON_HOLD_BOOK

### Configure Database

Update the database configuration file:

```properties
db.url=jdbc:mysql://localhost:3306/lms
db.user=root
db.password=YOUR_PASSWORD
```

---

## Setup Instructions

### Prerequisites

* Java 8 or later
* MySQL Server
* MySQL Connector/J
* Apache Ant

### Clone Repository

```bash
git clone https://github.com/YOUR_USERNAME/Library-Management-System-JAVA.git
cd Library-Management-System-JAVA
```

### Add MySQL Connector

Place:

```text
mysql-connector-j-9.7.0.jar
```

inside:

```text
libs/
```

---

## How to Run

### Using Ant

Compile:

```bash
ant compile
```

Run:

```bash
ant run
```

### Using VS Code

1. Open project folder.
2. Add MySQL Connector JAR to Referenced Libraries.
3. Run `Main.java`.

---

## Screenshots

### Login Screen

Add screenshot here:

```text
images/login.png
```

### Dashboard

Add screenshot here:

```text
images/dashboard.png
```

### Book Management

Add screenshot here:

```text
images/books.png
```

### Database Schema

Add screenshot here:

```text
images/database-schema.png
```

---

## Future Improvements

* JavaFX GUI
* Spring Boot REST API
* Role-based access controlgit add README.md
git commit -m "Improve README documentation"
git push
* Email notifications
* Online reservation system
* Report generation

---

## Author

Pavan Kumar

GitHub: https://github.com/vvspavankumar
