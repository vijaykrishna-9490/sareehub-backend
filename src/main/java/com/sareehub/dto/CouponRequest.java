package com.sareehub.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class CouponRequest {
    private String code;
    private Double discountPercent;
    private Double maxDiscountAmount;
    private Double minOrderValue;
    private LocalDate expiryDate;
    private boolean active = true;
}
