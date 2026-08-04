package com.sareehub.controller;

import com.sareehub.entity.Coupon;
import com.sareehub.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    @GetMapping("/active")
    public List<Coupon> getActiveCoupons() {
        return couponService.getActiveCoupons();
    }

    @PostMapping("/validate")
    public Coupon validate(@RequestBody Map<String, Object> body) {
        String code = (String) body.get("code");
        double orderValue = Double.parseDouble(body.get("orderValue").toString());
        return couponService.validate(code, orderValue);
    }
}
