package com.api.application.coupon.useCase;

import com.api.application.coupon.dto.CouponUseCaseOutput;
import com.api.domain.coupon.entity.Coupon;
import com.api.domain.coupon.repository.CouponRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListCouponUseCaseTest {

    @Mock
    private CouponRepository couponRepository;

    @InjectMocks
    private ListCouponUseCase listCouponUseCase;

    @Test
    @DisplayName("Deve listar todos os cupons com sucesso")
    void shouldListAllCouponsSuccessfully() {
        Coupon coupon1 = Coupon.builder()
                .id("1")
                .code("SUMMER")
                .description("Summer Sale")
                .discountValue(new BigDecimal("10.0"))
                .expirationDate(LocalDate.now().plusDays(10))
                .published(true)
                .createdDate(LocalDate.now())
                .build();

        Coupon coupon2 = Coupon.builder()
                .id("2")
                .code("WINTER")
                .description("Winter Sale")
                .discountValue(new BigDecimal("20.0"))
                .expirationDate(LocalDate.now().plusDays(20))
                .published(false)
                .createdDate(LocalDate.now())
                .build();

        when(couponRepository.findAll()).thenReturn(Arrays.asList(coupon1, coupon2));

        List<CouponUseCaseOutput> result = listCouponUseCase.execute();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getCode()).isEqualTo("SUMMER");
        assertThat(result.get(1).getCode()).isEqualTo("WINTER");

        verify(couponRepository).findAll();
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não houver cupons")
    void shouldReturnEmptyListWhenNoCouponsFound() {
        when(couponRepository.findAll()).thenReturn(Arrays.asList());

        List<CouponUseCaseOutput> result = listCouponUseCase.execute();

        assertThat(result).isEmpty();

        verify(couponRepository).findAll();
    }
}
