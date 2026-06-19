package com.example.ms_ventas.client;

import com.example.ms_ventas.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ProductClient {
    private final WebClient.Builder webClientBuilder;

    @Value("${services.product.url}")
    private String productServiceUrl;

    public Map<String, Object> getProduct(Long id, String token) {
        try {
            ApiResponse response = webClientBuilder.build()
                    .get()
                    .uri(productServiceUrl + "/api/productos/" + id)
                    .header("Authorization", token)
                    .retrieve()
                    .bodyToMono(ApiResponse.class)
                    .block();

            if (response != null && response.isSuccess()) {
                return (Map<String, Object>) response.getData();
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}
