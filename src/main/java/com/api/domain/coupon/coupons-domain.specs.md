# Coupon Domain Specification SDD (Clean Spec)

Representa um cupom de desconto válido no sistema.

| Campo          | Tipo      | Obrigatório         |
|----------------|-----------|---------------------|
| code           | String    | Sim                 |
| description    | String    | Sim                 |
| discountValue  | Decimal   | Sim                 |
| expirationDate | LocalDate | Sim                 |
| published      | Boolean   | Não @default(true)  |
| createdDate    | LocalDate | Não @default(now()) |

---

## 1. Regras Invariantes

### Code
- Obrigatório
- Após sanitização: `[A-Z0-9]{6}`
- Remover caracteres especiais
- Após sanitização deve conter exatamente 6 caracteres

### Discount Value
- `discountValue >= 0.5`
- Não possui limitação de valor máximo

### Expiration Date
- Não pode ser anterior à data atual

### Published
- Tipo boolean
- Default: `true` (create)

### Integridade
- Nenhuma entidade inválida pode ser persistida

---

## 3. Criar Cupom

- Permite criar cupom válido
- Aplica:
    - sanitização de `code`
    - default `published = true`
    - `createdDate = now()`

---

## 4. Atualizar Cupom

- Cupom deve existir

---

## 5. Deletar Coupom

- Permite excluir cupom existente
- Não pode excluir duas vezes
- Não pode atualizar cupom deletado

---

## 6. Validação de Input

### Campos obrigatórios
- code
- description
- discountValue
- expirationDate