package com.example.ms_ventas.controller;

import com.example.ms_ventas.dto.*;
import com.example.ms_ventas.model.Sale;
import com.example.ms_ventas.service.SaleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/sales")
@RequiredArgsConstructor
public class SaleController {

    private final SaleService service;

    @PostMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<ApiResponse<Sale>> crear(@Valid @RequestBody SaleDTO dto,
                                                  @RequestHeader("Authorization") String token) {
        Sale sale = service.procesarVenta(dto, token);
        return ResponseEntity.status(201).body(
                ApiResponse.<Sale>builder()
                        .success(true)
                        .message("Venta realizada exitosamente")
                        .data(sale)
                        .build()
        );
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<ApiResponse<List<Sale>>> listar() {
        return ResponseEntity.ok(
                ApiResponse.<List<Sale>>builder()
                        .success(true)
                        .message("Listado de ventas obtenido")
                        .data(service.listar())
                        .build()
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<ApiResponse<Sale>> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.<Sale>builder()
                        .success(true)
                        .message("Venta encontrada")
                        .data(service.obtener(id))
                        .build()
        );
    }
}
