package com.example.ms_notificaciones.controller;

import com.example.ms_notificaciones.dto.ApiResponse;
import com.example.ms_notificaciones.dto.NotificationRequest;
import com.example.ms_notificaciones.dto.NotificationResponse;
import com.example.ms_notificaciones.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notificaciones")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService service;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<NotificationResponse>> crearNotificacion(
            @Valid @RequestBody NotificationRequest request) {
        return ResponseEntity.status(201).body(
                ApiResponse.<NotificationResponse>builder()
                        .success(true)
                        .message("Notificación creada exitosamente")
                        .data(service.crearNotificacion(request))
                        .build()
        );
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> listarNotificaciones() {
        return ResponseEntity.ok(
                ApiResponse.<List<NotificationResponse>>builder()
                        .success(true)
                        .data(service.listarNotificaciones())
                        .build()
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<NotificationResponse>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.<NotificationResponse>builder()
                        .success(true)
                        .data(service.obtenerNotificacionPorId(id))
                        .build()
        );
    }
}
