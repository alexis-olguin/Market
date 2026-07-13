package com.example.ms_configuracion.controller;

import com.example.ms_configuracion.dto.TaxDTO;
import com.example.ms_configuracion.model.Tax;
import com.example.ms_configuracion.service.TaxService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.math.BigDecimal;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaxController.class)
@AutoConfigureMockMvc(addFilters = false)
class TaxControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TaxService service;

    @MockitoBean
    private com.example.ms_configuracion.security.JwtUtil jwtUtil;

    @Test
    void debeListarImpuestos() throws Exception {
        List<Tax> impuestos = List.of(
                new Tax(1L, "IGV", new BigDecimal("18.00"), true)
        );

        when(service.listar()).thenReturn(impuestos);

        mockMvc.perform(get("/api/taxes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Listado de impuestos obtenido"))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].name").value("IGV"))
                .andExpect(jsonPath("$.data[0].percentage").value(18.00));
    }

    @Test
    void debeObtenerImpuestoPorId() throws Exception {
        Tax tax = new Tax(1L, "IGV", new BigDecimal("18.00"), true);

        when(service.obtener(1L)).thenReturn(tax);

        // El controller retorna EntityModel<Tax> (HATEOAS), los datos
        // del objeto están en $.data.content.* y los links en $.data._links.*
        mockMvc.perform(get("/api/taxes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Impuesto obtenido"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("IGV"));
    }

    @Test
    void debeCrearImpuesto() throws Exception {
        TaxDTO dto = new TaxDTO();
        dto.setName("IGV");
        dto.setPercentage(new BigDecimal("18.00"));
        dto.setActive(true);

        Tax creado = new Tax(1L, "IGV", new BigDecimal("18.00"), true);

        when(service.crear(any(TaxDTO.class))).thenReturn(creado);

        mockMvc.perform(post("/api/taxes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Impuesto creado"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("IGV"));
    }

    @Test
    void debeActualizarImpuesto() throws Exception {
        TaxDTO dto = new TaxDTO();
        dto.setName("IGV Especial");
        dto.setPercentage(new BigDecimal("10.00"));
        dto.setActive(true);

        Tax actualizado = new Tax(1L, "IGV Especial", new BigDecimal("10.00"), true);

        when(service.actualizar(eq(1L), any(TaxDTO.class))).thenReturn(actualizado);

        mockMvc.perform(put("/api/taxes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Impuesto actualizado"))
                .andExpect(jsonPath("$.data.name").value("IGV Especial"));
    }
}
