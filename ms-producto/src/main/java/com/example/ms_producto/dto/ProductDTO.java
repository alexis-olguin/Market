package com.example.ms_producto.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ProductDTO {
    @NotBlank(message = "El nombre del producto es obligatorio")
    private String name;

    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser mayor a 0")
    private Double price;

    @NotNull(message = "El ID de categoría es obligatorio")
    private Long categoryId;

    private boolean active = true;
}
