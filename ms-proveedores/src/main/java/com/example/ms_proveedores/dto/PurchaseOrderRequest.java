package com.example.ms_proveedores.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseOrderRequest {

    @NotNull(message = "El ID del proveedor es requerido")
    private Long supplierId;

    @NotEmpty(message = "La orden debe contener al menos un producto")
    @Valid
    private List<PurchaseOrderItemRequest> items;
}
