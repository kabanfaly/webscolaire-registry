package com.webscolaire.registry.exception;

/**
 *
 * @since 12 avr. 2019
 * @author KABA N'faly
 */
public class WsRegisterException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public final WsRegisterError error;

  public WsRegisterException(WsRegisterError error) {
    this(error, null);
  }

  public WsRegisterException(WsRegisterError error, Throwable t) {
    super(error.getDescription(), t);
    this.error = error;
  }

}
