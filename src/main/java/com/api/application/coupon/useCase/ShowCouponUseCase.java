package com.api.application.coupon.useCase;

import com.api.application.coupon.dto.CouponUseCaseOutput;
import com.api.core.exception.RestException;
import com.api.domain.coupon.entity.Coupon;
import com.api.domain.coupon.repository.CouponRepository;
import org.springframework.stereotype.Service;

@Service
public class ShowCouponUseCase {

    private final CouponRepository couponRepository;

    public ShowCouponUseCase(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    public CouponUseCaseOutput execute(String id) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> RestException.notFound("Coupon not found"));

        return CouponUseCaseOutput.builder()
                .id(coupon.getId())
                .code(coupon.getCode())
                .description(coupon.getDescription())
                .discountValue(coupon.getDiscountValue())
                .expirationDate(coupon.getExpirationDate())
                .published(coupon.getPublished())
                .createdDate(coupon.getCreatedDate())
                .build();
    }
}
