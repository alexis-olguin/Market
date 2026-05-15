package com.example.ms_cliente.service;

import com.example.ms_cliente.dto.CustomerDTO;
import com.example.ms_cliente.model.Customer;
import com.example.ms_cliente.repository.CustomerRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;
import static net.logstash.logback.argument.StructuredArguments.keyValue;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerService {

    private final CustomerRepository repo;

    public Customer registrar(CustomerDTO dto) {
        log.info("Registrando nuevo cliente", keyValue("documento", dto.getDocumentNumber()));

        if (repo.existsByDocumentNumber(dto.getDocumentNumber())) {
            throw new RuntimeException("Ya existe un cliente con ese número de documento");
        }

        Customer c = new Customer(null, dto.getDocumentNumber(), dto.getFullName(), 
                                  dto.getEmail(), dto.getPhone(), 0, true);
        return repo.save(c);
    }

    public List<Customer> listar() {
        log.info("Listando todos los clientes");
        return repo.findAll();
    }

    public Customer obtener(Long id) {
        log.info("Buscando cliente por ID", keyValue("id", id));
        return repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado"));
    }

    public Customer actualizar(Long id, CustomerDTO dto) {
        log.info("Actualizando cliente", keyValue("id", id));
        Customer c = obtener(id);

        if (!c.getDocumentNumber().equals(dto.getDocumentNumber())) {
            if (repo.existsByDocumentNumber(dto.getDocumentNumber())) {
                throw new RuntimeException("El nuevo número de documento ya está en uso");
            }
        }

        c.setDocumentNumber(dto.getDocumentNumber());
        c.setFullName(dto.getFullName());
        c.setEmail(dto.getEmail());
        c.setPhone(dto.getPhone());
        c.setActive(dto.isActive());
        
        // Puntos no se actualizan por aquí generalmente, pero se mantienen
        if (dto.getPoints() != null) c.setPoints(dto.getPoints());

        return repo.save(c);
    }

    public void desactivar(Long id) {
        log.warn("Desactivando cliente (Soft Delete)", keyValue("id", id));
        Customer c = obtener(id);
        c.setActive(false);
        repo.save(c);
    }
}
