package com.sareehub.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "coupons")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    private Double discountPercent;
    private Double maxDiscountAmount;
    private Double minOrderValue;

    @Builder.Default
    private boolean active = true;

    private LocalDate expiryDate;
}
