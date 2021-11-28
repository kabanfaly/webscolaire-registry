package com.webscolaire.registry.domain.dto;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

public class SourceIdUpdateDto implements Serializable {

  private static final long serialVersionUID = 1L;

  @NotBlank
  public final String sourceId;

  @NotBlank
  public final String number;

  public SourceIdUpdateDto(String sourceId, String number) {
    this.sourceId = sourceId;
    this.number = number;
  }
}
