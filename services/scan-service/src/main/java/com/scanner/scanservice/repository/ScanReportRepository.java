package com.scanner.scanservice.repository;

import com.scanner.scanservice.model.ScanReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ScanReportRepository extends JpaRepository<ScanReport, UUID> {
}
