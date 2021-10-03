package com.webscolaire.registry.domain;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

import static com.webscolaire.registry.config.WsRegisterConstants.DATE_OF_BIRTH;
import static com.webscolaire.registry.config.WsRegisterConstants.FIRSTNAME;
import static com.webscolaire.registry.config.WsRegisterConstants.GENERATED_NUMBER;
import static com.webscolaire.registry.config.WsRegisterConstants.LASTNAME;
import static com.webscolaire.registry.config.WsRegisterConstants.SEX;
import static com.webscolaire.registry.config.WsRegisterConstants.SOURCE_ID;
import static com.webscolaire.registry.config.WsRegisterConstants.WEBSCOLAIRE_TYPE;

/**
 * @since 11 avr. 2019
 * @author kaban
 */

@Document(collection = "register")
public class WsRegister extends AbstractAuditingEntity {

  private static final long serialVersionUID = 1L;

  @Indexed
  @Field(WEBSCOLAIRE_TYPE)
  @NotNull
  private WebscolaireType webscolaireType;

  @Indexed
  @Field(DATE_OF_BIRTH)
  @NotNull
  private LocalDate dateOfBirth;

  @Field(LASTNAME)
  private String lastname;

  @Field(FIRSTNAME)
  @NotBlank
  private String firstname;

  @Field(SEX)
  @NotNull
  private SexType sex;

  @Indexed
  @Field(SOURCE_ID)
  @NotBlank
  private String sourceId;

  @Indexed(unique = true)
  @Field(GENERATED_NUMBER)
  @NotBlank
  private String generatedNumber;

  public WebscolaireType getWebscolaireType() {
    return webscolaireType;
  }

  public WsRegister setWebscolaireType(WebscolaireType webscolaireType) {
    this.webscolaireType = webscolaireType;
    return this;
  }

  public LocalDate getDateOfBirth() {
    return dateOfBirth;
  }

  public WsRegister setDateOfBirth(LocalDate dateOfBirth) {
    this.dateOfBirth = dateOfBirth;
    return this;
  }

  public String getLastname() {
    return lastname;
  }

  public WsRegister setLastname(String lastname) {
    this.lastname = lastname;
    return this;
  }

  public String getFirstname() {
    return firstname;
  }

  public WsRegister setFirstname(String firstname) {
    this.firstname = firstname;
    return this;
  }

  public SexType getSex() {
    return sex;
  }

  public WsRegister setSex(SexType sex) {
    this.sex = sex;
    return this;
  }

  public String getSourceId() {
    return sourceId;
  }

  public WsRegister setSourceId(String sourceId) {
    this.sourceId = sourceId;
    return this;
  }

  public String getGeneratedNumber() {
    return generatedNumber;
  }

  public WsRegister setGeneratedNumber(String generatedNumber) {
    this.generatedNumber = generatedNumber;
    return this;
  }
}
