package com.ecommerce.productservice.controller;

import com.ecommerce.productservice.dto.ProductWithStockDto;
import com.ecommerce.productservice.dto.InventoryCreationRequest;
import com.ecommerce.productservice.model.Product;
import com.ecommerce.productservice.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    @Autowired
    private final ProductService productService;
    private final WebClient.Builder webClientBuilder;

    public ProductController(ProductService productService, WebClient.Builder webClientBuilder) { // ✅ explicit injection
        this.productService = productService;
        this.webClientBuilder=webClientBuilder;

    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Product createProduct(@RequestBody Product product) {
        return productService.createProduct(product);
    }

    public void createInitialInventory(InventoryCreationRequest request) {
        try {

            webClientBuilder.build()
                    .post()
                    .uri("lb://inventory-service/api/inventory")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .retrieve()
                    .toBodilessEntity()
                    .block();

            System.out.println("Successfully created initial inventory for SKU: " + request.getProductSku());

        } catch (Exception e) {

            System.err.println("CRITICAL FAILURE: Could not create initial inventory for SKU " + request.getProductSku() + ": " + e.getMessage());

            throw new RuntimeException("Failed to create initial inventory.", e);
        }
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductWithStockDto> getProductById(@PathVariable UUID id) {
        return productService.getProductByIdWithStock(id)
                .map(productDto -> new ResponseEntity<>(productDto, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable UUID id, @RequestBody Product productDetails) {
        try {
            Product updatedProduct = productService.updateProduct(id, productDetails);
            return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable UUID id) {
        productService.deleteProduct(id);
    }
}