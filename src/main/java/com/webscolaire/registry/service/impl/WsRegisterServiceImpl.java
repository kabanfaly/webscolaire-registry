package com.webscolaire.registry.service.impl;

import com.webscolaire.registry.domain.WebscolaireType;
import com.webscolaire.registry.domain.WsRegister;
import com.webscolaire.registry.domain.dto.WsRegisterDto;
import com.webscolaire.registry.mapper.WsRegisterMapper;
import com.webscolaire.registry.exception.WsRegisterError;
import com.webscolaire.registry.exception.WsRegisterException;
import com.webscolaire.registry.repository.UniqueGenKeyRepository;
import com.webscolaire.registry.repository.WsRegisterRepository;
import com.webscolaire.registry.service.WsRegisterService;
import com.webscolaire.registry.utils.WsRegistryUtils;
import com.webscolaire.registry.validator.GenericValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import static com.webscolaire.registry.config.WsRegisterConstants.AUTHORIZED_NUMBERS;
import static com.webscolaire.registry.config.WsRegisterConstants.DATE_FORMAT_DD_MM_YYYY;
import static com.webscolaire.registry.config.WsRegisterConstants.DATE_OF_BIRTH;
import static com.webscolaire.registry.config.WsRegisterConstants.FIRSTNAME;
import static com.webscolaire.registry.config.WsRegisterConstants.LASTNAME;
import static com.webscolaire.registry.config.WsRegisterConstants.SEPARATOR;
import static com.webscolaire.registry.config.WsRegisterConstants.SEX;
import static com.webscolaire.registry.config.WsRegisterConstants.SOURCE_ID;
import static com.webscolaire.registry.config.WsRegisterConstants.TOKEN_LENGTH;
import static com.webscolaire.registry.config.WsRegisterConstants.WEBSCOLAIRE_TYPE;
import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 *
 * Classe d'implementation des services du registre.
 *
 * @since 12 avr. 2019
 * @author KABA N'faly
 */
public class WsRegisterServiceImpl implements WsRegisterService {

    private final Logger log = LoggerFactory.getLogger(WsRegisterServiceImpl.class);

    private final WsRegisterRepository registerRepository;
    private final UniqueGenKeyRepository uniqueGenKeyRepository;
    private final ReactiveMongoTemplate reactiveMongoTemplate;
    private final WsRegisterMapper wsRegisterMapper;
    private final GenericValidator validator;

    public WsRegisterServiceImpl(WsRegisterRepository registerRepository, UniqueGenKeyRepository uniqueGenKeyRepository,
                                 ReactiveMongoTemplate reactiveMongoTemplate, WsRegisterMapper wsRegisterMapper, GenericValidator validator) {
        this.registerRepository = registerRepository;
        this.uniqueGenKeyRepository = uniqueGenKeyRepository;
        this.reactiveMongoTemplate = reactiveMongoTemplate;
        this.wsRegisterMapper = wsRegisterMapper;
        this.validator = validator;
    }

    /**
     * Save a {@link WsRegister}.
     *
     * @param registerDto The entity DTO to save
     * @return The saved {@link WsRegister}
     */
    @Override
    public Mono<WsRegister> saveRegister(WsRegisterDto registerDto) {
        log.info("Saving register : {}", registerDto);
        return Mono.just(registerDto)
            .map(validator::validate)
            .flatMap(this::findRegister) // return existing
            .switchIfEmpty( // save new
                uniqueGenKeyRepository.findOneByUniqueKey(registerDto.getSourceId())
                    .map(uniqueGenKey -> registerDto
                        .setSourceId(uniqueGenKey.getUniqueKey())
                        .setGeneratedNumber(buildGeneratedNumber(registerDto.getWebscolaireType(), registerDto.getDateOfBirth())))
                    .map(wsRegisterMapper)
                    .flatMap(registerRepository::save)
                    .switchIfEmpty(Mono.error(new WsRegisterException(WsRegisterError.WS_005)))
            );
    }

    /**
     * Updates a WsRegister. The following attributes will be updated:
     *  - firstname
     *  - lastname
     *  - sex
     * @param registerDto the entity {@link WsRegisterDto} to update
     * @return The updated {@link WsRegister}
     */
    @Override
    public Mono<WsRegister> updateRegister(WsRegisterDto registerDto) {
        log.info("Updating register : {}", registerDto);
        return Mono.just(registerDto)
            .map(validator::validate)
            .flatMap(regDto -> findRegisterByNumber(regDto.getGeneratedNumber()))
            .flatMap(register -> {
                register.setFirstname(registerDto.getFirstname());
                register.setLastname(registerDto.getLastname());
                register.setSex(registerDto.getSex());
                return registerRepository.save(register);
            }).switchIfEmpty(Mono.error(new WsRegisterException(WsRegisterError.WS_007)));
    }

    /**
     * Update a {@link WsRegister} entity sourceId by generatedNumber.
     *
     * @param number The entity number
     * @param newSourceId The new sourceId
     * @return The updated {@link WsRegister}
     */
    @Override
    public Mono<WsRegister> updateRegisterSourceIdByNumber(String number, String newSourceId) {
        log.info("Request to update sourceId {} by number : {}", newSourceId, number);
        return uniqueGenKeyRepository.findOneByUniqueKey(newSourceId)
            .hasElement().flatMap(it -> {
                if (it) {
                    return registerRepository.findOneByGeneratedNumber(number)
                        .flatMap(register -> {
                            register.setSourceId(newSourceId);
                            return registerRepository.save(register);
                        }).switchIfEmpty(Mono.error(new WsRegisterException(WsRegisterError.WS_007)));
                }
                log.error("SourceId '{}' not found ", newSourceId);
                return Mono.error(new WsRegisterException(WsRegisterError.WS_005));
            });
    }

    /**
     * Save many {@link WsRegister}.
     *
     * @param registerDtos The entities to save
     * @return a {@link List} of {@link WsRegister}.
     */
    @Override
    public Flux<WsRegister> saveRegisters(List<WsRegisterDto> registerDtos) {
        log.info("Registering Many : {}", registerDtos);
        return Flux.fromIterable(registerDtos)
            .flatMap(this::saveRegister);
    }

    /**
     * Find a {@link WsRegister}.
     *
     * @return found {@link WsRegister}
     */
    private Mono<WsRegister> findRegister(WsRegisterDto wsRegisterDto) {
        var query = new Query();
        query.addCriteria(
            where(WEBSCOLAIRE_TYPE)
                .is(wsRegisterDto.getWebscolaireType())
                .and(SOURCE_ID).is(wsRegisterDto.getSourceId())
                .and(LASTNAME).is(wsRegisterDto.getLastname().toUpperCase(Locale.FRANCE))
                .and(FIRSTNAME).is(wsRegisterDto.getFirstname().toUpperCase(Locale.FRANCE))
                .and(DATE_OF_BIRTH).is(wsRegisterDto.getDateOfBirth())
                .and(SEX).is(wsRegisterDto.getSex())
        );
        return reactiveMongoTemplate.findOne(query, WsRegister.class);
    }

    /**
     * Build a unique number based on webscolaireType and date of birth.
     *
     * @param webscolaireType The webscolaire type
     * @param dateOfBirth The date of birth
     * @return String
     */
    private String buildGeneratedNumber(WebscolaireType webscolaireType, LocalDate dateOfBirth) {
        log.info("Building generated number.");

        var formatter = DateTimeFormatter.ofPattern(DATE_FORMAT_DD_MM_YYYY);
        // Generate the token
        var token = WsRegistryUtils.genToken(AUTHORIZED_NUMBERS, TOKEN_LENGTH);

        // Build a unique number
        return webscolaireType.getCode() + SEPARATOR + formatter.format(dateOfBirth).replaceAll("[-/ ]", "") + SEPARATOR + token;
    }

    /**
     * Recherche une entité WsRegister par son numéro
     *
     * @param number le numéro de cherche
     * @return WsRegister
     */
    @Override
    public Mono<WsRegister> findRegisterByNumber(String number) {
        log.info("Request to find register by number : {}", number);
        return registerRepository.findOneByGeneratedNumber(number);
    }

    /**
     * Fetch all {@link WsRegister} matching a sourceId.
     *
     * @param sourceId The sourceId
     * @return found {@link WsRegister}
     */
    @Override
    public Flux<WsRegister> findRegisterBySourceId(String sourceId) {
        log.info("Request to find register by sourceId : {}", sourceId);
        return registerRepository.findBySourceId(sourceId);
    }

    @Override
    public Flux<WsRegister> findByDateOfBirth(String dateOfBirth) {
        log.info("Request to find register by dateOfBirth : {}", dateOfBirth);
        return registerRepository.findByDateOfBirth(LocalDate.parse(dateOfBirth, DateTimeFormatter.ofPattern(DATE_FORMAT_DD_MM_YYYY)));
    }

    @Override
    public Flux<WsRegister> findBySourceIdAndWebscolaireType(String sourceId, WebscolaireType webscolaireType) {
        log.info("Request to find register by sourceId : {} and webscolaireType: {}", sourceId, webscolaireType);
        return registerRepository.findBySourceIdAndWebscolaireType(sourceId, webscolaireType);
    }

    @Override
    public Flux<WsRegister> findAllRegisters(Pageable page) {
        log.info("Request to find register par pagination");
        return registerRepository.findAll(page);
    }
}
