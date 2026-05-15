package com.example.ms_inventario.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ms_inventario.client.NotificationClient;
import com.example.ms_inventario.client.ProductClient;
import com.example.ms_inventario.dto.InventoryRequest;
import com.example.ms_inventario.dto.InventoryResponse;
import com.example.ms_inventario.dto.MovementRequest;
import com.example.ms_inventario.dto.NotificationRequest;
import com.example.ms_inventario.model.Inventory;
import com.example.ms_inventario.model.InventoryMovement;
import com.example.ms_inventario.repository.InventoryMovementRepository;
import com.example.ms_inventario.repository.InventoryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

    private final InventoryRepository inventoryRepo;
    private final InventoryMovementRepository movementRepo;
    private final ProductClient productClient;
    private final NotificationClient notificationClient;

    @Transactional
    public InventoryResponse registerInitialStock(InventoryRequest request, String token) {
        log.info("Registrando stock inicial para producto: {}", request.getProductId());

        var product = productClient.obtenerProducto(request.getProductId(), token);
        if (product == null) {
            throw new IllegalArgumentException("Producto no existe en product-service");
        }

        if (inventoryRepo.findByProductId(request.getProductId()).isPresent()) {
            throw new IllegalArgumentException("El inventario para este producto ya existe");
        }

        Inventory inventory = new Inventory(null, request.getProductId(), request.getInitialStock(), request.getMinimumStock());
        inventory = inventoryRepo.save(inventory);

        if (request.getInitialStock() > 0) {
            InventoryMovement movement = new InventoryMovement(null, request.getProductId(), "IN", request.getInitialStock(), "Stock inicial", LocalDateTime.now());
            movementRepo.save(movement);
        }

        return mapToResponse(inventory);
    }

    public List<InventoryResponse> getAllInventory() {
        return inventoryRepo.findAll().stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public InventoryResponse getInventoryByProduct(Long productId) {
        Inventory inv = inventoryRepo.findByProductId(productId)
                .orElseThrow(() -> new IllegalArgumentException("Inventario no encontrado para el producto"));
        return mapToResponse(inv);
    }

    @Transactional
    public InventoryResponse registerEntry(Long productId, MovementRequest request) {
        log.info("Registrando ENTRADA para producto: {} cantidad: {}", productId, request.getQuantity());

        Inventory inv = inventoryRepo.findByProductId(productId)
                .orElseThrow(() -> new IllegalArgumentException("Inventario no encontrado para el producto"));

        inv.setCurrentStock(inv.getCurrentStock() + request.getQuantity());
        inv = inventoryRepo.save(inv);

        InventoryMovement movement = new InventoryMovement(null, productId, "IN", request.getQuantity(), request.getReason(), LocalDateTime.now());
        movementRepo.save(movement);

        return mapToResponse(inv);
    }

    @Transactional
    public InventoryResponse registerExit(Long productId, MovementRequest request, String token) {
        log.info("Registrando SALIDA para producto: {} cantidad: {}", productId, request.getQuantity());

        Inventory inv = inventoryRepo.findByProductId(productId)
                .orElseThrow(() -> new IllegalArgumentException("Inventario no encontrado para el producto"));

        if (inv.getCurrentStock() < request.getQuantity()) {
            throw new IllegalArgumentException("Stock insuficiente. Stock actual: " + inv.getCurrentStock());
        }

        inv.setCurrentStock(inv.getCurrentStock() - request.getQuantity());
        inv = inventoryRepo.save(inv);

        InventoryMovement movement = new InventoryMovement(null, productId, "OUT", request.getQuantity(), request.getReason(), LocalDateTime.now());
        movementRepo.save(movement);

        checkCriticalStock(inv, token);

        return mapToResponse(inv);
    }

    public List<InventoryResponse> getCriticalStock() {
        return inventoryRepo.findAll().stream()
                .filter(inv -> inv.getCurrentStock() <= inv.getMinimumStock())
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private void checkCriticalStock(Inventory inv, String token) {
        if (inv.getCurrentStock() <= inv.getMinimumStock()) {
            log.warn("Stock crítico detectado para producto: {}. Actual: {}, Mínimo: {}", inv.getProductId(), inv.getCurrentStock(), inv.getMinimumStock());
            NotificationRequest notifReq = NotificationRequest.builder()
                    .type("CRITICAL_STOCK")
                    .message("El producto con ID " + inv.getProductId() + " tiene un stock crítico de " + inv.getCurrentStock())
                    .productId(inv.getProductId())
                    .currentStock(inv.getCurrentStock())
                    .build();
            notificationClient.enviarAlertaStockCritico(notifReq, token);
        }
    }

    private InventoryResponse mapToResponse(Inventory inventory) {
        return InventoryResponse.builder()
                .id(inventory.getId())
                .productId(inventory.getProductId())
                .currentStock(inventory.getCurrentStock())
                .minimumStock(inventory.getMinimumStock())
                .build();
    }
}
