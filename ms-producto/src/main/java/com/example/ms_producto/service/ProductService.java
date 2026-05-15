package com.example.ms_producto.service;

import com.example.ms_producto.client.ConfigurationClient;
import com.example.ms_producto.dto.ProductDTO;
import com.example.ms_producto.model.Product;
import com.example.ms_producto.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;
import static net.logstash.logback.argument.StructuredArguments.keyValue;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository repo;
    private final ConfigurationClient configClient;

    public Product crear(ProductDTO dto, String token) {
        log.info("Crear producto", keyValue("nombre", dto.getName()));

        // Validar categoría en configuration-service
        if (!configClient.isCategoryActive(dto.getCategoryId(), token)) {
            throw new RuntimeException("La categoría no existe o no está activa");
        }

        Product p = new Product(null, dto.getName(), dto.getPrice(), dto.getCategoryId(), dto.isActive());
        return repo.save(p);
    }

    public List<Product> listar() {
        log.info("Listar productos");
        return repo.findAll();
    }

    public Product obtener(Long id) {
        log.info("Obtener producto", keyValue("id", id));
        return repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado"));
    }

    public Product actualizar(Long id, ProductDTO dto, String token) {
        log.info("Actualizar producto", keyValue("id", id));

        Product p = obtener(id);

        // Validar categoría si cambió
        if (!p.getCategoryId().equals(dto.getCategoryId())) {
            if (!configClient.isCategoryActive(dto.getCategoryId(), token)) {
                throw new RuntimeException("La nueva categoría no existe o no está activa");
            }
        }

        p.setName(dto.getName());
        p.setPrice(dto.getPrice());
        p.setCategoryId(dto.getCategoryId());
        p.setActive(dto.isActive());

        return repo.save(p);
    }

    public void desactivar(Long id) {
        log.warn("Desactivar producto", keyValue("id", id));
        Product p = obtener(id);
        p.setActive(false);
        repo.save(p);
    }
}
