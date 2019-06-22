package com.webscolaire.registry.service.impl;

import com.webscolaire.registry.domain.SexType;
import com.webscolaire.registry.domain.UniqueGenKey;
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
import com.webscolaire.registry.repository.UniqueGenKeyRepository;

/**
 *
 * @since 13 avr. 2019
 * @author N'faly KABA
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class WsRegisterServiceImplTest {

  public static final String DOB = "20121999";
  public static final String GENERATED_NUMBER_PREFIX = "EE-20121999-";
  public static final String SOURCE_ID = "SRC-12235";

  @Inject
  private WsRegisterService registerService;

  @Inject
  private WsRegisterRepository registerRepository;

  @Inject
  private UniqueGenKeyRepository uniqueGenKeyRepository;

  @Before
  public void setUp() {
    registerRepository.deleteAll().subscribe();
    uniqueGenKeyRepository.deleteAll().subscribe();
    uniqueGenKeyRepository.save(new UniqueGenKey().setUniqueKey(SOURCE_ID)).subscribe();
  }

  @Test
  public void shouldThrowException_withWS_001_when_webscolaireType_IsEmptyOrNull() {
    try {
      registerService.registerOne(new WsRegisterParam().setWebscolaireType(""));
      fail("WsRegisterException(WS_001) expected");
    } catch (WsRegisterException ex) {
      assertEquals(WsRegisterError.WS_001, ex.getWsError());
    }
  }

  @Test
  public void shouldThrowException_withWS_002_when_dateOfBirth_IsInvalid() {
    try {
      registerService.registerOne(new WsRegisterParam()
          .setWebscolaireType(WebscolaireType.ELEVE_ETUDIANT.name()).setDateOfBirth("40802222"));
      fail("WsRegisterException(WS_002) expected");
    } catch (WsRegisterException ex) {
      assertEquals(WsRegisterError.WS_002, ex.getWsError());
    }

    // When dateOfBirth is empty
    try {
      registerService.registerOne(new WsRegisterParam()
          .setWebscolaireType(WebscolaireType.ELEVE_ETUDIANT.name()).setDateOfBirth(""));
      fail("WsRegisterException(WS_002) expected");
    } catch (WsRegisterException ex) {
      assertEquals(WsRegisterError.WS_002, ex.getWsError());
    }
  }

  @Test
  public void shouldThrowException_withWS_003_when_sex_IsEmptyOrNull() {
    try {
      registerService.registerOne(new WsRegisterParam()
          .setWebscolaireType(WebscolaireType.ELEVE_ETUDIANT.name())
          .setFirstname("N'faly").setLastname("Kaba").setDateOfBirth(DOB).setSex(""));
      fail("WsRegisterException(WS_003) expected");
    } catch (WsRegisterException ex) {
      assertEquals(WsRegisterError.WS_003, ex.getWsError());
    }
  }

  @Test
  public void shouldThrowException_withWS_004_when_lastname_IsEmptyOrNull() {
    try {
      registerService.registerOne(new WsRegisterParam().setSourceId(SOURCE_ID)
          .setWebscolaireType(WebscolaireType.ELEVE_ETUDIANT.name())
          .setFirstname("N'faly").setDateOfBirth(DOB).setSex(""));
      fail("WsRegisterException(WS_004) expected");
    } catch (WsRegisterException ex) {
      assertEquals(WsRegisterError.WS_004, ex.getWsError());
    }
  }

  @Test
  public void shouldThrowException_withWS_006_when_firstname_IsEmptyOrNull() {
    try {
      registerService.registerOne(new WsRegisterParam().setSourceId(SOURCE_ID)
          .setWebscolaireType(WebscolaireType.ELEVE_ETUDIANT.name()).setDateOfBirth(DOB).setFirstname(""));
      fail("WsRegisterException(WS_006) expected");
    } catch (WsRegisterException ex) {
      assertEquals(WsRegisterError.WS_006, ex.getWsError());
    }
  }

  @Test
  public void shouldThrowException_withWS_005_when_invalidSourceID() {
    StepVerifier.create(registerService.registerOne(new WsRegisterParam().setSourceId(SOURCE_ID)
        .setWebscolaireType(WebscolaireType.ELEVE_ETUDIANT.name()).setDateOfBirth(DOB)
        .setFirstname("N'faly").setLastname("KABA").setSex(SexType.H.name())))
        .expectErrorMessage(WsRegisterError.WS_005.getDescription());
  }

  @Test
  public void registerTestOK_should_save_new_data() {
    uniqueGenKeyRepository.save(new UniqueGenKey().setUniqueKey(SOURCE_ID)).subscribe();

    Mono<WsRegister> registerMono = registerService
        .registerOne(new WsRegisterParam().setSourceId(SOURCE_ID)
            .setWebscolaireType(WebscolaireType.ELEVE_ETUDIANT.name()).setDateOfBirth(DOB)
            .setFirstname("N'faly").setLastname("KABA").setSex(SexType.H.name()));

    StepVerifier.create(registerMono)
        .assertNext(register -> {
          assertEquals(DOB, register.getDateOfBirth());
          assertTrue(register.getGeneratedNumber().startsWith(GENERATED_NUMBER_PREFIX));
        }).expectComplete()
        .verify();

    assertEquals(1, registerRepository.findAll().collectList().block().size());

  }

  @Test
  public void registerTestOK_should_retreive_existing_data() {
    // Given
    registerRepository.save(newWsRegister()).subscribe();

    // When
    registerService.registerOne(new WsRegisterParam().setSourceId(SOURCE_ID)
        .setWebscolaireType(WebscolaireType.ELEVE_ETUDIANT.name()).setDateOfBirth(DOB)
        .setFirstname("N'faly").setLastname("KABA").setSex(SexType.H.name()));

    // Then
    assertEquals(1, registerRepository.findAll().collectList().block().size());
  }

  @Test
  public void findRegisterByNumber_when_data_found() {
    // Given
    var generatedNumber = GENERATED_NUMBER_PREFIX + "21473";
    registerRepository.save(newWsRegister().setGeneratedNumber(generatedNumber)).subscribe();

    StepVerifier.create(registerService.findRegisterByNumber(generatedNumber))
        .assertNext(register -> {
          assertEquals(generatedNumber, register.getGeneratedNumber());
        })
        .expectComplete()
        .verify();

  }

  @Test
  public void findRegisterByNumber_when_no_data_found() {

    var generatedNumber = GENERATED_NUMBER_PREFIX + "21473";

    WsRegister result = registerService.findRegisterByNumber(generatedNumber).block();
    assertNull(result);
  }

  @Test
  public void findRegisterBySourceId_when_data_found() {
    // Given
    registerRepository.save(newWsRegister()).subscribe();

    StepVerifier.create(registerService.findRegisterBySourceId(SOURCE_ID))
        .assertNext(register -> {
          assertEquals(SOURCE_ID, register.getSourceId());
        })
        .expectComplete()
        .verify();

  }

  @Test
  public void findRegisterBySourceId_when_no_data_found() {

    WsRegister result = registerService.findRegisterByNumber("SRC").block();
    assertNull(result);
  }

  @Test
  public void findRegisterByDateOfBirth_when_data_found() {
    // Given
    registerRepository.save(newWsRegister()).subscribe();

    StepVerifier.create(registerService.findByDateOfBirth(DOB))
        .assertNext(register -> {
          assertEquals(DOB, register.getDateOfBirth());
        })
        .expectComplete()
        .verify();

  }

  @Test
  public void findRegisterByDateOfBirth_when_no_data_found() {

    WsRegister result = registerService.findByDateOfBirth("DOB_").blockFirst();
    assertNull(result);
  }

  public static WsRegister newWsRegister() {
    return new WsRegister()
        .setSourceId(SOURCE_ID)
        .setDateOfBirth(DOB)
        .setLastname("kaba".toUpperCase())
        .setFirstname("N'faly".toUpperCase())
        .setSex(SexType.H)
        .setWebscolaireType(WebscolaireType.ELEVE_ETUDIANT);
  }
}
