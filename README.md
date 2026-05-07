# Enterprise Task Management System

A role-based backend system built using Spring Boot that simulates an enterprise-level task and user management workflow. The project focuses on authentication, authorization, department-based access control, task lifecycle management, and audit logging.

---

## Overview

This system is designed to manage users, managers, departments, and tasks with strict role-based access control. It demonstrates backend engineering concepts such as security, system design, validation, scalable service structure, and real-world backend architecture practices.

---

## Live API Documentation

You can test and explore the deployed APIs using Swagger UI:

- Production Swagger URL:  
  https://enterprise-system-production.up.railway.app/swagger-ui/index.html

- Local Swagger URL:  
  http://localhost:8080/swagger-ui/index.html

Admin Email:admin@enterprise.com
pass:admin1234
---

## Tech Stack

- Java 17
- Spring Boot
- Spring Security
- JWT Authentication
- Spring Data JPA (Hibernate)
- PostgreSQL
- Maven
- Docker
- Railway Deployment
- Swagger / OpenAPI

---

## Core Features

### Authentication & Security

- JWT-based stateless authentication
- Spring Security integration with role-based access control
- Custom `UserDetailsService` implementation
- `DaoAuthenticationProvider` setup
- `SecurityContextHolder` used for identifying logged-in users
- Account locking mechanism based on failed login attempts
- Auto-unlock logic based on time conditions
- BCrypt password encryption

---

### User Management

- User registration and login
- Profile retrieval and update
- Role-based restrictions (`USER`, `MANAGER`, `ADMIN`)
- Admin-controlled user activation and deactivation

---

### Department Management

- Departments created and managed by `ADMIN`
- Managers assigned to departments
- Users linked to departments for scoped access control

---

### Task Management

- Managers can create tasks within their assigned department
- Tasks can be assigned to users
- Task lifecycle tracking:
  - `OPEN`
  - `IN_PROGRESS`
  - `DONE`
  - `OVERDUE`
  - `CANCELLED`
- Enable/disable task functionality
- Validation to ensure department-level task isolation
- Limit on number of tasks per user

---

### Audit Logging

Logs key system actions such as:

- User registration
- Task creation and updates
- Role-based administrative actions

Stores:

- action
- actor
- timestamp
- target entity

---

## API Design

- RESTful API structure
- DTO-based request and response models
- Centralized validation layer
- Custom exception handling for business rules
- Pagination and filtering support for large datasets

---

## API Documentation

- Swagger UI integrated for API testing and documentation
- JWT authentication supported directly in Swagger requests

### Swagger Links

- Local:
  `http://localhost:8080/swagger-ui/index.html`

- Production:
  `https://enterprise-system-production.up.railway.app/swagger-ui/index.html`

---

## Database

- PostgreSQL used as the primary database
- Relational mapping between:
  - Users
  - Tasks
  - Departments
  - Audit Logs
- Optimized entity relationships using JPA/Hibernate

---

## Security Model

### Roles

- `ADMIN` → Full system management
- `MANAGER` → Department-level task management
- `USER` → Assigned task operations

### Security Features

- JWT token validation on every request
- Stateless authentication architecture
- BCrypt password hashing
- Filter-based JWT authentication pipeline
- Role-based endpoint authorization

---

## Architecture Highlights

- Layered architecture:
  - Controller → Service → Repository
- DTO separation from entities
- Centralized validation utilities
- Custom exception hierarchy
- JWT authentication filter integration
- Runtime user resolution using `SecurityContextHolder`
- Scalable and maintainable project structure

---

## Deployment

- Dockerized backend application
- Deployed on Railway cloud platform
- Production-ready environment configuration
- Environment variable-based configuration support

---

## Future Improvements

- Refresh token implementation
- Email verification system
- CI/CD pipeline integration
- Cloud storage integration
- Redis caching
- Microservices migration
- Role permission customization

---

## Author

**Bibek Pokhrel**
