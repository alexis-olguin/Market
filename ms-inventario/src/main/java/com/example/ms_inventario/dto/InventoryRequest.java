package com.example.ms_inventario.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryRequest {
    @NotNull(message = "El ID del producto es requerido")
    private Long productId;

    @NotNull(message = "El stock inicial es requerido")
    @Min(value = 0, message = "El stock inicial no puede ser negativo")
    private Integer initialStock;

    @NotNull(message = "El stock mínimo es requerido")
    @Min(value = 0, message = "El stock mínimo no puede ser negativo")
    private Integer minimumStock;
}
