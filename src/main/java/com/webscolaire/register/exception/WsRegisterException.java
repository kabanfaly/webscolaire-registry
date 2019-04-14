package com.webscolaire.register.exception;

import lombok.Data;
import lombok.Getter;

/**
 *
 * @since 12 avr. 2019
 * @author KABA N'faly
 */
@Data
public class WsRegisterException extends RuntimeException {
  
  private static final long serialVersionUID = 1L;
  
  @Getter
  private final WsRegisterError error;
  
  public WsRegisterException(WsRegisterError error){
      this(error, null);
  }
  
   public WsRegisterException(WsRegisterError error, Throwable t){
      super(error.getDescription(), t);
      this.error = error;
  }
}
