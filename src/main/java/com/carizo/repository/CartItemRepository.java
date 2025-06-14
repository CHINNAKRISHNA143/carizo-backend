package com.carizo.repository;

import com.carizo.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    
    @Transactional
    void deleteByProductId(Long productId);
}
