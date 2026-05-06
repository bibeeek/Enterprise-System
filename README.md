# Enterprise Task Management System

A role-based backend system built using Spring Boot that simulates an enterprise-level task and user management workflow. The project focuses on authentication, authorization, department-based access control, task lifecycle management, and audit logging.

---

## Overview

This system is designed to manage users, managers, departments, and tasks with strict role-based access control. It demonstrates backend engineering concepts such as security, system design, validation, and scalable service structure.

---

## Tech Stack

- Java 17
- Spring Boot
- Spring Security
- JWT Authentication
- Spring Data JPA (Hibernate)
- PostgreSQL
- Maven
- Swagger / OpenAPI

---

## Core Features

### Authentication & Security
- JWT-based stateless authentication
- Spring Security integration with role-based access control
- Custom UserDetailsService implementation
- DaoAuthenticationProvider setup
- SecurityContextHolder used for identifying logged-in users
- Account locking mechanism based on failed login attempts
- Auto-unlock logic based on time conditions

---

### User Management
- User registration and login
- Profile retrieval and update
- Role-based restrictions (USER, MANAGER, ADMIN)
- Admin-controlled user activation and deactivation

---

### Department Management
- Departments created and managed by ADMIN
- Managers assigned to departments
- Users linked to departments for scoped access control

---

### Task Management
- Managers can create tasks within their assigned department
- Tasks can be assigned to users
- Task lifecycle tracking (OPEN, IN_PROGRESS, DONE, OVERDUE, CANCELLED)
- Enable/disable task functionality
- Validation to ensure department-level task isolation
- Limit on number of tasks per user

---

### Audit Logging
- Logs key system actions such as:
  - User registration
  - Task creation and updates
  - Role-based administrative actions
- Stores action, actor, timestamp, and target entity

---

### API Design
- RESTful API structure
- DTO-based request and response models
- Centralized validation layer
- Custom exception handling for business rules
- Pagination and filtering support for large datasets

---

### API Documentation
- Swagger UI integrated for API testing and documentation
- Accessible at:
  http://localhost:8080/swagger-ui/index.html
- JWT authentication supported in Swagger requests

---

## Database

- PostgreSQL used as primary database
- Relational structure between Users, Tasks, and Departments
- AuditLogs table for system tracking

---

## Security Model

- Role-based access control:
  - ADMIN: system management
  - MANAGER: department task control
  - USER: assigned task operations
- JWT token validated per request
- Stateless authentication design
- Secure password storage using BCrypt

---

## Architecture Highlights

- Layered architecture (Controller → Service → Repository)
- DTO separation from entities
- Centralized validation utilities
- Custom exception handling
- Security integrated at filter level using JWT filter
- SecurityContextHolder used for runtime user resolution

---

## Future Improvements

- Refresh token implementation
- Email verification system
- Dockerized deployment pipeline
- Cloud database integration
---

## Author

Bibek Pokhrel
