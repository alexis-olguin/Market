package com.example.ms_configuracion.service;

import com.example.ms_configuracion.dto.CategoryDTO;
import com.example.ms_configuracion.model.Category;
import com.example.ms_configuracion.repository.CategoryRepository;
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
class CategoryServiceTest {

    @Mock
    private CategoryRepository repo;

    @InjectMocks
    private CategoryService service;

    @Test
    void deberiaCrearCategoria() {
        CategoryDTO dto = new CategoryDTO();
        dto.setName("Bebidas");
        dto.setDescription("Todo tipo de bebidas");
        dto.setActive(true);

        when(repo.save(any(Category.class))).thenAnswer(invocation -> {
            Category c = invocation.getArgument(0);
            c.setId(1L);
            return c;
        });

        Category creada = service.crear(dto);

        assertNotNull(creada.getId());
        assertEquals("Bebidas", creada.getName());
        verify(repo).save(any(Category.class));
    }

    @Test
    void deberiaRetornarListaCategorias() {
        List<Category> categorias = List.of(
                new Category(1L, "Bebidas", "Bebidas desc", true)
        );
        when(repo.findAll()).thenReturn(categorias);

        List<Category> resultado = service.listar();

        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        verify(repo).findAll();
    }

    @Test
    void deberiaRetornarCategoriaCuandoExiste() {
        Category category = new Category(1L, "Bebidas", "Bebidas desc", true);
        when(repo.findById(1L)).thenReturn(Optional.of(category));

        Category resultado = service.obtener(1L);

        assertNotNull(resultado);
        assertEquals("Bebidas", resultado.getName());
        verify(repo).findById(1L);
    }

    @Test
    void deberiaLanzarExcepcionCuandoCategoriaNoExiste() {
        when(repo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.obtener(1L));
        verify(repo).findById(1L);
    }

    @Test
    void deberiaActualizarCategoriaCorrectamente() {
        Category existente = new Category(1L, "Bebidas", "Bebidas desc", true);
        CategoryDTO dto = new CategoryDTO();
        dto.setName("Bebidas Gasificadas");
        dto.setDescription("Gaseosas y aguas");
        dto.setActive(true);

        when(repo.findById(1L)).thenReturn(Optional.of(existente));
        when(repo.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Category actualizada = service.actualizar(1L, dto);

        assertEquals("Bebidas Gasificadas", actualizada.getName());
        assertEquals("Gaseosas y aguas", actualizada.getDescription());
        verify(repo).save(any(Category.class));
    }

    @Test
    void deberiaRetornarEstaActivaTrue() {
        Category category = new Category(1L, "Bebidas", "Bebidas desc", true);
        when(repo.findById(1L)).thenReturn(Optional.of(category));

        boolean activa = service.estaActiva(1L);

        assertTrue(activa);
        verify(repo).findById(1L);
    }

    @Test
    void deberiaRetornarEstaActivaFalseCuandoInactiva() {
        Category category = new Category(1L, "Bebidas", "Bebidas desc", false);
        when(repo.findById(1L)).thenReturn(Optional.of(category));

        boolean activa = service.estaActiva(1L);

        assertFalse(activa);
        verify(repo).findById(1L);
    }

    @Test
    void deberiaRetornarEstaActivaFalseCuandoNoExiste() {
        when(repo.findById(1L)).thenReturn(Optional.empty());

        boolean activa = service.estaActiva(1L);

        assertFalse(activa);
        verify(repo).findById(1L);
    }
}
