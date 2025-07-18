package com.scanner.scanservice.service;

import java.util.UUID;

public interface ScanService {
    UUID createScan(String imageName);
}
