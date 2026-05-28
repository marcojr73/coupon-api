package com.api.application.coupon.dto;

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
public class CouponUseCaseOutput {
    private String id;
    private String code;
    private String description;
    private BigDecimal discountValue;
    private LocalDate expirationDate;
    private Boolean published;
    private LocalDate createdDate;
}
