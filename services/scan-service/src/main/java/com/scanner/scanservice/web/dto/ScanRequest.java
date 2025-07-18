package com.scanner.scanservice.web.dto;

import jakarta.validation.constraints.NotBlank;

public record ScanRequest(@NotBlank String image) {
}
