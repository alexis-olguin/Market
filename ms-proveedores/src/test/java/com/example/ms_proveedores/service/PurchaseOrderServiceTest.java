package com.example.ms_proveedores.service;

import com.example.ms_proveedores.client.InventoryClient;
import com.example.ms_proveedores.client.ProductClient;
import com.example.ms_proveedores.dto.PurchaseOrderItemRequest;
import com.example.ms_proveedores.dto.PurchaseOrderRequest;
import com.example.ms_proveedores.model.PurchaseOrder;
import com.example.ms_proveedores.model.PurchaseOrderItem;
import com.example.ms_proveedores.model.Supplier;
import com.example.ms_proveedores.repository.PurchaseOrderRepository;
import com.example.ms_proveedores.repository.SupplierRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PurchaseOrderServiceTest {

    @Mock
    private PurchaseOrderRepository orderRepository;

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private ProductClient productClient;

    @Mock
    private InventoryClient inventoryClient;

    @InjectMocks
    private PurchaseOrderService service;

    @Test
    void crearOrdenCalculatesTotalForActiveSupplier() {
        Supplier supplier = new Supplier(1L, "Distribuidora", "111", "ventas@proveedor.cl", "999", true);
        PurchaseOrderRequest request = new PurchaseOrderRequest(1L, List.of(
                new PurchaseOrderItemRequest(10L, 3, BigDecimal.valueOf(1500))
        ));

        when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier));
        when(productClient.obtenerProducto(10L, "token")).thenReturn(Map.of("id", 10L));
        when(orderRepository.save(any(PurchaseOrder.class))).thenAnswer(invocation -> {
            PurchaseOrder order = invocation.getArgument(0);
            order.setId(5L);
            return order;
        });

        var response = service.crearOrden(request, "token");

        assertThat(response.getTotal()).isEqualByComparingTo(BigDecimal.valueOf(4500));
        assertThat(response.getItems()).hasSize(1);
    }

    @Test
    void crearOrdenRejectsInactiveSupplier() {
        Supplier supplier = new Supplier(1L, "Distribuidora", "111", "ventas@proveedor.cl", "999", false);
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier));

        assertThatThrownBy(() -> service.crearOrden(new PurchaseOrderRequest(1L, List.of()), "token"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("activo");
    }

    @Test
    void recibirOrdenUpdatesInventoryForEachItem() {
        PurchaseOrderItem item = new PurchaseOrderItem(1L, 10L, 4, BigDecimal.TEN);
        PurchaseOrder order = new PurchaseOrder(7L, 1L, "CREATED", LocalDateTime.now(), BigDecimal.valueOf(40), List.of(item));

        when(orderRepository.findById(7L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(PurchaseOrder.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = service.recibirOrden(7L, "token");

        assertThat(response.getStatus()).isEqualTo("RECEIVED");
        verify(inventoryClient).aumentarStock(10L, 4, "token");
    }
}
