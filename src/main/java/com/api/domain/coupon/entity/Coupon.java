package com.api.domain.coupon.entity;

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
public class Coupon {
    private String id;
    private String code;
    private String description;
    private BigDecimal discountValue;
    private LocalDate expirationDate;
    private Boolean published;
    private LocalDate createdDate;

    public void sanitizeCode() {
        if (this.code != null) {
            this.code = this.code.replaceAll("[^a-zA-Z0-9]", "").toUpperCase();
        }
    }

    public boolean isValid() {
        if (code == null || code.length() != 6) return false;
        if (description == null || description.isEmpty()) return false;
        if (discountValue == null || discountValue.compareTo(new BigDecimal("0.5")) < 0) return false;
        if (expirationDate == null || expirationDate.isBefore(LocalDate.now())) return false;
        return true;
    }
}
