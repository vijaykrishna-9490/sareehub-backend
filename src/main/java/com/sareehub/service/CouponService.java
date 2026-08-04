package com.sareehub.service;

import com.sareehub.dto.CouponRequest;
import com.sareehub.entity.Coupon;
import com.sareehub.exception.ApiException;
import com.sareehub.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;

    public List<Coupon> getAll() {
        return couponRepository.findAll();
    }

    public Coupon create(CouponRequest request) {
        Coupon coupon = Coupon.builder()
                .code(request.getCode().toUpperCase())
                .discountPercent(request.getDiscountPercent())
                .maxDiscountAmount(request.getMaxDiscountAmount())
                .minOrderValue(request.getMinOrderValue())
                .expiryDate(request.getExpiryDate())
                .active(request.isActive())
                .build();
        return couponRepository.save(coupon);
    }

    public Coupon toggleActive(Long id) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new ApiException("Coupon not found", HttpStatus.NOT_FOUND));
        coupon.setActive(!coupon.isActive());
        return couponRepository.save(coupon);
    }

    public void delete(Long id) {
        couponRepository.deleteById(id);
    }

    public Coupon validate(String code, double orderValue) {
        Coupon coupon = couponRepository.findByCodeIgnoreCase(code)
                .orElseThrow(() -> new ApiException("Invalid coupon code", HttpStatus.BAD_REQUEST));

        if (!coupon.isActive()) {
            throw new ApiException("Coupon is not active", HttpStatus.BAD_REQUEST);
        }
        if (coupon.getExpiryDate() != null && coupon.getExpiryDate().isBefore(LocalDate.now())) {
            throw new ApiException("Coupon has expired", HttpStatus.BAD_REQUEST);
        }
        if (coupon.getMinOrderValue() != null && orderValue < coupon.getMinOrderValue()) {
            throw new ApiException("Minimum order value not met for this coupon", HttpStatus.BAD_REQUEST);
        }
        return coupon;
    }

    public double calculateDiscount(Coupon coupon, double orderValue) {
        double discount = orderValue * (coupon.getDiscountPercent() / 100.0);
        if (coupon.getMaxDiscountAmount() != null) {
            discount = Math.min(discount, coupon.getMaxDiscountAmount());
        }
        return discount;
    }
    public List<Coupon> getActiveCoupons() {
    return couponRepository.findActiveCoupons(LocalDate.now());
}
}
