package com.example.ms_configuracion.controller;

import com.example.ms_configuracion.dto.ApiResponse;
import com.example.ms_configuracion.dto.TaxDTO;
import com.example.ms_configuracion.model.Tax;
import com.example.ms_configuracion.service.TaxService;
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

@Tag(name = "Impuestos", description = "Operaciones de configuración para impuestos del minimarket")
@RestController
@RequestMapping("/api/taxes")
@RequiredArgsConstructor
public class TaxController {

    private final TaxService service;

    @Operation(
            summary = "Crear impuesto",
            description = "Registra una nueva regla de impuesto en el sistema. Requiere rol ADMIN."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Impuesto creado exitosamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado o token inválido"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acceso denegado: permisos insuficientes")
    })
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

    @Operation(
            summary = "Listar impuestos",
            description = "Obtiene todos los impuestos registrados. Requiere rol USER o ADMIN."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Listado de impuestos obtenido con éxito"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado o token inválido"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acceso denegado: permisos insuficientes")
    })
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

    @Operation(
            summary = "Obtener impuesto por ID",
            description = "Busca los detalles de un impuesto por su ID. La respuesta está enriquecida con enlaces HATEOAS. Requiere rol USER o ADMIN."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Impuesto obtenido exitosamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Impuesto no encontrado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado o token inválido"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acceso denegado: permisos insuficientes")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<ApiResponse<EntityModel<Tax>>> obtener(
            @Parameter(description = "Identificador único del impuesto", example = "1")
            @PathVariable Long id) {
        Tax tax = service.obtener(id);
        EntityModel<Tax> recurso = EntityModel.of(tax);

        recurso.add(linkTo(methodOn(TaxController.class).obtener(id)).withSelfRel());
        recurso.add(linkTo(methodOn(TaxController.class).listar()).withRel("all"));
        recurso.add(linkTo(methodOn(TaxController.class).actualizar(id, null)).withRel("update"));

        return ResponseEntity.ok(
                ApiResponse.<EntityModel<Tax>>builder()
                        .success(true)
                        .message("Impuesto obtenido")
                        .data(recurso)
                        .build()
        );
    }

    @Operation(
            summary = "Actualizar impuesto",
            description = "Modifica los datos de un impuesto existente por su ID. Requiere rol ADMIN."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Impuesto actualizado correctamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Impuesto no encontrado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado o token inválido"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acceso denegado: permisos insuficientes")
    })
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
