package com.carizo.repository;

import com.carizo.model.Product;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Add this method to enable fetching products by category ID
    List<Product> findByCategoryId(Long categoryId);
    
 // ✅ New method to fetch products by category name (case-insensitive, partial match)
    List<Product> findByCategory_NameContainingIgnoreCase(String categoryName);
    
 // New: find products where product name or category name matches query
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(p.category.name) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Product> searchByProductNameOrCategoryName(@Param("query") String query);
    
    // ✅ New Arrivals - products created after a specific date
    List<Product> findByCreatedAtAfter(LocalDateTime dateTime);
    
}


