package com.example.ms_producto.controller;

import com.example.ms_producto.dto.ProductDTO;
import com.example.ms_producto.model.Product;
import com.example.ms_producto.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.math.BigDecimal;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductService service;

    @Test
    void debeListarProductos() throws Exception {
        List<Product> productos = List.of(
                new Product(1L, "Arroz", "Arroz superior", new BigDecimal("4.50"), 1L, 1L, true)
        );

        when(service.listar()).thenReturn(productos);

        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Listado de productos obtenido"))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].name").value("Arroz"))
                .andExpect(jsonPath("$.data[0].price").value(4.50));
    }

    @Test
    void debeObtenerProductoPorId() throws Exception {
        Product producto = new Product(1L, "Leche", "Leche entera", new BigDecimal("3.20"), 1L, 1L, true);

        when(service.obtener(1L)).thenReturn(producto);

        // El controller retorna EntityModel<Product> (HATEOAS), por eso los datos
        // del objeto están en $.data.content.* y los links en $.data._links.*
        mockMvc.perform(get("/api/productos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Producto encontrado"))
                .andExpect(jsonPath("$.data.content.id").value(1))
                .andExpect(jsonPath("$.data.content.name").value("Leche"));
    }

    @Test
    void debeCrearProducto() throws Exception {
        ProductDTO dto = new ProductDTO();
        dto.setName("Fideos");
        dto.setDescription("Fideos delgados");
        dto.setPrice(new BigDecimal("2.50"));
        dto.setCategoryId(1L);
        dto.setTaxId(1L);
        dto.setActive(true);

        Product creado = new Product(1L, "Fideos", "Fideos delgados", new BigDecimal("2.50"), 1L, 1L, true);

        String token = "Bearer test_token";
        when(service.crear(any(ProductDTO.class), eq(token))).thenReturn(creado);

        mockMvc.perform(post("/api/productos")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Producto creado exitosamente"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("Fideos"));
    }

    @Test
    void debeActualizarProducto() throws Exception {
        ProductDTO dto = new ProductDTO();
        dto.setName("Aceite");
        dto.setDescription("Aceite vegetal");
        dto.setPrice(new BigDecimal("8.90"));
        dto.setCategoryId(1L);
        dto.setTaxId(1L);
        dto.setActive(true);

        Product actualizado = new Product(1L, "Aceite", "Aceite vegetal", new BigDecimal("8.90"), 1L, 1L, true);

        String token = "Bearer test_token";
        when(service.actualizar(eq(1L), any(ProductDTO.class), eq(token))).thenReturn(actualizado);

        mockMvc.perform(put("/api/productos/1")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Producto actualizado correctamente"))
                .andExpect(jsonPath("$.data.name").value("Aceite"));
    }

    @Test
    void debeDesactivarProducto() throws Exception {
        doNothing().when(service).desactivar(1L);

        mockMvc.perform(delete("/api/productos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Producto desactivado (Soft Delete)"));
    }
}
