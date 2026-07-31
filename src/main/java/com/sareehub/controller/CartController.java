package com.sareehub.controller;

import com.sareehub.dto.CartItemRequest;
import com.sareehub.entity.CartItem;
import com.sareehub.entity.User;
import com.sareehub.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public List<CartItem> getCart(@AuthenticationPrincipal User user) {
        return cartService.getCart(user);
    }

    @PostMapping
    public CartItem addToCart(@AuthenticationPrincipal User user, @RequestBody CartItemRequest request) {
        return cartService.addToCart(user, request);
    }

    @PutMapping("/{itemId}")
    public CartItem updateQuantity(@AuthenticationPrincipal User user, @PathVariable Long itemId,
                                    @RequestBody Map<String, Integer> body) {
        return cartService.updateQuantity(user, itemId, body.get("quantity"));
    }

    @DeleteMapping("/{itemId}")
    public void removeItem(@AuthenticationPrincipal User user, @PathVariable Long itemId) {
        cartService.removeItem(user, itemId);
    }

    @DeleteMapping
    public void clearCart(@AuthenticationPrincipal User user) {
        cartService.clearCart(user);
    }
}
