package com.webscolaire.register.service;

import com.webscolaire.register.domain.WsRegister;
import com.webscolaire.register.domain.dto.WsRegisterParam;
import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 *
 * Interface définissant les services du registre à exposer.
 *
 * @since 12 avr. 2019
 * @author KABA N'faly
 */
public interface WsRegisterService {
 
  Mono<WsRegister> registerOne(WsRegisterParam registerParam);
  Flux<WsRegister> registerMany(List<WsRegisterParam> registerParam);
}
