package com.example.ms_proveedores.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ms_proveedores.client.InventoryClient;
import com.example.ms_proveedores.client.ProductClient;
import com.example.ms_proveedores.dto.PurchaseOrderItemRequest;
import com.example.ms_proveedores.dto.PurchaseOrderItemResponse;
import com.example.ms_proveedores.dto.PurchaseOrderRequest;
import com.example.ms_proveedores.dto.PurchaseOrderResponse;
import com.example.ms_proveedores.model.PurchaseOrder;
import com.example.ms_proveedores.model.PurchaseOrderItem;
import com.example.ms_proveedores.model.Supplier;
import com.example.ms_proveedores.repository.PurchaseOrderRepository;
import com.example.ms_proveedores.repository.SupplierRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PurchaseOrderService {

    private final PurchaseOrderRepository orderRepository;
    private final SupplierRepository supplierRepository;
    private final ProductClient productClient;
    private final InventoryClient inventoryClient;

    @Transactional
    public PurchaseOrderResponse crearOrden(PurchaseOrderRequest request, String token) {
        log.info("Creando orden de compra para proveedor: {}", request.getSupplierId());

        Supplier supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(() -> new IllegalArgumentException("Proveedor no encontrado"));

        if (!supplier.getActive()) {
            throw new IllegalArgumentException("El proveedor debe estar activo para crear una orden");
        }

        BigDecimal total = BigDecimal.ZERO;
        
        PurchaseOrder order = new PurchaseOrder();
        order.setSupplierId(supplier.getId());
        order.setStatus("CREATED");
        order.setCreatedAt(LocalDateTime.now());
        
        List<PurchaseOrderItem> items = request.getItems().stream().map(itemReq -> {
            var product = productClient.obtenerProducto(itemReq.getProductId(), token);
            if (product == null) {
                throw new IllegalArgumentException("Producto ID " + itemReq.getProductId() + " no encontrado en product-service");
            }
            
            PurchaseOrderItem item = new PurchaseOrderItem();
            item.setProductId(itemReq.getProductId());
            item.setQuantity(itemReq.getQuantity());
            item.setUnitCost(itemReq.getUnitCost());
            return item;
        }).collect(Collectors.toList());

        for (PurchaseOrderItem item : items) {
            total = total.add(item.getUnitCost().multiply(new BigDecimal(item.getQuantity())));
        }

        order.setTotal(total);
        order.setItems(items);
        items.forEach(item -> item.setPurchaseOrder(order));
        PurchaseOrder savedOrder = orderRepository.save(order);

        return mapToResponse(savedOrder);
    }

    public List<PurchaseOrderResponse> listarOrdenes() {
        return orderRepository.findAll().stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Transactional
    public PurchaseOrderResponse recibirOrden(Long id, String token) {
        log.info("Recibiendo orden de compra: {}", id);

        PurchaseOrder order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Orden de compra no encontrada"));

        if (!"CREATED".equals(order.getStatus())) {
            throw new IllegalArgumentException("La orden no está en estado CREATED, estado actual: " + order.getStatus());
        }

        order.setStatus("RECEIVED");
        order = orderRepository.save(order);

        for (PurchaseOrderItem item : order.getItems()) {
            inventoryClient.aumentarStock(item.getProductId(), item.getQuantity(), token);
        }

        return mapToResponse(order);
    }

    private PurchaseOrderResponse mapToResponse(PurchaseOrder order) {
        return PurchaseOrderResponse.builder()
                .id(order.getId())
                .supplierId(order.getSupplierId())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .total(order.getTotal())
                .items(order.getItems().stream().map(item -> PurchaseOrderItemResponse.builder()
                        .id(item.getId())
                        .productId(item.getProductId())
                        .quantity(item.getQuantity())
                        .unitCost(item.getUnitCost())
                        .build()).collect(Collectors.toList()))
                .build();
    }
}
