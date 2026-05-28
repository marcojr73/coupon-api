package com.api.application.coupon.useCase;

import com.api.application.coupon.dto.CouponUseCaseOutput;
import com.api.domain.coupon.repository.CouponRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ListCouponUseCase {

    private final CouponRepository couponRepository;

    public ListCouponUseCase(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    public List<CouponUseCaseOutput> execute() {
        return couponRepository.findAll().stream()
                .map(coupon -> CouponUseCaseOutput.builder()
                        .id(coupon.getId())
                        .code(coupon.getCode())
                        .description(coupon.getDescription())
                        .discountValue(coupon.getDiscountValue())
                        .expirationDate(coupon.getExpirationDate())
                        .published(coupon.getPublished())
                        .createdDate(coupon.getCreatedDate())
                        .build())
                .collect(Collectors.toList());
    }
}
