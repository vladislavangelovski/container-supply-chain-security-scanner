package com.scanner.scanservice.repository;

import com.scanner.scanservice.model.ScanReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ScanReportRepository extends JpaRepository<ScanReport, UUID> {
}
