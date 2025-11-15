package com.ecommerce.inventoryservice.controller;

import com.ecommerce.inventoryservice.dto.StockUpdateRequest;
import com.ecommerce.inventoryservice.model.Inventory;
import com.ecommerce.inventoryservice.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    @Autowired
    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Inventory createOrUpdateInventory(@RequestBody Inventory inventory) {
        return inventoryService.createOrUpdateInventory(inventory);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Inventory> getAllInventory() {
        return inventoryService.getAllInventory();
    }

    @GetMapping("/{sku}")
    public ResponseEntity<Inventory> getInventoryBySku(@PathVariable String sku) {
        return inventoryService.getInventoryBySku(sku)
                .map(inventory -> new ResponseEntity<>(inventory, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{sku}/stock")
    public ResponseEntity<Inventory> updateInventoryStock(@PathVariable String sku, @RequestBody StockUpdateRequest request) {
        try {
            Inventory updatedInventory = inventoryService.updateStock(sku, request);
            return new ResponseEntity<>(updatedInventory, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // New endpoint to check stock availability
    @GetMapping("/{sku}/in-stock")
    public ResponseEntity<Boolean> isInStock(@PathVariable String sku, @RequestParam int quantity) {
        boolean inStock = inventoryService.isInStock(sku, quantity);
        return new ResponseEntity<>(inStock, HttpStatus.OK);
    }
}