package com.sareehub.controller;

import com.sareehub.dto.ReviewRequest;
import com.sareehub.entity.Review;
import com.sareehub.entity.User;
import com.sareehub.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/product/{productId}")
    public List<Review> getProductReviews(@PathVariable Long productId) {
        return reviewService.getProductReviews(productId);
    }

    @PostMapping
    public Review addReview(@AuthenticationPrincipal User user, @RequestBody ReviewRequest request) {
        return reviewService.addReview(user, request);
    }

    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
    }
}
