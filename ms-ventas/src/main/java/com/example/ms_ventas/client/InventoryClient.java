package com.example.ms_ventas.client;

import com.example.ms_ventas.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class InventoryClient {
    private final WebClient.Builder webClientBuilder;

    @Value("${services.inventory.url}")
    private String inventoryServiceUrl;

    public boolean checkStock(Long productId, Integer quantity, String token) {
        try {
            ApiResponse response = webClientBuilder.build()
                    .get()
                    .uri(inventoryServiceUrl + "/api/inventory/product/" + productId)
                    .header("Authorization", token)
                    .retrieve()
                    .bodyToMono(ApiResponse.class)
                    .block();

            if (response != null && response.isSuccess()) {
                Map<String, Object> data = (Map<String, Object>) response.getData();
                Integer currentStock = (Integer) data.get("currentStock");
                return currentStock != null && currentStock >= quantity;
            }
            return false;
        } catch (Exception e) {
            log.error("Error checking stock for product " + productId, e);
            return false;
        }
    }

    public void deductStock(Long productId, Integer quantity, String token) {
        try {
            Map<String, Object> request = Map.of(
                    "quantity", quantity,
                    "description", "Venta procesada"
            );

            webClientBuilder.build()
                    .post()
                    .uri(uriBuilder -> uriBuilder
                            .path(inventoryServiceUrl + "/api/inventory/movements")
                            .queryParam("productId", productId)
                            .build())
                    .header("Authorization", token)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(ApiResponse.class)
                    .block();

            log.info("Stock descontado para el producto " + productId + ", cantidad: " + quantity);
        } catch (Exception e) {
            log.error("Error descontando stock para el producto " + productId, e);
            throw new RuntimeException("Error al descontar stock: " + e.getMessage());
        }
    }
}
