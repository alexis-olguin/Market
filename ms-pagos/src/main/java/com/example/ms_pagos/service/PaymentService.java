package com.example.ms_pagos.service;

import com.example.ms_pagos.dto.PaymentRequestDTO;
import com.example.ms_pagos.model.Payment;
import com.example.ms_pagos.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import static net.logstash.logback.argument.StructuredArguments.keyValue;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepo;
    private final List<String> validMethods = Arrays.asList("CASH", "CARD", "TRANSFER");

    public Payment processPayment(PaymentRequestDTO dto) {
        log.info("Procesando pago para referencia", keyValue("saleReference", dto.getSaleReference()));

        Payment payment = new Payment();
        payment.setSaleReference(dto.getSaleReference());
        payment.setAmount(dto.getAmount());
        payment.setMethod(dto.getMethod().toUpperCase());
        payment.setCreatedAt(LocalDateTime.now());

        // Simulación de lógica de aprobación
        if (dto.getAmount().compareTo(BigDecimal.ZERO) > 0 && validMethods.contains(dto.getMethod().toUpperCase())) {
            payment.setStatus("APPROVED");
            log.info("Pago APROBADO", keyValue("amount", dto.getAmount()));
        } else {
            payment.setStatus("REJECTED");
            log.warn("Pago RECHAZADO", keyValue("reason", "Monto inválido o método no soportado"));
        }

        return paymentRepo.save(payment);
    }

    public List<Payment> getAll() {
        return paymentRepo.findAll();
    }

    public Payment getById(Long id) {
        return paymentRepo.findById(id).orElseThrow(() -> new RuntimeException("Pago no encontrado"));
    }
}
