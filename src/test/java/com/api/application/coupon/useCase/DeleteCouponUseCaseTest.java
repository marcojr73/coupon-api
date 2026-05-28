package com.api.application.coupon.useCase;

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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteCouponUseCaseTest {

    @Mock
    private CouponRepository couponRepository;

    @InjectMocks
    private DeleteCouponUseCase deleteCouponUseCase;

    @Test
    @DisplayName("Deve deletar um cupom com sucesso")
    void shouldDeleteCouponSuccessfully() {
        String id = "coupon-123";
        when(couponRepository.findById(id)).thenReturn(Optional.of(new Coupon()));

        deleteCouponUseCase.execute(id);

        verify(couponRepository).findById(id);
        verify(couponRepository).delete(id);
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar cupom inexistente")
    void shouldThrowExceptionWhenCouponNotFound() {
        String id = "non-existent";
        when(couponRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> deleteCouponUseCase.execute(id))
                .isInstanceOf(RestException.class)
                .hasMessage("Coupon not found")
                .extracting(e -> ((RestException) e).getStatus())
                .isEqualTo(HttpStatus.NOT_FOUND);

        verify(couponRepository).findById(id);
        verify(couponRepository, never()).delete(anyString());
    }
}
