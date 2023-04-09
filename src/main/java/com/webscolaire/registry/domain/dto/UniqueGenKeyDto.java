package com.webscolaire.registry.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@JsonInclude(NON_NULL)
@Data
@ToString
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class UniqueGenKeyDto implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  private String id;

  private Instant createdDate;

  private Instant lastModifiedDate;

}
