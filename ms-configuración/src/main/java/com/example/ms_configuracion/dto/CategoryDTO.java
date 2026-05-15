package com.example.ms_configuracion.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CategoryDTO {
    @NotBlank(message = "El nombre de la categoría es obligatorio")
    private String name;

    private String description;

    private boolean active = true;
}
