package com.scanner.scanservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

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

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false)
    private String rawJson;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "sbom_json", columnDefinition = "jsonb", nullable = false)
    private String sbomJson;
}
