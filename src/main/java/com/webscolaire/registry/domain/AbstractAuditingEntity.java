package com.webscolaire.registry.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

import static com.webscolaire.registry.config.Constants.CREATION_DATE;
import static com.webscolaire.registry.config.Constants.MODIFICATION_DATE;

/**
 *
 * @since 23 avr. 2019
 * @author KABA N'faly
 */
public abstract class AbstractAuditingEntity implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  @Id
  @JsonIgnore
  protected String id;

  @Field(CREATION_DATE)
  @CreatedDate
  protected Instant createdDate = Instant.now();

  @Field(MODIFICATION_DATE)
  @LastModifiedDate
  protected Instant lastModifiedDate = Instant.now();

  public String getId() {
    return id;
  }

  public AbstractAuditingEntity setId(String id) {
    this.id = id;
    return this;
  }

  public Instant getCreatedDate() {
    return createdDate;
  }

  public AbstractAuditingEntity setCreatedDate(Instant createdDate) {
    this.createdDate = createdDate;
    return this;
  }

  public Instant getLastModifiedDate() {
    return lastModifiedDate;
  }

  public AbstractAuditingEntity setLastModifiedDate(Instant lastModifiedDate) {
    this.lastModifiedDate = lastModifiedDate;
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    AbstractAuditingEntity that = (AbstractAuditingEntity) o;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
