package com.api.infrastructure.coupon.dao;

import com.api.domain.coupon.entity.Coupon;
import com.api.domain.coupon.repository.CouponRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CouponDao implements CouponRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Coupon> couponRowMapper = (rs, rowNum) -> Coupon.builder()
            .id(rs.getString("id"))
            .code(rs.getString("code"))
            .description(rs.getString("description"))
            .discountValue(rs.getBigDecimal("discount_value"))
            .expirationDate(rs.getDate("expiration_date").toLocalDate())
            .published(rs.getBoolean("published"))
            .createdDate(rs.getDate("created_date").toLocalDate())
            .build();

    public CouponDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Coupon save(Coupon coupon) {
        Optional<Coupon> existing = findById(coupon.getId());
        if (existing.isPresent()) {
            String sql = "UPDATE coupons SET code = ?, description = ?, discount_value = ?, expiration_date = ?, published = ? WHERE id = ?";
            jdbcTemplate.update(sql, coupon.getCode(), coupon.getDescription(), coupon.getDiscountValue(), coupon.getExpirationDate(), coupon.getPublished(), coupon.getId());
        } else {
            String sql = "INSERT INTO coupons (id, code, description, discount_value, expiration_date, published, created_date) VALUES (?, ?, ?, ?, ?, ?, ?)";
            jdbcTemplate.update(sql, coupon.getId(), coupon.getCode(), coupon.getDescription(), coupon.getDiscountValue(), coupon.getExpirationDate(), coupon.getPublished(), coupon.getCreatedDate());
        }
        return findById(coupon.getId()).orElse(coupon);
    }

    @Override
    public List<Coupon> findAll() {
        String sql = "SELECT id, code, description, discount_value, expiration_date, published, created_date FROM coupons";
        return jdbcTemplate.query(sql, couponRowMapper);
    }

    @Override
    public Optional<Coupon> findById(String id) {
        if (id == null) return Optional.empty();
        String sql = "SELECT id, code, description, discount_value, expiration_date, published, created_date FROM coupons WHERE id = ?";
        return jdbcTemplate.query(sql, couponRowMapper, id).stream().findFirst();
    }

    @Override
    public void delete(String id) {
        String sql = "DELETE FROM coupons WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public Optional<Coupon> findByCode(String code) {
        String sql = "SELECT id, code, description, discount_value, expiration_date, published, created_date FROM coupons WHERE code = ?";
        return jdbcTemplate.query(sql, couponRowMapper, code).stream().findFirst();
    }
}
