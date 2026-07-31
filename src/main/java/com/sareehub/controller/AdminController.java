package com.sareehub.controller;

import com.sareehub.dto.CouponRequest;
import com.sareehub.dto.ProductRequest;
import com.sareehub.entity.*;
import com.sareehub.exception.ApiException;
import com.sareehub.repository.UserRepository;
import com.sareehub.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final OrderService orderService;
    private final CouponService couponService;
    private final UserRepository userRepository;

    // ---- Products ----
    @PostMapping("/products")
    public Product createProduct(@RequestBody ProductRequest request) {
        return productService.create(request);
    }

    @PutMapping("/products/{id}")
    public Product updateProduct(@PathVariable Long id, @RequestBody ProductRequest request) {
        return productService.update(id, request);
    }

    @DeleteMapping("/products/{id}")
    public void deleteProduct(@PathVariable Long id) {
        productService.delete(id);
    }

    // ---- Categories ----
    @PostMapping("/categories")
    public Category createCategory(@RequestBody Category category) {
        return categoryService.create(category);
    }

    @PutMapping("/categories/{id}")
    public Category updateCategory(@PathVariable Long id, @RequestBody Category category) {
        return categoryService.update(id, category);
    }

    @DeleteMapping("/categories/{id}")
    public void deleteCategory(@PathVariable Long id) {
        categoryService.delete(id);
    }

    // ---- Orders ----
    @GetMapping("/orders")
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    @PutMapping("/orders/{id}/status")
    public Order updateOrderStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return orderService.updateStatus(id, OrderStatus.valueOf(body.get("status")));
    }

    // ---- Users ----
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @PutMapping("/users/{id}/block")
    public User toggleBlockUser(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ApiException("User not found", HttpStatus.NOT_FOUND));
        user.setBlocked(!user.isBlocked());
        return userRepository.save(user);
    }

    // ---- Coupons ----
    @GetMapping("/coupons")
    public List<Coupon> getAllCoupons() {
        return couponService.getAll();
    }

    @PostMapping("/coupons")
    public Coupon createCoupon(@RequestBody CouponRequest request) {
        return couponService.create(request);
    }

    @PutMapping("/coupons/{id}/toggle")
    public Coupon toggleCoupon(@PathVariable Long id) {
        return couponService.toggleActive(id);
    }

    @DeleteMapping("/coupons/{id}")
    public void deleteCoupon(@PathVariable Long id) {
        couponService.delete(id);
    }
}
