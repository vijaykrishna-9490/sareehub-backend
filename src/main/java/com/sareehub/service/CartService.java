package com.sareehub.service;

import com.sareehub.dto.CartItemRequest;
import com.sareehub.entity.CartItem;
import com.sareehub.entity.Product;
import com.sareehub.entity.User;
import com.sareehub.exception.ApiException;
import com.sareehub.repository.CartItemRepository;
import com.sareehub.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public List<CartItem> getCart(User user) {
        return cartItemRepository.findByUser(user);
    }

    public CartItem addToCart(User user, CartItemRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ApiException("Product not found", HttpStatus.NOT_FOUND));

        CartItem item = cartItemRepository.findByUserAndProductId(user, product.getId())
                .orElse(CartItem.builder().user(user).product(product).quantity(0).build());

        int newQty = item.getQuantity() + (request.getQuantity() != null ? request.getQuantity() : 1);
        if (newQty > product.getStock()) {
            throw new ApiException("Requested quantity exceeds available stock", HttpStatus.BAD_REQUEST);
        }
        item.setQuantity(newQty);
        return cartItemRepository.save(item);
    }

    public CartItem updateQuantity(User user, Long itemId, Integer quantity) {
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ApiException("Cart item not found", HttpStatus.NOT_FOUND));
        if (!item.getUser().getId().equals(user.getId())) {
            throw new ApiException("Not authorized", HttpStatus.FORBIDDEN);
        }
        if (quantity < 1) {
            throw new ApiException("Quantity must be at least 1", HttpStatus.BAD_REQUEST);
        }
        if (quantity > item.getProduct().getStock()) {
            throw new ApiException("Requested quantity exceeds available stock", HttpStatus.BAD_REQUEST);
        }
        item.setQuantity(quantity);
        return cartItemRepository.save(item);
    }

    public void removeItem(User user, Long itemId) {
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ApiException("Cart item not found", HttpStatus.NOT_FOUND));
        if (!item.getUser().getId().equals(user.getId())) {
            throw new ApiException("Not authorized", HttpStatus.FORBIDDEN);
        }
        cartItemRepository.delete(item);
    }

    public void clearCart(User user) {
        cartItemRepository.deleteByUser(user);
    }
}
