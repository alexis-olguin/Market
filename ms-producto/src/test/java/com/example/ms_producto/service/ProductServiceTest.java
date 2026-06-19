package com.example.ms_producto.service;

import com.example.ms_producto.client.ConfigurationClient;
import com.example.ms_producto.dto.ProductDTO;
import com.example.ms_producto.model.Product;
import com.example.ms_producto.repository.ProductRepository;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository repo;

    @Mock
    private ConfigurationClient configClient;

    @InjectMocks
    private ProductService service;

    @Test
    void deberiaCrearProductoCuandoCategoriaActiva() {
        ProductDTO dto = new ProductDTO();
        dto.setName("Arroz");
        dto.setDescription("Arroz superior");
        dto.setPrice(new BigDecimal("4.50"));
        dto.setCategoryId(1L);
        dto.setTaxId(1L);
        dto.setActive(true);

        String token = "Bearer token";
        when(configClient.isCategoryActive(1L, token)).thenReturn(true);
        when(repo.save(any(Product.class))).thenAnswer(invocation -> {
            Product p = invocation.getArgument(0);
            p.setId(1L);
            return p;
        });

        Product creado = service.crear(dto, token);

        assertNotNull(creado.getId());
        assertEquals("Arroz", creado.getName());
        verify(configClient).isCategoryActive(1L, token);
        verify(repo).save(any(Product.class));
    }

    @Test
    void deberiaLanzarExcepcionAlCrearProductoCuandoCategoriaInactiva() {
        ProductDTO dto = new ProductDTO();
        dto.setName("Arroz");
        dto.setCategoryId(1L);

        String token = "Bearer token";
        when(configClient.isCategoryActive(1L, token)).thenReturn(false);

        Exception exception = assertThrows(RuntimeException.class, () -> service.crear(dto, token));
        assertEquals("La categoría no existe o no está activa en ms-configuracion", exception.getMessage());
        verify(repo, never()).save(any(Product.class));
    }

    @Test
    void deberiaRetornarListaProductos() {
        List<Product> productos = List.of(
                new Product(1L, "Arroz", "Arroz superior", new BigDecimal("4.50"), 1L, 1L, true)
        );
        when(repo.findAll()).thenReturn(productos);

        List<Product> resultado = service.listar();

        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        verify(repo).findAll();
    }

    @Test
    void deberiaRetornarProductoCuandoExiste() {
        Product p = new Product(1L, "Arroz", "Arroz superior", new BigDecimal("4.50"), 1L, 1L, true);
        when(repo.findById(1L)).thenReturn(Optional.of(p));

        Product resultado = service.obtener(1L);

        assertNotNull(resultado);
        assertEquals("Arroz", resultado.getName());
        verify(repo).findById(1L);
    }

    @Test
    void deberiaLanzarExcepcionCuandoProductoNoExiste() {
        when(repo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.obtener(1L));
        verify(repo).findById(1L);
    }

    @Test
    void deberiaActualizarProductoCorrectamente() {
        Product pExistente = new Product(1L, "Arroz", "Arroz superior", new BigDecimal("4.50"), 1L, 1L, true);
        
        ProductDTO dto = new ProductDTO();
        dto.setName("Arroz Integral");
        dto.setDescription("Arroz integral superior");
        dto.setPrice(new BigDecimal("5.50"));
        dto.setCategoryId(1L); // No cambia la categoría
        dto.setTaxId(1L);
        dto.setActive(true);

        String token = "Bearer token";
        when(repo.findById(1L)).thenReturn(Optional.of(pExistente));
        when(repo.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Product actualizado = service.actualizar(1L, dto, token);

        assertEquals("Arroz Integral", actualizado.getName());
        assertEquals(new BigDecimal("5.50"), actualizado.getPrice());
        verify(configClient, never()).isCategoryActive(anyLong(), anyString());
        verify(repo).save(any(Product.class));
    }

    @Test
    void deberiaActualizarProductoConNuevaCategoriaActiva() {
        Product pExistente = new Product(1L, "Arroz", "Arroz superior", new BigDecimal("4.50"), 1L, 1L, true);
        
        ProductDTO dto = new ProductDTO();
        dto.setName("Arroz Integral");
        dto.setDescription("Arroz integral superior");
        dto.setPrice(new BigDecimal("5.50"));
        dto.setCategoryId(2L); // Cambia la categoría
        dto.setTaxId(1L);
        dto.setActive(true);

        String token = "Bearer token";
        when(repo.findById(1L)).thenReturn(Optional.of(pExistente));
        when(configClient.isCategoryActive(2L, token)).thenReturn(true);
        when(repo.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Product actualizado = service.actualizar(1L, dto, token);

        assertEquals(2L, actualizado.getCategoryId());
        verify(configClient).isCategoryActive(2L, token);
        verify(repo).save(any(Product.class));
    }

    @Test
    void deberiaLanzarExcepcionAlActualizarProductoConCategoriaInactiva() {
        Product pExistente = new Product(1L, "Arroz", "Arroz superior", new BigDecimal("4.50"), 1L, 1L, true);
        
        ProductDTO dto = new ProductDTO();
        dto.setName("Arroz Integral");
        dto.setCategoryId(2L); // Cambia la categoría

        String token = "Bearer token";
        when(repo.findById(1L)).thenReturn(Optional.of(pExistente));
        when(configClient.isCategoryActive(2L, token)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> service.actualizar(1L, dto, token));
        verify(repo, never()).save(any(Product.class));
    }

    @Test
    void deberiaDesactivarProductoCorrectamente() {
        Product pExistente = new Product(1L, "Arroz", "Arroz superior", new BigDecimal("4.50"), 1L, 1L, true);
        when(repo.findById(1L)).thenReturn(Optional.of(pExistente));
        when(repo.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.desactivar(1L);

        assertFalse(pExistente.isActive());
        verify(repo).save(pExistente);
    }
}
