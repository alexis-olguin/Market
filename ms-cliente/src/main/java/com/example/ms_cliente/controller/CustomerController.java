package com.example.ms_cliente.controller;

import com.example.ms_cliente.dto.ApiResponse;
import com.example.ms_cliente.dto.CustomerDTO;
import com.example.ms_cliente.model.Customer;
import com.example.ms_cliente.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService service;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Customer>> registrar(@Valid @RequestBody CustomerDTO dto) {
        Customer customer = service.registrar(dto);
        return ResponseEntity.status(201).body(
                ApiResponse.<Customer>builder()
                        .success(true)
                        .message("Cliente registrado exitosamente")
                        .data(customer)
                        .build()
        );
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<ApiResponse<List<Customer>>> listar() {
        return ResponseEntity.ok(
                ApiResponse.<List<Customer>>builder()
                        .success(true)
                        .message("Listado de clientes obtenido")
                        .data(service.listar())
                        .build()
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<ApiResponse<Customer>> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.<Customer>builder()
                        .success(true)
                        .message("Cliente encontrado")
                        .data(service.obtener(id))
                        .build()
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Customer>> actualizar(@PathVariable Long id, @Valid @RequestBody CustomerDTO dto) {
        Customer customer = service.actualizar(id, dto);
        return ResponseEntity.ok(
                ApiResponse.<Customer>builder()
                        .success(true)
                        .message("Cliente actualizado correctamente")
                        .data(customer)
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
                        .message("Cliente desactivado")
                        .build()
        );
    }
}
