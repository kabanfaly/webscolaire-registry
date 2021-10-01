package com.webscolaire.registry.exception;

/**
 *
 * @since 12 avr. 2019
 * @author KABA N'faly
 */
public enum WsRegisterError {

  WS_001("WS_001", "Invalid 'webscolaireType'"),
  WS_002("WS_002", "Invalid 'dateOfBirth'"),
  WS_003("WS_003", "The value of 'sex' is no valid."),
  WS_004("WS_004", "The value of 'lastname' must not be blank."),
  WS_005("WS_005", "The value of 'sourceId' in incorrect"),
  WS_006("WS_006", "The value of 'firstname' must not be blank."),
  WS_007("WS_007", "Invalid number."),
  WS_008("WS_008", "Data validation error.");

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
    return code + ": " + description;
  }
}
