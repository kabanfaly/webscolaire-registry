package com.webscolaire.registry.service.impl;

import com.webscolaire.registry.domain.SexType;
import com.webscolaire.registry.domain.UniqueGenKey;
import com.webscolaire.registry.domain.WebscolaireType;
import com.webscolaire.registry.domain.WsRegister;
import com.webscolaire.registry.domain.dto.WsRegisterDto;
import com.webscolaire.registry.exception.ValidationException;
import com.webscolaire.registry.exception.WsRegisterError;
import com.webscolaire.registry.repository.UniqueGenKeyRepository;
import com.webscolaire.registry.repository.WsRegisterRepository;
import com.webscolaire.registry.service.WsRegisterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.stream.Stream;

import static com.webscolaire.registry.TestUtils.MONGO_IMAGE;
import static com.webscolaire.registry.domain.SexType.H;
import static com.webscolaire.registry.domain.WebscolaireType.ELEVE_ETUDIANT;
import static com.webscolaire.registry.domain.WebscolaireType.ENSEIGNANT_PROF;
import static com.webscolaire.registry.domain.WebscolaireType.ETABLISSEMENT;
import static com.webscolaire.registry.exception.WsRegisterError.WS_002;
import static com.webscolaire.registry.exception.WsRegisterError.WS_005;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;

/**
 *
 * @since 13 avr. 2019
 * @author N'faly KABA
 */
@Testcontainers
@SpringBootTest
public class WsRegisterServiceImplTest {

  private static final LocalDate DOB = LocalDate.of(1999, 12, 20);
  private static final String DOB_STR = "1999-12-20";
  private static final String GENERATED_NUMBER_PREFIX = "EE-19991220-";
  private static final String SOURCE_ID = "SRC-12235";
  private static final String FIRST_NAME = "John";
  private static final String LAST_NAME = "Doe";

  @Container
  protected static final MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse(MONGO_IMAGE));

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

  @DynamicPropertySource
  static void configure(DynamicPropertyRegistry registry) {
    registry.add("MONGO_URI", mongoDBContainer::getReplicaSetUrl);
  }

  @ParameterizedTest
  @MethodSource("saveRegisterInvalidInputProvider")
  public void saveRegister_invalidInput(String sourceId, LocalDate dob, String firstName, SexType sex, WebscolaireType webscolaireType) {
    var register = newWsRegister(sourceId, dob, firstName, sex, webscolaireType);
    StepVerifier.create(registerService.saveRegister(register))
        .expectError(ValidationException.class)
        .verify();
  }

  @Test
  @DisplayName("Should not save Register when the lastName is not set for a type != ETABLISSEMENT")
  public void saveRegister_invalidLastName() {
    var register = newWsRegister(SOURCE_ID, DOB, FIRST_NAME, H, ELEVE_ETUDIANT);

    StepVerifier.create(registerService.saveRegister(register))
        .expectErrorMessage(WsRegisterError.WS_004.getDescription())
        .verify();
  }

  @Test
  public void registerOne_shouldThrowException_withWS_005_when_invalidSourceId() {
    uniqueGenKeyRepository.deleteAll().block();
    StepVerifier.create(registerService.saveRegister(new WsRegisterDto().setSourceId(SOURCE_ID)
            .setWebscolaireType(ELEVE_ETUDIANT).setDateOfBirth(DOB)
            .setFirstname("N'faly").setLastname("KABA").setSex(H)))
        .expectErrorMessage(WS_005.getDescription()).verify();
  }

  @Test
  @DisplayName("Should save a new WsRegister")
  public void saveRegister_new_data() {
    Mono<WsRegisterDto> registerMono = registerService
        .saveRegister(new WsRegisterDto().setSourceId(SOURCE_ID)
            .setWebscolaireType(ELEVE_ETUDIANT).setDateOfBirth(DOB)
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
  @DisplayName("Should retrieve an existing WsRegister")
  public void saveRegister_existing() {
    // Given
    registerRepository.save(newWsRegister()).block();

    // When
    registerService.saveRegister(new WsRegisterDto().setSourceId(SOURCE_ID)
        .setWebscolaireType(ELEVE_ETUDIANT).setDateOfBirth(DOB)
        .setFirstname("N'faly").setLastname("KABA").setSex(H));

    // Then
    assertEquals(1, registerRepository.findAll().collectList().block().size());
  }

  @Test
  @DisplayName("Should not update a sourceId by number when the sourceId is invalid")
  public void updateRegisterSourceIdByNumber_invalid_sourceId() {
    uniqueGenKeyRepository.deleteAll().block();
    StepVerifier.create(registerService.updateRegisterSourceIdByNumber(GENERATED_NUMBER_PREFIX, SOURCE_ID))
        .expectErrorMessage(WS_005.getDescription())
        .verify();
  }

  @Test
  @DisplayName("Should not update a sourceId by number when the number is invalid")
  public void updateRegisterSourceIdByNumber_invalid_number() {
    StepVerifier.create(registerService.updateRegisterSourceIdByNumber(GENERATED_NUMBER_PREFIX, SOURCE_ID))
        .expectErrorMessage(WsRegisterError.WS_007.getDescription()).
        verify();
  }

  @Test
  @DisplayName("Should update a sourceId by number")
  public void updateRegisterSourceIdByNumber() {
    //given
    String newSourceId = SOURCE_ID + "2";
    String number = GENERATED_NUMBER_PREFIX + "17325";
    uniqueGenKeyRepository.save(new UniqueGenKey().setUniqueKey(newSourceId)).block();
    registerRepository.save(newWsRegister().setGeneratedNumber(number)).block();

    StepVerifier.create(registerService.updateRegisterSourceIdByNumber(number, newSourceId))
        .assertNext(register -> {
          assertEquals(number, register.getGeneratedNumber());
          assertEquals(newSourceId, register.getSourceId());
        })
        .expectComplete()
        .verify();
  }

  @Test
  @DisplayName("Should find a WsRegister by number")
  public void findRegisterByNumber() {
    // Given
    var generatedNumber = GENERATED_NUMBER_PREFIX + "21473";
    registerRepository.save(newWsRegister().setGeneratedNumber(generatedNumber)).block();

    StepVerifier.create(registerService.findRegisterByNumber(generatedNumber))
        .assertNext(register -> assertEquals(generatedNumber, register.getGeneratedNumber()))
        .expectComplete()
        .verify();

  }

  @Test
  @DisplayName("Should not find a WsRegister by number")
  public void findRegisterByNumber_not_found() {
    // Given
    registerRepository.save(newWsRegister()).block();

    var generatedNumber = GENERATED_NUMBER_PREFIX + "21473";

    StepVerifier.create(registerService.findRegisterByNumber(generatedNumber))
        .expectNextCount(0)
        .verifyComplete();
  }

  @Test
  @DisplayName("Should retrieve all WsRegister by sourceId")
  public void findRegisterBySourceId() {
    // Given
    registerRepository.save(newWsRegister()).block();

    StepVerifier.create(registerService.findRegisterBySourceId(SOURCE_ID))
        .assertNext(register -> assertEquals(SOURCE_ID, register.getSourceId()))
        .expectComplete()
        .verify();
  }

  @Test
  @DisplayName("Should retrieve no WsRegister by sourceId")
  public void findRegisterBySourceId_not_found() {
    // Given
    registerRepository.save(newWsRegister()).block();
    StepVerifier.create(registerService.findRegisterByNumber("SRC"))
        .expectNextCount(0)
        .verifyComplete();
  }

  @Test
  @DisplayName("Should retrieve all WsRegister by dateOfBirth")
  public void findRegisterByDateOfBirth() {
    // Given
    registerRepository.save(newWsRegister()).block();

    StepVerifier.create(registerService.findByDateOfBirth(DOB_STR))
        .assertNext(register -> assertEquals(DOB, register.getDateOfBirth()))
        .expectComplete()
        .verify();
  }

  @Test
  @DisplayName("Should retrieve no WsRegister by dateOfBirth")
  public void findRegisterByDateOfBirth_not_found() {
    // Given
    registerRepository.save(newWsRegister()).block();

    StepVerifier.create(registerService.findByDateOfBirth("2000-01-01"))
        .expectNextCount(0)
        .verifyComplete();
  }

  @Test
  @DisplayName("Should retrieve no WsRegister by dateOfBirth with the date is invalid")
  public void findRegisterByDateOfBirth_invalidDate() {
    // Given
    registerRepository.save(newWsRegister()).block();

    StepVerifier.create(registerService.findByDateOfBirth("invalidDate"))
        .expectErrorMessage(WS_002.getDescription())
        .verify();
  }

  @Test
  @DisplayName("Should retrieve all WsRegister by sourceId and WebscolaireType")
  public void findBySourceIdAndWebscolaireType() {
    // Given
    registerRepository.save(newWsRegister()).block();

    StepVerifier.create(registerService.findBySourceIdAndWebscolaireType(SOURCE_ID, ELEVE_ETUDIANT))
        .assertNext(register -> {
          assertEquals(SOURCE_ID, register.getSourceId());
          assertEquals(ELEVE_ETUDIANT, register.getWebscolaireType());
        })
        .expectComplete()
        .verify();
  }

  @Test
  @DisplayName("Should retrieve no WsRegister by sourceId and WebscolaireType")
  public void findBySourceIdAndWebscolaireType_not_found() {
    // Given
    registerRepository.save(newWsRegister()).block();

    StepVerifier.create(registerService.findBySourceIdAndWebscolaireType("SOURCE_ID_", ELEVE_ETUDIANT))
        .expectNextCount(0)
        .verifyComplete();
  }

  @Test
  @DisplayName("Should retrieve all WsRegister")
  public void findAllRegisters() {
    // Given
    registerRepository.save(newWsRegister()).block();
    StepVerifier.create(registerService.findAllRegisters(PageRequest.of(0, 10)))
        .assertNext(register -> {
          assertEquals(SOURCE_ID, register.getSourceId());
          assertEquals(ELEVE_ETUDIANT, register.getWebscolaireType());
        })
        .expectComplete()
        .verify();
  }

  @Test
  @DisplayName("should not update WsRegister when the number is valid")
  public void updateRegister_invalidNumber() {
    // Given
    WsRegister wsRegister = registerRepository.save(newWsRegister()).block();
    StepVerifier.create(registerService.updateRegister(new WsRegisterDto()
            .setSourceId(wsRegister.getSourceId())
            .setWebscolaireType(wsRegister.getWebscolaireType())
            .setGeneratedNumber("NOT_FOUND")
            .setFirstname("ALY").setLastname("Kaba")
            .setDateOfBirth(DOB)
            .setSex(H)))
        .expectErrorMessage(WsRegisterError.WS_007.getDescription())
        .verify();
  }

  @Test
  @DisplayName("should not update WsRegister when the sourceId is valid")
  public void updateRegister_invalidSourceId() {
    // Given
    WsRegister wsRegister = registerRepository.save(newWsRegister()).block();
    StepVerifier.create(registerService.updateRegister(new WsRegisterDto()
            .setSourceId("INVALID_SOURCE_ID")
            .setWebscolaireType(wsRegister.getWebscolaireType())
            .setGeneratedNumber(wsRegister.getGeneratedNumber())
            .setFirstname("ALY").setLastname("Kaba")
            .setDateOfBirth(DOB)
            .setSex(H)))
        .expectErrorMessage(WsRegisterError.WS_005.getDescription())
        .verify();
  }

  @Test
  @DisplayName("should not update WsRegister when the wsType mismatch")
  public void updateRegister_mismatchType() {
    // Given
    WsRegister wsRegister = registerRepository.save(newWsRegister()).block();
    StepVerifier.create(registerService.updateRegister(new WsRegisterDto()
            .setSourceId(wsRegister.getSourceId())
            .setWebscolaireType(ENSEIGNANT_PROF)
            .setGeneratedNumber(wsRegister.getGeneratedNumber())
            .setFirstname("ALY").setLastname("Kaba")
            .setDateOfBirth(DOB)
            .setSex(H)))
        .expectErrorMessage(WsRegisterError.WS_001.getDescription()).verify();
  }


  @Test
  @DisplayName("should update WsRegister")
  public void updateRegister() {
    // Given
    WsRegister wsRegister = registerRepository.save(newWsRegister()).block();

    StepVerifier.create(registerService.updateRegister(new WsRegisterDto()
            .setSourceId(wsRegister.getSourceId())
            .setWebscolaireType(ELEVE_ETUDIANT)
            .setGeneratedNumber(wsRegister.getGeneratedNumber())
            .setFirstname("ALY").setLastname("Kaba")
            .setDateOfBirth(DOB)
            .setSex(H)))
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
        .setLastname("kaba")
        .setFirstname("N'faly".toUpperCase())
        .setSex(H)
        .setWebscolaireType(ELEVE_ETUDIANT);
  }

  public static WsRegisterDto newWsRegister(String sourceId, LocalDate dob, String firstName, SexType sex, WebscolaireType webscolaireType) {
    return new WsRegisterDto()
        .setSourceId(sourceId)
        .setDateOfBirth(dob)
        .setFirstname(firstName)
        .setSex(sex)
        .setWebscolaireType(webscolaireType);
  }

  static Stream<Arguments> saveRegisterInvalidInputProvider() {
    return Stream.of(
        arguments(null, DOB, FIRST_NAME, H, ETABLISSEMENT),
        arguments(SOURCE_ID, null, FIRST_NAME, H, ETABLISSEMENT),
        arguments(SOURCE_ID, DOB, null, H, ETABLISSEMENT),
        arguments(SOURCE_ID, DOB, FIRST_NAME, null, ETABLISSEMENT),
        arguments(SOURCE_ID, DOB, FIRST_NAME, H, null)
    );
  }
}
