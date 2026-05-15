package com.example.ms_informes.controller;

import com.example.ms_informes.dto.ApiResponse;
import com.example.ms_informes.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService service;

    @GetMapping("/sales-daily")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<Object>>> getSalesDaily(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(
                ApiResponse.<List<Object>>builder()
                        .success(true)
                        .message("Reporte de ventas diarias generado")
                        .data(service.getDailySalesReport(token))
                        .build()
        );
    }

    @GetMapping("/critical-stock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<Object>>> getCriticalStock(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(
                ApiResponse.<List<Object>>builder()
                        .success(true)
                        .message("Reporte de stock crítico generado")
                        .data(service.getCriticalStockReport(token))
                        .build()
        );
    }

    @GetMapping("/summary")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSummary(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(
                ApiResponse.<Map<String, Object>>builder()
                        .success(true)
                        .message("Reporte resumen generado")
                        .data(service.getSummaryReport(token))
                        .build()
        );
    }
}
