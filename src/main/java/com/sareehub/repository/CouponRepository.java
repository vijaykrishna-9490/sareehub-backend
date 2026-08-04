package com.sareehub.repository;

import com.sareehub.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
    Optional<Coupon> findByCodeIgnoreCase(String code);

    @Query("SELECT c FROM Coupon c WHERE c.active = true AND (c.expiryDate IS NULL OR c.expiryDate >= :today)")
    List<Coupon> findActiveCoupons(LocalDate today);
}
