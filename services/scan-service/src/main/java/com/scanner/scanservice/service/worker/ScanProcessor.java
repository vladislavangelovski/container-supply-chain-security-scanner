package com.scanner.scanservice.service.worker;

import com.scanner.scanservice.model.Scan;
import com.scanner.scanservice.model.ScanStatus;
import com.scanner.scanservice.repository.ScanRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScanProcessor {
    private final ScanRepository scanRepository;

    @Scheduled(fixedDelay = 10_000)
    @Transactional
    public void processNewScans() {
        List<Scan> newScans = scanRepository.findTop10ByStatus(ScanStatus.NEW);

        newScans.forEach(s -> s.setStatus(ScanStatus.PROCESSING));
        scanRepository.saveAll(newScans);

        newScans.forEach(scan -> {
            log.info("Processing scan {}", scan.getId());
            try {
                Thread.sleep(2_000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            scan.setStatus(ScanStatus.DONE);
        });

        scanRepository.saveAll(newScans);
        log.info("Finished {} scans -> DONE", newScans.size());
    }
}
