package com.webscolaire.registry.exception;

/**
 *
 * @since 12 avr. 2019
 * @author KABA N'faly
 */
public enum WsRegisterError {

  WS_001("WS_001", "La valeur de l'attribut 'webscolaireType' est invalide ou n'est pas définie."),
  WS_002("WS_002", "Le format de la date de naissance (dateOfBirth) est invalid. le format valide est : AAAA-MM-JJ."),
  WS_003("WS_003", "La valeur de l'attribut 'sex' est invalide ou n'est pas définie."),
  WS_004("WS_004", "L'attribut 'lastname' ne doit pas être vide ou 'null'."),
  WS_005("WS_005", "La valeur de l'attribut 'sourceId' est invalide."),
  WS_006("WS_006", "L'attribut 'firstname' ne doit pas être vide ou 'null'."),
  WS_007("WS_007", "Le numéro fourni est incorrect."),
  WS_008("WS_008", "Data validation error");

  private final String code;
  
  private final String description;

  WsRegisterError(String code, String description) {
    this.code = code;
    this.description = description;
  }

  public String getCode() {
    return code;
  }

  public String getDescription() {
    return description;
  }

  @Override
  public String toString() {
    return description;
  }
}
