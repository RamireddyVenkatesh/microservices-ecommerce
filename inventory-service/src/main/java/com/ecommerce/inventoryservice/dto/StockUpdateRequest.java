package com.ecommerce.inventoryservice.dto;


import lombok.Data;

@Data
public class StockUpdateRequest {
    private String productSku;
    private Integer quantity;
    private StockUpdateType type;

    public StockUpdateRequest() {
    }

    public StockUpdateRequest(String productSku, Integer quantity, StockUpdateType type) {
        this.productSku = productSku;
        this.quantity = quantity;
        this.type = type;
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

    public StockUpdateType getType() {
        return type;
    }

    public void setType(StockUpdateType type) {
        this.type = type;
    }
}