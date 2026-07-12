package com.example.ms_proveedores.client;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.function.client.WebClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryClient {

    private final WebClient webClient;

    @Value("${services.inventario.url:http://localhost:8086/api/inventory/product}")
    private String inventarioUrl;

    public void aumentarStock(Long productId, Integer quantity, String token) {
        try {
            webClient.put()
                    .uri(inventarioUrl + "/" + productId + "/stock")
                    .header("Authorization", "Bearer " + token)
                    .bodyValue(Map.of(
                            "quantity", quantity,
                            "reason", "Entrada por Orden de Compra"
                    ))
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
            log.info("Stock aumentado exitosamente en inventory-service para producto {}", productId);
        } catch (Exception e) {
            log.error("Error al aumentar stock en inventory-service: {}", e.getMessage());
            throw new RuntimeException("No se pudo actualizar el inventario. " + e.getMessage());
        }
    }
}
