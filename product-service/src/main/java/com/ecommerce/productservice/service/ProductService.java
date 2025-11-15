package com.ecommerce.productservice.service;

import com.ecommerce.productservice.client.InventoryServiceClient;
import com.ecommerce.productservice.dto.InventoryCreationRequest;
import com.ecommerce.productservice.dto.ProductWithStockDto;
import com.ecommerce.productservice.model.Product;
import com.ecommerce.productservice.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProductService {

    @Autowired
    private final ProductRepository productRepository;
    private final InventoryServiceClient inventoryServiceClient;

    public ProductService(ProductRepository productRepository, InventoryServiceClient inventoryServiceClient) {
        this.productRepository = productRepository;
        this.inventoryServiceClient = inventoryServiceClient;
    }

    @Transactional
    public Product createProduct(Product product) {

        Product savedProduct = productRepository.save(product);


        InventoryCreationRequest inventoryRequest = new InventoryCreationRequest(savedProduct.getSku());

        try {

            inventoryServiceClient.createInitialInventory(inventoryRequest);
        } catch (RuntimeException e) {

            throw new RuntimeException("Product creation failed because initial inventory could not be set.", e);
        }

        return savedProduct;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> getProductById(UUID id) {
        return productRepository.findById(id);
    }

    public Product updateProduct(UUID id, Product productDetails) {
        return productRepository.findById(id)
                .map(product -> {
                    product.setName(productDetails.getName());
                    product.setDescription(productDetails.getDescription());
                    product.setPrice(productDetails.getPrice());
                    product.setCategory(productDetails.getCategory());
                    product.setSku(productDetails.getSku());
                    return productRepository.save(product);
                }).orElseThrow(() -> new RuntimeException("Product not found with id " + id));
    }

    public void deleteProduct(UUID id) {
        productRepository.deleteById(id);
    }


    public Optional<ProductWithStockDto> getProductByIdWithStock(UUID id) {
        return productRepository.findById(id)
                .map(product -> {

                    Optional<Boolean> inStock = inventoryServiceClient.isProductInStock(product.getSku());

                    return new ProductWithStockDto(
                            product.getId(),
                            product.getName(),
                            product.getDescription(),
                            product.getPrice(),
                            product.getCategory(),
                            product.getSku(),

                            inStock.orElse(false)
                    );
                });
    }
}