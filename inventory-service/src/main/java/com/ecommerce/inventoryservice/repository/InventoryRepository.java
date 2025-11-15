package com.ecommerce.inventoryservice.repository;

import com.ecommerce.inventoryservice.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
@Repository
public interface InventoryRepository extends JpaRepository<Inventory, UUID> {

    Optional<Inventory> findByProductSku(String productSku);

    boolean existsByProductSku(String productSku);
}