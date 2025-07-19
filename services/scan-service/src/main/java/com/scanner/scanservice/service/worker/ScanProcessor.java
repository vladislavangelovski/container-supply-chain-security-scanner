package com.scanner.scanservice.service.worker;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scanner.scanservice.model.Scan;
import com.scanner.scanservice.model.ScanReport;
import com.scanner.scanservice.model.ScanStatus;
import com.scanner.scanservice.model.VulnerabilityFinding;
import com.scanner.scanservice.repository.ScanReportRepository;
import com.scanner.scanservice.repository.ScanRepository;
import com.scanner.scanservice.repository.VulnerabilityFindingRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScanProcessor {
    private final ScanRepository scanRepository;
    private final ScanReportRepository reportRepository;
    @Autowired
    private final VulnerabilityFindingRepository vulnerabilityFindingRepository;

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

    @Autowired
    private ObjectMapper objectMapper;

    @Transactional
    public void handleScan(Scan scan) {
        try {
            String rawJson = runTrivy(scan.getImageName());
            ScanReport report = new ScanReport();
            report.setScan(scan);
            report.setRawJson(rawJson);
            reportRepository.save(report);

            JsonNode root = objectMapper.readTree(rawJson);
            JsonNode results = root.path("Results");
            for(JsonNode result : results) {
                JsonNode vulns = result.path("Vulnerabilities");
                if(!vulns.isMissingNode()) {
                    for(JsonNode v : vulns) {
                        VulnerabilityFinding vf = new VulnerabilityFinding();
                        vf.setScanReport(report);
                        vf.setPkgName(result.path("Target").asText(null));
                        vf.setCve(v.path("VulnerabilityID").asText(null));
                        vf.setSeverity(v.path("Severity").asText(null));
                        vf.setFixedVersion(v.path("FixedVersion").asText(null));
                        vulnerabilityFindingRepository.save(vf);
                    }
                }
            }
            scan.setStatus(ScanStatus.DONE);
            log.info("Scan {} DONE ({} bytes)", scan.getId(), rawJson.length());
        } catch (Exception ex) {
            scan.setStatus(ScanStatus.FAILED);
            log.error("Scan {} FAILED: {}", scan.getId(), ex.getMessage());
        }
        scanRepository.save(scan);
    }
}
