# specs.md

# Cloud & Arquitetura - Coupom api Java Spring

## Objetivo

Definir os requisitos técnicos, arquiteturais e operacionais para o desenvolvimento de uma API REST utilizando Java + Spring Boot seguindo:

- Clean Architecture
- SOLID
- Domain-Driven Design (DDD)
- Spec Driven Development (SDD)
- API first

---

## Stack
- Java 17
- Spring boot
- Maven

## Persistência
- Spring Data JPA
- Hibernate
- PostgreSQL

## Documentação
- OpenAPI 3 / Swagger


## Testes
- JUnit 5
- Mockito


## Architecture
- DDD
- Clean Architecture.

```text

src/main/java/com/api
│
├── domain
│   ├── user
│   │   ├── entity
│   │   └── repository
│   │
│   └── coupon
│       ├── entity
│       └── repository
│
├── application
│   ├── user
│   │   ├── usecase
│   │   └── dto
│   │
│   └── coupon
│       ├── usecase
│       └── dto
│
└── controllers
    ├── user
    └── coupon

