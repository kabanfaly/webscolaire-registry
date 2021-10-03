package com.webscolaire.registry.domain;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 *
 * @since 23 avr. 2019
 * @author KABA N'faly
 */
@Document(collection = "unique_gen_key")
public class UniqueGenKey extends AbstractAuditingEntity {

  private static final long serialVersionUID = 1L;

}
