package com.example.ms_proveedores.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SupplierRequest {
    @NotBlank(message = "El nombre es requerido")
    private String name;

    @NotBlank(message = "El RUC es requerido")
    private String ruc;

    @NotBlank(message = "El email es requerido")
    @Email(message = "Debe ser un email válido")
    private String email;

    private String phone;
}
