package com.scanner.scanservice.service.impl;

import com.scanner.scanservice.model.Scan;
import com.scanner.scanservice.model.ScanReport;
import com.scanner.scanservice.repository.ScanReportRepository;
import com.scanner.scanservice.repository.ScanRepository;
import com.scanner.scanservice.repository.VulnerabilityFindingRepository;
import com.scanner.scanservice.service.ScanService;
import com.scanner.scanservice.service.exceptions.NotFoundException;
import com.scanner.scanservice.service.exceptions.ScanNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ScanServiceImpl implements ScanService {

    private final ScanRepository scanRepository;
    private final VulnerabilityFindingRepository vulnerabilityFindingRepository;
    private final ScanReportRepository scanReportRepository;

    @Override
    public UUID createScan(String imageName) {
        Scan scan = new Scan();
        scan.setImageName(imageName);
        return scanRepository.save(scan).getId();
    }

    @Override
    public Scan getScan(UUID scanId) {
        return scanRepository.findById(scanId).orElseThrow(() -> new ScanNotFoundException(scanId.toString()));
    }

    @Override
    public Page<Scan> listScans(Pageable pageable) {
        return scanRepository.findAll(pageable);
    }

    @Override
    public long countFindings(UUID reportId) {
        return vulnerabilityFindingRepository.countByScanReport_Scan_Id(reportId);
    }

    @Override
    public String getRawReportJson(UUID scanId) {
        return scanReportRepository.findByScan_Id(scanId)
                .map(ScanReport::getRawJson)
                .orElseThrow(() -> new NotFoundException("No raw report for scan" + scanId));
    }

    @Override
    public String getSbomJson(UUID scanId) {
        return scanReportRepository.findByScan_Id(scanId)
                .map(ScanReport::getSbomJson)
                .orElseThrow(() -> new NotFoundException("No SBOM for scan " + scanId));
    }
}
