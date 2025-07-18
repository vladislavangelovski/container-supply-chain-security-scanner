package com.scanner.scanservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "scan")
@Getter @Setter @NoArgsConstructor
public class Scan {
    @Id @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String imageName;

    @Column(nullable = false)
    private Instant requestedAt = Instant.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ScanStatus status = ScanStatus.NEW;
}
