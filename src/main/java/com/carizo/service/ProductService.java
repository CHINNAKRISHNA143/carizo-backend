package com.carizo.service;

import com.carizo.model.Category;
import com.carizo.model.Product;

import com.carizo.model.User;
import com.carizo.repository.CategoryRepository;
import com.carizo.repository.ProductRepository;
import com.carizo.repository.UserRepository;
import com.carizo.repository.CartItemRepository;
import com.carizo.repository.OrderItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CartItemRepository cartItemRepository;
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderItemRepository orderItemRepository; // ✅ Inject OrderItemRepository
    
 // ✅ Get New Arrivals (products created in the last 7 days)
    public List<Product> getNewArrivals() {
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusDays(7);
        return productRepository.findByCreatedAtAfter(oneWeekAgo);
    }

    // ✅ Get Best Sellers (top 10 products by order count)
    public List<Product> getBestSellers() {
        return orderItemRepository.findTopProductsByOrderCount(PageRequest.of(0, 5));
    }

    public Product saveProduct(Product product) {
        if (product.getCategory() != null && product.getCategory().getId() != null) {
            Category category = categoryRepository.findById(product.getCategory().getId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
            product.setCategory(category);
        } else {
            product.setCategory(null);
        }
        
     // Set the current logged-in user as the product's user (vendor/admin)
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User currentUser = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        product.setUser(currentUser);
        
        return productRepository.save(product);
    }

    
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    @Transactional
    public void deleteProductById(Long id) {
        // ✅ Block deletion if product is in any order
        if (orderItemRepository.existsById(id)) {
            throw new IllegalStateException("Cannot delete product. It is associated with existing orders.");
        }

        // ✅ Remove from cart before deleting product
        cartItemRepository.deleteById(id);
        productRepository.deleteById(id);
    }

    public List<Product> getProductsByCategoryId(Long categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }
    // ✅ New method: Search products by category name (partial, case-insensitive)
    public List<Product> searchProductsByCategoryName(String categoryName) {
        return productRepository.findByCategory_NameContainingIgnoreCase(categoryName);
    }
    
    public List<Product> searchProducts(String query) {
        return productRepository.findByCategory_NameContainingIgnoreCase(query);
    }
    
    


}
