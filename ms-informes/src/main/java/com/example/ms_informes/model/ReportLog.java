package com.example.ms_informes.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "report_logs")
@Data
public class ReportLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String reportType; // SALES_DAILY, CRITICAL_STOCK, SUMMARY

    @Column(nullable = false)
    private LocalDateTime generatedAt;

    @Column(nullable = false, length = 500)
    private String description;
}
