package com.webscolaire.registry.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import static com.webscolaire.registry.config.WsRegisterConstants.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 *
 * Le document représentant un registre.
 *
 * @since 11 avr. 2019
 * @author kaban
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@Accessors(chain = true)
@Document(collection = "register")
public class WsRegister  extends AbstractAuditingEntity {

  private static final long serialVersionUID = 1L;

  @Id
  @JsonIgnore
  private String id;

  @Indexed
  @Field(WEBSCOLAIRE_TYPE)
  private WebscolaireType webscolaireType;

  @Indexed
  @Field(DATE_OF_BIRTH)
  private String dateOfBirth;

  @Field(LASTNAME)
  private String lastname;

  @Field(FIRSTNAME)
  private String firstname;

  @Field(SEX)
  private SexType sex;

  @Indexed
  @Field(SOURCE_ID)
  private String sourceId;

  /**
   * Identifiant unique généré
   */
  @Field(GENERATED_NUMBER)
  private String generatedNumber;
}
