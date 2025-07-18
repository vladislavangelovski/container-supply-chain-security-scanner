package com.scanner.scanservice.service.impl;

import com.scanner.scanservice.model.Scan;
import com.scanner.scanservice.repository.ScanRepository;
import com.scanner.scanservice.service.ScanService;
import jdk.jshell.spi.ExecutionControl;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ScanServiceImpl implements ScanService {

    private final ScanRepository scanRepository;

    public ScanServiceImpl(ScanRepository scanRepository) {
        this.scanRepository = scanRepository;
    }

    @Override
    public UUID createScan(String imageName) {
        Scan scan = new Scan();
        scan.setImageName(imageName);
        return scanRepository.save(scan).getId();
    }
}
