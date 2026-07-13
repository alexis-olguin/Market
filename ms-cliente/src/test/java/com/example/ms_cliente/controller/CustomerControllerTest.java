package com.example.ms_cliente.controller;

import com.example.ms_cliente.dto.CustomerDTO;
import com.example.ms_cliente.model.Customer;
import com.example.ms_cliente.service.CustomerService;
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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
@AutoConfigureMockMvc(addFilters = false)
public class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CustomerService service;

    @MockitoBean
    private com.example.ms_cliente.security.JwtUtil jwtUtil;

    @Test
    public void debeListarClientes() throws Exception {
        List<Customer> clientes = List.of(
                new Customer(1L, "12345678", "Juan Perez", "juan@gmail.com", "987654321", 0, true)
        );

        when(service.listar()).thenReturn(clientes);

        mockMvc.perform(get("/api/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Listado de clientes obtenido"))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].fullName").value("Juan Perez"));
    }

    @Test
    public void debeObtenerClientePorId() throws Exception {
        Customer customer = new Customer(1L, "12345678", "Juan Perez", "juan@gmail.com", "987654321", 0, true);

        when(service.obtener(1L)).thenReturn(customer);

        // El controller retorna EntityModel<Customer> (HATEOAS), los datos
        // del objeto están en $.data.content.* y los links en $.data._links.*
        mockMvc.perform(get("/api/customers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Cliente encontrado"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.fullName").value("Juan Perez"));
    }

    @Test
    public void debeRegistrarCliente() throws Exception {
        CustomerDTO dto = new CustomerDTO();
        dto.setDocumentNumber("12345678");
        dto.setFullName("Juan Perez");
        dto.setEmail("juan@gmail.com");
        dto.setPhone("987654321");
        dto.setActive(true);

        Customer creado = new Customer(1L, "12345678", "Juan Perez", "juan@gmail.com", "987654321", 0, true);

        when(service.registrar(any(CustomerDTO.class))).thenReturn(creado);

        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Cliente registrado exitosamente"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.fullName").value("Juan Perez"));
    }

    @Test
    public void debeActualizarCliente() throws Exception {
        CustomerDTO dto = new CustomerDTO();
        dto.setDocumentNumber("12345678");
        dto.setFullName("Juan Perez Actualizado");
        dto.setEmail("juan_new@gmail.com");
        dto.setPhone("987654322");
        dto.setActive(true);

        Customer actualizado = new Customer(1L, "12345678", "Juan Perez Actualizado", "juan_new@gmail.com", "987654322", 0, true);

        when(service.actualizar(eq(1L), any(CustomerDTO.class))).thenReturn(actualizado);

        mockMvc.perform(put("/api/customers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Cliente actualizado correctamente"))
                .andExpect(jsonPath("$.data.fullName").value("Juan Perez Actualizado"));
    }

    @Test
    public void debeDesactivarCliente() throws Exception {
        doNothing().when(service).desactivar(1L);

        mockMvc.perform(delete("/api/customers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Cliente desactivado"));
    }
}
