package com.webscolaire.registry.domain;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

import static com.webscolaire.registry.config.Constants.DATE_OF_BIRTH;
import static com.webscolaire.registry.config.Constants.FIRSTNAME;
import static com.webscolaire.registry.config.Constants.GENERATED_NUMBER;
import static com.webscolaire.registry.config.Constants.LASTNAME;
import static com.webscolaire.registry.config.Constants.SEX;
import static com.webscolaire.registry.config.Constants.SOURCE_ID;
import static com.webscolaire.registry.config.Constants.WEBSCOLAIRE_TYPE;

/**
 * @since 11 avr. 2019
 * @author kaban
 */

@Document(collection = "register")
public class Register extends AbstractAuditingEntity {

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
  private Sex sex;

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

  public Register setWebscolaireType(WebscolaireType webscolaireType) {
    this.webscolaireType = webscolaireType;
    return this;
  }

  public LocalDate getDateOfBirth() {
    return dateOfBirth;
  }

  public Register setDateOfBirth(LocalDate dateOfBirth) {
    this.dateOfBirth = dateOfBirth;
    return this;
  }

  public String getLastname() {
    return lastname;
  }

  public Register setLastname(String lastname) {
    this.lastname = lastname;
    return this;
  }

  public String getFirstname() {
    return firstname;
  }

  public Register setFirstname(String firstname) {
    this.firstname = firstname;
    return this;
  }

  public Sex getSex() {
    return sex;
  }

  public Register setSex(Sex sex) {
    this.sex = sex;
    return this;
  }

  public String getSourceId() {
    return sourceId;
  }

  public Register setSourceId(String sourceId) {
    this.sourceId = sourceId;
    return this;
  }

  public String getGeneratedNumber() {
    return generatedNumber;
  }

  public Register setGeneratedNumber(String generatedNumber) {
    this.generatedNumber = generatedNumber;
    return this;
  }
}
