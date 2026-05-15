package com.example.ms_ventas.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.List;

@Data
public class SaleDTO {
    private Long customerId;

    @NotNull(message = "El ID de usuario es obligatorio")
    private Long userId;

    @NotEmpty(message = "La venta debe tener al menos un producto")
    private List<SaleItemDTO> items;
}
