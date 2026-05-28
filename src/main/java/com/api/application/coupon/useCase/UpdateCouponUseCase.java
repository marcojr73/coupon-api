package com.api.application.coupon.useCase;

import com.api.application.coupon.dto.CouponUseCaseOutput;
import com.api.core.exception.RestException;
import com.api.domain.coupon.entity.Coupon;
import com.api.domain.coupon.repository.CouponRepository;
import org.springframework.stereotype.Service;

@Service
public class UpdateCouponUseCase {

    private final CouponRepository couponRepository;

    public UpdateCouponUseCase(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    public CouponUseCaseOutput execute(String id, Coupon updateInfo) {
        Coupon existingCoupon = couponRepository.findById(id)
                .orElseThrow(() -> RestException.notFound("Coupon not found"));

        if (updateInfo.getCode() != null) {
            updateInfo.sanitizeCode();
            if (!updateInfo.getCode().equals(existingCoupon.getCode()) &&
                couponRepository.findByCode(updateInfo.getCode()).isPresent()) {
                throw RestException.badRequest("Coupon code already exists");
            }
            existingCoupon.setCode(updateInfo.getCode());
        }

        if (updateInfo.getDescription() != null) {
            existingCoupon.setDescription(updateInfo.getDescription());
        }

        if (updateInfo.getDiscountValue() != null) {
            existingCoupon.setDiscountValue(updateInfo.getDiscountValue());
        }

        if (updateInfo.getExpirationDate() != null) {
            existingCoupon.setExpirationDate(updateInfo.getExpirationDate());
        }

        if (updateInfo.getPublished() != null) {
            existingCoupon.setPublished(updateInfo.getPublished());
        }

        if (!existingCoupon.isValid()) {
            throw RestException.badRequest("Invalid coupon data after update");
        }

        Coupon savedCoupon = couponRepository.save(existingCoupon);

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
