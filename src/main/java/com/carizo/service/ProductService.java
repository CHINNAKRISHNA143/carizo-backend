package com.carizo.service;

import com.carizo.model.Category;
import com.carizo.model.Product;
import com.carizo.model.User;
import com.carizo.repository.CategoryRepository;
import com.carizo.repository.ProductRepository;
import com.carizo.repository.UserRepository;
import com.carizo.repository.CartItemRepository;
import com.carizo.repository.OrderItemRepository;
import org.springframework.data.domain.Sort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
    private OrderItemRepository orderItemRepository;

    public List<Product> getNewArrivals() {
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusDays(7);
        return productRepository.findByCreatedAtAfter(oneWeekAgo);
    }

    public List<Product> getBestSellers() {
        return orderItemRepository.findTopProductsByOrderCount(PageRequest.of(0, 5));
    }

    public List<Product> filterNewArrivals(Double minPrice, Double maxPrice, Boolean inStock, String sort) {
        List<Product> base = getNewArrivals();
        return applyFilters(base, minPrice, maxPrice, inStock, sort);
    }

    public List<Product> filterBestSellers(Double minPrice, Double maxPrice, Boolean inStock, String sort) {
        List<Product> base = getBestSellers();
        return applyFilters(base, minPrice, maxPrice, inStock, sort);
    }

    public List<Product> applyFilters(List<Product> products, Double minPrice, Double maxPrice, Boolean inStock, String sort) {
        List<Product> filtered = products.stream()
                .filter(p -> minPrice == null || p.getPrice() >= minPrice)
                .filter(p -> maxPrice == null || p.getPrice() <= maxPrice)
                .filter(p -> inStock == null || (inStock && p.getStock() > 0))
                .collect(Collectors.toList());

        if (sort != null) {
            if (sort.equalsIgnoreCase("asc")) {
                filtered.sort((a, b) -> Double.compare(a.getPrice(), b.getPrice()));
            } else if (sort.equalsIgnoreCase("desc")) {
                filtered.sort((a, b) -> Double.compare(b.getPrice(), a.getPrice()));
            }
        }

        return filtered;
    }

    public Product saveProduct(Product product) {
        if (product.getCategory() != null && product.getCategory().getId() != null) {
            Category category = categoryRepository.findById(product.getCategory().getId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            product.setCategory(category);
        } else {
            product.setCategory(null);
        }

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
        if (orderItemRepository.existsById(id)) {
            throw new IllegalStateException("Cannot delete product. It is associated with existing orders.");
        }
        cartItemRepository.deleteById(id);
        productRepository.deleteById(id);
    }

    public List<Product> getProductsByCategoryId(Long categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    public List<Product> searchProductsByCategoryName(String categoryName) {
        return productRepository.findByCategory_NameContainingIgnoreCase(categoryName);
    }

    public List<Product> searchProducts(String query) {
        return productRepository.searchByProductNameOrCategoryName(query);
    }

    public List<Product> filterProducts(Double minPrice, Double maxPrice, Boolean inStock, String sortDirection) {
        List<Product> filtered = productRepository.filterProducts(minPrice, maxPrice, inStock);

        if (sortDirection != null) {
            if (sortDirection.equalsIgnoreCase("asc")) {
                filtered.sort((a, b) -> Double.compare(a.getPrice(), b.getPrice()));
            } else if (sortDirection.equalsIgnoreCase("desc")) {
                filtered.sort((a, b) -> Double.compare(b.getPrice(), a.getPrice()));
            }
        }

        return filtered;
    }
    
    public List<Product> filterProductsByCategory(Long categoryId, Double minPrice, Double maxPrice, Boolean inStock, String sortDirection) {
        List<Product> filtered = productRepository.findByCategoryWithFilters(categoryId, minPrice, maxPrice, inStock);

        // Sort manually
        if (sortDirection != null) {
            if (sortDirection.equalsIgnoreCase("asc")) {
                filtered.sort((a, b) -> Double.compare(a.getPrice(), b.getPrice()));
            } else if (sortDirection.equalsIgnoreCase("desc")) {
                filtered.sort((a, b) -> Double.compare(b.getPrice(), a.getPrice()));
            }
        }

        return filtered;
    }

}