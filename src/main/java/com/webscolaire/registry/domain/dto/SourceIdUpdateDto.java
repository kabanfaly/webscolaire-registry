package com.webscolaire.registry.domain.dto;

import jakarta.validation.constraints.NotBlank;

public record SourceIdUpdateDto(
        @NotBlank String sourceId,
        @NotBlank String number) {}
