package com.api.domain.coupon.repository;

import com.api.domain.coupon.entity.Coupon;

import java.util.List;
import java.util.Optional;

public interface CouponRepository {
    Coupon save(Coupon coupon);
    List<Coupon> findAll();
    Optional<Coupon> findById(String id);
    void delete(String id);
    Optional<Coupon> findByCode(String code);
}
