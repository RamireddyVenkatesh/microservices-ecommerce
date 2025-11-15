package com.ecommerce.productservice.client;

import com.ecommerce.productservice.dto.InventoryCreationRequest;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Optional;

@Component
public class InventoryServiceClient {

    private final WebClient.Builder webClientBuilder;

    @Autowired
    public InventoryServiceClient(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    @CircuitBreaker(name = "inventoryCheckCircuitBreaker", fallbackMethod = "isProductInStockFallback")
    public Optional<Boolean> isProductInStock(String sku) {

        Boolean isInStock = webClientBuilder.build()
                .get()
                .uri("lb://inventory-service/api/inventory/{sku}/in-stock",
                        uriBuilder -> uriBuilder.queryParam("quantity", 1).build(sku))
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();

        return Optional.ofNullable(isInStock);
    }


    public Optional<Boolean> isProductInStockFallback(String sku, Throwable t) {
        System.err.println("--- CIRCUIT BREAKER ACTIVATED / FALLBACK METHOD CALLED ---");
        System.err.println("Inventory Service is unavailable or call failed: " + t.getMessage());

        return Optional.of(false);
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
}