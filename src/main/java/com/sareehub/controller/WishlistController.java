package com.sareehub.controller;

import com.sareehub.entity.User;
import com.sareehub.entity.WishlistItem;
import com.sareehub.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    @GetMapping
    public List<WishlistItem> getWishlist(@AuthenticationPrincipal User user) {
        return wishlistService.getWishlist(user);
    }

    @PostMapping("/{productId}")
    public WishlistItem addItem(@AuthenticationPrincipal User user, @PathVariable Long productId) {
        return wishlistService.addItem(user, productId);
    }

    @DeleteMapping("/{productId}")
    public void removeItem(@AuthenticationPrincipal User user, @PathVariable Long productId) {
        wishlistService.removeItem(user, productId);
    }
}
