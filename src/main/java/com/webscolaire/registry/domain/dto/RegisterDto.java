package com.webscolaire.registry.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.webscolaire.registry.domain.Register;
import com.webscolaire.registry.domain.Sex;
import com.webscolaire.registry.domain.WebscolaireType;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;


/**
 * A DTO for the {@link Register} entity.
 *
 * @since 14 avr. 2019
 * @author KABA N'faly
 */
@JsonInclude(NON_NULL)
public class RegisterDto implements Serializable {

  private static final long serialVersionUID = 1L;

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
  private Sex sex;

  private String generatedNumber;

  private Instant createdDate;

  private Instant lastModifiedDate;

  public String getId() {
    return id;
  }

  public RegisterDto setId(String id) {
    this.id = id;
    return this;
  }

  public String getSourceId() {
    return sourceId;
  }

  public RegisterDto setSourceId(String sourceId) {
    this.sourceId = sourceId;
    return this;
  }

  public WebscolaireType getWebscolaireType() {
    return webscolaireType;
  }

  public RegisterDto setWebscolaireType(WebscolaireType webscolaireType) {
    this.webscolaireType = webscolaireType;
    return this;
  }

  public LocalDate getDateOfBirth() {
    return dateOfBirth;
  }

  public RegisterDto setDateOfBirth(LocalDate dateOfBirth) {
    this.dateOfBirth = dateOfBirth;
    return this;
  }

  public String getFirstname() {
    return firstname;
  }

  public RegisterDto setFirstname(String firstname) {
    this.firstname = firstname;
    return this;
  }

  public String getLastname() {
    return lastname;
  }

  public RegisterDto setLastname(String lastname) {
    this.lastname = lastname;
    return this;
  }

  public Sex getSex() {
    return sex;
  }

  public RegisterDto setSex(Sex sex) {
    this.sex = sex;
    return this;
  }

  public String getGeneratedNumber() {
    return generatedNumber;
  }

  public RegisterDto setGeneratedNumber(String generatedNumber) {
    this.generatedNumber = generatedNumber;
    return this;
  }

  public Instant getCreatedDate() {
    return createdDate;
  }

  public RegisterDto setCreatedDate(Instant createdDate) {
    this.createdDate = createdDate;
    return this;
  }

  public Instant getLastModifiedDate() {
    return lastModifiedDate;
  }

  public RegisterDto setLastModifiedDate(Instant lastModifiedDate) {
    this.lastModifiedDate = lastModifiedDate;
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    RegisterDto that = (RegisterDto) o;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
