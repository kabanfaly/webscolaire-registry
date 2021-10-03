package com.webscolaire.registry.service.impl;

import com.webscolaire.registry.domain.Register;
import com.webscolaire.registry.domain.WebscolaireType;
import com.webscolaire.registry.domain.dto.RegisterDto;
import com.webscolaire.registry.domain.dto.SourceIdUpdateDto;
import com.webscolaire.registry.mapper.RegisterMapper;
import com.webscolaire.registry.exception.RegisterError;
import com.webscolaire.registry.exception.RegisterException;
import com.webscolaire.registry.repository.UniqueGenKeyRepository;
import com.webscolaire.registry.repository.RegisterRepository;
import com.webscolaire.registry.service.RegisterService;
import com.webscolaire.registry.utils.WsRegistryUtils;
import com.webscolaire.registry.validator.GenericValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import static com.webscolaire.registry.config.Constants.AUTHORIZED_NUMBERS;
import static com.webscolaire.registry.config.Constants.DATE_FORMAT_DD_MM_YYYY;
import static com.webscolaire.registry.config.Constants.DATE_OF_BIRTH;
import static com.webscolaire.registry.config.Constants.FIRSTNAME;
import static com.webscolaire.registry.config.Constants.LASTNAME;
import static com.webscolaire.registry.config.Constants.SEPARATOR;
import static com.webscolaire.registry.config.Constants.SEX;
import static com.webscolaire.registry.config.Constants.SOURCE_ID;
import static com.webscolaire.registry.config.Constants.TOKEN_LENGTH;
import static com.webscolaire.registry.config.Constants.WEBSCOLAIRE_TYPE;
import static com.webscolaire.registry.domain.WebscolaireType.ETABLISSEMENT;
import static com.webscolaire.registry.exception.RegisterError.WS_007;
import static java.util.Objects.nonNull;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.util.StringUtils.hasLength;
import static reactor.core.publisher.Mono.error;

/**
 * WsRegisterService implementation class
 *
 * @since 12 avr. 2019
 * @author KABA N'faly
 */
public class RegisterServiceImpl implements RegisterService {

    private final Logger log = LoggerFactory.getLogger(RegisterServiceImpl.class);

    private final RegisterRepository registerRepository;
    private final UniqueGenKeyRepository uniqueGenKeyRepository;
    private final ReactiveMongoTemplate reactiveMongoTemplate;
    private final RegisterMapper registerMapper;
    private final GenericValidator validator;

    public RegisterServiceImpl(RegisterRepository registerRepository, UniqueGenKeyRepository uniqueGenKeyRepository,
                               ReactiveMongoTemplate reactiveMongoTemplate, RegisterMapper registerMapper, GenericValidator validator) {
        this.registerRepository = registerRepository;
        this.uniqueGenKeyRepository = uniqueGenKeyRepository;
        this.reactiveMongoTemplate = reactiveMongoTemplate;
        this.registerMapper = registerMapper;
        this.validator = validator;
    }

    /**
     * Save a {@link Register}.
     *
     * @param registerDto The entity DTO to save
     * @return The saved {@link RegisterDto}
     */
    @Override
    public Mono<RegisterDto> saveRegister(RegisterDto registerDto) {
        log.info("Saving register : {}", registerDto);
        return Mono.just(this.validate(registerDto))
            .flatMap(this::findRegister) // return existing
            .switchIfEmpty( // save new
                uniqueGenKeyRepository.findById(registerDto.getSourceId())
                    .map(uniqueGenKey -> registerDto
                        .setSourceId(uniqueGenKey.getId())
                        .setGeneratedNumber(buildGeneratedNumber(registerDto.getWebscolaireType(), registerDto.getDateOfBirth())))
                    .map(registerMapper::toEntity)
                    .flatMap(registerRepository::save)
                    .switchIfEmpty(error(new RegisterException(RegisterError.WS_005)))
            )
            .map(registerMapper::toDto);
    }

    private RegisterDto validate(RegisterDto registerDto) {
        if (nonNull(registerDto.getWebscolaireType())
            && registerDto.getWebscolaireType() != ETABLISSEMENT
            && !hasLength(registerDto.getLastname())) {
            throw new RegisterException(RegisterError.WS_004);
        }
        return validator.validate(registerDto);
    }

    /**
     * Updates a WsRegister. The following attributes will be updated:
     *  - firstname
     *  - lastname
     *  - sex
     * @param registerDto the entity {@link RegisterDto} to update
     * @return The updated {@link RegisterDto}
     */
    @Override
    public Mono<RegisterDto> updateRegister(RegisterDto registerDto) {
        log.info("Updating register : {}", registerDto);
        return Mono.just(registerDto)
            .map(this::validate)
            .flatMap(regDto -> registerRepository.findOneByGeneratedNumber(regDto.getGeneratedNumber()))
            .map(regDto -> {
                // sourceId should not change
                if (!regDto.getSourceId().equals(registerDto.getSourceId())) {
                    log.error("Invalid input 'sourceId'. Different value found for corresponding number");
                    throw new RegisterException(RegisterError.WS_005);
                }
                if (!regDto.getWebscolaireType().equals(registerDto.getWebscolaireType())) {
                    log.error("Invalid input 'WebscolaireType'. Different value found for corresponding number");
                    throw new RegisterException(RegisterError.WS_001);
                }
                // update only necessary fields
                regDto.setFirstname(registerDto.getFirstname());
                regDto.setLastname(registerDto.getLastname());
                regDto.setDateOfBirth(registerDto.getDateOfBirth());
                regDto.setSex(registerDto.getSex());
                return regDto;
            })
            .flatMap(registerRepository::save)
            .map(registerMapper::toDto)
            .switchIfEmpty(error(new RegisterException(RegisterError.WS_007)));
    }

    /**
     * Update a {@link Register} entity sourceId by generatedNumber.
     *
     * @param sourceIdUpdateDto The input
     * @return The updated {@link RegisterDto}
     */
    @Override
    public Mono<RegisterDto> updateRegisterSourceIdByNumber(SourceIdUpdateDto sourceIdUpdateDto) {
        log.info("Request to update sourceId : {}", sourceIdUpdateDto);
        return uniqueGenKeyRepository.findById(sourceIdUpdateDto.sourceId)
            .hasElement().flatMap(it -> {
                if (it) {
                    return registerRepository.findOneByGeneratedNumber(sourceIdUpdateDto.number)
                        .flatMap(register -> {
                            register.setSourceId(sourceIdUpdateDto.sourceId);
                            return registerRepository.save(register);
                        }).switchIfEmpty(error(new RegisterException(WS_007)));
                }
                log.error("SourceId '{}' not found ", sourceIdUpdateDto.sourceId);
                return error(new RegisterException(RegisterError.WS_005));
            })
            .map(registerMapper::toDto);
    }

    /**
     * Save many {@link Register}.
     *
     * @param registerDtos The entities to save
     * @return a {@link List} of {@link Register}.
     */
    @Override
    public Flux<RegisterDto> saveRegisters(List<RegisterDto> registerDtos) {
        log.info("Registering Many : {}", registerDtos);
        return Flux.fromIterable(registerDtos)
            .flatMap(this::saveRegister);
    }

    /**
     * Find a {@link Register}.
     *
     * @return found {@link Register}
     */
    private Mono<Register> findRegister(RegisterDto registerDto) {
        var query = new Query();
        Criteria criteria = where(WEBSCOLAIRE_TYPE)
            .is(registerDto.getWebscolaireType())
            .and(SOURCE_ID).is(registerDto.getSourceId())
            .and(FIRSTNAME).regex(registerDto.getFirstname(), "i")
            .and(DATE_OF_BIRTH).is(registerDto.getDateOfBirth())
            .and(SEX).is(registerDto.getSex());
        if (ETABLISSEMENT != registerDto.getWebscolaireType()) {
            criteria = criteria.and(LASTNAME).regex(registerDto.getLastname(), "i");
        }
        query.addCriteria(criteria);
        return reactiveMongoTemplate.findOne(query, Register.class);
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
    public Mono<RegisterDto> findRegisterByNumber(String number) {
        log.info("Request to find register by number : {}", number);
        return registerRepository.findOneByGeneratedNumber(number)
            .map(registerMapper::toDto);
    }

    /**
     * Fetch all {@link Register} matching a sourceId.
     *
     * @param sourceId The sourceId
     * @return found {@link Register}
     */
    @Override
    public Flux<RegisterDto> findRegisterBySourceId(String sourceId) {
        log.info("Request to find register by sourceId : {}", sourceId);
        return registerRepository.findBySourceId(sourceId)
            .map(registerMapper::toDto);
    }

    @Override
    public Flux<RegisterDto> findByDateOfBirth(String dateOfBirth) {
        log.info("Request to find register by dateOfBirth : {}", dateOfBirth);
        try {
        LocalDate date = LocalDate.parse(dateOfBirth, DateTimeFormatter.ofPattern(DATE_FORMAT_DD_MM_YYYY));
            return registerRepository.findByDateOfBirth(date)
                .map(registerMapper::toDto);
        } catch (DateTimeParseException exception) {
            return Flux.error(new RegisterException(RegisterError.WS_002));
        }
    }

    @Override
    public Flux<RegisterDto> findBySourceIdAndWebscolaireType(String sourceId, WebscolaireType webscolaireType) {
        log.info("Request to find register by sourceId : {} and webscolaireType: {}", sourceId, webscolaireType);
        return registerRepository.findBySourceIdAndWebscolaireType(sourceId, webscolaireType)
            .map(registerMapper::toDto);
    }

    @Override
    public Flux<RegisterDto> findAllRegisters(Pageable page) {
        log.info("Request to find register par pagination");
        return registerRepository.findAll(page)
            .map(registerMapper::toDto);
    }
}
