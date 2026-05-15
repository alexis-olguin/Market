package com.example.ms_auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    @NotBlank(message = "Username es requerido")
    private String username;

    @NotBlank(message = "Password es requerido")
    private String password;

    @NotBlank(message = "Email es requerido")
    @Email(message = "Email inválido")
    private String email;

    @NotNull(message = "Role ID es requerido")
    private Long roleId;
}
