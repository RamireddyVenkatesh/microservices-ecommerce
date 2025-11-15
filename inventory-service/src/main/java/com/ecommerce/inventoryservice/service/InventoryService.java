package com.ecommerce.inventoryservice.service;

import com.ecommerce.inventoryservice.dto.StockUpdateRequest;
import com.ecommerce.inventoryservice.model.Inventory;
import com.ecommerce.inventoryservice.repository.InventoryRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class InventoryService {

    @Autowired
    private final InventoryRepository inventoryRepository;

    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    public Inventory createOrUpdateInventory(Inventory inventory) {

        return inventoryRepository.save(inventory);
    }

    public List<Inventory> getAllInventory() {
        return inventoryRepository.findAll();
    }

    public Optional<Inventory> getInventoryBySku(String productSku) {
        return inventoryRepository.findByProductSku(productSku);
    }

    public Optional<Inventory> getInventoryById(UUID id) {
        return inventoryRepository.findById(id);
    }

    @Transactional
    public Inventory updateStock(String productSku, StockUpdateRequest request) {
        Optional<Inventory> inventoryOptional = inventoryRepository.findByProductSku(productSku);
        if (inventoryOptional.isEmpty()) {
            throw new RuntimeException("Inventory not found for SKU: " + productSku);
        }

        Inventory inventory = inventoryOptional.get();
        int currentQuantity = inventory.getQuantity();
        int currentReserved = inventory.getReservedQuantity();
        int updateQuantity = request.getQuantity();

        switch (request.getType()) {
            case ADD:
                inventory.setQuantity(currentQuantity + updateQuantity);
                break;
            case DEDUCT:

                if ( (currentQuantity - currentReserved) < updateQuantity) {
                    throw new RuntimeException("Not enough available stock to deduct " + updateQuantity + " for SKU: " + productSku);
                }
                inventory.setQuantity(currentQuantity - updateQuantity);
                break;
            case RESERVE:
                if ( (currentQuantity - currentReserved) < updateQuantity) {
                    throw new RuntimeException("Not enough available stock to reserve " + updateQuantity + " for SKU: " + productSku);
                }
                inventory.setReservedQuantity(currentReserved + updateQuantity);
                break;
            case UNRESERVE:
                if (currentReserved < updateQuantity) {
                    throw new RuntimeException("Cannot unreserve more than reserved for SKU: " + productSku);
                }
                inventory.setReservedQuantity(currentReserved - updateQuantity);
                break;
            default:
                throw new IllegalArgumentException("Unknown stock update type: " + request.getType());
        }

        inventory.setLastUpdated(Instant.now());
        return inventoryRepository.save(inventory);
    }


    public boolean isInStock(String productSku, int requiredQuantity) {
        return inventoryRepository.findByProductSku(productSku)
                .map(inventory -> (inventory.getQuantity() - inventory.getReservedQuantity()) >= requiredQuantity)
                .orElse(false);
    }
}