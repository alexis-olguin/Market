package com.example.ms_ventas.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class InventoryClient {
    private final WebClient.Builder webClientBuilder;

    @Value("${services.inventory.url}")
    private String inventoryServiceUrl;

    public boolean checkStock(Long productId, Integer quantity, String token) {
        // TODO: Implement actual call to ms-inventario
        // For now, assume stock is always sufficient
        return true;
    }

    public void deductStock(Long productId, Integer quantity, String token) {
        // TODO: Implement actual call to ms-inventario
        log.info("Deducting stock for product " + productId + ", quantity: " + quantity);
    }
}
