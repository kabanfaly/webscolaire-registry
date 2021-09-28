package com.webscolaire.registry.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.webscolaire.registry.domain.SexType;
import com.webscolaire.registry.domain.WebscolaireType;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;

import static com.webscolaire.registry.config.WsRegisterConstants.DATE_FORMAT_DD_MM_YYYY;

/**
 *
 * @since 14 avr. 2019
 * @author KABA N'faly
 */
public class  WsRegisterDto implements Serializable {

  @NotBlank
  private String sourceId;

  @NotNull
  private WebscolaireType webscolaireType;

  @NotNull
  @JsonFormat(pattern=DATE_FORMAT_DD_MM_YYYY)
  private LocalDate dateOfBirth;

  @NotBlank
  private String firstname;

  private String lastname = "";

  private SexType sex = SexType.A;

  private String generatedNumber;

  public String getSourceId() {
    return sourceId;
  }

  public WsRegisterDto setSourceId(String sourceId) {
    this.sourceId = sourceId;
    return this;
  }

  public WebscolaireType getWebscolaireType() {
    return webscolaireType;
  }

  public WsRegisterDto setWebscolaireType(WebscolaireType webscolaireType) {
    this.webscolaireType = webscolaireType;
    return this;
  }

  public LocalDate getDateOfBirth() {
    return dateOfBirth;
  }

  public WsRegisterDto setDateOfBirth(LocalDate dateOfBirth) {
    this.dateOfBirth = dateOfBirth;
    return this;
  }

  public String getFirstname() {
    return firstname;
  }

  public WsRegisterDto setFirstname(String firstname) {
    this.firstname = firstname;
    return this;
  }

  public String getLastname() {
    return lastname;
  }

  public WsRegisterDto setLastname(String lastname) {
    this.lastname = lastname;
    return this;
  }

  public SexType getSex() {
    return sex;
  }

  public WsRegisterDto setSex(SexType sex) {
    this.sex = sex;
    return this;
  }

  public String getGeneratedNumber() {
    return generatedNumber;
  }

  public WsRegisterDto setGeneratedNumber(String generatedNumber) {
    this.generatedNumber = generatedNumber;
    return this;
  }
}
