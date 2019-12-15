package com.webscolaire.registry.service.impl;

import com.webscolaire.registry.config.WsRegisterProperties;
import com.webscolaire.registry.domain.SexType;
import com.webscolaire.registry.domain.UniqueGenKey;
import com.webscolaire.registry.domain.WebscolaireType;
import com.webscolaire.registry.domain.dto.WsRegisterParam;
import com.webscolaire.registry.repository.UniqueGenKeyRepository;
import com.webscolaire.registry.repository.WsRegisterRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import javax.inject.Inject;
import java.util.UUID;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.springSecurity;
import static org.springframework.web.reactive.function.client.ExchangeFilterFunctions.basicAuthentication;

/**
 * @author KABA N'faly
 * @since 15/12/2019 00:32
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Import(WsRegisterServiceIT.TestConfig.class)
public class WsRegisterServiceIT {

  private static final String SOURCE_ID = UUID.randomUUID().toString();
  private static final String DOB = "20121999";

  @Inject
  private WebTestClient webTestClient;

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

  public WsRegisterParam createWsRegisterParam() {
    return new WsRegisterParam().setSourceId(SOURCE_ID)
        .setWebscolaireType(WebscolaireType.ELEVE_ETUDIANT.name()).setDateOfBirth(DOB)
        .setFirstname("N'faly").setLastname("KABA").setSex(SexType.H.name());
  }

  @Test
  public void testRegisterOneWith200() {
    webTestClient.post()
        .uri("/register")
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .body(Mono.just(createWsRegisterParam()), WsRegisterParam.class)
        .exchange()
        .expectStatus().isOk()
        .expectBody()
        .jsonPath("$.generatedNumber").isNotEmpty()
        .jsonPath("$.webscolaireType").isNotEmpty()
        .jsonPath("$.webscolaireType").isEqualTo(WebscolaireType.ELEVE_ETUDIANT.name());
  }

  @TestConfiguration
  public static class TestConfig {

    @Bean
    public WebTestClient webClient(WsRegisterProperties properties, ApplicationContext context) {
      return WebTestClient.bindToApplicationContext(context)
          .apply(springSecurity())
          .configureClient()
          .filter(basicAuthentication(properties.getUsername(), properties.getPassword()))
          .build();
    }
  }
}
