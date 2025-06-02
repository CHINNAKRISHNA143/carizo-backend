package com.carizo.service;

import com.carizo.model.Cart;
import com.carizo.model.CartItem;
import com.carizo.model.Product;
import com.carizo.repository.CartItemRepository;
import com.carizo.repository.CartRepository;
import com.carizo.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    public Cart getCartByUserId(Long userId) {
        Cart cart = cartRepository.findByUserId(userId).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUserId(userId);
            newCart.setItems(new ArrayList<>()); // Ensure items list is initialized
            return cartRepository.save(newCart);
        });

        if (cart.getItems() == null) {
            cart.setItems(new ArrayList<>()); // Null check for existing cart
        }

        return cart;
    }

    public Cart addItemToCart(Long userId, Long productId, int quantity) {
        Cart cart = getCartByUserId(userId);
        Optional<Product> productOpt = productRepository.findById(productId);

        if (cart.getItems() == null) {
            cart.setItems(new ArrayList<>());
        }

        if (productOpt.isPresent()) {
            Product product = productOpt.get();

            for (CartItem item : cart.getItems()) {
                if (item.getProduct().getId().equals(productId)) {
                    int newQuantity = item.getQuantity() + quantity;

                    if (newQuantity <= 0) {
                        cart.getItems().remove(item);
                        cartItemRepository.delete(item);
                        return cartRepository.save(cart);
                    } else {
                        item.setQuantity(newQuantity);
                        return cartRepository.save(cart);
                    }
                }
            }

            // If item not found, add new item only if quantity > 0
            if (quantity > 0) {
                CartItem cartItem = new CartItem();
                cartItem.setCart(cart);
                cartItem.setProduct(product);
                cartItem.setQuantity(quantity);
                cart.getItems().add(cartItem);
            }
        }

        return cartRepository.save(cart);
    }


    public Cart removeItemFromCart(Long userId, Long productId) {
        Cart cart = getCartByUserId(userId);

        if (cart.getItems() == null) {
            return cart;
        }

        Iterator<CartItem> iterator = cart.getItems().iterator();
        while (iterator.hasNext()) {
            CartItem item = iterator.next();
            if (item.getProduct().getId().equals(productId)) {
                iterator.remove(); // remove from cart's item list
                cartItemRepository.delete(item); // remove from DB
                break;
            }
        }

        return cartRepository.save(cart);
    }

    public void removeItem(Long cartItemId) {
        cartItemRepository.deleteById(cartItemId);
    }

    public void clearCart(Long userId) {
        Cart cart = getCartByUserId(userId);

        if (cart.getItems() != null) {
            cartItemRepository.deleteAll(cart.getItems());
            cart.getItems().clear();
        }

        cartRepository.save(cart);
    }
}
