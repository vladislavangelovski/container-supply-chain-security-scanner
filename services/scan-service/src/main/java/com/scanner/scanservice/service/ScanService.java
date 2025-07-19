package com.scanner.scanservice.service;

import com.scanner.scanservice.model.Scan;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ScanService {
    UUID createScan(String imageName);
    Scan getScan(UUID scanId);
    Page<Scan> listScans(Pageable pageable);
    long countFindings(UUID reportId);
    String getRawReportJson(UUID scanId);
    String getSbomJson(UUID scanId);
}
