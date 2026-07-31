package com.sareehub.service;

import com.sareehub.dto.ProductRequest;
import com.sareehub.entity.Category;
import com.sareehub.entity.Product;
import com.sareehub.entity.ProductImage;
import com.sareehub.exception.ApiException;
import com.sareehub.repository.CategoryRepository;
import com.sareehub.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public Page<Product> search(String keyword, Long categoryId, String color, String fabric,
                                 String occasion, BigDecimal minPrice, BigDecimal maxPrice,
                                 Double minRating, Boolean inStock,
                                 String sortBy, int page, int size) {

        Sort sort = switch (sortBy == null ? "" : sortBy) {
            case "priceLowToHigh" -> Sort.by("price").ascending();
            case "priceHighToLow" -> Sort.by("price").descending();
            case "newest" -> Sort.by("createdAt").descending();
            case "topRated" -> Sort.by("avgRating").descending();
            default -> Sort.by("createdAt").descending();
        };

        Pageable pageable = PageRequest.of(page, size, sort);

        return productRepository.findAll(
                ProductSpecification.filter(keyword, categoryId, color, fabric, occasion, minPrice, maxPrice, minRating, inStock),
                pageable
        );
    }

    public Product getById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ApiException("Product not found", HttpStatus.NOT_FOUND));
    }

    public Product create(ProductRequest request) {
        Product product = mapToEntity(request, new Product());
        return productRepository.save(product);
    }

    public Product update(Long id, ProductRequest request) {
        Product existing = getById(id);
        Product updated = mapToEntity(request, existing);
        return productRepository.save(updated);
    }

    public void delete(Long id) {
        Product product = getById(id);
        product.setActive(false);
        productRepository.save(product);
    }

    private Product mapToEntity(ProductRequest request, Product product) {
        Category category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ApiException("Category not found", HttpStatus.NOT_FOUND));
        }

        product.setName(request.getName());
        product.setSku(request.getSku() != null && !request.getSku().isBlank() ? request.getSku().trim() : null);
        product.setDescription(request.getDescription());
        product.setCategory(category);
        product.setBrand(request.getBrand());
        product.setFabric(request.getFabric());
        product.setColor(request.getColor());
        product.setOccasion(request.getOccasion());
        product.setSize(request.getSize());
        product.setWeight(request.getWeight());
        product.setPrice(request.getPrice());
        product.setDiscountPercent(request.getDiscountPercent() != null ? request.getDiscountPercent() : 0.0);
        product.setStock(request.getStock() != null ? request.getStock() : 0);

        if (request.getImageUrls() != null) {
            product.getImages().clear();
            List<ProductImage> images = new ArrayList<>();
            for (int i = 0; i < request.getImageUrls().size(); i++) {
                images.add(ProductImage.builder()
                        .url(request.getImageUrls().get(i))
                        .primary(i == 0)
                        .product(product)
                        .build());
            }
            product.getImages().addAll(images);
        }

        return product;
    }
}
