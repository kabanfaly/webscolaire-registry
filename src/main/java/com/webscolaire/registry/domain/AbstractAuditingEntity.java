package com.webscolaire.registry.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.time.Instant;

import static com.webscolaire.registry.config.WsRegisterConstants.CREATION_DATE;
import static com.webscolaire.registry.config.WsRegisterConstants.MODIFICATION_DATE;

/**
 *
 * @since 23 avr. 2019
 * @author KABA N'faly
 */
@Data
public abstract class AbstractAuditingEntity implements Serializable {

  private static final long serialVersionUID = 1L;
  
  @Id
  @JsonIgnore
  protected String id;

  @Field(CREATION_DATE)
  @JsonIgnore
  @CreatedDate
  protected Instant createdDate = Instant.now();

  @Field(MODIFICATION_DATE)
  @JsonIgnore
  @LastModifiedDate
  protected Instant lastModifiedDate = Instant.now();
  
}
