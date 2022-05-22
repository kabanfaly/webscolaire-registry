package com.webscolaire.registry.domain.dto;

import javax.validation.constraints.NotBlank;

public record SourceIdUpdateDto(
        @NotBlank String sourceId,
        @NotBlank String number) {}
