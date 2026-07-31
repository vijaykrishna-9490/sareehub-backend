package com.sareehub.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductRequest {
    private String name;
    private String sku;
    private String description;
    private Long categoryId;
    private String brand;
    private String fabric;
    private String color;
    private String occasion;
    private String size;
    private Double weight;
    private BigDecimal price;
    private Double discountPercent;
    private Integer stock;
    private List<String> imageUrls;
}
