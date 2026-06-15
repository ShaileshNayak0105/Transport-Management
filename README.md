# School Bus Transport Management System

## Project Overview
This project is a School Bus Transport Management System built with Spring Boot and Thymeleaf.
It helps a school manage students, buses, routes, drivers, and parent accounts in one server-rendered web application.

The application uses role-based access:
- `ADMIN` can manage students, buses, routes, drivers, and parent accounts.
- `PARENT` can log in and view only their linked student's transport details.

## Tech Stack
- `Java 17`: Core programming language
- `Spring Boot 3.x`: Application framework and runtime bootstrap
- `Spring Security`: Session-based authentication and role-based authorization
- `Spring Data JPA`: Database access using repositories and entities
- `MySQL`: Relational database
- `Thymeleaf`: Server-side HTML rendering
- `Bootstrap 5`: Frontend styling and layout
- `Maven`: Build and dependency management


## Features
- Session-based login for Admin and Parent users
- Default admin creation on first startup
- Optional sample seed data for quick testing
- Admin dashboard with summary counts
- Student management
- Bus management
- Route management
- Driver management
- Parent account creation linked to students
- Parent dashboard with child transport details
- Bus capacity validation
- Duplicate email validation
- Centralized exception handling with user-friendly messages

## Project Structure
```text
src/
└── main/
    ├── java/com/school/busmanagement/
    │   ├── config/
    │   ├── controller/
    │   ├── dto/
    │   ├── entity/
    │   ├── exception/
    │   ├── repository/
    │   ├── security/
    │   └── service/
    └── resources/
        ├── static/css/
        ├── templates/admin/
        ├── templates/fragments/
        └── templates/parent/
```

## Prerequisites
- Java 17 installed
- Maven installed and available on `PATH`
- MySQL Server running
- A MySQL user with permission to create and update the `bus_management` database

## MySQL Setup
Update `src/main/resources/application.properties` with your MySQL username and password:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/bus_management?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=Asia/Kolkata
spring.datasource.username=root
spring.datasource.password=your_mysql_password
```

Recommended local setup:
1. Start MySQL server
2. Confirm the credentials in `application.properties`
3. Let Spring Boot create/update the schema with `spring.jpa.hibernate.ddl-auto=update`

## How To Run Locally
1. Open a terminal in the project root
2. Make sure MySQL is running
3. Run:

```bash
mvn spring-boot:run
```


```

## Default Login Credentials
Created automatically on first startup:

- Admin
  - Email: `admin@school.com`
  - Password: `admin123`

Sample seeded parent:
- Parent
  - Email: `parent1@school.com`
  - Password: `parent123`

## Seed Data
The application currently seeds sample data on startup for testing:
- 2 routes
- 2 drivers
- 2 buses
- 3 students
- 1 sample parent

Important:
- Remove seed data before production deployment.

## Database Schema Diagram
```text
users
├── id (PK)
├── name
├── email (UNIQUE)
├── password
├── role
└── student_id (FK -> students.id, nullable for ADMIN)

students
├── id (PK)
├── name
├── age
├── class_name
├── bus_id (FK -> buses.id)
└── route_id (FK -> routes.id)

buses
├── id (PK)
├── bus_number
├── capacity
├── driver_id (FK -> drivers.id)
└── route_id (FK -> routes.id)

drivers
├── id (PK)
├── name
└── phone

routes
├── id (PK)
├── route_name
└── pickup_points
```


### Flow 1: Admin Login
1. Navigate to `http://localhost:8080/login`
2. Enter `admin@school.com` / `admin123`
3. Click `Login`
4. Expected result:
   - Redirect to `/admin/dashboard`
   - Dashboard cards show student, bus, route, and parent counts

### Flow 2: Admin Adds Route
1. Navigate to `/admin/routes/new`
2. Enter Route Name: `Whitefield Route`
3. Enter Pickup Points: `MG Road, Marathahalli, Whitefield`
4. Submit the form
5. Expected result:
   - Redirect to `/admin/routes`
   - The new route appears in the route list

### Flow 3: Admin Adds Driver
1. Navigate to `/admin/drivers/new`
2. Enter Name: `Suresh Kumar`
3. Enter Phone: `9876543210`
4. Submit the form
5. Expected result:
   - Redirect to `/admin/drivers`
   - The new driver appears in the driver list

### Flow 4: Admin Adds Bus
1. Navigate to `/admin/buses/new`
2. Enter a Bus Number such as `KA03EF9876`
3. Enter Capacity: `40`
4. Select an available Driver
5. Select a Route
6. Submit the form
7. Expected result:
   - Redirect to `/admin/buses`
   - The new bus appears in the bus list

### Flow 5: Admin Adds Student
1. Navigate to `/admin/students/new`
2. Enter student Name, Age, and Class
3. Select Bus and Route
4. Submit the form
5. Expected result:
   - Redirect to `/admin/students`
   - The new student appears in the student list
   - The student is linked to the selected bus and route

### Flow 6: Bus Capacity Validation
1. Choose a bus with a known capacity
2. Add students until the bus reaches that capacity
3. Try to add one more student to the same bus
4. Expected result:
   - The form redirects back with an error message
   - Message shown: `Bus is at full capacity`

### Flow 7: Admin Creates Parent Account
1. Navigate to `/admin/parents/new`
2. Enter Parent Name
3. Enter Email
4. Enter a temporary password
5. Select a student from the dropdown
6. Submit the form
7. Expected result:
   - Redirect to `/admin/parents`
   - The parent appears in the parent list with the linked student

### Flow 8: Parent Login and Dashboard
1. Navigate to `/login`
2. Enter the parent email and password
3. Submit the form
4. Expected result:
   - Redirect to `/parent/dashboard`
   - The page shows:
     - Student name
     - Class
     - Bus number and capacity
     - Route name and pickup points
     - Driver name and phone number
   - No add, edit, or delete buttons are shown

### Flow 9: Role Security Check
1. Log in as a Parent
2. Manually open `/admin/students`
3. Expected result:
   - Access denied behavior from Spring Security
   - Typically `403 Forbidden` or redirect depending on configuration

## Notes For Testing
- If you changed seeded data or already have existing rows in the database, counts and sample records may differ.
- If schema changes were made during development, starting with a fresh `bus_management` database can avoid old-column conflicts.
- The application uses session-based authentication, so log out before switching between Admin and Parent accounts.
