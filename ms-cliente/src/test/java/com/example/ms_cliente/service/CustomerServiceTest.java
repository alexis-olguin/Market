package com.example.ms_cliente.service;

import com.example.ms_cliente.dto.CustomerDTO;
import com.example.ms_cliente.model.Customer;
import com.example.ms_cliente.repository.CustomerRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository repo;

    @InjectMocks
    private CustomerService service;

    @Test
    void deberiaRegistrarClienteExitosamente() {
        CustomerDTO dto = new CustomerDTO();
        dto.setDocumentNumber("12345678");
        dto.setFullName("Juan Perez");
        dto.setEmail("juan@gmail.com");
        dto.setPhone("987654321");
        dto.setActive(true);

        when(repo.existsByDocumentNumber("12345678")).thenReturn(false);
        when(repo.save(any(Customer.class))).thenAnswer(invocation -> {
            Customer c = invocation.getArgument(0);
            c.setId(1L);
            return c;
        });

        Customer registrado = service.registrar(dto);

        assertNotNull(registrado.getId());
        assertEquals("Juan Perez", registrado.getFullName());
        verify(repo).existsByDocumentNumber("12345678");
        verify(repo).save(any(Customer.class));
    }

    @Test
    void deberiaLanzarExcepcionAlRegistrarClienteConDocumentoDuplicado() {
        CustomerDTO dto = new CustomerDTO();
        dto.setDocumentNumber("12345678");

        when(repo.existsByDocumentNumber("12345678")).thenReturn(true);

        Exception exception = assertThrows(RuntimeException.class, () -> service.registrar(dto));
        assertEquals("Ya existe un cliente con ese número de documento", exception.getMessage());
        verify(repo, never()).save(any(Customer.class));
    }

    @Test
    void deberiaRetornarListaClientes() {
        List<Customer> clientes = List.of(
                new Customer(1L, "12345678", "Juan Perez", "juan@gmail.com", "987654321", 0, true)
        );
        when(repo.findAll()).thenReturn(clientes);

        List<Customer> resultado = service.listar();

        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        verify(repo).findAll();
    }

    @Test
    void deberiaRetornarClienteCuandoExiste() {
        Customer c = new Customer(1L, "12345678", "Juan Perez", "juan@gmail.com", "987654321", 0, true);
        when(repo.findById(1L)).thenReturn(Optional.of(c));

        Customer resultado = service.obtener(1L);

        assertNotNull(resultado);
        assertEquals("Juan Perez", resultado.getFullName());
        verify(repo).findById(1L);
    }

    @Test
    void deberiaLanzarExcepcionCuandoClienteNoExiste() {
        when(repo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.obtener(1L));
        verify(repo).findById(1L);
    }

    @Test
    void deberiaActualizarClienteExitosamente() {
        Customer existente = new Customer(1L, "12345678", "Juan Perez", "juan@gmail.com", "987654321", 0, true);
        CustomerDTO dto = new CustomerDTO();
        dto.setDocumentNumber("12345678"); // Mismo documento
        dto.setFullName("Juan Perez Actualizado");
        dto.setEmail("juan_new@gmail.com");
        dto.setPhone("987654322");
        dto.setActive(true);

        when(repo.findById(1L)).thenReturn(Optional.of(existente));
        when(repo.save(any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Customer actualizado = service.actualizar(1L, dto);

        assertEquals("Juan Perez Actualizado", actualizado.getFullName());
        assertEquals("juan_new@gmail.com", actualizado.getEmail());
        verify(repo, never()).existsByDocumentNumber(anyString());
        verify(repo).save(any(Customer.class));
    }

    @Test
    void deberiaActualizarClienteConNuevoDocumentoExitosamente() {
        Customer existente = new Customer(1L, "12345678", "Juan Perez", "juan@gmail.com", "987654321", 0, true);
        CustomerDTO dto = new CustomerDTO();
        dto.setDocumentNumber("87654321"); // Nuevo documento
        dto.setFullName("Juan Perez Actualizado");
        dto.setEmail("juan_new@gmail.com");
        dto.setPhone("987654322");
        dto.setActive(true);

        when(repo.findById(1L)).thenReturn(Optional.of(existente));
        when(repo.existsByDocumentNumber("87654321")).thenReturn(false);
        when(repo.save(any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Customer actualizado = service.actualizar(1L, dto);

        assertEquals("87654321", actualizado.getDocumentNumber());
        verify(repo).existsByDocumentNumber("87654321");
        verify(repo).save(any(Customer.class));
    }

    @Test
    void deberiaLanzarExcepcionAlActualizarClienteConDocumentoDuplicado() {
        Customer existente = new Customer(1L, "12345678", "Juan Perez", "juan@gmail.com", "987654321", 0, true);
        CustomerDTO dto = new CustomerDTO();
        dto.setDocumentNumber("87654321"); // Nuevo documento

        when(repo.findById(1L)).thenReturn(Optional.of(existente));
        when(repo.existsByDocumentNumber("87654321")).thenReturn(true);

        assertThrows(RuntimeException.class, () -> service.actualizar(1L, dto));
        verify(repo).existsByDocumentNumber("87654321");
        verify(repo, never()).save(any(Customer.class));
    }

    @Test
    void deberiaDesactivarClienteCorrectamente() {
        Customer existente = new Customer(1L, "12345678", "Juan Perez", "juan@gmail.com", "987654321", 0, true);
        
        when(repo.findById(1L)).thenReturn(Optional.of(existente));
        when(repo.save(any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.desactivar(1L);

        assertFalse(existente.isActive());
        verify(repo).save(existente);
    }
}
