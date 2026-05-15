package com.example.ms_configuracion.controller;

import com.example.ms_configuracion.dto.ApiResponse;
import com.example.ms_configuracion.dto.CategoryDTO;
import com.example.ms_configuracion.model.Category;
import com.example.ms_configuracion.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService service;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Category>> crear(@Valid @RequestBody CategoryDTO dto) {
        Category category = service.crear(dto);
        return ResponseEntity.status(201).body(
                ApiResponse.<Category>builder()
                        .success(true)
                        .message("Categoría creada")
                        .data(category)
                        .build()
        );
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<ApiResponse<List<Category>>> listar() {
        return ResponseEntity.ok(
                ApiResponse.<List<Category>>builder()
                        .success(true)
                        .message("Listado de categorías obtenido")
                        .data(service.listar())
                        .build()
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<ApiResponse<Category>> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.<Category>builder()
                        .success(true)
                        .message("Categoría obtenida")
                        .data(service.obtener(id))
                        .build()
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Category>> actualizar(@PathVariable Long id, @Valid @RequestBody CategoryDTO dto) {
        Category category = service.actualizar(id, dto);
        return ResponseEntity.ok(
                ApiResponse.<Category>builder()
                        .success(true)
                        .message("Categoría actualizada")
                        .data(category)
                        .build()
        );
    }

    @GetMapping("/{id}/active")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> estaActiva(@PathVariable Long id) {
        boolean active = service.estaActiva(id);
        return ResponseEntity.ok(
                ApiResponse.<Boolean>builder()
                        .success(true)
                        .message(active ? "Categoría activa" : "Categoría inactiva o no existe")
                        .data(active)
                        .build()
        );
    }
}
