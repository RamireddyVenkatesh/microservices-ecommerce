package com.ecommerce.productservice.dto;

import java.math.BigDecimal;
import java.util.UUID;

// Simple POJO to combine Product data with InStock status
public class ProductWithStockDto {
    private UUID id;
    private String name;
    private String description;
    private BigDecimal price;
    private String category;
    private String sku;
    private Boolean isInStock; // NEW FIELD for M4


    public ProductWithStockDto() {}

    public ProductWithStockDto(UUID id, String name, String description, BigDecimal price, String category, String sku, Boolean isInStock) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.sku = sku;
        this.isInStock = isInStock;
    }


    public UUID getId() {
        return id;
    }
    public void setId(UUID id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public BigDecimal getPrice() {
        return price;
    }
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public String getSku() {
        return sku;
    }
    public void setSku(String sku) {
        this.sku = sku;
    }
    public Boolean getIsInStock() {
        return isInStock;
    }
    public void setIsInStock(Boolean isInStock) {
        this.isInStock = isInStock;
    }
}