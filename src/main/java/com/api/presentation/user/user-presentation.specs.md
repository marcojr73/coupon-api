# Authentication Controller Specification

Controller de autenticação seguindo princípios de Spec Driven Development (SDD).

---

## Rest endpoints

- POST `/auth/sign-up`
  - Cria um novo usuário
  - Recebe um UserRequestDto `presentation/user/dto/UserRequestDto` no corpo da requisição
  - Chama SignInUserUseCase `application/user/useCase/SignUpUserUseCase`
  - Retorna: ```status: 201 {"id": "id-do-usuário", message: "Created"}```

- POST `/auth/sign-in`
  - Realiza autenticação do usuário
  - Recebe um UserSignInRequestDto `presentation/user/dto/UserSignInRequestDto` no corpo da requisição
  - Chama SignInUserUseCase `application/user/useCase/SignInUserUseCase` para autenticação
  - Deve retornar token um token JWT
  - Retorna: ```status: 200 {"accessToken": "token-jwt-do-usuário", message: "Created"}```
