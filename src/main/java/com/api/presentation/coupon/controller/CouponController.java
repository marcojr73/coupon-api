package com.api.presentation.coupon.controller;

import com.api.application.coupon.dto.CouponUseCaseOutput;
import com.api.application.coupon.useCase.*;
import com.api.domain.coupon.entity.Coupon;
import com.api.presentation.coupon.dto.CouponRequestDto;
import com.api.presentation.coupon.dto.CouponUpdateRequestDto;
import com.api.presentation.shared.dto.DefaultResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/coupon")
@RequiredArgsConstructor
public class CouponController {

    private final CreateCouponUseCase createCouponUseCase;
    private final ListCouponUseCase listCouponUseCase;
    private final ShowCouponUseCase showCouponUseCase;
    private final UpdateCouponUseCase updateCouponUseCase;
    private final DeleteCouponUseCase deleteCouponUseCase;

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public DefaultResponseDto create(@Valid @RequestBody CouponRequestDto request) {
        Coupon coupon = Coupon.builder()
                .code(request.getCode())
                .description(request.getDescription())
                .discountValue(request.getDiscountValue())
                .expirationDate(request.getExpirationDate())
                .published(request.getPublished())
                .build();

        CouponUseCaseOutput output = createCouponUseCase.execute(coupon);

        return DefaultResponseDto.builder()
                .id(output.getId())
                .message("Created")
                .build();
    }

    @GetMapping("")
    public ResponseEntity<List<CouponUseCaseOutput>> list() {
        return ResponseEntity.ok(listCouponUseCase.execute());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CouponUseCaseOutput> show(@PathVariable String id) {
        return ResponseEntity.ok(showCouponUseCase.execute(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> update(@PathVariable String id, @Valid @RequestBody CouponUpdateRequestDto request) {
        Coupon coupon = Coupon.builder()
                .code(request.getCode())
                .description(request.getDescription())
                .discountValue(request.getDiscountValue())
                .expirationDate(request.getExpirationDate())
                .published(request.getPublished())
                .build();

        CouponUseCaseOutput output = updateCouponUseCase.execute(id, coupon);

        return ResponseEntity.ok(Map.of(
                "id", output.getId(),
                "message", "Ok"
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable String id) {
        deleteCouponUseCase.execute(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "id", id,
                "message", "Ok"
        ));
    }
}
