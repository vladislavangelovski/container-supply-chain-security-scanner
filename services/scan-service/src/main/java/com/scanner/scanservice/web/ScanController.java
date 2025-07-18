package com.scanner.scanservice.web;

import com.scanner.scanservice.service.ScanService;
import com.scanner.scanservice.web.dto.ScanDetailsResponse;
import com.scanner.scanservice.web.dto.ScanRequest;
import com.scanner.scanservice.web.dto.ScanResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/scan")
@RequiredArgsConstructor
public class ScanController {
    private final ScanService scanService;

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
}
