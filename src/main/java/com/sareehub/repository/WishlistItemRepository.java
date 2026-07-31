package com.sareehub.repository;

import com.sareehub.entity.User;
import com.sareehub.entity.WishlistItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface WishlistItemRepository extends JpaRepository<WishlistItem, Long> {
    List<WishlistItem> findByUser(User user);
    Optional<WishlistItem> findByUserAndProductId(User user, Long productId);
    void deleteByUserAndProductId(User user, Long productId);
}
