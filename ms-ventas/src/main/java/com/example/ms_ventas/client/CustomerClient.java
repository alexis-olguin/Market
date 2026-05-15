package com.example.ms_ventas.client;

import com.example.ms_ventas.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class CustomerClient {
    private final WebClient.Builder webClientBuilder;

    @Value("${services.customer.url}")
    private String customerServiceUrl;

    public boolean existsCustomer(Long id, String token) {
        try {
            ApiResponse response = webClientBuilder.build()
                    .get()
                    .uri(customerServiceUrl + "/api/customers/" + id)
                    .header("Authorization", token)
                    .retrieve()
                    .bodyToMono(ApiResponse.class)
                    .block();

            return response != null && response.isSuccess();
        } catch (Exception e) {
            return false;
        }
    }
}
