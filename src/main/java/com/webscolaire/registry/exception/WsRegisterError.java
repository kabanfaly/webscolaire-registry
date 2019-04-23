package com.webscolaire.registry.exception;

import lombok.Getter;

/**
 *
 * @since 12 avr. 2019
 * @author KABA N'faly
 */
public enum WsRegisterError {
  WS_001("WS_001", "Type Webscolaire invalide."),
  WS_002("WS_002", "Le format de la date de naissance est invalide."),
  WS_003("WS_003", "L'attribut 'webscolaireType' ne doit pas être vide ou 'null'."),
  WS_004("WS_004", "L'attribut 'dateOfBirth' ne doit pas être vide ou 'null'.");

  @Getter
  private final String code;
  @Getter
  private final String description;

  private WsRegisterError(String code, String description) {
    this.code = code;
    this.description = description;
  }

  /**
   *
   * @param str
   * @return WsRegisterError
   */
  public static WsRegisterError getErrorFromCodeStr(String str) {

    for (WsRegisterError error : WsRegisterError.values()) {
      if (error.name().equals(str)) {
        return error;
      }
    }
    return null;
  }
}
