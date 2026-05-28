# Coupon Application Specification SDD (Clean Spec)

Casos de uso da aplicação seguindo princípios de Spec Driven Development (SDD).

---

## Casos de uso

- Criação de cupom `application/coupon/useCase/CreateCouponUseCase`
    - Recebe um Coupon `domain/coupon/entity/Coupon` como parâmetro
    - Implementa as regras de criação de cupom
        - Sanitiza o código do cupom antes da validação
        - Define `published` como `true` quando o valor não for informado
        - Define `createdDate` com a data atual
        - 400: Não deve permitir dados inválidos para o cupom
        - 400: Não deve existir um cupom com o mesmo código cadastrado
    - Gera um identificador único para o cupom
    - Faz a chamada para que couponRepository.save `domain/coupon/repository/CouponRepository` salve o novo cupom
    - Retorna um CouponUseCaseOutput `application/coupon/dto/CouponUseCaseOutput`

- Listagem de cupons `application/coupon/useCase/ListCouponUseCase`
    - Não recebe parâmetros
    - Faz a chamada para que couponRepository.findAll `domain/coupon/repository/CouponRepository` busque todos os cupons
    - Converte cada Coupon `domain/coupon/entity/Coupon` encontrado para CouponUseCaseOutput `application/coupon/dto/CouponUseCaseOutput`
    - Retorna uma lista de CouponUseCaseOutput `application/coupon/dto/CouponUseCaseOutput`

- Visualização de cupom `application/coupon/useCase/ShowCouponUseCase`
    - Recebe o id do cupom como parâmetro
    - Implementa as regras de visualização de cupom
        - 404: Deve existir um cupom para o id informado
    - Faz a chamada para que couponRepository.findById `domain/coupon/repository/CouponRepository` busque o cupom
    - Converte o Coupon `domain/coupon/entity/Coupon` encontrado para CouponUseCaseOutput `application/coupon/dto/CouponUseCaseOutput`
    - Retorna um CouponUseCaseOutput `application/coupon/dto/CouponUseCaseOutput`

- Atualização de cupom `application/coupon/useCase/UpdateCouponUseCase`
    - Recebe o id do cupom e um Coupon `domain/coupon/entity/Coupon` com os dados de atualização como parâmetros
    - Implementa as regras de atualização de cupom
        - 404: Deve existir um cupom para o id informado
        - Sanitiza o código do cupom quando um novo código for informado
        - 400: Não deve permitir alterar o código para um código já cadastrado em outro cupom
        - Atualiza apenas os campos informados
        - 400: Não deve permitir que o cupom fique com dados inválidos após a atualização
    - Faz a chamada para que couponRepository.save `domain/coupon/repository/CouponRepository` salve o cupom atualizado
    - Retorna um CouponUseCaseOutput `application/coupon/dto/CouponUseCaseOutput`

- Remoção de cupom `application/coupon/useCase/DeleteCouponUseCase`
    - Recebe o id do cupom como parâmetro
    - Implementa as regras de remoção de cupom
        - 404: Deve existir um cupom para o id informado
    - Faz a chamada para que couponRepository.findById `domain/coupon/repository/CouponRepository` verifique se o cupom existe
    - Faz a chamada para que couponRepository.delete `domain/coupon/repository/CouponRepository` remova o cupom
    - Não retorna conteúdo