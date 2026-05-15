package com.example.ms_notificaciones.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "notifications")
public class Notification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String type; // LOW_STOCK, PROMOTION, SYSTEM

    @Column(nullable = false)
    private String message;

    @Column(nullable = false)
    private String status; // CREATED, SENT

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
