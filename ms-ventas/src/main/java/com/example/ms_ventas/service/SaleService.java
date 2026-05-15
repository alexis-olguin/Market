package com.example.ms_ventas.service;

import com.example.ms_ventas.client.*;
import com.example.ms_ventas.dto.*;
import com.example.ms_ventas.model.*;
import com.example.ms_ventas.repository.SaleRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import static net.logstash.logback.argument.StructuredArguments.keyValue;

@Service
@RequiredArgsConstructor
@Slf4j
public class SaleService {

    private final SaleRepository saleRepo;
    private final ProductClient productClient;
    private final InventoryClient inventoryClient;
    private final PaymentClient paymentClient;
    private final CustomerClient customerClient;

    @Transactional
    public Sale procesarVenta(SaleDTO dto, String token) {
        log.info("Iniciando proceso de venta", keyValue("userId", dto.getUserId()));

        // 1. Validar Cliente (opcional)
        if (dto.getCustomerId() != null) {
            if (!customerClient.existsCustomer(dto.getCustomerId(), token)) {
                throw new RuntimeException("Cliente no encontrado en ms-cliente");
            }
        }

        BigDecimal totalVenta = BigDecimal.ZERO;
        List<SaleItem> itemsParaGuardar = new ArrayList<>();

        // 2. Validar Productos y Stock
        for (SaleItemDTO itemDto : dto.getItems()) {
            Map<String, Object> productData = productClient.getProduct(itemDto.getProductId(), token);
            if (productData == null) {
                throw new RuntimeException("Producto no encontrado: " + itemDto.getProductId());
            }

            if (!inventoryClient.checkStock(itemDto.getProductId(), itemDto.getQuantity(), token)) {
                throw new RuntimeException("Stock insuficiente para el producto: " + itemDto.getProductId());
            }

            BigDecimal unitPrice = new BigDecimal(productData.get("price").toString());
            BigDecimal subtotal = unitPrice.multiply(new BigDecimal(itemDto.getQuantity()));
            totalVenta = totalVenta.add(subtotal);

            SaleItem item = new SaleItem();
            item.setProductId(itemDto.getProductId());
            item.setQuantity(itemDto.getQuantity());
            item.setUnitPrice(unitPrice);
            item.setSubtotal(subtotal);
            itemsParaGuardar.add(item);
        }

        // 3. Procesar Pago
        String paymentStatus = paymentClient.processPayment(totalVenta, token);
        log.info("Estado del pago", keyValue("status", paymentStatus));

        if (!"APPROVED".equals(paymentStatus)) {
            throw new RuntimeException("El pago fue rechazado. Estado: " + paymentStatus);
        }

        // 4. Registrar Venta
        Sale sale = new Sale();
        sale.setCustomerId(dto.getCustomerId());
        sale.setUserId(dto.getUserId());
        sale.setTotal(totalVenta);
        sale.setPaymentStatus(paymentStatus);
        sale.setCreatedAt(LocalDateTime.now());
        
        // Asociar items
        for (SaleItem item : itemsParaGuardar) {
            item.setSale(sale);
        }
        sale.setItems(itemsParaGuardar);

        Sale saleGuardada = saleRepo.save(sale);

        // 5. Descontar Inventario
        for (SaleItem item : itemsParaGuardar) {
            inventoryClient.deductStock(item.getProductId(), item.getQuantity(), token);
        }

        log.info("Venta procesada exitosamente", keyValue("saleId", saleGuardada.getId()));
        return saleGuardada;
    }

    public List<Sale> listar() {
        return saleRepo.findAll();
    }

    public Sale obtener(Long id) {
        return saleRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Venta no encontrada"));
    }
}
