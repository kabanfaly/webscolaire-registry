package com.webscolaire.registry.service.impl;

import com.webscolaire.registry.domain.SexType;
import com.webscolaire.registry.domain.WebscolaireType;
import com.webscolaire.registry.domain.WsRegister;
import com.webscolaire.registry.domain.dto.WsRegisterParam;
import com.webscolaire.registry.exception.WsRegisterError;
import com.webscolaire.registry.exception.WsRegisterException;
import com.webscolaire.registry.repository.WsRegisterRepository;
import com.webscolaire.registry.service.WsRegisterService;
import javax.inject.Inject;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 *
 * @since 13 avr. 2019
 * @author N'faly KABA
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class WsRegisterServiceImplTest {

  public static final String DOB = "20121999";
  public static final String GENERATED_NUMBER_PREFIX = "EE-20121999";

  @Inject
  private WsRegisterService registerService;

  @Inject
  private WsRegisterRepository registerRepository;

  @Before
  public void setUp() {
    registerRepository.deleteAll().subscribe();
  }

  @Test
  public void shouldThrowException_withWS_001_when_webscolaireType_IsEmptyOrNull() {
    try {
      registerService.registerOne(new WsRegisterParam("", "", "DOB", "", "", ""));
      fail("WsRegisterException(WS_001) expected");
    } catch (WsRegisterException ex) {
      assertEquals(WsRegisterError.WS_001, ex.getWsError());
    }
  }

  @Test
  public void shouldThrowException_withWS_002_when_dateOfBirth_IsInvalid() {
    try {
      registerService.registerOne(new WsRegisterParam("", "ELEVE_ETUDIANT", "40802222", "", "", ""));
      fail("WsRegisterException(WS_002) expected");
    } catch (WsRegisterException ex) {
      assertEquals(WsRegisterError.WS_002, ex.getWsError());
    }

    // When dateOfBirth is empty
    try {
      registerService.registerOne(new WsRegisterParam("", "ELEVE_ETUDIANT", "", "", "", ""));
      fail("WsRegisterException(WS_002) expected");
    } catch (WsRegisterException ex) {
      assertEquals(WsRegisterError.WS_002, ex.getWsError());
    }
  }

  @Test
  public void shouldThrowException_withWS_003_when_sex_IsEmptyOrNull() {
    try {
      registerService.registerOne(new WsRegisterParam("", "ELEVE_ETUDIANT", DOB, "", "", ""));
      fail("WsRegisterException(WS_003) expected");
    } catch (WsRegisterException ex) {
      assertEquals(WsRegisterError.WS_003, ex.getWsError());
    }
  }

  @Test
  public void shouldThrowException_withWS_004_when_lastname_IsEmptyOrNull() {
    try {
      registerService.registerOne(new WsRegisterParam("", "ELEVE_ETUDIANT", DOB, "", "", "H"));
      fail("WsRegisterException(WS_004) expected");
    } catch (WsRegisterException ex) {
      assertEquals(WsRegisterError.WS_004, ex.getWsError());
    }
  }

  @Test
  public void registerTestOK_should_save_new_data() {
    Mono<WsRegister> registerMono = registerService
        .registerOne(new WsRegisterParam("SRC-12235", "ELEVE_ETUDIANT", DOB, "KABA", "N'faly", "H"));
    StepVerifier.create(registerMono).expectComplete();
    WsRegister register = registerMono.block();
    assertEquals(1, registerRepository.findAll().collectList().block().size());
    assertTrue(register.getGeneratedNumber().startsWith(GENERATED_NUMBER_PREFIX));
    assertEquals(DOB, register.getDateOfBirth());
  }

  @Test
  public void registerTestOK_should_retreive_existing_data() {
    // Given
    registerRepository.save(new WsRegister()
        .setSourceId("SRC-12235")
        .setDateOfBirth(DOB)
        .setLastname("kaba".toUpperCase())
        .setFirstname("N'faly".toUpperCase())
        .setSex(SexType.H)
        .setWebscolaireType(WebscolaireType.ELEVE_ETUDIANT)).subscribe();

    // When
    registerService
        .registerOne(new WsRegisterParam("SRC-12235", "ELEVE_ETUDIANT", DOB, "KABA", "N'faly", "H"));    

    // Then
    assertEquals(1, registerRepository.findAll().collectList().block().size());
  }

}
