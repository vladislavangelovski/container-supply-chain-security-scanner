package com.scanner.scanservice.web.dto;

import java.time.Instant;
import java.util.UUID;

public record ScanListItemResponse(
        UUID id,
        String name,
        String status,
        Instant requestedAt,
        long findingCount
) {
}
