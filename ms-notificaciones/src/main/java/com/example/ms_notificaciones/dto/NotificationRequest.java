package com.example.ms_notificaciones.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationRequest {

    @NotBlank(message = "El tipo de notificación es requerido")
    @Pattern(regexp = "^(LOW_STOCK|PROMOTION|SYSTEM)$", message = "El tipo debe ser LOW_STOCK, PROMOTION o SYSTEM")
    private String type;

    @NotBlank(message = "El mensaje es requerido")
    private String message;
}
