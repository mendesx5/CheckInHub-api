# CheckInHub

CheckInHub is a **backend REST API for event registration and attendance validation using QR Codes**.

The project models a complete event flow: organizers create and publish events, participants register, receive a unique QR Code, and attendance is validated at the entrance through a secure check-in process.

It was developed as a portfolio project focused mainly on **backend engineering with Java and Spring Boot**, applying real-world concepts such as authentication, authorization, relational modeling, business rules, automated testing, database migrations, containerization, and CI.

> **Project Status:** Active Development / MVP hardening
> The core business flow is implemented. The current focus is improving integration tests, delivery automation, and production readiness.

---

## 📌 About the Project

CheckInHub solves a common problem in in-person events: managing registrations and reliably validating who actually attended.

The main flow is:

```text
Organizer creates an event
        ↓
Event is published
        ↓
Participant registers
        ↓
Unique QR Code is generated
        ↓
QR Code is scanned at the entrance
        ↓
Backend validates the enrollment
        ↓
Check-in is recorded
```

The backend is responsible for protecting this flow with authentication, authorization, and business rules.

---

## User Roles

### ORGANIZER

Organizers can manage their own events and access operational information related to them.

Main responsibilities:

* Create events
* Publish events
* Cancel events
* View their own events
* View participants enrolled in their events
* Access attendance information

### PARTICIPANT

Participants can interact with published events.

Main responsibilities:

* Browse available events
* Register for events
* View their enrollments
* Access their QR Code
* Cancel their enrollment

### DOORKEEPER

The doorkeeper role is intended for attendance validation at the event entrance.

Main responsibility:

* Scan and validate participant QR Codes

---

## Main Features

### Authentication and Authorization

* User registration and login
* JWT-based authentication
* Spring Security integration
* Protected endpoints
* Role-based authorization
* Resource ownership validation

The authenticated user is obtained from the security context, avoiding reliance on user IDs manually sent by the client for protected operations.

---

### Event Management

Events are associated with organizers and follow a lifecycle.

Implemented operations include:

* Create events as drafts
* Publish events
* List published events
* List events owned by the authenticated organizer
* Find events by ID
* Cancel events
* Restrict private operations to the event owner

---

### Enrollment Management

An enrollment connects a participant to an event.

It is also responsible for holding the unique token used by the QR Code.

Implemented operations include:

* Register for an event
* Generate a unique QR Code token
* List authenticated participant enrollments
* List participants enrolled in an organizer's event
* Cancel an enrollment
* Validate ownership and business rules

---

### QR Code

Each valid enrollment receives a unique token that can be represented as a QR Code.

QR Code generation uses **ZXing**.

The QR Code represents the unique enrollment token used by the backend to identify and validate the participant's registration.

---

### Check-in

The check-in flow validates an enrollment through its QR Code token.

The API verifies the enrollment and records attendance while enforcing business rules such as preventing duplicate check-ins.

```text
QR Code Token
      ↓
CheckInController
      ↓
CheckInService
      ↓
Enrollment validation
      ↓
Check-in record
```

---

### Exception Handling

The API uses centralized exception handling to provide consistent HTTP responses.

Custom exceptions cover cases such as:

* Resource not found
* Unauthorized operations
* Business rule violations
* Invalid requests

The project also uses a standardized API error response model.

---

### Validation

Incoming requests are validated through **Jakarta Bean Validation**.

DTO validation prevents invalid data from reaching the service layer and helps keep the API contract consistent.

---

## Architecture

The project follows a layered backend architecture while organizing the code around the main business domains.

```text
Client
   ↓
Controller
   ↓
Service
   ↓
Repository
   ↓
PostgreSQL
```

Main modules include:

```text
check_in_hub
├── auth
├── checkin
├── config
├── enrollment
├── event
├── exception
├── qrcode
├── security
└── user
```

DTOs are used to keep persistence entities separated from the public API contract.

---

## Domain Model

The main domain can be represented as:

```text
User
 │
 ├── Organizer ──► Event
 │                  │
 │                  ▼
 └── Participant ─► Enrollment
                       │
                       ├── QR Code Token
                       │
                       ▼
                    CheckIn
```

| Entity       | Responsibility                                                    |
| ------------ | ----------------------------------------------------------------- |
| `User`       | Represents authenticated users and their roles                    |
| `Event`      | Represents an event created and managed by an organizer           |
| `Enrollment` | Connects a participant to an event and stores the unique QR token |
| `CheckIn`    | Records attendance validation for an enrollment                   |

---

## Tech Stack

### Backend

* Java 21
* Spring Boot
* Spring Web MVC
* Spring Data JPA
* Hibernate
* Lombok
* Maven

### Database

* PostgreSQL
* Flyway

### Security

* Spring Security
* JWT
* JJWT
* Role-based authorization

### Validation and API Documentation

* Jakarta Bean Validation
* Swagger UI
* OpenAPI
* springdoc-openapi

### QR Code

* ZXing

### Testing

* JUnit 5
* Mockito
* Spring Boot Test
* Spring Security Test

### DevOps

* Git
* GitHub
* Docker
* Docker Compose
* GitHub Actions
* GitHub Container Registry

---

## Testing

The project includes automated tests for the main application modules.

Current test areas include:

* Authentication
* Users
* Events
* Enrollments
* QR Code
* Check-in

The current strategy includes unit tests and Spring application tests.

Expanding the integration test suite is one of the next major quality improvements.

Run the tests with:

```bash
./mvnw test
```

On Windows:

```bash
mvnw.cmd test
```

---

## Continuous Integration

The project already includes a **GitHub Actions CI pipeline**.

On pushes and pull requests targeting the `main` branch, the pipeline:

1. Checks out the repository
2. Configures Java 21
3. Runs the automated test suite
4. Builds the application with Maven
5. On pushes to `main`, builds the Docker image
6. Publishes the image to GitHub Container Registry

Docker images are versioned with:

```text
latest
<short-commit-sha>
```

This provides an initial CI foundation and prepares the project for a future Continuous Deployment flow.

---

## Docker

The application includes a `Dockerfile` and Docker Compose configuration.

Build the application:

```bash
./mvnw clean package
```

Build the Docker image:

```bash
docker build -t checkinhub-api .
```

Start the configured environment with Docker Compose:

```bash
docker compose up -d
```

---

## Database Migrations

Database schema changes are managed using **Flyway**.

This allows the database structure to evolve through version-controlled migrations instead of relying on automatic Hibernate schema generation in production.

---

## API Documentation

The API is documented using **OpenAPI and Swagger UI**.

When the application is running locally, Swagger can be used to explore endpoints and test authenticated requests.

Common local paths:

```text
/swagger-ui/index.html
/v3/api-docs
```

---

## Running Locally

### Requirements

Make sure you have:

* Java 21
* Docker and Docker Compose, or a local PostgreSQL instance
* Git

### Clone the repository

```bash
git clone https://github.com/mendesx5/CheckInHub-api.git
cd CheckInHub-api
```

### Start dependencies

```bash
docker compose up -d
```

### Run the application

Linux/macOS:

```bash
./mvnw spring-boot:run
```

Windows:

```bash
mvnw.cmd spring-boot:run
```

---

## Development Roadmap

### Core Backend

* [x] User domain
* [x] Event domain
* [x] Enrollment domain
* [x] QR Code flow
* [x] Check-in flow
* [x] Layered architecture
* [x] DTO-based API contracts

### Security

* [x] Spring Security
* [x] JWT authentication
* [x] Login flow
* [x] Role-based authorization
* [x] Resource ownership rules

### Data and API Quality

* [x] Bean Validation
* [x] Global exception handling
* [x] Custom exceptions
* [x] Flyway migrations
* [x] Database constraints
* [x] Swagger / OpenAPI

### Testing

* [x] Unit tests
* [ ] Expand integration test coverage

### DevOps

* [x] Dockerfile
* [x] Docker Compose
* [x] GitHub Actions CI
* [x] Docker image publishing to GHCR
* [ ] Production deployment
* [ ] Continuous Deployment

### Post-MVP

* [ ] Edit draft events
* [ ] User profile updates
* [ ] Simple auditing
* [ ] Additional security hardening
* [ ] Improved observability

---

## What This Project Practices

CheckInHub was created not only to deliver a working application, but also to consolidate backend engineering concepts through a realistic project.

Main concepts practiced include:

* Object-Oriented Programming
* REST API design
* HTTP methods and status codes
* Layered architecture
* Dependency Injection
* DTO pattern
* JPA / Hibernate
* Entity relationships
* PostgreSQL
* Database constraints
* Database migrations
* Authentication and authorization
* JWT
* Spring Security
* Resource ownership
* Business rule validation
* Exception handling
* Unit testing
* Mocking with Mockito
* Integration testing
* API documentation
* Docker
* Container registries
* CI/CD fundamentals
* GitHub Actions

---

## Project Evolution

The project has been developed incrementally, starting from domain modeling and basic API operations and gradually adding production-oriented concerns.

```text
Domain Modeling
      ↓
REST API
      ↓
Business Rules
      ↓
JWT Security
      ↓
Validation
      ↓
Exception Handling
      ↓
Flyway + Constraints
      ↓
Unit Tests
      ↓
Swagger
      ↓
Docker
      ↓
CI
      ↓
Integration Tests
      ↓
CD / Deployment
```

---

## Project Goals

The main goal of CheckInHub is to build a backend that goes beyond basic CRUD operations.

The project focuses on understanding how real backend applications deal with:

* Authentication
* Authorization
* Ownership rules
* State transitions
* Relational data
* Validation
* Error handling
* Automated testing
* Database evolution
* Containerization
* Automated delivery pipelines

The objective is to progressively transform a functional MVP into a more reliable and production-ready application.

---

## 👨‍💻 Author

**José Gabriel**

Computer Science student focused on Backend Development and Software Engineering with Java and Spring Boot.

GitHub: [@mendesx5](https://github.com/mendesx5)

---

## License

This project is currently maintained for educational and portfolio purposes.

License information may be added in a future version.
