package com.example.ms_producto.controller;

import com.example.ms_producto.dto.ApiResponse;
import com.example.ms_producto.dto.ProductDTO;
import com.example.ms_producto.model.Product;
import com.example.ms_producto.service.ProductService;
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

@Tag(name = "Productos", description = "Operaciones de catálogo para productos del minimarket")
@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService service;

    @Operation(
            summary = "Crear producto",
            description = "Registra un nuevo producto en el catálogo. Requiere rol ADMIN."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Producto creado exitosamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Solicitud o datos de entrada inválidos"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado o token inválido"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acceso denegado: permisos insuficientes")
    })
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

    @Operation(
            summary = "Listar productos",
            description = "Retorna el listado completo de productos registrados. Requiere rol USER o ADMIN."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Listado de productos obtenido con éxito"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado o token inválido"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acceso denegado: permisos insuficientes")
    })
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

    @Operation(
            summary = "Obtener producto por ID",
            description = "Busca un producto por su ID y retorna la información enriquecida con enlaces hipermedia HATEOAS. Requiere rol USER o ADMIN."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Producto encontrado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Producto no encontrado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado o token inválido"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acceso denegado: permisos insuficientes")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<ApiResponse<EntityModel<Product>>> obtener(
            @Parameter(description = "Identificador único del producto", example = "1")
            @PathVariable Long id) {
        Product product = service.obtener(id);
        EntityModel<Product> recurso = EntityModel.of(product);

        // Enlaces HATEOAS
        recurso.add(linkTo(methodOn(ProductController.class).obtener(id)).withSelfRel());
        recurso.add(linkTo(methodOn(ProductController.class).listar()).withRel("all"));
        recurso.add(linkTo(methodOn(ProductController.class).actualizar(id, null, null)).withRel("update"));
        recurso.add(linkTo(methodOn(ProductController.class).desactivar(id)).withRel("delete"));

        return ResponseEntity.ok(
                ApiResponse.<EntityModel<Product>>builder()
                        .success(true)
                        .message("Producto encontrado")
                        .data(recurso)
                        .build()
        );
    }

    @Operation(
            summary = "Actualizar producto",
            description = "Modifica los datos de un producto existente. Requiere rol ADMIN."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Producto actualizado correctamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Producto no encontrado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado o token inválido"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acceso denegado: permisos insuficientes")
    })
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

    @Operation(
            summary = "Desactivar producto",
            description = "Realiza un borrado lógico (desactivación) del producto usando su ID. Requiere rol ADMIN."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Producto desactivado correctamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Producto no encontrado"),
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
                        .message("Producto desactivado (Soft Delete)")
                        .build()
        );
    }
}
