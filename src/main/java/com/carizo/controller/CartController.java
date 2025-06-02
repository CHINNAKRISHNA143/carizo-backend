package com.carizo.controller;

import com.carizo.dto.AddToCartRequest;
import com.carizo.model.Cart;
import com.carizo.model.User;
import com.carizo.repository.UserRepository;
import com.carizo.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private UserRepository userRepository;

    // Helper method to get current logged in user ID
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
        return user.getId();
    }

    // Get cart for current logged-in user
    @GetMapping
    public Cart getCart() {
        Long userId = getCurrentUserId();
        return cartService.getCartByUserId(userId);
    }

    // Add a product to cart for current logged-in user
    @PostMapping("/add")
    public Cart addItemToCart(@RequestBody AddToCartRequest request) {
        Long userId = getCurrentUserId();
        return cartService.addItemToCart(userId, request.getProductId(), request.getQuantity());
    }

    // Remove a product from cart for current logged-in user
    @DeleteMapping("/remove")
    public Cart removeFromCart(@RequestParam Long productId) {
        Long userId = getCurrentUserId();
        return cartService.removeItemFromCart(userId, productId);
    }

    // Clear entire cart for current logged-in user
    @DeleteMapping("/clear")
    public void clearCart() {
        Long userId = getCurrentUserId();
        cartService.clearCart(userId);
    }
}
