package com.webscolaire.register.service.impl;

import com.webscolaire.register.domain.WebscolaireType;
import com.webscolaire.register.domain.WsRegister;
import com.webscolaire.register.repository.WsRegisterRepository;
import com.webscolaire.register.service.WsRegisterService;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;


/**
 *
 * Classe d'implementation des services du registre.
 *
 * @since 12 avr. 2019
 * @author KABA N'faly
 */

@Service
@Data
@NoArgsConstructor
@Transactional
public class WsRegisterServiceImpl implements WsRegisterService {

  private WsRegisterRepository registerRepository;
  
  @Override
  public Mono<WsRegister> register(String webscolaireType, String dateOfBirh) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

}
