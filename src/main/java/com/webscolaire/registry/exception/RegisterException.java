package com.webscolaire.registry.exception;

/**
 *
 * @since 12 avr. 2019
 * @author KABA N'faly
 */
public class RegisterException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public final RegisterError error;

  public RegisterException(RegisterError error) {
    this(error, null);
  }

  public RegisterException(RegisterError error, Throwable t) {
    super(error.getDescription(), t);
    this.error = error;
  }

}
