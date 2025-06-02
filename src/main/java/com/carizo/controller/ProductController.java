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
    public List<Product> getAllProducts() {
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
    
 /*// 🔥 New Endpoint to get products by tag
    @GetMapping("/tag/{tag}")
    public ResponseEntity<List<Product>> getProductsByTag(@PathVariable String tag) {
        try {
            ProductTag productTag = ProductTag.valueOf(tag.toUpperCase());
            List<Product> products = productService.getProductsByTag(productTag);
            return ResponseEntity.ok(products);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }*/
    
    @GetMapping("/new-arrivals")
    public ResponseEntity<List<Product>> getNewArrivals() {
        List<Product> newArrivals = productService.getNewArrivals();
        return ResponseEntity.ok(newArrivals);
    }

    @GetMapping("/best-sellers")
    public ResponseEntity<List<Product>> getBestSellers() {
        List<Product> bestSellers = productService.getBestSellers();
        return ResponseEntity.ok(bestSellers);
    }

    

}
