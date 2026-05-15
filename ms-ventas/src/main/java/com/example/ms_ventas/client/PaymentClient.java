package com.example.ms_ventas.client;

import com.example.ms_ventas.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import java.math.BigDecimal;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentClient {
    private final WebClient.Builder webClientBuilder;

    @Value("${services.payment.url}")
    private String paymentServiceUrl;

    public String processPayment(BigDecimal amount, String token) {
        try {
            Map<String, Object> request = Map.of(
                    "saleReference", "SALE-" + System.currentTimeMillis(),
                    "amount", amount,
                    "method", "CARD" // Por defecto simulamos CARD
            );

            ApiResponse response = webClientBuilder.build()
                    .post()
                    .uri(paymentServiceUrl + "/api/payments")
                    .header("Authorization", token)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(ApiResponse.class)
                    .block();

            if (response != null && response.isSuccess()) {
                Map<String, Object> data = (Map<String, Object>) response.getData();
                return (String) data.get("status");
            }
            return "REJECTED";
        } catch (Exception e) {
            log.error("Error processing payment", e);
            return "REJECTED";
        }
    }
}
