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

## Nomenclatura

| Elemento       | Convenção            | Exemplo                   |
|----------------| -------------------- |---------------------------|
| Pastas         | camelCase           | `useCase`                 |
| Classes        | PascalCase           | `CreateUserUseCase`       |
| Interfaces     | PascalCase           | `UserRepository`          |
| Métodos        | camelCase            | `createUser()`            |
| Variáveis      | camelCase            | `userName`                |
| Constantes     | SCREAMING_SNAKE_CASE | `MAX_RETRY`               |
| Pacotes        | lowercase            | `com.project.auth`        |
| Arquivos `.md` | kebab-case           | `create-user-use-case.md` |
| Endpoints REST | kebab-case           | `/sign-in`                |
| Campos JSON    | camelCase            | `expirationDate`          |


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
├── infrastructure
│   ├── user
│   │   ├── service
│   │   └── dao
│   │
│   └── coupon
│       ├── service
│       └── dao
│
├── application
│   ├── user
│   │   ├── use-case
│   │   └── dto
│   │
│   └── coupon
│       ├── use-case
│       └── dto
│
└── interface
    ├── user
    │    ├── controller
    │    └── dto
    │
    └── coupon
         ├── controller
         └── dto

