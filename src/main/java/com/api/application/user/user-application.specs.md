# Authentication Controller Specification

Casos de uso da aplicação seguindo princípios de Spec Driven Development (SDD).

---

## Casos de uso

- Criação de usuário `application/user/useCase/SignUpUserUseCase`
    - Recebe um SignUpUserUseCaseInput `application/user/dto/SignUpUserUseCaseInput` como parametro
    - implementa as regras de criação de usuário
      - 409: Não deve existir um usuário com o mesmo e-mail cadastrado
    - Criptografa a senha
    - Faz a chamada para que userRepository.create `domain/user/repository/UserRepository` crie um novo usuário
    - Retorna um SignUpUserUseCaseOutput `application/user/dto/SignUpUserUseCaseOutput`

- Autenticação de usuário `application/user/useCase/SignInUserUseCase`
    - Recebe um UserInput `application/user/dto/UserSignInInput`
    - implementa as regras de autenticação do usuário
      - Verifica se existe o usuário para o email informado
      - Verifica se a senha esta correta para o usuário encontrado
    - Gera um token JWT com validade de 24 horas
    - Retorna o token JWT