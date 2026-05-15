package com.example.ms_producto.client;

import com.example.ms_producto.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class ConfigurationClient {

    private final WebClient.Builder webClientBuilder;

    @Value("${services.configuration.url}")
    private String configurationServiceUrl;

    public boolean isCategoryActive(Long categoryId, String token) {
        try {
            ApiResponse response = webClientBuilder.build()
                    .get()
                    .uri(configurationServiceUrl + "/api/categories/" + categoryId + "/active")
                    .header("Authorization", token)
                    .retrieve()
                    .bodyToMono(ApiResponse.class)
                    .block();

            return response != null && response.isSuccess() && Boolean.TRUE.equals(response.getData());
        } catch (Exception e) {
            return false;
        }
    }
}
