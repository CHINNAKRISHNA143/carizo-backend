package com.carizo.controller;

import com.carizo.model.Product;
import com.carizo.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public List<Product> getAllProducts(
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Boolean inStock,
            @RequestParam(required = false) String sort // "asc" or "desc"
    ) {
        if (minPrice != null || maxPrice != null || inStock != null || sort != null) {
            return productService.filterProducts(minPrice, maxPrice, inStock, sort);
        }
        return productService.getAllProducts();
    }

    @GetMapping("/{id:[0-9]+}")
    public Product getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    @PostMapping
    public Product addProduct(@RequestBody Product product) {
        return productService.saveProduct(product);
    }

    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteProductById(id);
    }

    @GetMapping("/category/{categoryId}")
    public List<Product> getProductsByCategoryId(@PathVariable Long categoryId) {
        return productService.getProductsByCategoryId(categoryId);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProducts(@RequestParam("q") String query) {
        List<Product> results = productService.searchProducts(query);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/new-arrivals")
    public ResponseEntity<List<Product>> getNewArrivals(
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Boolean inStock,
            @RequestParam(required = false) String sort
    ) {
        List<Product> newArrivals = productService.filterNewArrivals(minPrice, maxPrice, inStock, sort);
        return ResponseEntity.ok(newArrivals);
    }

    @GetMapping("/best-sellers")
    public ResponseEntity<List<Product>> getBestSellers(
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Boolean inStock,
            @RequestParam(required = false) String sort
    ) {
        List<Product> bestSellers = productService.filterBestSellers(minPrice, maxPrice, inStock, sort);
        return ResponseEntity.ok(bestSellers);
    }
    
    @GetMapping("/category/{categoryId}/filter")
    public ResponseEntity<List<Product>> filterProductsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Boolean inStock,
            @RequestParam(required = false) String sort
    ) {
        List<Product> filtered = productService.filterProductsByCategory(categoryId, minPrice, maxPrice, inStock, sort);
        return ResponseEntity.ok(filtered);
    }

}
