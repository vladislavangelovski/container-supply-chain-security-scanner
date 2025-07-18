package com.scanner.scanservice.web.dto;

import java.time.Instant;
import java.util.UUID;

public record ScanDetailsResponse(UUID scanId, String image, Instant requestedAt) {
}
