package com.example.ms_proveedores.client;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import com.example.ms_proveedores.dto.ApiResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProductClient {

    private final WebClient webClient;
    private final String BASE_URL = "http://localhost:8082/api/productos/";

    public Object obtenerProducto(Long id, String token) {
        ApiResponse<Object> response = webClient.get()
                .uri(BASE_URL + id)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToMono(new org.springframework.core.ParameterizedTypeReference<ApiResponse<Object>>() {})
                .block();

        return response != null ? response.getData() : null;
    }
}
