package com.webscolaire.registry.service.impl;

import com.webscolaire.registry.domain.Register;
import com.webscolaire.registry.domain.Sex;
import com.webscolaire.registry.domain.UniqueGenKey;
import com.webscolaire.registry.domain.WebscolaireType;
import com.webscolaire.registry.domain.dto.RegisterDto;
import com.webscolaire.registry.domain.dto.SourceIdUpdateDto;
import com.webscolaire.registry.exception.RegisterException;
import com.webscolaire.registry.exception.ValidationException;
import com.webscolaire.registry.exception.RegisterError;
import com.webscolaire.registry.repository.UniqueGenKeyRepository;
import com.webscolaire.registry.repository.RegisterRepository;
import com.webscolaire.registry.service.RegisterService;
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
import static com.webscolaire.registry.domain.Sex.MALE;
import static com.webscolaire.registry.domain.WebscolaireType.STUDENT;
import static com.webscolaire.registry.domain.WebscolaireType.TEACHER;
import static com.webscolaire.registry.domain.WebscolaireType.SCHOOL;
import static com.webscolaire.registry.exception.RegisterError.WS_002;
import static com.webscolaire.registry.exception.RegisterError.WS_004;
import static com.webscolaire.registry.exception.RegisterError.WS_005;
import static com.webscolaire.registry.exception.RegisterError.WS_007;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;

/**
 *
 * @since 13 avr. 2019
 * @author N'faly KABA
 */
@Testcontainers
@SpringBootTest
public class RegisterServiceImplTest {

  private static final LocalDate DOB = LocalDate.of(1999, 12, 20);
  private static final String DOB_STR = "1999-12-20";
  private static final String GENERATED_NUMBER_PREFIX = "EE-19991220-";
  private static String SOURCE_ID;
  private static final String FIRST_NAME = "John";

  @Container
  protected static final MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse(MONGO_IMAGE));

  @Autowired
  private RegisterService registerService;

  @Autowired
  private RegisterRepository registerRepository;

  @Autowired
  private UniqueGenKeyRepository uniqueGenKeyRepository;

  @BeforeEach
  public void setUp() {
    registerRepository.deleteAll().block();
    uniqueGenKeyRepository.deleteAll().block();
    SOURCE_ID = uniqueGenKeyRepository.save(new UniqueGenKey()).block().getId();
  }

  @DynamicPropertySource
  static void configure(DynamicPropertyRegistry registry) {
    registry.add("MONGO_URI", mongoDBContainer::getReplicaSetUrl);
  }

  @ParameterizedTest
  @MethodSource("saveRegisterInvalidInputProvider")
  public void saveRegister_invalidInput(String sourceId, LocalDate dob, String firstName, Sex sex, WebscolaireType webscolaireType) {
    if (sourceId != null) {
      sourceId = uniqueGenKeyRepository.save(new UniqueGenKey()).block().getId();
    }
    var register = newWsRegister(sourceId, dob, firstName, sex, webscolaireType);
    assertThrows(ValidationException.class, () -> registerService.saveRegister(register));
  }

  @Test
  @DisplayName("Should not save Register when the lastName is not set for a type != ETABLISSEMENT")
  public void saveRegister_invalidLastName() {
    var register = newWsRegister(SOURCE_ID, DOB, FIRST_NAME, MALE, STUDENT);

    RegisterException exception = assertThrows(RegisterException.class, () -> registerService.saveRegister(register));
    assertEquals(WS_004.getDescription(), exception.getMessage());
  }

  @Test
  public void registerOne_shouldThrowException_withWS_005_when_invalidSourceId() {
    uniqueGenKeyRepository.deleteAll().block();
    StepVerifier.create(registerService.saveRegister(new RegisterDto().setSourceId(SOURCE_ID)
            .setWebscolaireType(STUDENT).setDateOfBirth(DOB)
            .setFirstname("N'faly").setLastname("KABA").setSex(MALE)))
        .expectErrorMessage(WS_005.getDescription()).verify();
  }

  @Test
  @DisplayName("Should save a new WsRegister")
  public void saveRegister_new_data() {
    Mono<RegisterDto> registerMono = registerService
        .saveRegister(new RegisterDto().setSourceId(SOURCE_ID)
            .setWebscolaireType(STUDENT).setDateOfBirth(DOB)
            .setFirstname("N'faly").setLastname("KABA").setSex(MALE));

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
    registerService.saveRegister(new RegisterDto().setSourceId(SOURCE_ID)
        .setWebscolaireType(STUDENT).setDateOfBirth(DOB)
        .setFirstname("N'faly").setLastname("KABA").setSex(MALE));

    // Then
    assertEquals(1, registerRepository.findAll().collectList().block().size());
  }

  @Test
  @DisplayName("Should not update a sourceId by number when the sourceId is invalid")
  public void updateRegisterSourceIdByNumber_invalid_sourceId() {
    uniqueGenKeyRepository.deleteAll().block();
    StepVerifier.create(registerService.updateRegisterSourceIdByNumber(new SourceIdUpdateDto(SOURCE_ID, GENERATED_NUMBER_PREFIX)))
        .expectErrorMessage(WS_005.getDescription())
        .verify();
  }

  @Test
  @DisplayName("Should not update a sourceId by number when the number is invalid")
  public void updateRegisterSourceIdByNumber_invalid_number() {
    StepVerifier.create(registerService.updateRegisterSourceIdByNumber(new SourceIdUpdateDto(SOURCE_ID, GENERATED_NUMBER_PREFIX)))
        .expectErrorMessage(WS_007.getDescription()).
        verify();
  }

  @Test
  @DisplayName("Should update a sourceId by number")
  public void updateRegisterSourceIdByNumber() {
    //given
    String number = GENERATED_NUMBER_PREFIX + "17325";
    var newSourceId = uniqueGenKeyRepository.save(new UniqueGenKey()).block().getId();
    registerRepository.save(newWsRegister().setGeneratedNumber(number)).block();

    StepVerifier.create(registerService.updateRegisterSourceIdByNumber(new SourceIdUpdateDto(newSourceId, number)))
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

    StepVerifier.create(registerService.findBySourceIdAndWebscolaireType(SOURCE_ID, STUDENT))
        .assertNext(register -> {
          assertEquals(SOURCE_ID, register.getSourceId());
          assertEquals(STUDENT, register.getWebscolaireType());
        })
        .expectComplete()
        .verify();
  }

  @Test
  @DisplayName("Should retrieve no WsRegister by sourceId and WebscolaireType")
  public void findBySourceIdAndWebscolaireType_not_found() {
    // Given
    registerRepository.save(newWsRegister()).block();

    StepVerifier.create(registerService.findBySourceIdAndWebscolaireType("SOURCE_ID_", STUDENT))
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
          assertEquals(STUDENT, register.getWebscolaireType());
        })
        .expectComplete()
        .verify();
  }

  @Test
  @DisplayName("should not update WsRegister when the number is not valid")
  public void updateRegister_invalidNumber() {
    // Given
    Register register = registerRepository.save(newWsRegister().setGeneratedNumber("number")).block();
    StepVerifier.create(registerService.updateRegister(new RegisterDto()
            .setSourceId(register.getSourceId())
            .setWebscolaireType(register.getWebscolaireType())
            .setGeneratedNumber(null)
            .setFirstname("ALY").setLastname("Kaba")
            .setDateOfBirth(DOB)
            .setSex(MALE)))
        .expectErrorMessage(WS_007.getDescription())
        .verify();
  }

  @Test
  @DisplayName("should not update WsRegister when the sourceId is valid")
  public void updateRegister_invalidSourceId() {
    // Given
    Register register = registerRepository.save(newWsRegister().setGeneratedNumber("number")).block();
    StepVerifier.create(registerService.updateRegister(new RegisterDto()
            .setSourceId("INVALID_SOURCE_ID")
            .setWebscolaireType(register.getWebscolaireType())
            .setGeneratedNumber(register.getGeneratedNumber())
            .setFirstname("ALY").setLastname("Kaba")
            .setDateOfBirth(DOB)
            .setSex(MALE)))
        .expectErrorMessage(RegisterError.WS_005.getDescription())
        .verify();
  }

  @Test
  @DisplayName("should not update WsRegister when the wsType mismatch")
  public void updateRegister_mismatchType() {
    // Given
    Register register = registerRepository.save(newWsRegister()).block();
    StepVerifier.create(registerService.updateRegister(new RegisterDto()
            .setSourceId(register.getSourceId())
            .setWebscolaireType(TEACHER)
            .setGeneratedNumber(register.getGeneratedNumber())
            .setFirstname("ALY").setLastname("Kaba")
            .setDateOfBirth(DOB)
            .setSex(MALE)))
        .expectErrorMessage(RegisterError.WS_001.getDescription()).verify();
  }


  @Test
  @DisplayName("should update WsRegister")
  public void updateRegister() {
    // Given
    Register wsRegister = registerRepository.save(newWsRegister()).block();

    StepVerifier.create(registerService.updateRegister(new RegisterDto()
            .setSourceId(wsRegister.getSourceId())
            .setWebscolaireType(STUDENT)
            .setGeneratedNumber(wsRegister.getGeneratedNumber())
            .setFirstname("ALY").setLastname("Kaba")
            .setDateOfBirth(DOB)
            .setSex(MALE)))
        .assertNext(register  -> {
          assertEquals("ALY", register.getFirstname());
          assertEquals("Kaba", register.getLastname());
          assertEquals(MALE, register.getSex());
          assertEquals(wsRegister.getSourceId(), register.getSourceId());
          assertEquals(wsRegister.getGeneratedNumber(), register.getGeneratedNumber());
        })
        .expectComplete()
        .verify();
  }

  public static Register newWsRegister() {
    return new Register()
        .setSourceId(SOURCE_ID)
        .setDateOfBirth(DOB)
        .setLastname("kaba")
        .setFirstname("N'faly".toUpperCase())
        .setSex(MALE)
        .setWebscolaireType(STUDENT);
  }

  public static RegisterDto newWsRegister(String sourceId, LocalDate dob, String firstName, Sex sex, WebscolaireType webscolaireType) {
    return new RegisterDto()
        .setSourceId(sourceId)
        .setDateOfBirth(dob)
        .setFirstname(firstName)
        .setSex(sex)
        .setWebscolaireType(webscolaireType);
  }

  static Stream<Arguments> saveRegisterInvalidInputProvider() {
    return Stream.of(
        arguments(null, DOB, FIRST_NAME, MALE, SCHOOL),
        arguments(SOURCE_ID, null, FIRST_NAME, MALE, SCHOOL),
        arguments(SOURCE_ID, DOB, null, MALE, SCHOOL),
        arguments(SOURCE_ID, DOB, FIRST_NAME, null, SCHOOL),
        arguments(SOURCE_ID, DOB, FIRST_NAME, MALE, null)
    );
  }
}
