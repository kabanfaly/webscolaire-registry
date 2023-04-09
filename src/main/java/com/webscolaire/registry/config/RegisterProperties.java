package com.webscolaire.registry.config;

import jakarta.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties("webscolaire.registry")
@Validated
public record RegisterProperties(@NotNull String username, @NotNull String password, @NotNull String timeZone) {}
