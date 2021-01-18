package com.webscolaire.registry.service.impl;

import com.webscolaire.registry.domain.UniqueGenKey;
import com.webscolaire.registry.repository.UniqueGenKeyRepository;
import com.webscolaire.registry.service.UniqueGenKeyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Testcontainers
class UniqueGenKeyServiceImplTest {

    @Container
    final static MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:4.0.10"));

    @Autowired
    private UniqueGenKeyService uniqueGenKeyService;

    @Autowired
    private UniqueGenKeyRepository uniqueKeyRepository;

    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry) {
        registry.add("MONGO_URI", mongoDBContainer::getReplicaSetUrl);
    }

    @BeforeEach
    public void setUp() {
        uniqueKeyRepository.deleteAll().subscribe();
    }

    @DisplayName("Should generate and save uniqueKey")
    @Test
    void generateUniqueKey() {
        StepVerifier.create(uniqueGenKeyService.generateUniqueKey())
                .assertNext(uniqueGenKey -> assertNotNull(uniqueGenKey.getUniqueKey()))
                .verifyComplete();
    }

    @DisplayName("Should find uniqueKey")
    @Test
    void findByUniqueKeyOK() {
        var uniqueKey = UUID.randomUUID().toString().replace("-", "");
        uniqueKeyRepository.save(new UniqueGenKey().setUniqueKey(uniqueKey)).subscribe();
        StepVerifier.create(uniqueGenKeyService.findByUniqueKey(uniqueKey))
                .assertNext(uniqueGenKey -> assertEquals(uniqueKey, uniqueGenKey.getUniqueKey()))
                .verifyComplete();
    }

    @DisplayName("Should not find uniqueKey")
    @Test
    void findByUniqueKeyKO() {
        StepVerifier.create(uniqueGenKeyService.findByUniqueKey("AAAAAA"))
                .expectNextCount(0)
                .verifyComplete();
    }
}
