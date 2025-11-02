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

    // Fetch by Category ID
    List<Product> findByCategoryId(Long categoryId);

    // Search by Category Name (case-insensitive)
    List<Product> findByCategory_NameContainingIgnoreCase(String categoryName);

    // Search by Product or Category name
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(p.category.name) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Product> searchByProductNameOrCategoryName(@Param("query") String query);

    // New Arrivals after given date
    List<Product> findByCreatedAtAfter(LocalDateTime dateTime);

    // ✅ Flexible Filtering by Price and Stock
    @Query("SELECT p FROM Product p " +
           "WHERE (:minPrice IS NULL OR p.price >= :minPrice) " +
           "AND (:maxPrice IS NULL OR p.price <= :maxPrice) " +
           "AND (:inStock IS NULL OR p.stock > 0)")
    List<Product> filterProducts(
        @Param("minPrice") Double minPrice,
        @Param("maxPrice") Double maxPrice,
        @Param("inStock") Boolean inStock
    );
    
 // ✅ NEW: Filter Products by Category with Optional Filters
    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId " +
           "AND (:minPrice IS NULL OR p.price >= :minPrice) " +
           "AND (:maxPrice IS NULL OR p.price <= :maxPrice) " +
           "AND (:inStock IS NULL OR p.stock > 0)")
    List<Product> findByCategoryWithFilters(
        @Param("categoryId") Long categoryId,
        @Param("minPrice") Double minPrice,
        @Param("maxPrice") Double maxPrice,
        @Param("inStock") Boolean inStock
    );
}
