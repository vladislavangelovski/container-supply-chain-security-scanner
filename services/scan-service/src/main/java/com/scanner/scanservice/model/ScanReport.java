package com.scanner.scanservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "scan_report")
@Getter
@Setter
@NoArgsConstructor
public class ScanReport {
    @Id
    @GeneratedValue
    private UUID id;

    @OneToOne(optional = false)
    @JoinColumn(name = "scan_id", unique = true)
    private Scan scan;

    @Column(columnDefinition = "text", nullable = false)
    private String rawJson;
}
