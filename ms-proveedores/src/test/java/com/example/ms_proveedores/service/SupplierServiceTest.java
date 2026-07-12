package com.example.ms_proveedores.service;

import com.example.ms_proveedores.dto.SupplierRequest;
import com.example.ms_proveedores.model.Supplier;
import com.example.ms_proveedores.repository.SupplierRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SupplierServiceTest {

    @Mock
    private SupplierRepository repository;

    @InjectMocks
    private SupplierService service;

    @Test
    void crearProveedorStoresActiveSupplier() {
        Supplier saved = new Supplier(1L, "Proveedor", "123", "contacto@proveedor.cl", "999", true);
        when(repository.save(any(Supplier.class))).thenReturn(saved);

        var response = service.crearProveedor(new SupplierRequest("Proveedor", "123", "contacto@proveedor.cl", "999"));

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getActive()).isTrue();
    }

    @Test
    void listarProveedoresMapsRepositoryResults() {
        when(repository.findAll()).thenReturn(List.of(
                new Supplier(1L, "Proveedor", "123", "contacto@proveedor.cl", "999", true)
        ));

        var response = service.listarProveedores();

        assertThat(response).hasSize(1);
        assertThat(response.get(0).getName()).isEqualTo("Proveedor");
    }
}
