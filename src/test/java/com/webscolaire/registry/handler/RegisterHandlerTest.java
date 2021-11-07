package com.webscolaire.registry.handler;

import com.webscolaire.registry.config.RegisterProperties;
import com.webscolaire.registry.domain.Register;
import com.webscolaire.registry.domain.Sex;
import com.webscolaire.registry.domain.UniqueGenKey;
import com.webscolaire.registry.domain.dto.RegisterDto;
import com.webscolaire.registry.domain.dto.SourceIdUpdateDto;
import com.webscolaire.registry.mapper.RegisterMapper;
import com.webscolaire.registry.repository.RegisterRepository;
import com.webscolaire.registry.repository.UniqueGenKeyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

import static com.webscolaire.registry.TestUtils.MONGO_IMAGE;
import static com.webscolaire.registry.domain.WebscolaireType.STUDENT;
import static com.webscolaire.registry.exception.RegisterError.WS_007;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.client.ExchangeFilterFunctions.basicAuthentication;

@Testcontainers
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class RegisterHandlerTest {

  @LocalServerPort
  private int port;

  @Container
  private static final MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse(MONGO_IMAGE));

  private static String SOURCE_ID;
  private static final LocalDate DOB = LocalDate.of(1999, 10, 11);

  private WebTestClient webTestClient;

  @Autowired
  private RegisterProperties properties;

  @Autowired
  private RegisterRepository registerRepository;

  @Autowired
  private UniqueGenKeyRepository uniqueGenKeyRepository;

  @Autowired
  private RegisterMapper registerMapper;

  @BeforeEach
  public void setUp() {
    webTestClient = WebTestClient.bindToServer()
        .baseUrl("http://localhost:" + port)
        .filter(basicAuthentication(properties.username, properties.password))
        .defaultHeader("WS-TOKEN", "token")
        .build();
    registerRepository.deleteAll().block();
    uniqueGenKeyRepository.deleteAll().block();
    SOURCE_ID = uniqueGenKeyRepository.save(new UniqueGenKey()).block().getId();
  }

  @DynamicPropertySource
  static void configure(DynamicPropertyRegistry registry) {
    registry.add("MONGO_URI", mongoDBContainer::getReplicaSetUrl);
  }

  public RegisterDto createWsRegisterDto() {
    return new RegisterDto().setSourceId(SOURCE_ID)
        .setWebscolaireType(STUDENT).setDateOfBirth(DOB)
        .setFirstname("N'faly").setLastname("KABA").setSex(Sex.MALE);
  }

  @DisplayName("Should save a register with a 200 OK")
  @Test
  public void saveRegister() {
    var register = createWsRegisterDto();
    webTestClient.post()
        .uri("/register")
        .contentType(APPLICATION_JSON)
        .accept(APPLICATION_JSON)
        .body(Mono.just(register), RegisterDto.class)
        .exchange()
        .expectStatus().isOk()
        .expectBody()
        .jsonPath("$.generatedNumber").isNotEmpty()
        .jsonPath("$.webscolaireType").isEqualTo(register.getWebscolaireType().name())
        .jsonPath("$.firstname").isEqualTo(register.getFirstname())
        .jsonPath("$.lastname").isEqualTo(register.getLastname())
        .jsonPath("$.sex").isEqualTo(register.getSex().name())
        .jsonPath("$.dateOfBirth").isEqualTo(register.getDateOfBirth().toString());
  }

  @DisplayName("Should return a bad request on saveRegister")
  @Test
  public void saveRegister_badRequest() {
    var register = createWsRegisterDto();
    register.setSourceId(null);
    webTestClient.post()
        .uri("/register")
        .contentType(APPLICATION_JSON)
        .accept(APPLICATION_JSON)
        .body(Mono.just(register), RegisterDto.class)
        .exchange()
        .expectStatus().isBadRequest();
  }

  @DisplayName("Should save a registers with a 200 OK")
  @Test
  public void saveRegisters() {
    var register1 = createWsRegisterDto().setFirstname("John");
    var register2 = createWsRegisterDto();
    webTestClient.post()
        .uri("/registers")
        .contentType(APPLICATION_JSON)
        .accept(APPLICATION_JSON)
        .body(Flux.just(register1, register2), RegisterDto.class)
        .exchange()
        .expectStatus().isOk()
        .expectBody()
        .jsonPath("$.length()").isEqualTo(2)
        .jsonPath("$[0].id").isNotEmpty()
        .jsonPath("$[0].generatedNumber").isNotEmpty()
        .jsonPath("$[0].webscolaireType").isEqualTo(register1.getWebscolaireType().name())
        .jsonPath("$[0].firstname").isEqualTo(register1.getFirstname())
        .jsonPath("$[0].lastname").isEqualTo(register1.getLastname())
        .jsonPath("$[0].sex").isEqualTo(register1.getSex().name())
        .jsonPath("$[0].dateOfBirth").isEqualTo(register1.getDateOfBirth().toString())
        .jsonPath("$[1].id").isNotEmpty()
        .jsonPath("$[1].generatedNumber").isNotEmpty()
        .jsonPath("$[1].webscolaireType").isEqualTo(register2.getWebscolaireType().name())
        .jsonPath("$[1].firstname").isEqualTo(register2.getFirstname())
        .jsonPath("$[1].lastname").isEqualTo(register2.getLastname())
        .jsonPath("$[1].sex").isEqualTo(register2.getSex().name())
        .jsonPath("$[1].dateOfBirth").isEqualTo(register2.getDateOfBirth().toString());
  }

  @DisplayName("Should update a register with a 200 OK")
  @Test
  public void updateRegister() {
    var registerDto = createWsRegisterDto().setGeneratedNumber("number");
    registerRepository.save(registerMapper.toEntity(registerDto)).block();

    var registerDtoUpdated = registerDto.setFirstname("John").setLastname("Doe");
    webTestClient.put()
        .uri("/register")
        .contentType(APPLICATION_JSON)
        .accept(APPLICATION_JSON)
        .body(Mono.just(registerDtoUpdated), RegisterDto.class)
        .exchange()
        .expectStatus().isOk()
        .expectBody()
        .jsonPath("$.generatedNumber").isEqualTo(registerDto.getGeneratedNumber())
        .jsonPath("$.webscolaireType").isEqualTo(registerDto.getWebscolaireType().name())
        .jsonPath("$.firstname").isEqualTo(registerDtoUpdated.getFirstname())
        .jsonPath("$.lastname").isEqualTo(registerDtoUpdated.getLastname())
        .jsonPath("$.sex").isEqualTo(registerDtoUpdated.getSex().name())
        .jsonPath("$.dateOfBirth").isEqualTo(registerDtoUpdated.getDateOfBirth().toString());
  }

  @DisplayName("Should update a register with a 400 Bad Request")
  @Test
  public void updateRegister_badRequest() {
    var registerDto = createWsRegisterDto().setGeneratedNumber("number");
    registerRepository.save(registerMapper.toEntity(registerDto)).block();

    var registerDtoUpdated = registerDto.setFirstname("John").setLastname("Doe")
        .setGeneratedNumber(null);
    webTestClient.put()
        .uri("/register")
        .contentType(APPLICATION_JSON)
        .accept(APPLICATION_JSON)
        .body(Mono.just(registerDtoUpdated), RegisterDto.class)
        .exchange()
        .expectStatus().isBadRequest()
        .expectBody()
        .jsonPath("$.error.title").isEqualTo(WS_007.name())
        .jsonPath("$.error.detail").isEqualTo(WS_007.name())
        .jsonPath("$.error.statusCode").isEqualTo(400);
  }

  @DisplayName("Should find all registers")
  @Test
  public void findAll() {
    var registerDto = createWsRegisterDto().setGeneratedNumber("number");
    registerRepository.save(registerMapper.toEntity(registerDto)).block();

    webTestClient.get()
        .uri("/registers")
        .accept(APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk()
        .expectBody()
        .jsonPath("$.length()").isEqualTo(1)
        .jsonPath("$[0].generatedNumber").isEqualTo(registerDto.getGeneratedNumber())
        .jsonPath("$[0].webscolaireType").isEqualTo(registerDto.getWebscolaireType().name())
        .jsonPath("$[0].firstname").isEqualTo(registerDto.getFirstname())
        .jsonPath("$[0].lastname").isEqualTo(registerDto.getLastname())
        .jsonPath("$[0].sex").isEqualTo(registerDto.getSex().name())
        .jsonPath("$[0].dateOfBirth").isEqualTo(registerDto.getDateOfBirth().toString());
  }

  @DisplayName("Should find by number with 200 OK")
  @Test
  public void findRegisterByNumber_OK() {
    String number = "AAAABBBB";
    registerRepository.save(new Register()
        .setSourceId(SOURCE_ID)
        .setWebscolaireType(STUDENT)
        .setGeneratedNumber(number)).block();

    webTestClient.get()
        .uri("/number/" + number)
        .accept(APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk()
        .expectBody(RegisterDto.class)
        .value(wsRegister -> {
          assertEquals(number, wsRegister.getGeneratedNumber());
          assertEquals(SOURCE_ID, wsRegister.getSourceId());
        });
  }

  @DisplayName("Should find by number with 404 NOT_FOUND")
  @Test
  public void findRegisterByNumber_notFound() {

    webTestClient.get()
        .uri("/number/AAABBB")
        .accept(APPLICATION_JSON)
        .exchange()
        .expectStatus().isNotFound();
  }

  @DisplayName("Should find register by date of birth with 200 OK")
  @Test
  public void findRegisterByDateOfBirth_OK() {
    String number = "AAAABBBB";
    LocalDate dob = LocalDate.now();
    var register = registerRepository.save(
        registerMapper.toEntity(createWsRegisterDto().setDateOfBirth(dob).setGeneratedNumber(number))
    ).block();

    webTestClient.get()
        .uri("/dob/" + dob)
        .accept(APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk()
        .expectBody()
        .jsonPath("$.length()").isEqualTo(1)
        .jsonPath("$[0].generatedNumber").isEqualTo(register.getGeneratedNumber())
        .jsonPath("$[0].webscolaireType").isEqualTo(register.getWebscolaireType().name())
        .jsonPath("$[0].firstname").isEqualTo(register.getFirstname())
        .jsonPath("$[0].lastname").isEqualTo(register.getLastname())
        .jsonPath("$[0].sex").isEqualTo(register.getSex().name())
        .jsonPath("$[0].dateOfBirth").isEqualTo(register.getDateOfBirth().toString());
  }

  @DisplayName("Should find register date of birth with 404 NOT_FOUND")
  @Test
  public void findRegisterByDateOfBirth_notFound() {
    LocalDate dob = LocalDate.now();
    webTestClient.get()
        .uri("/dob/" + dob)
        .accept(APPLICATION_JSON)
        .exchange()
        .expectStatus().isNotFound();
  }

  @DisplayName("Should find register date of birth with 400 Bad Request")
  @Test
  public void findRegisterByDateOfBirth_badRequest() {
    webTestClient.get()
        .uri("/dob/INVALID_DATE")
        .accept(APPLICATION_JSON)
        .exchange()
        .expectStatus().isBadRequest();
  }

  @DisplayName("Should find register by number with 200 OK")
  @Test
  public void findRegisterBySourceId_OK() {
    var register = registerRepository.save(
        registerMapper.toEntity(createWsRegisterDto().setGeneratedNumber("number"))
    ).block();

    webTestClient.get()
        .uri("/sourceId/" + SOURCE_ID)
        .accept(APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk()
        .expectBody()
        .jsonPath("$.length()").isEqualTo(1)
        .jsonPath("$[0].generatedNumber").isEqualTo(register.getGeneratedNumber())
        .jsonPath("$[0].webscolaireType").isEqualTo(register.getWebscolaireType().name())
        .jsonPath("$[0].firstname").isEqualTo(register.getFirstname())
        .jsonPath("$[0].lastname").isEqualTo(register.getLastname())
        .jsonPath("$[0].sex").isEqualTo(register.getSex().name())
        .jsonPath("$[0].dateOfBirth").isEqualTo(register.getDateOfBirth().toString());
  }


  @DisplayName("Should find by number with 404 NOT_FOUND")
  @Test
  public void findRegisterBySourceId_notFound() {

    webTestClient.get()
        .uri("/sourceId/AAABBB")
        .accept(APPLICATION_JSON)
        .exchange()
        .expectStatus().isNotFound();
  }

  @DisplayName("Should find register by sourceId and type with 200 OK")
  @Test
  public void findRegisterBySourceIdAndWebscolaireType_OK() {
    var register = registerRepository.save(
        registerMapper.toEntity(createWsRegisterDto().setGeneratedNumber("number"))
    ).block();

    webTestClient.get()
        .uri("/sourceId/" + register.getSourceId() + "/type/" + register.getWebscolaireType())
        .accept(APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk()
        .expectBody()
        .jsonPath("$.length()").isEqualTo(1)
        .jsonPath("$[0].generatedNumber").isEqualTo(register.getGeneratedNumber())
        .jsonPath("$[0].webscolaireType").isEqualTo(register.getWebscolaireType().name())
        .jsonPath("$[0].firstname").isEqualTo(register.getFirstname())
        .jsonPath("$[0].lastname").isEqualTo(register.getLastname())
        .jsonPath("$[0].sex").isEqualTo(register.getSex().name())
        .jsonPath("$[0].dateOfBirth").isEqualTo(register.getDateOfBirth().toString());
  }

  @DisplayName("Should find register by sourceId and type with with 404 NOT_FOUND")
  @Test
  public void findRegisterBySourceIdAndWebscolaireType_notFound() {

    webTestClient.get()
        .uri("/sourceId/SOURCE_ID/type/ELEVE_ETUDIANT")
        .accept(APPLICATION_JSON)
        .exchange()
        .expectStatus().isNotFound();
  }

  @DisplayName("Should update register's sourceId by number with 200 OK")
  @Test
  public void updateSourceIdByNumber() {
    var register = registerRepository.save(
        registerMapper.toEntity(createWsRegisterDto().setGeneratedNumber("number"))
    ).block();
    var newSourceID = uniqueGenKeyRepository.save(new UniqueGenKey()).block().getId();

    webTestClient.put()
        .uri("/updateSourceId")
        .contentType(APPLICATION_JSON)
        .accept(APPLICATION_JSON)
        .body(Mono.just(new SourceIdUpdateDto(newSourceID, register.getGeneratedNumber())), SourceIdUpdateDto.class)
        .exchange()
        .expectStatus().isOk()
        .expectBody()
        .jsonPath("$.sourceId").isEqualTo(newSourceID)
        .jsonPath("$.generatedNumber").isEqualTo(register.getGeneratedNumber())
        .jsonPath("$.webscolaireType").isEqualTo(register.getWebscolaireType().name())
        .jsonPath("$.firstname").isEqualTo(register.getFirstname())
        .jsonPath("$.lastname").isEqualTo(register.getLastname())
        .jsonPath("$.sex").isEqualTo(register.getSex().name())
        .jsonPath("$.dateOfBirth").isEqualTo(register.getDateOfBirth().toString());
  }

}
