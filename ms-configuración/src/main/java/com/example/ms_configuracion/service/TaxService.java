package com.example.ms_configuracion.service;

import com.example.ms_configuracion.dto.TaxDTO;
import com.example.ms_configuracion.model.Tax;
import com.example.ms_configuracion.repository.TaxRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;
import static net.logstash.logback.argument.StructuredArguments.keyValue;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaxService {

    private final TaxRepository repo;

    public Tax crear(TaxDTO dto) {
        log.info("Crear impuesto", keyValue("nombre", dto.getName()));
        Tax t = new Tax(null, dto.getName(), dto.getPercentage(), dto.isActive());
        return repo.save(t);
    }

    public List<Tax> listar() {
        log.info("Listar impuestos");
        return repo.findAll();
    }

    public Tax obtener(Long id) {
        log.info("Obtener impuesto", keyValue("id", id));
        return repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Impuesto no encontrado"));
    }

    public Tax actualizar(Long id, TaxDTO dto) {
        log.info("Actualizar impuesto", keyValue("id", id));
        Tax t = obtener(id);
        t.setName(dto.getName());
        t.setPercentage(dto.getPercentage());
        t.setActive(dto.isActive());
        return repo.save(t);
    }
}
