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
class UpdateCouponUseCaseTest {

    @Mock
    private CouponRepository couponRepository;

    @InjectMocks
    private UpdateCouponUseCase updateCouponUseCase;

    @Test
    @DisplayName("Deve atualizar um cupom com sucesso")
    void shouldUpdateCouponSuccessfully() {
        String id = "coupon-123";
        Coupon existingCoupon = Coupon.builder()
                .id(id)
                .code("OLDCOD")
                .description("Old Description")
                .discountValue(new BigDecimal("10.0"))
                .expirationDate(LocalDate.now().plusDays(10))
                .published(true)
                .createdDate(LocalDate.now())
                .build();

        Coupon updateInfo = Coupon.builder()
                .code("NEWCOD")
                .description("New Description")
                .discountValue(new BigDecimal("15.0"))
                .expirationDate(LocalDate.now().plusDays(20))
                .published(false)
                .build();

        when(couponRepository.findById(id)).thenReturn(Optional.of(existingCoupon));
        when(couponRepository.findByCode("NEWCOD")).thenReturn(Optional.empty());
        when(couponRepository.save(any(Coupon.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CouponUseCaseOutput result = updateCouponUseCase.execute(id, updateInfo);

        assertThat(result.getCode()).isEqualTo("NEWCOD");
        assertThat(result.getDescription()).isEqualTo("New Description");
        assertThat(result.getDiscountValue()).isEqualTo(new BigDecimal("15.0"));
        assertThat(result.getPublished()).isFalse();

        verify(couponRepository).findById(id);
        verify(couponRepository).findByCode("NEWCOD");
        verify(couponRepository).save(any(Coupon.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar cupom inexistente")
    void shouldThrowExceptionWhenCouponNotFound() {
        String id = "non-existent";
        when(couponRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> updateCouponUseCase.execute(id, new Coupon()))
                .isInstanceOf(RestException.class)
                .hasMessage("Coupon not found")
                .extracting(e -> ((RestException) e).getStatus())
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Deve lançar exceção quando o novo código já existe em outro cupom")
    void shouldThrowExceptionWhenNewCodeAlreadyExists() {
        String id = "coupon-123";
        Coupon existingCoupon = Coupon.builder()
                .id(id)
                .code("OLDCOD")
                .build();

        Coupon updateInfo = Coupon.builder()
                .code("EXISTI")
                .build();

        when(couponRepository.findById(id)).thenReturn(Optional.of(existingCoupon));
        when(couponRepository.findByCode("EXISTI")).thenReturn(Optional.of(new Coupon()));

        assertThatThrownBy(() -> updateCouponUseCase.execute(id, updateInfo))
                .isInstanceOf(RestException.class)
                .hasMessage("Coupon code already exists")
                .extracting(e -> ((RestException) e).getStatus())
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("Deve lançar exceção quando dados atualizados forem inválidos")
    void shouldThrowExceptionWhenUpdatedDataIsInvalid() {
        String id = "coupon-123";
        Coupon existingCoupon = Coupon.builder()
                .id(id)
                .code("VALIDO")
                .description("Desc")
                .discountValue(new BigDecimal("10.0"))
                .expirationDate(LocalDate.now().plusDays(10))
                .build();

        Coupon updateInfo = Coupon.builder()
                .discountValue(new BigDecimal("0.1")) // Invalid value
                .build();

        when(couponRepository.findById(id)).thenReturn(Optional.of(existingCoupon));

        assertThatThrownBy(() -> updateCouponUseCase.execute(id, updateInfo))
                .isInstanceOf(RestException.class)
                .hasMessage("Invalid coupon data after update")
                .extracting(e -> ((RestException) e).getStatus())
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
