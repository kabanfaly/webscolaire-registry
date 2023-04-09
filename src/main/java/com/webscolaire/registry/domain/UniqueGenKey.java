package com.webscolaire.registry.domain;

import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serial;

/**
 *
 * @since 23 avr. 2019
 * @author KABA N'faly
 */
@Document(collection = "unique_gen_key")
public class UniqueGenKey extends AbstractAuditingEntity {

  @Serial
  private static final long serialVersionUID = 1L;

}
