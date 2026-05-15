package com.example.ms_informes.repository;

import com.example.ms_informes.model.ReportLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportLogRepository extends JpaRepository<ReportLog, Long> {
}
