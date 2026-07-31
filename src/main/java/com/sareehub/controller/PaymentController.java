package com.sareehub.controller;

import com.sareehub.entity.Order;
import com.sareehub.entity.User;
import com.sareehub.service.RazorpayService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments/razorpay")
@RequiredArgsConstructor
public class PaymentController {

    private final RazorpayService razorpayService;

    @PostMapping("/create-order")
    public Map<String, Object> createOrder(@AuthenticationPrincipal User user, @RequestBody Map<String, Long> body) {
        return razorpayService.createRazorpayOrder(user, body.get("orderId"));
    }

    @PostMapping("/verify")
    public Order verify(@AuthenticationPrincipal User user, @RequestBody Map<String, String> body) {
        Long orderId = Long.parseLong(body.get("orderId"));
        return razorpayService.verifyPayment(
                user,
                orderId,
                body.get("razorpayOrderId"),
                body.get("razorpayPaymentId"),
                body.get("razorpaySignature")
        );
    }
}
