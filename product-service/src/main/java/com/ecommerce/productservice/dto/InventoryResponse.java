package com.ecommerce.productservice.dto;

public class InventoryResponse {
    private String productSku;
    private Boolean isInStock;


    public InventoryResponse() {}

    public String getProductSku() {
        return productSku;
    }

    public void setProductSku(String productSku) {
        this.productSku = productSku;
    }

    public Boolean getIsInStock() {
        return isInStock;
    }

    public void setIsInStock(Boolean isInStock) {
        this.isInStock = isInStock;
    }
}