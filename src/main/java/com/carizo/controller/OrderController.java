package com.carizo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.carizo.dto.CheckoutRequest;
import com.carizo.model.Order;
import com.carizo.model.User;
import com.carizo.repository.UserRepository;
import com.carizo.service.OrderService;
import com.carizo.util.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;
    
    @Autowired
    private JwtUtil jwtUtil;


    @Autowired
    private UserRepository userRepository;

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
        return user.getId();
    }

    @PostMapping("/checkout")
    public ResponseEntity<Order> checkout(@RequestBody CheckoutRequest checkoutRequest) {
        Long userId = getCurrentUserId(); // implement this to get the logged-in user ID
        Order order = orderService.checkout(userId, checkoutRequest);
        return ResponseEntity.ok(order);
    }
    
    @GetMapping("/my")
    public ResponseEntity<List<Order>> getMyOrders() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();  // This is the email set in authentication
        List<Order> orders = orderService.getOrdersByUserEmail(userEmail);
        return ResponseEntity.ok(orders);
    }

    
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Order>> getAllOrders() {
        Long adminUserId = getCurrentUserId();
        List<Order> orders = orderService.getOrdersByAdminUserId(adminUserId);
        return ResponseEntity.ok(orders);
    }




}
