package com.example.ms_proveedores.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.ms_proveedores.dto.ApiResponse;
import com.example.ms_proveedores.dto.PurchaseOrderRequest;
import com.example.ms_proveedores.dto.PurchaseOrderResponse;
import com.example.ms_proveedores.service.PurchaseOrderService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/purchase-orders")
@RequiredArgsConstructor
public class PurchaseOrderController {

    private final PurchaseOrderService service;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<PurchaseOrderResponse>> crearOrden(
            @Valid @RequestBody PurchaseOrderRequest request,
            @RequestHeader("Authorization") String token) {
        
        String rawToken = token.startsWith("Bearer ") ? token.substring(7) : token;

        return ResponseEntity.status(201).body(
                ApiResponse.<PurchaseOrderResponse>builder()
                        .success(true)
                        .message("Orden de compra creada")
                        .data(service.crearOrden(request, rawToken))
                        .build()
        );
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<ApiResponse<List<PurchaseOrderResponse>>> listarOrdenes() {
        return ResponseEntity.ok(
                ApiResponse.<List<PurchaseOrderResponse>>builder()
                        .success(true)
                        .data(service.listarOrdenes())
                        .build()
        );
    }

    @PutMapping("/{id}/receive")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<PurchaseOrderResponse>> recibirOrden(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {

        String rawToken = token.startsWith("Bearer ") ? token.substring(7) : token;

        return ResponseEntity.ok(
                ApiResponse.<PurchaseOrderResponse>builder()
                        .success(true)
                        .message("Orden de compra recibida")
                        .data(service.recibirOrden(id, rawToken))
                        .build()
        );
    }
}
