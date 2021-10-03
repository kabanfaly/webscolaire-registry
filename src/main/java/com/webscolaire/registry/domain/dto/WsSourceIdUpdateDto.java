package com.webscolaire.registry.domain.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class WsSourceIdUpdateDto {

  @NotBlank
  public final String sourceId;

  @NotBlank
  public final String number;

  public WsSourceIdUpdateDto(String sourceId, String number) {
    this.sourceId = sourceId;
    this.number = number;
  }
}
