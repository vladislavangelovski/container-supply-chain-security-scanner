package com.scanner.scanservice.web.dto;

import java.util.UUID;

public record FindingResponse(
        UUID id,
        String pkgName,
        String cve,
        String severity,
        String fixedVersion,
        String detailsUrl
) {
}
