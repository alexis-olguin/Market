package com.example.ms_inventario.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.ms_inventario.dto.*;
import com.example.ms_inventario.service.InventoryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/inventario")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService service;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<InventoryResponse>> registerInitialStock(
            @Valid @RequestBody InventoryRequest request,
            @RequestHeader("Authorization") String token) {
        
        String rawToken = token.startsWith("Bearer ") ? token.substring(7) : token;

        return ResponseEntity.status(201).body(
                ApiResponse.<InventoryResponse>builder()
                        .success(true)
                        .message("Inventario inicial registrado")
                        .data(service.registerInitialStock(request, rawToken))
                        .build()
        );
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<ApiResponse<List<InventoryResponse>>> getAllInventory() {
        return ResponseEntity.ok(
                ApiResponse.<List<InventoryResponse>>builder()
                        .success(true)
                        .data(service.getAllInventory())
                        .build()
        );
    }

    @GetMapping("/product/{productId}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<ApiResponse<InventoryResponse>> getInventoryByProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(
                ApiResponse.<InventoryResponse>builder()
                        .success(true)
                        .data(service.getInventoryByProduct(productId))
                        .build()
        );
    }

    @PutMapping("/product/{productId}/stock")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<InventoryResponse>> registerEntry(
            @PathVariable Long productId,
            @Valid @RequestBody MovementRequest request) {
        return ResponseEntity.ok(
                ApiResponse.<InventoryResponse>builder()
                        .success(true)
                        .message("Entrada de stock registrada")
                        .data(service.registerEntry(productId, request))
                        .build()
        );
    }

    @PostMapping("/movements")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<ApiResponse<InventoryResponse>> registerExit(
            @RequestParam Long productId,
            @Valid @RequestBody MovementRequest request,
            @RequestHeader("Authorization") String token) {
        
        String rawToken = token.startsWith("Bearer ") ? token.substring(7) : token;
        return ResponseEntity.ok(
                ApiResponse.<InventoryResponse>builder()
                        .success(true)
                        .message("Salida de stock registrada")
                        .data(service.registerExit(productId, request, rawToken))
                        .build()
        );
    }

    @GetMapping("/stock-critico")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<InventoryResponse>>> getCriticalStock() {
        return ResponseEntity.ok(
                ApiResponse.<List<InventoryResponse>>builder()
                        .success(true)
                        .data(service.getCriticalStock())
                        .build()
        );
    }
}
