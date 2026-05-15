package com.example.ms_pagos.controller;

import com.example.ms_pagos.dto.ApiResponse;
import com.example.ms_pagos.dto.PaymentRequestDTO;
import com.example.ms_pagos.model.Payment;
import com.example.ms_pagos.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService service;

    @PostMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<ApiResponse<Payment>> create(@Valid @RequestBody PaymentRequestDTO dto) {
        Payment payment = service.processPayment(dto);
        return ResponseEntity.status(201).body(
                ApiResponse.<Payment>builder()
                        .success(true)
                        .message("Pago procesado: " + payment.getStatus())
                        .data(payment)
                        .build()
        );
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<Payment>>> list() {
        return ResponseEntity.ok(
                ApiResponse.<List<Payment>>builder()
                        .success(true)
                        .message("Listado de pagos obtenido")
                        .data(service.getAll())
                        .build()
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<ApiResponse<Payment>> get(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.<Payment>builder()
                        .success(true)
                        .message("Pago encontrado")
                        .data(service.getById(id))
                        .build()
        );
    }
}
