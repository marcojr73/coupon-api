package com.api.presentation.coupon.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponUpdateRequestDto {
    private String code;
    private String description;

    @DecimalMin(value = "0.5", message = "Discount value must be at least 0.5")
    private BigDecimal discountValue;

    @FutureOrPresent(message = "Expiration date must be in the present or future")
    private LocalDate expirationDate;

    private Boolean published;
}
