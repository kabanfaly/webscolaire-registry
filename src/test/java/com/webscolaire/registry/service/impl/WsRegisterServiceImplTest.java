package com.webscolaire.registry.service.impl;

import com.webscolaire.registry.domain.UniqueGenKey;
import com.webscolaire.registry.domain.WebscolaireType;
import com.webscolaire.registry.domain.WsRegister;
import com.webscolaire.registry.domain.dto.WsRegisterDto;
import com.webscolaire.registry.exception.WsRegisterError;
import com.webscolaire.registry.exception.WsRegisterException;
import com.webscolaire.registry.repository.UniqueGenKeyRepository;
import com.webscolaire.registry.repository.WsRegisterRepository;
import com.webscolaire.registry.service.WsRegisterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;

import static com.webscolaire.registry.domain.SexType.H;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

/**
 *
 * @since 13 avr. 2019
 * @author N'faly KABA
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class WsRegisterServiceImplTest {

  private static final LocalDate DOB = LocalDate.of(1990, 12, 20);
  private static final String DOB_STR = "20121990";
  private static final String GENERATED_NUMBER_PREFIX = "EE-20121999-";
  private static final String SOURCE_ID = "SRC-12235";

  @Autowired
  private WsRegisterService registerService;

  @Autowired
  private WsRegisterRepository registerRepository;

  @Autowired
  private UniqueGenKeyRepository uniqueGenKeyRepository;

  @BeforeEach
  public void setUp() {
    registerRepository.deleteAll().block();
    uniqueGenKeyRepository.deleteAll().block();
    uniqueGenKeyRepository.save(new UniqueGenKey().setUniqueKey(SOURCE_ID)).block();
  }

  @Test
  @ParameterizedTest
  @MethodSource()
  public void registerOne_shouldThrowException_withWS_001_when_webscolaireType_IsEmptyOrNull() {
    try {
      registerService.saveRegister(new WsRegisterDto().setWebscolaireType(null));
      fail("WsRegisterException(WS_001) expected");
    } catch (WsRegisterException ex) {
      assertEquals(WsRegisterError.WS_001, ex.wsError);
    }
  }


  @Test
  public void registerOne_shouldThrowException_withWS_002_when_dateOfBirth_IsInvalid() {
    try {
      registerService.saveRegister(new WsRegisterDto()
              .setWebscolaireType(WebscolaireType.ELEVE_ETUDIANT).setDateOfBirth(LocalDate.of(100, 13, 10)));
      fail("WsRegisterException(WS_002) expected");
    } catch (WsRegisterException ex) {
      assertEquals(WsRegisterError.WS_002, ex.wsError);
    }

    // When dateOfBirth is empty
    try {
      registerService.saveRegister(new WsRegisterDto()
              .setWebscolaireType(WebscolaireType.ELEVE_ETUDIANT).setDateOfBirth(null));
      fail("WsRegisterException(WS_002) expected");
    } catch (WsRegisterException ex) {
      assertEquals(WsRegisterError.WS_002, ex.wsError);
    }
  }

  @Test
  public void registerOne_shouldThrowException_withWS_003_when_sex_IsEmptyOrNull() {
    try {
      registerService.saveRegister(new WsRegisterDto()
              .setWebscolaireType(WebscolaireType.ELEVE_ETUDIANT)
              .setFirstname("N'faly").setLastname("Kaba").setDateOfBirth(DOB).setSex(null));
      fail("WsRegisterException(WS_003) expected");
    } catch (WsRegisterException ex) {
      assertEquals(WsRegisterError.WS_003, ex.wsError);
    }
  }

  @Test
  public void registerOne_shouldThrowException_withWS_004_when_lastname_IsEmptyOrNull() {
    try {
      registerService.saveRegister(new WsRegisterDto().setSourceId(SOURCE_ID)
              .setWebscolaireType(WebscolaireType.ELEVE_ETUDIANT)
              .setFirstname("N'faly").setDateOfBirth(DOB).setSex(null));
      fail("WsRegisterException(WS_004) expected");
    } catch (WsRegisterException ex) {
      assertEquals(WsRegisterError.WS_004, ex.wsError);
    }
  }

  @Test
  public void registerOne_shouldThrowException_withWS_006_when_firstname_IsEmptyOrNull() {
    try {
      registerService.saveRegister(new WsRegisterDto().setSourceId(SOURCE_ID)
              .setWebscolaireType(WebscolaireType.ELEVE_ETUDIANT).setDateOfBirth(DOB).setFirstname(""));
      fail("WsRegisterException(WS_006) expected");
    } catch (WsRegisterException ex) {
      assertEquals(WsRegisterError.WS_006, ex.wsError);
    }
  }

  @Test
  public void registerOne_shouldThrowException_withWS_005_when_invalidSourceId() {
    uniqueGenKeyRepository.deleteAll().subscribe();
    StepVerifier.create(registerService.saveRegister(new WsRegisterDto().setSourceId(SOURCE_ID)
            .setWebscolaireType(WebscolaireType.ELEVE_ETUDIANT).setDateOfBirth(DOB)
            .setFirstname("N'faly").setLastname("KABA").setSex(H)))
            .expectErrorMessage(WsRegisterError.WS_005.getDescription()).verify();
  }

  @Test
  public void registerOne_should_save_new_data() {
    Mono<WsRegister> registerMono = registerService
            .saveRegister(new WsRegisterDto().setSourceId(SOURCE_ID)
                    .setWebscolaireType(WebscolaireType.ELEVE_ETUDIANT).setDateOfBirth(DOB)
                    .setFirstname("N'faly").setLastname("KABA").setSex(H));

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
    registerService.saveRegister(new WsRegisterDto().setSourceId(SOURCE_ID)
            .setWebscolaireType(WebscolaireType.ELEVE_ETUDIANT).setDateOfBirth(DOB)
            .setFirstname("N'faly").setLastname("KABA").setSex(H));

    // Then
    assertEquals(1, registerRepository.findAll().collectList().block().size());
  }

  @Test
  public void updateSourceIdByNumber_shouldThrowException_withWS_005_when_invalidSourceId() {
    uniqueGenKeyRepository.deleteAll().subscribe();
    StepVerifier.create(registerService.updateRegisterSourceIdByNumber(GENERATED_NUMBER_PREFIX, SOURCE_ID))
            .expectErrorMessage(WsRegisterError.WS_005.getDescription()).verify();
  }

  @Test
  public void updateSourceIdByNumber_shouldThrowException_withWS_007_when_invalidNumber() {
    StepVerifier.create(registerService.updateRegisterSourceIdByNumber(GENERATED_NUMBER_PREFIX, SOURCE_ID))
            .expectErrorMessage(WsRegisterError.WS_007.getDescription()).verify();
  }

  @Test
  public void updateSourceIdByNumber_shouldUpdate() {
    //given
    String newSourceId = SOURCE_ID + "2";
    String number = GENERATED_NUMBER_PREFIX + "17325";
    uniqueGenKeyRepository.save(new UniqueGenKey().setUniqueKey(newSourceId)).subscribe();
    registerRepository.save(newWsRegister().setGeneratedNumber(number)).subscribe();

    StepVerifier.create(registerService.updateRegisterSourceIdByNumber(number, newSourceId))
            .assertNext(register -> {
              assertEquals(number, register.getGeneratedNumber());
              assertEquals(newSourceId, register.getSourceId());
            })
            .expectComplete()
            .verify();
  }

  @Test
  public void findRegisterByNumber_when_data_found() {
    // Given
    var generatedNumber = GENERATED_NUMBER_PREFIX + "21473";
    registerRepository.save(newWsRegister().setGeneratedNumber(generatedNumber)).subscribe();

    StepVerifier.create(registerService.findRegisterByNumber(generatedNumber))
            .assertNext(register -> assertEquals(generatedNumber, register.getGeneratedNumber()))
            .expectComplete()
            .verify();

  }

  @Test
  public void findRegisterByNumber_when_no_data_found() {
    // Given
    registerRepository.save(newWsRegister()).subscribe();

    var generatedNumber = GENERATED_NUMBER_PREFIX + "21473";

    StepVerifier.create(registerService.findRegisterByNumber(generatedNumber))
            .expectNextCount(0)
            .verifyComplete();
  }

  @Test
  public void findRegisterBySourceId_when_data_found() {
    // Given
    registerRepository.save(newWsRegister()).subscribe();

    StepVerifier.create(registerService.findRegisterBySourceId(SOURCE_ID))
            .assertNext(register -> assertEquals(SOURCE_ID, register.getSourceId()))
            .expectComplete()
            .verify();
  }

  @Test
  public void findRegisterBySourceId_when_no_data_found() {
    // Given
    registerRepository.save(newWsRegister()).subscribe();
    StepVerifier.create(registerService.findRegisterByNumber("SRC"))
            .expectNextCount(0)
            .verifyComplete();
  }

  @Test
  public void findRegisterByDateOfBirth_when_data_found() {
    // Given
    registerRepository.save(newWsRegister()).subscribe();

    StepVerifier.create(registerService.findByDateOfBirth(DOB_STR))
            .assertNext(register -> assertEquals(DOB, register.getDateOfBirth()))
            .expectComplete()
            .verify();

  }

  @Test
  public void findRegisterByDateOfBirth_when_no_data_found() {
    // Given
    registerRepository.save(newWsRegister()).subscribe();

    StepVerifier.create(registerService.findByDateOfBirth("DOB_"))
            .expectNextCount(0)
            .verifyComplete();
  }

  @Test
  public void findBySourceIdAndWebscolaireType_when_data_found() {
    // Given
    registerRepository.save(newWsRegister()).subscribe();

    StepVerifier.create(registerService.findBySourceIdAndWebscolaireType(SOURCE_ID, WebscolaireType.ELEVE_ETUDIANT))
            .assertNext(register -> {
              assertEquals(SOURCE_ID, register.getSourceId());
              assertEquals(WebscolaireType.ELEVE_ETUDIANT, register.getWebscolaireType());
            })
            .expectComplete()
            .verify();

  }

  @Test
  public void findBySourceIdAndWebscolaireType_when_no_data_found() {
    // Given
    registerRepository.save(newWsRegister()).subscribe();

    StepVerifier.create(registerService.findBySourceIdAndWebscolaireType("SOURCE_ID_", WebscolaireType.ELEVE_ETUDIANT))
            .expectNextCount(0)
            .verifyComplete();
  }

  @Test
  public void findAll() {
    // Given
    registerRepository.save(newWsRegister()).subscribe();
    StepVerifier.create(registerService.findAllRegisters(PageRequest.of(0, 10)))
            .assertNext(register -> {
              assertEquals(SOURCE_ID, register.getSourceId());
              assertEquals(WebscolaireType.ELEVE_ETUDIANT, register.getWebscolaireType());
            })
            .expectComplete()
            .verify();
  }

  @Test
  public void updateRegister_when_number_is_not_correct(){
    // Given
    registerRepository.save(newWsRegister()).subscribe();
    StepVerifier.create(registerService.updateRegister(new WsRegisterDto()
            .setWebscolaireType(WebscolaireType.ELEVE_ETUDIANT)
            .setGeneratedNumber("NOT_FOUND")
            .setFirstname("ALY").setLastname("Kaba").setDateOfBirth(DOB).setSex(H)))
            .expectErrorMessage(WsRegisterError.WS_007.getDescription()).verify();
  }

  @Test
  public void updateRegister_when_number_is_correct(){
    // Given
    WsRegister wsRegister = registerRepository.save(newWsRegister()).block();

    StepVerifier.create(registerService.updateRegister(new WsRegisterDto()
            .setWebscolaireType(WebscolaireType.ELEVE_ETUDIANT)
            .setGeneratedNumber(wsRegister.getGeneratedNumber())
            .setFirstname("ALY").setLastname("Kaba").setDateOfBirth(DOB).setSex(H)))
            .assertNext(register  -> {
              assertEquals("ALY", register.getFirstname());
              assertEquals("Kaba", register.getLastname());
              assertEquals(H, register.getSex());
              assertEquals(wsRegister.getSourceId(), register.getSourceId());
              assertEquals(wsRegister.getGeneratedNumber(), register.getGeneratedNumber());
            })
            .expectComplete()
            .verify();
  }

  public static WsRegister newWsRegister() {
    return new WsRegister()
            .setSourceId(SOURCE_ID)
            .setDateOfBirth(DOB)
            .setLastname("kaba".toUpperCase())
            .setFirstname("N'faly".toUpperCase())
            .setSex(H)
            .setWebscolaireType(WebscolaireType.ELEVE_ETUDIANT);
  }
}
