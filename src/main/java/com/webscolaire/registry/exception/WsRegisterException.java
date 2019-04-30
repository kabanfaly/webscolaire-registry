package com.webscolaire.registry.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 *
 * @since 12 avr. 2019
 * @author KABA N'faly
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class WsRegisterException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  @Getter
  private final WsRegisterError wsError;

  public WsRegisterException(WsRegisterError error) {
    this(error, null);
  }

  public WsRegisterException(String msg, Throwable t) {
    super(msg, t);
    wsError = null;
  }

  public WsRegisterException(WsRegisterError error, Throwable t) {
    super(error.getDescription(), t);
    this.wsError = error;
  }
}
