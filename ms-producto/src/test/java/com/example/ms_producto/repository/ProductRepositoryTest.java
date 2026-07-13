package com.example.ms_producto.repository;

import com.example.ms_producto.model.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProductRepositoryTest {

    @Autowired
    private ProductRepository repository;

    @Test
    void debeGuardarProducto() {
        Product producto = new Product(null, "Arroz", "Arroz superior", new BigDecimal("4.50"), 1L, 1L, true);

        Product guardado = repository.save(producto);

        assertNotNull(guardado.getId());
        assertEquals("Arroz", guardado.getName());
        assertEquals(0, guardado.getPrice().compareTo(new BigDecimal("4.50")));
    }

    @Test
    void debeBuscarProductoPorId() {
        Product producto = new Product(null, "Leche", "Leche entera", new BigDecimal("3.20"), 1L, 1L, true);
        Product guardado = repository.save(producto);

        Optional<Product> resultado = repository.findById(guardado.getId());

        assertTrue(resultado.isPresent());
        assertEquals("Leche", resultado.get().getName());
    }

    @Test
    void debeListarProductos() {
        repository.save(new Product(null, "Fideos", "Fideos delgados", new BigDecimal("2.50"), 1L, 1L, true));
        repository.save(new Product(null, "Aceite", "Aceite vegetal", new BigDecimal("8.90"), 1L, 1L, true));

        List<Product> resultado = repository.findAll();

        assertFalse(resultado.isEmpty());
        assertTrue(resultado.size() >= 2);
    }

    @Test
    void debeEliminarProducto() {
        Product producto = new Product(null, "Azucar", "Azucar blanca", new BigDecimal("3.80"), 1L, 1L, true);
        Product guardado = repository.save(producto);

        repository.deleteById(guardado.getId());

        Optional<Product> resultado = repository.findById(guardado.getId());
        assertFalse(resultado.isPresent());
    }
}
