package com.example.ms_informes.client;

import com.example.ms_informes.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SaleClient {
    private final WebClient.Builder webClientBuilder;

    @Value("${services.sale.url}")
    private String saleServiceUrl;

    public List<Object> getDailySales(String token) {
        try {
            ApiResponse response = webClientBuilder.build()
                    .get()
                    .uri(saleServiceUrl + "/api/sales")
                    .header("Authorization", token)
                    .retrieve()
                    .bodyToMono(ApiResponse.class)
                    .block();

            return (List<Object>) response.getData();
        } catch (Exception e) {
            return List.of();
        }
    }
}
