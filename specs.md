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
- H2 em memória

## Documentação
- OpenAPI 3 / Swagger

## Testes
- JUnit
- Mockito

## Nomenclatura

| Elemento             | Convenção    | Exemplo                   |
|----------------------|--------------|---------------------------|
| Pastas               | camelCase    | `useCase`                 |
| Classes              | PascalCase   | `CreateUserUseCase`       |
| Interfaces           | PascalCase   | `UserRepository`          |
| Métodos              | camelCase    | `createUser()`            |
| Variáveis            | camelCase    | `userName`                |
| Constantes           | SNAKE_CASE   | `MAX_RETRY`               |
| Pacotes              | lowercase    | `com.project.auth`        |
| Arquivos `.md`       | kebab-case   | `create-user-use-case.md` |
| Endpoints REST       | kebab-case   | `/sign-in`                |
| Campos JSON          | camelCase    | `expirationDate`          |

## Architecture
- DDD
- Clean Architecture.

```text

src/main/java/com/api
│
├── domain
│   └── [entidade]
|        ├── repository (interfaces que abstraem a conexão ao serviço de armazenamento)
│        └── entity 
│
├── application
│   └── [entidade]
│        ├── port (interfaces que abstraem a implementação de ferramentas externas)
│        └── useCase
│
├── infrastructure
│   └── [entidade]
│        ├── service
│        └── dao (Camada de acesso ao banco de dados, implementa uma abstraçãod de repository)
│
└── presentation
    └── [entidade]
         ├── controller
         └── dto (Validação de payloads com jakarta)

