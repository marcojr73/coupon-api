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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShowCouponUseCaseTest {

    @Mock
    private CouponRepository couponRepository;

    @InjectMocks
    private ShowCouponUseCase showCouponUseCase;

    @Test
    @DisplayName("Deve mostrar detalhes de um cupom com sucesso")
    void shouldShowCouponDetailsSuccessfully() {
        String id = "coupon-123";
        Coupon coupon = Coupon.builder()
                .id(id)
                .code("SUMMER")
                .description("Summer Sale")
                .discountValue(new BigDecimal("10.0"))
                .expirationDate(LocalDate.now().plusDays(10))
                .published(true)
                .createdDate(LocalDate.now())
                .build();

        when(couponRepository.findById(id)).thenReturn(Optional.of(coupon));

        CouponUseCaseOutput result = showCouponUseCase.execute(id);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getCode()).isEqualTo("SUMMER");

        verify(couponRepository).findById(id);
    }

    @Test
    @DisplayName("Deve lançar exceção ao mostrar cupom inexistente")
    void shouldThrowExceptionWhenCouponNotFound() {
        String id = "non-existent";
        when(couponRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> showCouponUseCase.execute(id))
                .isInstanceOf(RestException.class)
                .hasMessage("Coupon not found")
                .extracting(e -> ((RestException) e).getStatus())
                .isEqualTo(HttpStatus.NOT_FOUND);

        verify(couponRepository).findById(id);
    }
}
