package com.sareehub.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true)
    private String sku;

    @Column(length = 4000)
    private String description;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    private String brand;
    private String fabric;
    private String color;
    private String occasion;
    private String size;
    private Double weight;

    @Column(nullable = false)
    private BigDecimal price;

    @Builder.Default
    private Double discountPercent = 0.0;

    @Builder.Default
    private Integer stock = 0;

    @Builder.Default
    private Double avgRating = 0.0;

    @Builder.Default
    private Integer reviewCount = 0;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProductImage> images = new ArrayList<>();

    @Builder.Default
    private boolean active = true;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
