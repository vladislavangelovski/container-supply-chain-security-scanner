package com.scanner.scanservice.repository;

import com.scanner.scanservice.model.Scan;
import com.scanner.scanservice.model.ScanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ScanRepository extends JpaRepository<Scan, UUID> {
    List<Scan> findTop10ByStatus(ScanStatus status);
}
