package com.sareehub.controller;

import com.sareehub.dto.CheckoutRequest;
import com.sareehub.entity.Order;
import com.sareehub.entity.User;
import com.sareehub.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/checkout")
    public Order checkout(@AuthenticationPrincipal User user, @RequestBody CheckoutRequest request) {
        return orderService.checkout(user, request);
    }

    @GetMapping
    public List<Order> myOrders(@AuthenticationPrincipal User user) {
        return orderService.getUserOrders(user);
    }

    @GetMapping("/{id}")
    public Order getOrder(@PathVariable Long id) {
        return orderService.getOrder(id);
    }

    @PostMapping("/{id}/cancel")
    public Order cancel(@AuthenticationPrincipal User user, @PathVariable Long id) {
        return orderService.cancelOrder(user, id);
    }
}
