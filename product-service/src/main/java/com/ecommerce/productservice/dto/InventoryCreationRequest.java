package com.ecommerce.productservice.dto;

public class InventoryCreationRequest {
    private String productSku;
    private Integer quantity = 0;
    private Integer reservedQuantity = 0;

    public InventoryCreationRequest(String productSku) {
        this.productSku = productSku;
    }

    public String getProductSku() {
        return productSku;
    }
    public void setProductSku(String productSku) {
        this.productSku = productSku;
    }
    public Integer getQuantity() {
        return quantity;
    }
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    public Integer getReservedQuantity() {
        return reservedQuantity;
    }
    public void setReservedQuantity(Integer reservedQuantity) {
        this.reservedQuantity = reservedQuantity;
    }
}