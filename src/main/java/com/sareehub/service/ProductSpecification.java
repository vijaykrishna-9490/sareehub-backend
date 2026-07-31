package com.sareehub.service;

import com.sareehub.entity.Product;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ProductSpecification {

    public static Specification<Product> filter(
            String keyword, Long categoryId, String color, String fabric,
            String occasion, BigDecimal minPrice, BigDecimal maxPrice,
            Double minRating, Boolean inStock
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.isTrue(root.get("active")));

            if (keyword != null && !keyword.isBlank()) {
                String like = "%" + keyword.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("name")), like),
                        cb.like(cb.lower(root.get("description")), like),
                        cb.like(cb.lower(root.get("brand")), like)
                ));
            }
            if (categoryId != null) {
                predicates.add(cb.equal(root.get("category").get("id"), categoryId));
            }
            if (color != null && !color.isBlank()) {
                predicates.add(cb.equal(cb.lower(root.get("color")), color.toLowerCase()));
            }
            if (fabric != null && !fabric.isBlank()) {
                predicates.add(cb.equal(cb.lower(root.get("fabric")), fabric.toLowerCase()));
            }
            if (occasion != null && !occasion.isBlank()) {
                predicates.add(cb.equal(cb.lower(root.get("occasion")), occasion.toLowerCase()));
            }
            if (minPrice != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), minPrice));
            }
            if (maxPrice != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), maxPrice));
            }
            if (minRating != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("avgRating"), minRating));
            }
            if (Boolean.TRUE.equals(inStock)) {
                predicates.add(cb.greaterThan(root.get("stock"), 0));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
