package com.example.ms_inventario.service;

import com.example.ms_inventario.client.NotificationClient;
import com.example.ms_inventario.client.ProductClient;
import com.example.ms_inventario.dto.InventoryRequest;
import com.example.ms_inventario.dto.MovementRequest;
import com.example.ms_inventario.model.Inventory;
import com.example.ms_inventario.repository.InventoryMovementRepository;
import com.example.ms_inventario.repository.InventoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepo;

    @Mock
    private InventoryMovementRepository movementRepo;

    @Mock
    private ProductClient productClient;

    @Mock
    private NotificationClient notificationClient;

    @InjectMocks
    private InventoryService service;

    @Test
    void registerInitialStockValidatesProductAndCreatesInventory() {
        InventoryRequest request = new InventoryRequest(100L, 20, 5);
        when(productClient.obtenerProducto(100L, "token")).thenReturn(Map.of("id", 100L));
        when(inventoryRepo.findByProductId(100L)).thenReturn(Optional.empty());
        when(inventoryRepo.save(any(Inventory.class))).thenReturn(new Inventory(1L, 100L, 20, 5));

        var response = service.registerInitialStock(request, "token");

        assertThat(response.getCurrentStock()).isEqualTo(20);
        verify(movementRepo).save(any());
    }

    @Test
    void registerExitRejectsInsufficientStock() {
        when(inventoryRepo.findByProductId(100L)).thenReturn(Optional.of(new Inventory(1L, 100L, 2, 5)));

        assertThatThrownBy(() -> service.registerExit(100L, new MovementRequest(3, "Venta"), "token"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Stock insuficiente");
    }

    @Test
    void registerExitSendsCriticalStockNotification() {
        Inventory inventory = new Inventory(1L, 100L, 6, 5);
        when(inventoryRepo.findByProductId(100L)).thenReturn(Optional.of(inventory));
        when(inventoryRepo.save(any(Inventory.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = service.registerExit(100L, new MovementRequest(1, "Venta"), "token");

        assertThat(response.getCurrentStock()).isEqualTo(5);
        verify(notificationClient).enviarAlertaStockCritico(any(), any());
    }

    @Test
    void registerEntryIncreasesCurrentStock() {
        when(inventoryRepo.findByProductId(100L)).thenReturn(Optional.of(new Inventory(1L, 100L, 5, 2)));
        when(inventoryRepo.save(any(Inventory.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = service.registerEntry(100L, new MovementRequest(7, "Compra"));

        assertThat(response.getCurrentStock()).isEqualTo(12);
        verify(movementRepo).save(any());
    }

    @Test
    void getCriticalStockReturnsOnlyProductsAtMinimum() {
        when(inventoryRepo.findAll()).thenReturn(List.of(
                new Inventory(1L, 100L, 2, 5),
                new Inventory(2L, 200L, 10, 5)
        ));

        var response = service.getCriticalStock();

        assertThat(response).hasSize(1);
        assertThat(response.get(0).getProductId()).isEqualTo(100L);
    }

    @Test
    void getInventoryByProductFailsWhenMissing() {
        when(inventoryRepo.findByProductId(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getInventoryByProduct(404L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Inventario no encontrado");
    }
}
