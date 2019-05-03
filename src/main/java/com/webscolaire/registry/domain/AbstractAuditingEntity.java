package com.webscolaire.registry.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import static com.webscolaire.registry.config.WsRegisterConstants.CREATION_DATE;
import static com.webscolaire.registry.config.WsRegisterConstants.MODIFICATION_DATE;
import java.io.Serializable;
import java.time.ZonedDateTime;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 *
 * @since 23 avr. 2019
 * @author KABA N'faly
 */
@Data
@Accessors(chain = true)
public abstract class AbstractAuditingEntity implements Serializable {

  private static final long serialVersionUID = 1L;
  
  @Id
  @JsonIgnore
  protected String id;

  @Field(CREATION_DATE)
  @JsonIgnore
  protected ZonedDateTime creationDate = ZonedDateTime.now();

  @Field(MODIFICATION_DATE)
  @JsonIgnore
  protected ZonedDateTime modificationDate = ZonedDateTime.now();
}
