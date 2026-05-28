# Authentication Controller Specification

Controller de cupons seguindo princípios de Spec Driven Development (SDD).

---

## Rest endpoints

- POST `/coupon`
  - Cria um novo cupom
  - Recebe um CouponRequestDto `presentation/coupon/dto/CouponRequestDto` no corpo da requisição
  - Chama CouponCreateUseCase `application/coupon/useCase/CreateCouponUseCase`
  - Retorna: ```status: 201 {"id": "id-do-cupom", message: "Created"}```

- GET `/coupon`
  - Lista todos os cupons
  - Chama ListCouponUseCase `application/coupon/useCase/ListCouponUseCase`
  - Retorna: ```status: 200 e lista de cupons```

- GET `/coupon/{:id}`
  - Lista cupom por id
  - Chama ShowCouponUseCase `application/coupon/useCase/ShowCouponUseCase`
  - Retorna: ```status: 200 e cupom```

- PUT `/coupon/{:id}`
  - Atualiza um cupom por id
  - Recebe um CouponUpdateRequestDto `presentation/coupon/dto/CouponUpdateRequestDto` no corpo da requisição
  - Chama UpdateCouponUseCase `application/coupon/useCase/UpdateCouponUseCase`
  - Retorna: ```status: 200 {"id": "id-do-cupom", message: "Ok"}```

- DELETE `/coupon/{:id}`
  - Exclui um cupom
  - Chama DeleteCouponUseCase `application/coupon/useCase/DeleteCouponUseCase`
  - Retorna: ```status: 201 {"id": "id-do-cupom", message: "Ok"}```
