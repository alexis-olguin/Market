package com.example.ms_proveedores.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseOrderItemRequest {

    @NotNull(message = "El ID del producto es requerido")
    private Long productId;

    @NotNull(message = "La cantidad es requerida")
    @Min(value = 1, message = "La cantidad debe ser mayor a 0")
    private Integer quantity;

    @NotNull(message = "El costo unitario es requerido")
    @Min(value = 0, message = "El costo unitario no puede ser negativo")
    private BigDecimal unitCost;
}
