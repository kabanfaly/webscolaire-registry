package com.webscolaire.register.exception;

import lombok.Getter;

/**
 *
 * @since 12 avr. 2019
 * @author KABA N'faly
 */
public enum WsRegisterError {
  WS_001("WS_001", "");
  
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
  public static WsRegisterError getErrorFromCodeStr(String str){
    
    for(WsRegisterError error : WsRegisterError.values()){
        if(error.name().equals(str)){
          return error;
        }
      }
      return null;
  }
}
