package com.example.ms_proveedores.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseOrderResponse {
    private Long id;
    private Long supplierId;
    private String status;
    private LocalDateTime createdAt;
    private BigDecimal total;
    private List<PurchaseOrderItemResponse> items;
}
