package com.example.ms_configuracion.repository;

import com.example.ms_configuracion.model.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository repository;

    @Test
    void debeGuardarCategoria() {
        Category categoria = new Category(null, "Bebidas", "Todo tipo de bebidas", true);

        Category guardada = repository.save(categoria);

        assertNotNull(guardada.getId());
        assertEquals("Bebidas", guardada.getName());
    }

    @Test
    void debeBuscarCategoriaPorId() {
        Category categoria = new Category(null, "Lácteos", "Leches y quesos", true);
        Category guardada = repository.save(categoria);

        Optional<Category> resultado = repository.findById(guardada.getId());

        assertTrue(resultado.isPresent());
        assertEquals("Lácteos", resultado.get().getName());
    }

    @Test
    void debeListarCategorias() {
        repository.save(new Category(null, "Snacks", "Papas fritas y dulces", true));
        repository.save(new Category(null, "Limpieza", "Detergentes y jabón", true));

        List<Category> resultado = repository.findAll();

        assertFalse(resultado.isEmpty());
        assertTrue(resultado.size() >= 2);
    }
}
