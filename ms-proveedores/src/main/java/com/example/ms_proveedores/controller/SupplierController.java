package com.example.ms_proveedores.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.ms_proveedores.dto.ApiResponse;
import com.example.ms_proveedores.dto.SupplierRequest;
import com.example.ms_proveedores.dto.SupplierResponse;
import com.example.ms_proveedores.service.SupplierService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService service;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<SupplierResponse>> crearProveedor(@Valid @RequestBody SupplierRequest request) {
        return ResponseEntity.status(201).body(
                ApiResponse.<SupplierResponse>builder()
                        .success(true)
                        .message("Proveedor creado")
                        .data(service.crearProveedor(request))
                        .build()
        );
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<ApiResponse<List<SupplierResponse>>> listarProveedores() {
        return ResponseEntity.ok(
                ApiResponse.<List<SupplierResponse>>builder()
                        .success(true)
                        .data(service.listarProveedores())
                        .build()
        );
    }
}
