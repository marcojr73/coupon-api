package com.api.application.coupon.useCase;

import com.api.core.exception.RestException;
import com.api.domain.coupon.repository.CouponRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class DeleteCouponUseCase {

    private final CouponRepository couponRepository;

    public DeleteCouponUseCase(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    public void execute(String id) {
        if (couponRepository.findById(id).isEmpty()) {
            throw RestException.notFound("Coupon not found");
        }
        couponRepository.delete(id);
    }
}
