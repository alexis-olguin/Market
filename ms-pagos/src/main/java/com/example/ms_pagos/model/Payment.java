package com.example.ms_pagos.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String saleReference;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private String method; // CASH, CARD, TRANSFER

    @Column(nullable = false)
    private String status; // APPROVED, REJECTED

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
