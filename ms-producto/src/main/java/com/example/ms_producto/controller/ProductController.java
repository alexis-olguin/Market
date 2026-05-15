package com.example.ms_producto.controller;

import com.example.ms_producto.dto.ApiResponse;
import com.example.ms_producto.dto.ProductDTO;
import com.example.ms_producto.model.Product;
import com.example.ms_producto.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService service;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Product>> crear(@Valid @RequestBody ProductDTO dto,
                                                     @RequestHeader("Authorization") String token) {
        Product product = service.crear(dto, token);
        return ResponseEntity.status(201).body(
                ApiResponse.<Product>builder()
                        .success(true)
                        .message("Producto creado exitosamente")
                        .data(product)
                        .build()
        );
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<ApiResponse<List<Product>>> listar() {
        return ResponseEntity.ok(
                ApiResponse.<List<Product>>builder()
                        .success(true)
                        .message("Listado de productos obtenido")
                        .data(service.listar())
                        .build()
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<ApiResponse<Product>> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.<Product>builder()
                        .success(true)
                        .message("Producto encontrado")
                        .data(service.obtener(id))
                        .build()
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Product>> actualizar(@PathVariable Long id,
                                                          @Valid @RequestBody ProductDTO dto,
                                                          @RequestHeader("Authorization") String token) {
        Product product = service.actualizar(id, dto, token);
        return ResponseEntity.ok(
                ApiResponse.<Product>builder()
                        .success(true)
                        .message("Producto actualizado correctamente")
                        .data(product)
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        service.desactivar(id);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Producto desactivado (Soft Delete)")
                        .build()
        );
    }
}
