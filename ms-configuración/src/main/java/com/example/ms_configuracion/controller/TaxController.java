package com.example.ms_configuracion.controller;

import com.example.ms_configuracion.dto.ApiResponse;
import com.example.ms_configuracion.dto.TaxDTO;
import com.example.ms_configuracion.model.Tax;
import com.example.ms_configuracion.service.TaxService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/taxes")
@RequiredArgsConstructor
public class TaxController {

    private final TaxService service;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Tax>> crear(@Valid @RequestBody TaxDTO dto) {
        Tax tax = service.crear(dto);
        return ResponseEntity.status(201).body(
                ApiResponse.<Tax>builder()
                        .success(true)
                        .message("Impuesto creado")
                        .data(tax)
                        .build()
        );
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<ApiResponse<List<Tax>>> listar() {
        return ResponseEntity.ok(
                ApiResponse.<List<Tax>>builder()
                        .success(true)
                        .message("Listado de impuestos obtenido")
                        .data(service.listar())
                        .build()
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<ApiResponse<Tax>> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.<Tax>builder()
                        .success(true)
                        .message("Impuesto obtenido")
                        .data(service.obtener(id))
                        .build()
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Tax>> actualizar(@PathVariable Long id, @Valid @RequestBody TaxDTO dto) {
        Tax tax = service.actualizar(id, dto);
        return ResponseEntity.ok(
                ApiResponse.<Tax>builder()
                        .success(true)
                        .message("Impuesto actualizado")
                        .data(tax)
                        .build()
        );
    }
}
