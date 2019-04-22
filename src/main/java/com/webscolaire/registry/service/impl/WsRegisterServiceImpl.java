package com.webscolaire.registry.service.impl;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.webscolaire.registry.config.WsRegisterConstants;
import static com.webscolaire.registry.config.WsRegisterConstants.*;
import com.webscolaire.registry.domain.SexeType;
import com.webscolaire.registry.domain.WebscolaireType;
import com.webscolaire.registry.domain.WsRegister;
import com.webscolaire.registry.domain.dto.WsRegisterParam;
import com.webscolaire.registry.exception.WsRegisterError;
import com.webscolaire.registry.exception.WsRegisterException;
import com.webscolaire.registry.repository.WsRegisterRepository;
import com.webscolaire.registry.service.WsRegisterService;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.inject.Inject;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 *
 * Classe d'implementation des services du registre.
 *
 * @since 12 avr. 2019
 * @author KABA N'faly
 */
@Service
@Data
@NoArgsConstructor
@Transactional
@Slf4j
public class WsRegisterServiceImpl implements WsRegisterService {

    @Inject
    private WsRegisterRepository registerRepository;

    @Inject
    private MongoTemplate mongoTemplate;

    /**
     *
     * Enregistre une entité webScolaire {@link WebscolaireType} dans le
     * registre.
     *
     * @param registerParam
     * @return
     * @throws WsRegisterException
     */
    @Override
    public Mono<WsRegister> registerOne(WsRegisterParam registerParam) throws WsRegisterException {
        log.info("Request to register {}", registerParam);

        try {
            final String type = registerParam.getWebscolaireType();
            final String dateNaissance = registerParam.getDateNaissance();
            final String sexe = registerParam.getSexe();

            Preconditions.checkArgument(!Strings.isNullOrEmpty(type), WsRegisterError.WS_003.getCode());
            Preconditions.checkArgument(!Strings.isNullOrEmpty(dateNaissance), WsRegisterError.WS_004.getCode());
            // Contruction du type
            WebscolaireType webscolaireType = WebscolaireType.getTypeFromString(type);
            Preconditions.checkNotNull(webscolaireType, WsRegisterError.WS_001.getCode());

            // Vérification du format de la date de naissance
            Preconditions.checkState(isValidDateOfBirth(dateNaissance), WsRegisterError.WS_002.getCode());
            WsRegister register = findRegister(registerParam.getSourceId(), registerParam.getNom(), registerParam.getPrenom(),
                    registerParam.getDateNaissance(), webscolaireType, SexeType.valueOf(registerParam.getSexe()));

            if (null == findRegister(registerParam.getSourceId(), registerParam.getNom(), registerParam.getPrenom(),
                    registerParam.getDateNaissance(), webscolaireType, SexeType.valueOf(registerParam.getSexe()))) {
                long serialNumber = getNextSerialNumber(webscolaireType, dateNaissance);
                String numeroGenere = buildGeneratedNumber(webscolaireType, dateNaissance, serialNumber);
                register = new WsRegister()
                        .setWebscolaireType(webscolaireType)
                        .setDateNaissance(dateNaissance)
                        .setNom(registerParam.getNom().toUpperCase())
                        .setSexe(SexeType.valueOf(registerParam.getSexe()))
                        .setPrenom(registerParam.getPrenom().toUpperCase())
                        .setNumeroGenere(numeroGenere)
                        .setSourceId(registerParam.getSourceId());

                registerRepository.save(register).subscribe();
            } else {
                log.info("Retrieving existing data : {} ", register);
            }
            return Mono.just(register);

        } catch (IllegalArgumentException | IllegalStateException | NullPointerException ex) {
            throw new WsRegisterException(WsRegisterError.getErrorFromCodeStr(ex.getMessage()));
        }
    }

    /**
     * Enregistre plusieurs entités webScolaires {@link WebscolaireType} dans le
     * registre.
     *
     * @param registerParams
     * @return
     */
    @Override
    public Flux<WsRegister> registerMany(List<WsRegisterParam> registerParams) {
        log.info("Request to register {}", registerParams);
        Flux<WsRegister> registers = Flux.fromIterable(registerParams)
                .flatMap(r -> registerOne(r))
                .onErrorReturn(WsRegisterException.class, new WsRegister());
        registers.subscribe();
        return registers;
    }

    /**
     * Vérifie que la date fournie est correcte.
     *
     * @param dateOfBirh
     * @return boolean
     */
    private boolean isValidDateOfBirth(String dateOfBirh) {
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT_DD_MM_YYYY);
        formatter.setLenient(false);
        try {
            formatter.parse(dateOfBirh);
            return true;
        } catch (ParseException ex) {
            log.error(ExceptionUtils.getStackTrace(ex));
            return false;
        }
    }

    /**
     * Génère le prochain numéro
     *
     * @param webscolaireType
     * @param dateNaissance
     * @param sourceId
     * @return Long
     */
    private long getNextSerialNumber(WebscolaireType webscolaireType, String dateNaissance) {

        Query query = new Query();
        query.addCriteria(
                where(WEBSCOLAIRE_TYPE).is(webscolaireType)
                        .and(DATE_NAISSANCE).is(dateNaissance)
        );
        List<WsRegister> registers = mongoTemplate.find(query, WsRegister.class);

        return WsRegisterConstants.DEFAULT_NUMBER * (registers.size() + 1);
    }

    private WsRegister findRegister(String sourceId, String nom, String prenom, String dateNaissance,
            WebscolaireType webscolaireType, SexeType sexe) {

        Query query = new Query();
        query.addCriteria(
                where(WEBSCOLAIRE_TYPE).is(webscolaireType)
                        .and(SOURCE_ID).is(sourceId)
                        .and(NOM).is(nom.toUpperCase())
                        .and(PRENOM).is(prenom.toUpperCase())
                        .and(DATE_NAISSANCE).is(dateNaissance)
                        .and(SEXE).is(sexe)
        );
        List<WsRegister> registers = mongoTemplate.find(query, WsRegister.class);

        return registers.stream().findFirst().orElse(null);
    }

    /**
     *
     * Construit le numero webscolaire
     *
     * @param webscolaireType
     * @param dateOfBirth
     * @param serialNumber
     * @return String
     */
    private String buildGeneratedNumber(WebscolaireType webscolaireType, String dateOfBirth, long serialNumber) {
        return webscolaireType.getCode() + SEPARATOR + dateOfBirth + SEPARATOR + serialNumber;
    }
}
