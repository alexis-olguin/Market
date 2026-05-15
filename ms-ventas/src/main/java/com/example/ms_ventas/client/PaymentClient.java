package com.example.ms_ventas.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class PaymentClient {
    private final WebClient.Builder webClientBuilder;

    @Value("${services.payment.url}")
    private String paymentServiceUrl;

    public String processPayment(BigDecimal amount, String token) {
        // TODO: Implement actual call to ms-pagos
        // For now, always return APPROVED
        return "APPROVED";
    }
}
