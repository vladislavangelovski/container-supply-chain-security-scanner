package com.scanner.scanservice.service.worker;

import com.scanner.scanservice.model.Scan;
import com.scanner.scanservice.model.ScanReport;
import com.scanner.scanservice.model.ScanStatus;
import com.scanner.scanservice.repository.ScanReportRepository;
import com.scanner.scanservice.repository.ScanRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScanProcessor {
    private final ScanRepository scanRepository;
    private final ScanReportRepository reportRepository;

    @Scheduled(fixedDelay = 10_000)
    public void processNewScans() {
        log.info("Scheduler tick – looking for NEW scans");
        var newScans = claimNewScans();
        newScans.forEach(s -> s.setStatus(ScanStatus.PROCESSING));
        scanRepository.saveAll(newScans);

        for (Scan scan : newScans) {
            handleScan(scan);
        }
        scanRepository.saveAll(newScans);
    }

    private String runTrivy(String imageName) throws Exception {
        Process proc = new ProcessBuilder(
                "trivy", "image",
                "--scanners", "vuln",
                "--format", "json",
                "--quiet",
                imageName
        ).redirectErrorStream(true).start();

        try (InputStream in = proc.getInputStream()) {
            String json = new String(in.readAllBytes());
            int exit = proc.waitFor();
            if (exit != 0) {
                throw new RuntimeException("Trivy exit code " + exit);
            }
            return json;
        } finally {
            proc.destroy();
        }
    }

    @Transactional
    public List<Scan> claimNewScans() {
        List<Scan> scans = scanRepository.findTop10ByStatus(ScanStatus.NEW);
        scans.forEach(s -> s.setStatus(ScanStatus.PROCESSING));
        return scanRepository.saveAll(scans);
    }

    @Transactional
    public void handleScan(Scan scan) {
        try {
            String json = runTrivy(scan.getImageName());
            ScanReport report = new ScanReport();
            report.setScan(scan);
            report.setRawJson(json);
            reportRepository.save(report);

            scan.setStatus(ScanStatus.DONE);
            log.info("Scan {} DONE ({} bytes)", scan.getId(), json.length());
        } catch (Exception ex) {
            scan.setStatus(ScanStatus.FAILED);
            log.error("Scan {} FAILED: {}", scan.getId(), ex.getMessage());
        }
    }
}
