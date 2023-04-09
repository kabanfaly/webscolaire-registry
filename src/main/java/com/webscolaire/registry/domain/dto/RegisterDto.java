package com.webscolaire.registry.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.webscolaire.registry.domain.Register;
import com.webscolaire.registry.domain.Sex;
import com.webscolaire.registry.domain.WebscolaireType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;


/**
 * A DTO for the {@link Register} entity.
 *
 * @since 14 avr. 2019
 * @author KABA N'faly
 */
@JsonInclude(NON_NULL)
@Data
@ToString
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class RegisterDto implements Serializable {

  @Serial
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

}
