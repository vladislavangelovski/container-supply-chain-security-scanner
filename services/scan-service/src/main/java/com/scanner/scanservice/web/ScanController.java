package com.scanner.scanservice.web;

import com.scanner.scanservice.service.ScanService;
import com.scanner.scanservice.service.VulnerabilityFindingService;
import com.scanner.scanservice.web.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/scan")
@RequiredArgsConstructor
public class ScanController {
    private final ScanService scanService;
    private final VulnerabilityFindingService vulnerabilityFindingService;

    @PostMapping
    public ResponseEntity<ScanResponse> create(@Valid @RequestBody ScanRequest scanRequest) {
        UUID id = scanService.createScan(scanRequest.image());
        return ResponseEntity
                .created(URI.create("/scan/" + id))
                .body(new ScanResponse(id));
    }

    @GetMapping("/{id}")
    public ScanDetailsResponse getScan(@PathVariable UUID id) {
        var scan = scanService.getScan(id);
        return new ScanDetailsResponse(
                scan.getId(),
                scan.getImageName(),
                scan.getRequestedAt()
        );
    }

    @GetMapping
    public Page<ScanListItemResponse> listScans(Pageable pageable) {
        return scanService.listScans(pageable)
                .map(scan -> new ScanListItemResponse(
                        scan.getId(),
                        scan.getImageName(),
                        scan.getStatus().name(),
                        scan.getRequestedAt(),
                        scanService.countFindings(scan.getId())
                ));
    }

    @GetMapping("/{id}/findings")
    public Page<FindingResponse> listFindings(
            @PathVariable UUID id,
            @RequestParam Optional<String> severity,
            @RequestParam Optional<String> pkgName,
            Pageable pageable
            ) {
        return vulnerabilityFindingService.listFindings(id, severity, pkgName, pageable)
                .map(finding -> new FindingResponse(
                        finding.getId(),
                        finding.getPkgName(),
                        finding.getCve(),
                        finding.getSeverity(),
                        finding.getFixedVersion(),
                        "https://nvd.nist.gov/vuln/detail/" + finding.getCve()
                ));
    }
}
