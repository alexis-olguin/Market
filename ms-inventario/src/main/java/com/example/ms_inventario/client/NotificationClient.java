package com.example.ms_inventario.client;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import com.example.ms_inventario.dto.NotificationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationClient {
    private final WebClient webClient;
    private final String BASE_URL = "http://localhost:8089/api/notificaciones";

    public void enviarAlertaStockCritico(NotificationRequest request, String token) {
        try {
            webClient.post()
                    .uri(BASE_URL)
                    .header("Authorization", "Bearer " + token)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
            log.info("Alerta de stock crítico enviada para producto {}", request.getProductId());
        } catch (Exception e) {
            log.error("Error al enviar alerta de stock crítico: {}", e.getMessage());
        }
    }
}
