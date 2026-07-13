package com.example.ms_cliente.repository;

import com.example.ms_cliente.model.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository repository;

    @Test
    void debeGuardarCliente() {
        Customer cliente = new Customer(null, "12345678", "Juan Perez", "juan@gmail.com", "987654321", 0, true);

        Customer guardado = repository.save(cliente);

        assertNotNull(guardado.getId());
        assertEquals("Juan Perez", guardado.getFullName());
    }

    @Test
    void debeBuscarClientePorId() {
        Customer cliente = new Customer(null, "87654321", "Maria Gomez", "maria@gmail.com", "987654322", 0, true);
        Customer guardado = repository.save(cliente);

        Optional<Customer> resultado = repository.findById(guardado.getId());

        assertTrue(resultado.isPresent());
        assertEquals("Maria Gomez", resultado.get().getFullName());
    }

    @Test
    void debeBuscarClientePorDocumentNumber() {
        Customer cliente = new Customer(null, "11112222", "Carlos Ruiz", "carlos@gmail.com", "987654323", 0, true);
        repository.save(cliente);

        Optional<Customer> resultado = repository.findByDocumentNumber("11112222");

        assertTrue(resultado.isPresent());
        assertEquals("Carlos Ruiz", resultado.get().getFullName());
    }

    @Test
    void debeVerificarSiExistePorDocumentNumber() {
        Customer cliente = new Customer(null, "33334444", "Ana Lopez", "ana@gmail.com", "987654324", 0, true);
        repository.save(cliente);

        boolean existe = repository.existsByDocumentNumber("33334444");
        boolean noExiste = repository.existsByDocumentNumber("99999999");

        assertTrue(existe);
        assertFalse(noExiste);
    }

    @Test
    void debeListarClientes() {
        repository.save(new Customer(null, "55556666", "Pedro Picapiedra", "pedro@gmail.com", "987654325", 0, true));
        repository.save(new Customer(null, "77778888", "Pablo Marmol", "pablo@gmail.com", "987654326", 0, true));

        List<Customer> resultado = repository.findAll();

        assertFalse(resultado.isEmpty());
        assertTrue(resultado.size() >= 2);
    }
}
