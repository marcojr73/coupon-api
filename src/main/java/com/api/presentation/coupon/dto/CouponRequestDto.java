package com.api.presentation.coupon.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class CouponRequestDto {

    @NotBlank(message = "Code is required")
    private String code;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Discount value is required")
    @DecimalMin(value = "0.5", message = "Discount value must be at least 0.5")
    private BigDecimal discountValue;

    @NotNull(message = "Expiration date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @FutureOrPresent(message = "Expiration date must be in the present or future")
    private LocalDate expirationDate;

    private Boolean published;
}
