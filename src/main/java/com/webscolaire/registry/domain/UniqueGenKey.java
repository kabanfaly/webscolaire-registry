package com.webscolaire.registry.domain;

import static com.webscolaire.registry.config.WsRegisterConstants.UNIQUE_KEY;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 *
 * @since 23 avr. 2019
 * @author KABA N'faly
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@Accessors(chain = true)
@Document(collection = "unique_gen_key")
public class UniqueGenKey extends AbstractAuditingEntity {

  private static final long serialVersionUID = 1L;
  
  @Indexed(unique = true)
  @Field(UNIQUE_KEY)
  private String uniqueKey;
  
}
