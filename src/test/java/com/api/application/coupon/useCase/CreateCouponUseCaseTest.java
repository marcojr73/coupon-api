package com.api.application.coupon.useCase;

import com.api.application.coupon.dto.CouponUseCaseOutput;
import com.api.core.exception.RestException;
import com.api.domain.coupon.entity.Coupon;
import com.api.domain.coupon.repository.CouponRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateCouponUseCaseTest {

    @Mock
    private CouponRepository couponRepository;

    @InjectMocks
    private CreateCouponUseCase createCouponUseCase;

    @Test
    @DisplayName("Deve criar um cupom com sucesso")
    void shouldCreateCouponSuccessfully() {
        Coupon input = Coupon.builder()
                .code("SUMMER")
                .description("Summer Sale")
                .discountValue(new BigDecimal("10.0"))
                .expirationDate(LocalDate.now().plusDays(30))
                .build();

        when(couponRepository.findByCode("SUMMER")).thenReturn(Optional.empty());
        when(couponRepository.save(any(Coupon.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CouponUseCaseOutput output = createCouponUseCase.execute(input);

        assertThat(output).isNotNull();
        assertThat(output.getId()).isNotNull();
        assertThat(output.getCode()).isEqualTo("SUMMER");
        assertThat(output.getPublished()).isTrue();
        assertThat(output.getCreatedDate()).isEqualTo(LocalDate.now());

        verify(couponRepository).findByCode("SUMMER");
        verify(couponRepository).save(any(Coupon.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar cupom com dados inválidos")
    void shouldThrowExceptionWhenCouponIsInvalid() {
        Coupon input = Coupon.builder()
                .code("SUM") // Short code (isValid requires 6 chars)
                .description("Short")
                .discountValue(new BigDecimal("0.1"))
                .expirationDate(LocalDate.now().minusDays(1))
                .build();

        assertThatThrownBy(() -> createCouponUseCase.execute(input))
                .isInstanceOf(RestException.class)
                .hasMessage("Invalid coupon data")
                .extracting(e -> ((RestException) e).getStatus())
                .isEqualTo(HttpStatus.BAD_REQUEST);

        verify(couponRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando código do cupom já existe")
    void shouldThrowExceptionWhenCouponCodeAlreadyExists() {
        Coupon input = Coupon.builder()
                .code("SUMMER")
                .description("Summer Sale")
                .discountValue(new BigDecimal("10.0"))
                .expirationDate(LocalDate.now().plusDays(30))
                .build();

        when(couponRepository.findByCode("SUMMER")).thenReturn(Optional.of(new Coupon()));

        assertThatThrownBy(() -> createCouponUseCase.execute(input))
                .isInstanceOf(RestException.class)
                .hasMessage("Coupon code already exists")
                .extracting(e -> ((RestException) e).getStatus())
                .isEqualTo(HttpStatus.BAD_REQUEST);

        verify(couponRepository, never()).save(any());
    }
}
