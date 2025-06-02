package com.carizo.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.carizo.dto.CheckoutRequest;
import com.carizo.model.Cart;
import com.carizo.model.CartItem;
import com.carizo.model.Order;
import com.carizo.model.OrderItem;
import com.carizo.model.User;
import com.carizo.repository.OrderItemRepository;
import com.carizo.repository.OrderRepository;
import com.carizo.repository.UserRepository;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private UserRepository userRepository;


    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private CartService cartService;

    public Order checkout(Long userId, CheckoutRequest checkoutRequest) {
        Cart cart = cartService.getCartByUserId(userId);

        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty!");
        }

        Order order = new Order();
        order.setUserId(userId);
        order.setOrderDate(LocalDateTime.now());

        // Set new address fields here:
        order.setAddress(checkoutRequest.getAddress());
        order.setCity(checkoutRequest.getCity());
        order.setPostalCode(checkoutRequest.getPostalCode());
        order.setPhoneNumber(checkoutRequest.getPhoneNumber());

        List<OrderItem> orderItems = new ArrayList<>();
        double total = 0.0;

        for (CartItem cartItem : cart.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getProduct().getPrice());

            total += cartItem.getProduct().getPrice() * cartItem.getQuantity();
            orderItems.add(orderItem);
        }

        order.setItems(orderItems);
        order.setTotalAmount(total);

        Order savedOrder = orderRepository.save(order);

        // clear cart after successful order
        cartService.clearCart(userId);

        return savedOrder;
    }
    
    public List<Order> getOrdersByUserEmail(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return orderRepository.findByUser(user);
    }

    
    public List<Order> getAllOrders() {
        return orderRepository.findAll(); // Admin sees every order
    }
    
    public List<Order> getOrdersByAdminUserId(Long adminUserId) {
        return orderRepository.findOrdersByAdminProducts(adminUserId);
    }


}
