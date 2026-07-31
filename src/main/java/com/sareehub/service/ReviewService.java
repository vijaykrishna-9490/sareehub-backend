package com.sareehub.service;

import com.sareehub.dto.ReviewRequest;
import com.sareehub.entity.Product;
import com.sareehub.entity.Review;
import com.sareehub.entity.User;
import com.sareehub.exception.ApiException;
import com.sareehub.repository.ProductRepository;
import com.sareehub.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;

    public List<Review> getProductReviews(Long productId) {
        return reviewRepository.findByProductIdOrderByCreatedAtDesc(productId);
    }

    @Transactional
    public Review addReview(User user, ReviewRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ApiException("Product not found", HttpStatus.NOT_FOUND));

        if (request.getRating() == null || request.getRating() < 1 || request.getRating() > 5) {
            throw new ApiException("Rating must be between 1 and 5", HttpStatus.BAD_REQUEST);
        }

        Review review = Review.builder()
                .product(product)
                .user(user)
                .rating(request.getRating())
                .comment(request.getComment())
                .photoUrl(request.getPhotoUrl())
                .build();

        Review saved = reviewRepository.save(review);
        recalculateRating(product);
        return saved;
    }

    public void deleteReview(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ApiException("Review not found", HttpStatus.NOT_FOUND));
        Product product = review.getProduct();
        reviewRepository.delete(review);
        recalculateRating(product);
    }

    private void recalculateRating(Product product) {
        List<Review> reviews = reviewRepository.findByProductIdOrderByCreatedAtDesc(product.getId());
        double avg = reviews.stream().mapToInt(Review::getRating).average().orElse(0.0);
        product.setAvgRating(Math.round(avg * 10.0) / 10.0);
        product.setReviewCount(reviews.size());
        productRepository.save(product);
    }
}
