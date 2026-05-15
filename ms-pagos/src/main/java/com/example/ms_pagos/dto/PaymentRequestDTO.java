package com.example.ms_pagos.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class PaymentRequestDTO {
    @NotBlank(message = "La referencia de venta es obligatoria")
    private String saleReference;

    @NotNull(message = "El monto es obligatorio")
    @DecimalMin(value = "0.01", message = "El monto debe ser mayor a 0")
    private BigDecimal amount;

    @NotBlank(message = "El método de pago es obligatorio")
    private String method; // CASH, CARD, TRANSFER
}
