package com.example.ms_proveedores.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SupplierResponse {
    private Long id;
    private String name;
    private String ruc;
    private String email;
    private String phone;
    private Boolean active;
}
