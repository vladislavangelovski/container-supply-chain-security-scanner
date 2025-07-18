package com.scanner.scanservice.service;

import com.scanner.scanservice.model.Scan;

import java.util.UUID;

public interface ScanService {
    UUID createScan(String imageName);
    Scan getScan(UUID scanId);
}
