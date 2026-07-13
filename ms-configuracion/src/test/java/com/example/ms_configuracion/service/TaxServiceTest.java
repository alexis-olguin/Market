package com.example.ms_configuracion.service;

import com.example.ms_configuracion.dto.TaxDTO;
import com.example.ms_configuracion.model.Tax;
import com.example.ms_configuracion.repository.TaxRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaxServiceTest {

    @Mock
    private TaxRepository repo;

    @InjectMocks
    private TaxService service;

    @Test
    void deberiaCrearImpuesto() {
        TaxDTO dto = new TaxDTO();
        dto.setName("IGV");
        dto.setPercentage(new BigDecimal("18.00"));
        dto.setActive(true);

        when(repo.save(any(Tax.class))).thenAnswer(invocation -> {
            Tax t = invocation.getArgument(0);
            t.setId(1L);
            return t;
        });

        Tax creado = service.crear(dto);

        assertNotNull(creado.getId());
        assertEquals("IGV", creado.getName());
        assertEquals(0, creado.getPercentage().compareTo(new BigDecimal("18.00")));
        verify(repo).save(any(Tax.class));
    }

    @Test
    void deberiaRetornarListaImpuestos() {
        List<Tax> impuestos = List.of(
                new Tax(1L, "IGV", new BigDecimal("18.00"), true)
        );
        when(repo.findAll()).thenReturn(impuestos);

        List<Tax> resultado = service.listar();

        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        verify(repo).findAll();
    }

    @Test
    void deberiaRetornarImpuestoCuandoExiste() {
        Tax impuesto = new Tax(1L, "IGV", new BigDecimal("18.00"), true);
        when(repo.findById(1L)).thenReturn(Optional.of(impuesto));

        Tax resultado = service.obtener(1L);

        assertNotNull(resultado);
        assertEquals("IGV", resultado.getName());
        verify(repo).findById(1L);
    }

    @Test
    void deberiaLanzarExcepcionCuandoImpuestoNoExiste() {
        when(repo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.obtener(1L));
        verify(repo).findById(1L);
    }

    @Test
    void deberiaActualizarImpuestoCorrectamente() {
        Tax existente = new Tax(1L, "IGV", new BigDecimal("18.00"), true);
        TaxDTO dto = new TaxDTO();
        dto.setName("IGV Especial");
        dto.setPercentage(new BigDecimal("10.00"));
        dto.setActive(true);

        when(repo.findById(1L)).thenReturn(Optional.of(existente));
        when(repo.save(any(Tax.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Tax actualizado = service.actualizar(1L, dto);

        assertEquals("IGV Especial", actualizado.getName());
        assertEquals(new BigDecimal("10.00"), actualizado.getPercentage());
        verify(repo).save(any(Tax.class));
    }
}
