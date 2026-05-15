package com.example.ms_informes.service;

import com.example.ms_informes.client.InventoryClient;
import com.example.ms_informes.client.SaleClient;
import com.example.ms_informes.model.ReportLog;
import com.example.ms_informes.repository.ReportLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static net.logstash.logback.argument.StructuredArguments.keyValue;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {

    private final ReportLogRepository reportLogRepo;
    private final SaleClient saleClient;
    private final InventoryClient inventoryClient;

    public List<Object> getDailySalesReport(String token) {
        log.info("Generando reporte de ventas diarias");
        List<Object> sales = saleClient.getDailySales(token);
        
        saveLog("SALES_DAILY", "Reporte de ventas generado con " + sales.size() + " registros");
        return sales;
    }

    public List<Object> getCriticalStockReport(String token) {
        log.info("Generando reporte de stock crítico");
        List<Object> stocks = inventoryClient.getCriticalStock(token);
        
        saveLog("CRITICAL_STOCK", "Reporte de stock crítico generado con " + stocks.size() + " productos");
        return stocks;
    }

    public Map<String, Object> getSummaryReport(String token) {
        log.info("Generando reporte resumen");
        List<Object> sales = saleClient.getDailySales(token);
        List<Object> stocks = inventoryClient.getCriticalStock(token);

        Map<String, Object> summary = new HashMap<>();
        summary.put("totalSales", sales.size());
        summary.put("criticalProductsCount", stocks.size());
        summary.put("generatedAt", LocalDateTime.now());

        saveLog("SUMMARY", "Reporte resumen generado");
        return summary;
    }

    private void saveLog(String type, String desc) {
        ReportLog reportLog = new ReportLog();
        reportLog.setReportType(type);
        reportLog.setGeneratedAt(LocalDateTime.now());
        reportLog.setDescription(desc);
        reportLogRepo.save(reportLog);
        log.info("Log de reporte guardado", keyValue("type", type));
    }
}
