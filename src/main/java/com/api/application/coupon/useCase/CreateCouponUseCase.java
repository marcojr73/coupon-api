package com.api.application.coupon.useCase;

import com.api.application.coupon.dto.CouponUseCaseOutput;
import com.api.core.exception.RestException;
import com.api.domain.coupon.entity.Coupon;
import com.api.domain.coupon.repository.CouponRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
public class CreateCouponUseCase {

    private final CouponRepository couponRepository;

    public CreateCouponUseCase(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    public CouponUseCaseOutput execute(Coupon couponInput) {
        couponInput.sanitizeCode();
        
        if (couponInput.getPublished() == null) {
            couponInput.setPublished(true);
        }
        couponInput.setCreatedDate(LocalDate.now());

        if (!couponInput.isValid()) {
            throw RestException.badRequest("Invalid coupon data");
        }

        if (couponRepository.findByCode(couponInput.getCode()).isPresent()) {
            throw RestException.badRequest("Coupon code already exists");
        }

        couponInput.setId(UUID.randomUUID().toString());
        Coupon savedCoupon = couponRepository.save(couponInput);

        return CouponUseCaseOutput.builder()
                .id(savedCoupon.getId())
                .code(savedCoupon.getCode())
                .description(savedCoupon.getDescription())
                .discountValue(savedCoupon.getDiscountValue())
                .expirationDate(savedCoupon.getExpirationDate())
                .published(savedCoupon.getPublished())
                .createdDate(savedCoupon.getCreatedDate())
                .build();
    }
}
