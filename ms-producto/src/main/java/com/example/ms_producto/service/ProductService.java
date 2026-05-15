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
        log.info("Intentando crear producto", keyValue("nombre", dto.getName()));

        // Validar categoría en configuration-service (puerto 8090)
        if (!configClient.isCategoryActive(dto.getCategoryId(), token)) {
            log.error("Categoría inválida o inactiva", keyValue("categoryId", dto.getCategoryId()));
            throw new RuntimeException("La categoría no existe o no está activa en ms-configuracion");
        }

        Product p = new Product(null, dto.getName(), dto.getDescription(), dto.getPrice(), 
                                dto.getCategoryId(), dto.getTaxId(), dto.isActive());
        return repo.save(p);
    }

    public List<Product> listar() {
        log.info("Listando productos");
        return repo.findAll();
    }

    public Product obtener(Long id) {
        log.info("Obteniendo producto", keyValue("id", id));
        return repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado"));
    }

    public Product actualizar(Long id, ProductDTO dto, String token) {
        log.info("Actualizando producto", keyValue("id", id));
        Product p = obtener(id);

        if (!p.getCategoryId().equals(dto.getCategoryId())) {
            if (!configClient.isCategoryActive(dto.getCategoryId(), token)) {
                throw new RuntimeException("La nueva categoría no existe o no está activa");
            }
        }

        p.setName(dto.getName());
        p.setDescription(dto.getDescription());
        p.setPrice(dto.getPrice());
        p.setCategoryId(dto.getCategoryId());
        p.setTaxId(dto.getTaxId());
        p.setActive(dto.isActive());

        return repo.save(p);
    }

    public void desactivar(Long id) {
        log.warn("Desactivación lógica de producto", keyValue("id", id));
        Product p = obtener(id);
        p.setActive(false);
        repo.save(p);
    }
}
