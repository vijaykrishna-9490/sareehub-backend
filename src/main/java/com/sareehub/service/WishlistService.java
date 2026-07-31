package com.sareehub.service;

import com.sareehub.entity.Product;
import com.sareehub.entity.User;
import com.sareehub.entity.WishlistItem;
import com.sareehub.exception.ApiException;
import com.sareehub.repository.ProductRepository;
import com.sareehub.repository.WishlistItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WishlistService {

    private final WishlistItemRepository wishlistItemRepository;
    private final ProductRepository productRepository;

    public List<WishlistItem> getWishlist(User user) {
        return wishlistItemRepository.findByUser(user);
    }

    public WishlistItem addItem(User user, Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ApiException("Product not found", HttpStatus.NOT_FOUND));

        return wishlistItemRepository.findByUserAndProductId(user, productId)
                .orElseGet(() -> wishlistItemRepository.save(
                        WishlistItem.builder().user(user).product(product).build()));
    }

    public void removeItem(User user, Long productId) {
        wishlistItemRepository.deleteByUserAndProductId(user, productId);
    }
}
