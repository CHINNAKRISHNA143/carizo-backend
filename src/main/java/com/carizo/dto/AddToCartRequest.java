package com.carizo.dto;

public class AddToCartRequest {
    private Long productId;
    private int quantity;

    // Constructors
    public AddToCartRequest() {}

    public AddToCartRequest(Long productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    // Getters and setters
    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
