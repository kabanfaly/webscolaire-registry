package com.webscolaire.registry.exception;

import lombok.Getter;

/**
 *
 * @since 12 avr. 2019
 * @author KABA N'faly
 */
public enum WsRegisterError {
  WS_001("WS_001", "La valeur de l'attribut 'webscolaireType' est invalide ou n'est pas définie."),
  WS_002("WS_002", "Le format de la date de naissance (dateOfBirth) est invalid. le format valide est : JJMMAAAA."),
  WS_003("WS_003", "La valeur de l'attribut 'sex' est invalide ou n'est pas définie."),
  WS_004("WS_004", "L'attribut 'lastname' ne doit pas être vide ou 'null'."),
  WS_005("WS_005", "La valeur de l'attribut 'sourceId' est invalide."),
  WS_006("WS_006", "L'attribut 'firstname' ne doit pas être vide ou 'null'."),
  WS_007("WS_007", "Le nunéro fourni est incorrect.");

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

  @Override
  public String toString() {
    return description;
  }
}
