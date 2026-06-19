package com.example.ms_cliente.controller;

import com.example.ms_cliente.dto.ApiResponse;
import com.example.ms_cliente.dto.CustomerDTO;
import com.example.ms_cliente.model.Customer;
import com.example.ms_cliente.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;

@Tag(name = "Clientes", description = "Operaciones de gestión de clientes del minimarket")
@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService service;

    @Operation(
            summary = "Registrar cliente",
            description = "Registra un nuevo cliente en el sistema. Requiere rol ADMIN."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Cliente registrado con éxito"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado o token inválido"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acceso denegado: permisos insuficientes")
    })
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

    @Operation(
            summary = "Listar clientes",
            description = "Retorna el listado completo de clientes registrados. Requiere rol USER o ADMIN."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Listado de clientes obtenido con éxito"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado o token inválido"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acceso denegado: permisos insuficientes")
    })
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

    @Operation(
            summary = "Obtener cliente por ID",
            description = "Busca los detalles de un cliente específico por su ID y enriquece la respuesta con enlaces HATEOAS. Requiere rol USER o ADMIN."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cliente encontrado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Cliente no encontrado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado o token inválido"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acceso denegado: permisos insuficientes")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<ApiResponse<EntityModel<Customer>>> obtener(
            @Parameter(description = "Identificador único del cliente", example = "1")
            @PathVariable Long id) {
        Customer customer = service.obtener(id);
        EntityModel<Customer> recurso = EntityModel.of(customer);

        recurso.add(linkTo(methodOn(CustomerController.class).obtener(id)).withSelfRel());
        recurso.add(linkTo(methodOn(CustomerController.class).listar()).withRel("all"));
        recurso.add(linkTo(methodOn(CustomerController.class).actualizar(id, null)).withRel("update"));
        recurso.add(linkTo(methodOn(CustomerController.class).desactivar(id)).withRel("delete"));

        return ResponseEntity.ok(
                ApiResponse.<EntityModel<Customer>>builder()
                        .success(true)
                        .message("Cliente encontrado")
                        .data(recurso)
                        .build()
        );
    }

    @Operation(
            summary = "Actualizar cliente",
            description = "Modifica la información de un cliente existente por su ID. Requiere rol ADMIN."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cliente actualizado correctamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Cliente no encontrado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado o token inválido"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acceso denegado: permisos insuficientes")
    })
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

    @Operation(
            summary = "Desactivar cliente",
            description = "Realiza un borrado lógico (desactivación) del cliente. Requiere rol ADMIN."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cliente desactivado correctamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Cliente no encontrado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado o token inválido"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acceso denegado: permisos insuficientes")
    })
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
