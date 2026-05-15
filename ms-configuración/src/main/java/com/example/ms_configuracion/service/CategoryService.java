package com.example.ms_configuracion.service;

import com.example.ms_configuracion.dto.CategoryDTO;
import com.example.ms_configuracion.model.Category;
import com.example.ms_configuracion.repository.CategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;
import static net.logstash.logback.argument.StructuredArguments.keyValue;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {

    private final CategoryRepository repo;

    public Category crear(CategoryDTO dto) {
        log.info("Crear categoría", keyValue("nombre", dto.getName()));
        
        Category c = new Category(null, dto.getName(), dto.getDescription(), dto.isActive());
        return repo.save(c);
    }

    public List<Category> listar() {
        log.info("Listar categorías");
        return repo.findAll();
    }

    public Category obtener(Long id) {
        log.info("Obtener categoría", keyValue("id", id));
        return repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Categoría no encontrada"));
    }

    public Category actualizar(Long id, CategoryDTO dto) {
        log.info("Actualizar categoría", keyValue("id", id));
        Category c = obtener(id);
        c.setName(dto.getName());
        c.setDescription(dto.getDescription());
        c.setActive(dto.isActive());
        return repo.save(c);
    }

    public boolean estaActiva(Long id) {
        log.info("Validar si categoría está activa", keyValue("id", id));
        return repo.findById(id)
                .map(Category::isActive)
                .orElse(false);
    }
}
