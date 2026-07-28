# CheckInHub

A REST API for event registration and attendance validation via QR Code.

> ⚠️ **Status: work in progress.** This project is under active development and is not feature-complete. Some modules described below are still being built.

## What is this project?

CheckInHub models a common real-world problem for local events: how do you know, reliably, who registered and who actually showed up?

The platform connects three roles in a single flow:

- **Organizers** create and manage events
- **Participants** register for events and receive a unique QR Code
- At the event entrance, the QR Code is scanned and a check-in is recorded, with timestamp and who validated it

The core of the domain is a simple chain: **User → Event → Enrollment → Check-in**. The `Enrollment` is the connecting piece — it's what carries the QR Code, not the user or the event directly.

## Why this project exists

CheckInHub is a portfolio project, not a commercial product. Its purpose is to give me a realistic domain to practice the skills laid out in my personal backend study roadmap, applying each concept to real code instead of isolated exercises.

Right now, the focus is on the **Java + Spring Boot** fundamentals:

- Layered architecture (Controller / Service / Repository)
- JPA entity relationships modeled correctly (`@ManyToOne`, `@OneToOne`, `@JoinColumn`)
- Database schema versioned with Flyway migrations instead of relying on Hibernate auto-generation
- Request/response separation through DTOs, keeping entities out of the API surface
- Bean Validation on incoming data
- API documentation via OpenAPI/Swagger

As the project progresses, it will also be used to practice topics further down the roadmap:

- Authentication and authorization with Spring Security (JWT)
- Global exception handling
- Automated testing (JUnit 5 + Mockito)
- Containerization with Docker
- CI/CD with GitHub Actions
- Cloud deployment on AWS

## Tech stack (so far)

- Java 21
- Spring Boot
- Spring Data JPA
- Flyway
- Bean Validation
- springdoc-openapi (Swagger UI)

## Domain entities

| Entity | Responsibility |
|---|---|
| `User` | A person in the system, either an organizer or a participant (`role`) |
| `Event` | Created by an organizer, has a status lifecycle (draft, published, canceled, closed) |
| `Enrollment` | Links a participant to an event; holds the unique QR Code token |
| `CheckIn` | Records the validation of an enrollment's QR Code at the event entrance |

## Note

This README intentionally focuses on the *why* and *what* of the project rather than setup instructions — those will be added once the API reaches a stable first version.