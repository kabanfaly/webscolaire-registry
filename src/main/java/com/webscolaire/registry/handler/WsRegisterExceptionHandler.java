package com.webscolaire.registry.handler;

import com.webscolaire.registry.exception.WsRegisterException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.reactive.function.server.ServerRequest;

/**
 *
 * @since 25 avr. 2019 -  16 h 21 min 00 s
 * @author KABA N'faly
 */
@ControllerAdvice
@Slf4j
public class WsRegisterExceptionHandler {

  @ResponseBody
  @ExceptionHandler(value = WsRegisterException.class)
  public ResponseEntity<?> handleHystrixBadRequestExceptions(ServerRequest req, WsRegisterException exception) {
    return null;
  }
}
