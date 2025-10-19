# 📚 Library Management System

## 📝 Project Overview
The **Library Management System** is a Spring Boot based application designed to efficiently manage a library's operations.  
It allows users to perform full **CRUD (Create, Read, Update, Delete)** operations on books, borrowers, and borrowing records helping track library inventory, memberships, and transactions seamlessly.

---

## ⚙️ Setup and Installation

### 🧩 Prerequisites
Before running the project, ensure the following are installed on your system:
- **Java 17** or later  
- **Maven 3.8+**  
- **MySQL Server 8+**  
- **IntelliJ IDEA / Eclipse** (recommended)  
- **Postman** (for testing the APIs)

---

### 🚀 Steps to Run the Project

#### 1. Clone the repository
```bash
git clone https://github.com/krishnabamne/library-management.git
cd library-management
```

#### 2. Configure MySQL Database  
Create a database in MySQL:
```sql
CREATE DATABASE library_db;
```

Then, update your database credentials in  
`src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/library_db
spring.datasource.username=root
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

#### 3. Build and Run the Project
Using Maven:
```bash
mvn clean install
mvn spring-boot:run
```

Or, run the main class:
```
LibraryManagementApplication.java
```

Once started, the application will run on:  
👉 **http://localhost:8080**

---

## 🛠️ Tools, Libraries & Frameworks Used

- **Java 17**
- **Spring Boot 3.x**
- **Spring Data JPA (Hibernate)**
- **MySQL Database**
- **Lombok**
- **Maven**
- **Spring Validation**
- **Postman** for API testing

---

## 🌐 API Endpoints

### 📘 Books
| Method | Endpoint | Description |
|--------|-----------|-------------|
| POST | `/api/books` | Add a new book |
| GET | `/api/books` | Get all books |
| GET | `/api/books/{id}` | Get book by ID |
| PUT | `/api/books/{id}` | Update book details |
| DELETE | `/api/books/{id}` | Delete a book |

**Sample Request Body (Add Book):**
```json
{
  "title": "Effective Java",
  "author": "Joshua Bloch",
  "category": "Programming",
  "totalCopies": 5
}
```

---

### 👤 Borrowers
| Method | Endpoint | Description |
|--------|-----------|-------------|
| POST | `/api/borrowers` | Add a new borrower |
| GET | `/api/borrowers` | Get all borrowers |
| GET | `/api/borrowers/{id}` | Get borrower by ID |

**Sample Request Body (Add Borrower):**
```json
{
  "name": "John Doe",
  "email": "john@example.com"
}
```

---

### 🔖 Borrow Records
| Method | Endpoint | Description |
|--------|-----------|-------------|
| POST | `/api/borrow` | Borrow a book |
| PUT | `/api/borrow/return/{recordId}` | Return a borrowed book |
| GET | `/api/borrow/overdue` | Get overdue borrow records |
| GET | `/api/borrow/{borrowerId}` | Get borrow records by borrower ID |

**Sample Request Body (Borrow Request):**
```json
{
  "bookId": "uuid-of-book",
  "borrowerId": "uuid-of-borrower",
  "dueDate": "2025-10-25"
}
```

---

## 💡 Overall Approach and Thought Process

- Followed **Controller → Service → Repository** layered architecture for clean separation of concerns.  
- Used **DTOs** (`BookRequest`, `BorrowRequest`, etc.) to handle input and output separately from entity classes.  
- Implemented **Exception Handling** using custom exceptions like `ResourceNotFoundException` and `DuplicateResourceException`.  
- Ensured **input validation** using annotations such as `@NotNull`, `@Valid`, and `@Size`.  
- Adopted **UUID** identifiers for entity uniqueness and easy integration.  
- Used **Spring Data JPA** for database operations, making code concise and readable.  
- Designed REST APIs with consistent JSON responses using a unified `ApiResponse` DTO.

---

## 🧩 Challenges Faced and Solutions

### 1. Data Duplication Issues
**Challenge:** Duplicate book entries caused SQL constraint violations.  
**Solution:** Implemented validation checks in the service layer and threw a custom `DuplicateResourceException`.

### 2. Tracking Overdue Books
**Challenge:** Needed to efficiently fetch records that were overdue and not returned.  
**Solution:** Added a custom JPA query:
```java
List<BorrowRecord> findByReturnDateIsNullAndDueDateBefore(LocalDate date);
```

### 3. Managing Circular Dependencies
**Challenge:** Service dependencies were causing circular reference issues.  
**Solution:** Used **constructor-based dependency injection** with Lombok’s `@RequiredArgsConstructor`.

### 4. Clean API Response Structure
**Challenge:** Inconsistent responses across endpoints.  
**Solution:** Created a reusable `ApiResponse` class to maintain a consistent JSON structure.

---

## 👨‍💻 Author

**Marshal Krishna**  
📍 Bangalore, India  
📧 [krishnabamne2018@gmail.com](mailto:krishnabamne2018@gmail.com)  
💼 [LinkedIn](https://linkedin.com/in/marshal-krishna-77579a1b7)
