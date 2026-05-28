package com.api.domain.coupon.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class CouponTest {

    private static final String VALID_CODE = "JEDI10";
    private static final String DESCRIPTION = "Cupom de desconto para sabre de luz";
    private static final BigDecimal DISCOUNT_VALUE = new BigDecimal("10.50");
    private static final LocalDate EXPIRATION_DATE = LocalDate.now().plusDays(10);

    @Test
    @DisplayName("Deve sanitizar código removendo caracteres especiais e convertendo para maiúsculo")
    void shouldSanitizeCodeRemovingSpecialCharactersAndConvertingToUpperCase() {
        Coupon coupon = Coupon.builder()
                .code("ab-c_1@2#3")
                .build();

        coupon.sanitizeCode();

        assertThat(coupon.getCode()).isEqualTo("ABC123");
    }

    @Test
    @DisplayName("Não deve alterar código quando for nulo ao sanitizar")
    void shouldNotChangeCodeWhenCodeIsNullOnSanitize() {
        Coupon coupon = Coupon.builder()
                .code(null)
                .build();

        coupon.sanitizeCode();

        assertThat(coupon.getCode()).isNull();
    }

    @Test
    @DisplayName("Deve retornar true quando cupom for válido")
    void shouldReturnTrueWhenCouponIsValid() {
        Coupon coupon = validCoupon();

        boolean result = coupon.isValid();

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Deve retornar true quando data de expiração for hoje")
    void shouldReturnTrueWhenExpirationDateIsToday() {
        Coupon coupon = validCoupon();
        coupon.setExpirationDate(LocalDate.now());

        boolean result = coupon.isValid();

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Deve retornar false quando código for nulo")
    void shouldReturnFalseWhenCodeIsNull() {
        Coupon coupon = validCoupon();
        coupon.setCode(null);

        boolean result = coupon.isValid();

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Deve retornar false quando código tiver menos de 6 caracteres")
    void shouldReturnFalseWhenCodeHasLessThanSixCharacters() {
        Coupon coupon = validCoupon();
        coupon.setCode("ABC12");

        boolean result = coupon.isValid();

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Deve retornar false quando código tiver mais de 6 caracteres")
    void shouldReturnFalseWhenCodeHasMoreThanSixCharacters() {
        Coupon coupon = validCoupon();
        coupon.setCode("ABC1234");

        boolean result = coupon.isValid();

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Deve retornar false quando descrição for nula")
    void shouldReturnFalseWhenDescriptionIsNull() {
        Coupon coupon = validCoupon();
        coupon.setDescription(null);

        boolean result = coupon.isValid();

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Deve retornar false quando descrição for vazia")
    void shouldReturnFalseWhenDescriptionIsEmpty() {
        Coupon coupon = validCoupon();
        coupon.setDescription("");

        boolean result = coupon.isValid();

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Deve retornar false quando valor de desconto for nulo")
    void shouldReturnFalseWhenDiscountValueIsNull() {
        Coupon coupon = validCoupon();
        coupon.setDiscountValue(null);

        boolean result = coupon.isValid();

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Deve retornar false quando valor de desconto for menor que 0.5")
    void shouldReturnFalseWhenDiscountValueIsLessThanMinimum() {
        Coupon coupon = validCoupon();
        coupon.setDiscountValue(new BigDecimal("0.49"));

        boolean result = coupon.isValid();

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Deve retornar true quando valor de desconto for igual a 0.5")
    void shouldReturnTrueWhenDiscountValueIsEqualToMinimum() {
        Coupon coupon = validCoupon();
        coupon.setDiscountValue(new BigDecimal("0.5"));

        boolean result = coupon.isValid();

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Deve retornar false quando data de expiração for nula")
    void shouldReturnFalseWhenExpirationDateIsNull() {
        Coupon coupon = validCoupon();
        coupon.setExpirationDate(null);

        boolean result = coupon.isValid();

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Deve retornar false quando data de expiração estiver no passado")
    void shouldReturnFalseWhenExpirationDateIsInThePast() {
        Coupon coupon = validCoupon();
        coupon.setExpirationDate(LocalDate.now().minusDays(1));

        boolean result = coupon.isValid();

        assertThat(result).isFalse();
    }

    private Coupon validCoupon() {
        return Coupon.builder()
                .code(VALID_CODE)
                .description(DESCRIPTION)
                .discountValue(DISCOUNT_VALUE)
                .expirationDate(EXPIRATION_DATE)
                .published(true)
                .build();
    }
}
