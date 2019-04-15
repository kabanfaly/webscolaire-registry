package com.webscolaire.register.service;

import com.webscolaire.register.domain.WsRegister;
import com.webscolaire.register.domain.WsRegisterParam;
import reactor.core.publisher.Mono;

/**
 *
 * Interface définissant les services du registre à exposer.
 *
 * @since 12 avr. 2019
 * @author KABA N'faly
 */
public interface WsRegisterService {
 
  Mono<WsRegister> register(WsRegisterParam registerParam);
}
