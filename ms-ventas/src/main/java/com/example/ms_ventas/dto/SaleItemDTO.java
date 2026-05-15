package com.example.ms_ventas.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class SaleItemDTO {
    @NotNull(message = "El ID de producto es obligatorio")
    private Long productId;

    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer quantity;
}
