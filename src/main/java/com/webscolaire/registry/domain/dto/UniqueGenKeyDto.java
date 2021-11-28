package com.webscolaire.registry.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.time.Instant;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@JsonInclude(NON_NULL)
public class UniqueGenKeyDto implements Serializable {

  private static final long serialVersionUID = 1L;

  private String id;

  private Instant createdDate;

  private Instant lastModifiedDate;

  public String getId() {
    return id;
  }

  public UniqueGenKeyDto setId(String id) {
    this.id = id;
    return this;
  }

  public Instant getCreatedDate() {
    return createdDate;
  }

  public UniqueGenKeyDto setCreatedDate(Instant createdDate) {
    this.createdDate = createdDate;
    return this;
  }

  public Instant getLastModifiedDate() {
    return lastModifiedDate;
  }

  public UniqueGenKeyDto setLastModifiedDate(Instant lastModifiedDate) {
    this.lastModifiedDate = lastModifiedDate;
    return this;
  }
}
