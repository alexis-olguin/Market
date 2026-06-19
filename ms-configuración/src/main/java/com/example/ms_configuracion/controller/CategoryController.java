package com.example.ms_configuracion.controller;

import com.example.ms_configuracion.dto.ApiResponse;
import com.example.ms_configuracion.dto.CategoryDTO;
import com.example.ms_configuracion.model.Category;
import com.example.ms_configuracion.service.CategoryService;
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

@Tag(name = "Categorías", description = "Operaciones de configuración para categorías de productos")
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService service;

    @Operation(
            summary = "Crear categoría",
            description = "Crea una nueva categoría en el sistema. Requiere rol ADMIN."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Categoría creada con éxito"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado o token inválido"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acceso denegado: permisos insuficientes")
    })
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

    @Operation(
            summary = "Listar categorías",
            description = "Obtiene la lista completa de todas las categorías. Requiere rol USER o ADMIN."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Listado de categorías obtenido con éxito"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado o token inválido"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acceso denegado: permisos insuficientes")
    })
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

    @Operation(
            summary = "Obtener categoría por ID",
            description = "Busca los detalles de una categoría específica por su ID. La respuesta está enriquecida con enlaces HATEOAS. Requiere rol USER o ADMIN."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Categoría encontrada"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Categoría no encontrada"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado o token inválido"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acceso denegado: permisos insuficientes")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<ApiResponse<EntityModel<Category>>> obtener(
            @Parameter(description = "Identificador único de la categoría", example = "1")
            @PathVariable Long id) {
        Category category = service.obtener(id);
        EntityModel<Category> recurso = EntityModel.of(category);

        recurso.add(linkTo(methodOn(CategoryController.class).obtener(id)).withSelfRel());
        recurso.add(linkTo(methodOn(CategoryController.class).listar()).withRel("all"));
        recurso.add(linkTo(methodOn(CategoryController.class).actualizar(id, null)).withRel("update"));

        return ResponseEntity.ok(
                ApiResponse.<EntityModel<Category>>builder()
                        .success(true)
                        .message("Categoría obtenida")
                        .data(recurso)
                        .build()
        );
    }

    @Operation(
            summary = "Actualizar categoría",
            description = "Modifica los datos de una categoría usando su ID. Requiere rol ADMIN."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Categoría actualizada con éxito"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Categoría no encontrada"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado o token inválido"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acceso denegado: permisos insuficientes")
    })
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

    @Operation(
            summary = "Verificar si la categoría está activa",
            description = "Comprueba si una categoría específica está habilitada en el sistema. Requiere rol USER o ADMIN."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Estado de actividad verificado exitosamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado o token inválido"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acceso denegado: permisos insuficientes")
    })
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
