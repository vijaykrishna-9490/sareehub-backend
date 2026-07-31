package com.sareehub.dto;

import com.sareehub.entity.PaymentMethod;
import lombok.Data;

@Data
public class CheckoutRequest {
    private Long addressId;
    private PaymentMethod paymentMethod;
    private String couponCode;
}
