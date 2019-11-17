package com.webscolaire.registry.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 *
 * @since 14 avr. 2019
 * @author KABA N'faly
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class WsRegisterParam implements Serializable {

  @NonNull
  private String sourceId;

  @NonNull
  private String webscolaireType;

  @NonNull
  private String dateOfBirth;

  @NonNull
  private String firstname;
  private String lastname = "";
  private String sex;
}
