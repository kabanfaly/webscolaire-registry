package com.webscolaire.registry.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.webscolaire.registry.domain.SexType;
import com.webscolaire.registry.domain.WebscolaireType;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;


/**
 * A DTO for the {@link com.webscolaire.registry.domain.WsRegister} entity.
 *
 * @since 14 avr. 2019
 * @author KABA N'faly
 */
@JsonInclude(NON_NULL)
public class WsRegisterDto implements Serializable {

  private String id;

  @NotBlank
  private String sourceId;

  @NotNull
  private WebscolaireType webscolaireType;

  @NotNull
  private LocalDate dateOfBirth;

  @NotBlank
  private String firstname;

  private String lastname;

  @NotNull
  private SexType sex;

  private String generatedNumber;

  protected Instant createdDate;

  protected Instant lastModifiedDate;

  public String getId() {
    return id;
  }

  public WsRegisterDto setId(String id) {
    this.id = id;
    return this;
  }

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

  public Instant getCreatedDate() {
    return createdDate;
  }

  public WsRegisterDto setCreatedDate(Instant createdDate) {
    this.createdDate = createdDate;
    return this;
  }

  public Instant getLastModifiedDate() {
    return lastModifiedDate;
  }

  public WsRegisterDto setLastModifiedDate(Instant lastModifiedDate) {
    this.lastModifiedDate = lastModifiedDate;
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    WsRegisterDto that = (WsRegisterDto) o;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
