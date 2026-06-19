package com.example.ms_informes.client;

import com.example.ms_informes.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class InventoryClient {
    private final WebClient.Builder webClientBuilder;

    @Value("${services.inventory.url}")
    private String inventoryServiceUrl;

    public List<Object> getCriticalStock(String token) {
        try {
            ApiResponse response = webClientBuilder.build()
                    .get()
                    .uri(inventoryServiceUrl + "/api/inventario/stock-critico")
                    .header("Authorization", token)
                    .retrieve()
                    .bodyToMono(ApiResponse.class)
                    .block();

            return (List<Object>) response.getData();
        } catch (Exception e) {
            log.error("Error al obtener stock crítico desde el servicio de inventario: {}", e.getMessage(), e);
            return List.of();
        }
    }
}
