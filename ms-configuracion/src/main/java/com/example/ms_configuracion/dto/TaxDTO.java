package com.example.ms_configuracion.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class TaxDTO {
    @NotBlank(message = "El nombre del impuesto es obligatorio")
    private String name;

    @NotNull(message = "El porcentaje es obligatorio")
    @DecimalMin(value = "0.0", message = "El porcentaje debe ser mayor o igual a 0")
    private BigDecimal percentage;

    private boolean active = true;
}
