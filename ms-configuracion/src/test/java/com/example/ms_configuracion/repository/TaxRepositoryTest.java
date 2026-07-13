package com.example.ms_configuracion.repository;

import com.example.ms_configuracion.model.Tax;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TaxRepositoryTest {

    @Autowired
    private TaxRepository repository;

    @Test
    void debeGuardarImpuesto() {
        Tax impuesto = new Tax(null, "IGV", new BigDecimal("18.00"), true);

        Tax guardado = repository.save(impuesto);

        assertNotNull(guardado.getId());
        assertEquals("IGV", guardado.getName());
        assertEquals(0, guardado.getPercentage().compareTo(new BigDecimal("18.00")));
    }

    @Test
    void debeBuscarImpuestoPorId() {
        Tax impuesto = new Tax(null, "Exento", new BigDecimal("0.00"), true);
        Tax guardado = repository.save(impuesto);

        Optional<Tax> resultado = repository.findById(guardado.getId());

        assertTrue(resultado.isPresent());
        assertEquals("Exento", resultado.get().getName());
    }

    @Test
    void debeListarImpuestos() {
        repository.save(new Tax(null, "IVA", new BigDecimal("19.00"), true));
        repository.save(new Tax(null, "Retencion", new BigDecimal("10.00"), true));

        List<Tax> resultado = repository.findAll();

        assertFalse(resultado.isEmpty());
        assertTrue(resultado.size() >= 2);
    }
}
