package com.example.ms_inventario.client;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.function.client.WebClient;
import com.example.ms_inventario.dto.ApiResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProductClient {
    private final WebClient webClient;

    @Value("${services.productos.url:http://localhost:8081/api/productos}")
    private String productosUrl;

    public Object obtenerProducto(Long id, String token) {
        ApiResponse<Object> response = webClient.get()
                .uri(productosUrl + "/" + id)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToMono(new org.springframework.core.ParameterizedTypeReference<ApiResponse<Object>>() {})
                .block();
        return response != null ? response.getData() : null;
    }
}
