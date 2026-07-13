package com.example.ms_configuracion.controller;

import com.example.ms_configuracion.dto.CategoryDTO;
import com.example.ms_configuracion.model.Category;
import com.example.ms_configuracion.service.CategoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
@AutoConfigureMockMvc(addFilters = false)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CategoryService service;

    @MockitoBean
    private com.example.ms_configuracion.security.JwtUtil jwtUtil;

    @Test
    void debeListarCategorias() throws Exception {
        List<Category> categorias = List.of(
                new Category(1L, "Bebidas", "Todo tipo de bebidas", true)
        );

        when(service.listar()).thenReturn(categorias);

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Listado de categorías obtenido"))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].name").value("Bebidas"));
    }

    @Test
    void debeObtenerCategoriaPorId() throws Exception {
        Category category = new Category(1L, "Bebidas", "Todo tipo de bebidas", true);

        when(service.obtener(1L)).thenReturn(category);

        // El controller retorna EntityModel<Category> (HATEOAS), los datos
        // del objeto están en $.data.content.* y los links en $.data._links.*
        mockMvc.perform(get("/api/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Categoría obtenida"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("Bebidas"));
    }

    @Test
    void debeCrearCategoria() throws Exception {
        CategoryDTO dto = new CategoryDTO();
        dto.setName("Bebidas");
        dto.setDescription("Todo tipo de bebidas");
        dto.setActive(true);

        Category creada = new Category(1L, "Bebidas", "Todo tipo de bebidas", true);

        when(service.crear(any(CategoryDTO.class))).thenReturn(creada);

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Categoría creada"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("Bebidas"));
    }

    @Test
    void debeActualizarCategoria() throws Exception {
        CategoryDTO dto = new CategoryDTO();
        dto.setName("Bebidas Gasificadas");
        dto.setDescription("Gaseosas y aguas");
        dto.setActive(true);

        Category actualizada = new Category(1L, "Bebidas Gasificadas", "Gaseosas y aguas", true);

        when(service.actualizar(eq(1L), any(CategoryDTO.class))).thenReturn(actualizada);

        mockMvc.perform(put("/api/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Categoría actualizada"))
                .andExpect(jsonPath("$.data.name").value("Bebidas Gasificadas"));
    }

    @Test
    void debeVerificarSiEstaActiva() throws Exception {
        when(service.estaActiva(1L)).thenReturn(true);

        mockMvc.perform(get("/api/categories/1/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Categoría activa"))
                .andExpect(jsonPath("$.data").value(true));
    }
}
