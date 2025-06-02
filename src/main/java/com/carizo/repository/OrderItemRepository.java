package com.carizo.repository;

import java.awt.print.Pageable;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.carizo.model.OrderItem;
import com.carizo.model.Product;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> { 
	
	// ✅ Custom query to find top products based on total quantity sold
    @Query("SELECT oi.product FROM OrderItem oi GROUP BY oi.product ORDER BY SUM(oi.quantity) DESC")
    List<Product> findTopProductsByOrderCount(PageRequest pageRequest);

    // Optional: check if any OrderItem contains a specific product
    boolean existsByProduct_Id(Long productId);
	
}
