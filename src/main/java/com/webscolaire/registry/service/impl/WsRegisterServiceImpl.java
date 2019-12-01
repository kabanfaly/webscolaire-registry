package com.webscolaire.registry.service.impl;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import static com.webscolaire.registry.config.WsRegisterConstants.*;
import com.webscolaire.registry.domain.SexType;
import com.webscolaire.registry.domain.WebscolaireType;
import com.webscolaire.registry.domain.WsRegister;
import com.webscolaire.registry.domain.dto.WsRegisterParam;
import com.webscolaire.registry.exception.WsRegisterError;
import com.webscolaire.registry.exception.WsRegisterException;
import com.webscolaire.registry.repository.WsRegisterRepository;
import com.webscolaire.registry.service.WsRegisterService;
import com.webscolaire.registry.utils.WsRegistryUtils;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.inject.Inject;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.apache.commons.lang.exception.ExceptionUtils;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import com.webscolaire.registry.repository.UniqueGenKeyRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;

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
@Log4j2
public class WsRegisterServiceImpl implements WsRegisterService {

  @Inject
  private WsRegisterRepository registerRepository;

  @Inject
  private UniqueGenKeyRepository uniqueGenKeyRepository;

  @Inject
  private ReactiveMongoTemplate reactiveMongoTemplate;

  /**
   *
   * Enregistre une entité webScolaire {@link WebscolaireType} dans le registre.
   *
   * @param registerParam Les paramètres d'entrée.
   * @return WsRegister
   * @throws WsRegisterException
   */
  @Override
  public Mono<WsRegister> registerOne(WsRegisterParam registerParam) {
    log.info("Registering one : {}", registerParam);

    try {
      // Validation 
      validateWsRegisterParam(registerParam);

      val webscolaireType = WebscolaireType.getTypeFromString(registerParam.getWebscolaireType());

      // Recherche d'une entité existante
      Mono<WsRegister> registerMono = findRegister(registerParam.getSourceId(), registerParam.getLastname(), registerParam.getFirstname(),
          registerParam.getDateOfBirth(), webscolaireType, SexType.valueOf(registerParam.getSex()));

      // Enregistre si aucune entité existante trouvée.
      return registerMono.hasElement().flatMap(it -> {
        if (it) {
          log.info("Retrieving existing data : {} ", registerMono);
          return registerMono;
        }
        val numeroGenere = buildGeneratedNumber(webscolaireType, registerParam.getDateOfBirth());
        return uniqueGenKeyRepository.findOneByUniqueKey(registerParam.getSourceId())
            .flatMap(uniqueGenKey
                -> {
              return registerRepository.save(new WsRegister()
                  .setWebscolaireType(webscolaireType)
                  .setDateOfBirth(registerParam.getDateOfBirth())
                  .setLastname(registerParam.getLastname().toUpperCase())
                  .setSex(SexType.valueOf(registerParam.getSex()))
                  .setFirstname(registerParam.getFirstname().toUpperCase())
                  .setGeneratedNumber(numeroGenere)
                  .setSourceId(uniqueGenKey.getUniqueKey()));

            })
            .switchIfEmpty(Mono.error(new WsRegisterException(WsRegisterError.WS_005)));
      });

    } catch (IllegalArgumentException | IllegalStateException | NullPointerException ex) {
      log.error(ExceptionUtils.getStackTrace(ex));
      WsRegisterError registerError = WsRegisterError.getErrorFromCodeStr(ex.getMessage());
      log.error("{} --- {}", registerError.toString(), registerParam);
      throw new WsRegisterException(registerError);
    }
  }

  /**
   * Modifie une entity
   *
   * @param registerParam Les paramètres d'entrée.
   * @return WsRegister
   */
  @Override
  public Mono<WsRegister> updateRegister(WsRegisterParam registerParam) {
    try {
      // Validation 
      validateWsRegisterParam(registerParam);
      Mono<WsRegister> registerMono = findRegisterByNumber(registerParam.getGeneratedNumber());
      return registerMono.flatMap(register -> {
        register.setFirstname(registerParam.getFirstname());
        register.setLastname(registerParam.getLastname());
        register.setDateOfBirth(registerParam.getDateOfBirth());
        register.setSex(SexType.valueOf(registerParam.getSex()));
        return registerRepository.save(register);
      }).switchIfEmpty(Mono.error(new WsRegisterException(WsRegisterError.WS_007)));

    } catch (IllegalArgumentException | IllegalStateException | NullPointerException ex) {
      log.error(ExceptionUtils.getStackTrace(ex));
      WsRegisterError registerError = WsRegisterError.getErrorFromCodeStr(ex.getMessage());
      log.error("{} --- {}", registerError.toString(), registerParam);
      throw new WsRegisterException(registerError);
    }
  }

  /**
   * Modifie le source id d'une entité register identifiée par son numéro
   * (generatedNumber)
   *
   * @param number le numéro d'identification
   * @param sourceId la sourceId
   * @return WsRegister
   */
  @Override
  public Mono<WsRegister> updateSourceIdByNumber(String number, String sourceId) {
    log.info("Request to update sourceId {} by number : {}", sourceId, number);

    return uniqueGenKeyRepository.findOneByUniqueKey(sourceId)
        .hasElement().flatMap(it -> {
          if (it) {
            return registerRepository.findOneByGeneratedNumber(number)
                .flatMap(register -> {
                  register.setSourceId(sourceId);
                  return registerRepository.save(register);
                }).switchIfEmpty(Mono.error(new WsRegisterException(WsRegisterError.WS_007)));
          }
          log.error("SourceId '{}' not found ", sourceId);
          return Mono.error(new WsRegisterException(WsRegisterError.WS_005));
        });
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
    log.info("Registering Many : {}", registerParams);
    Flux<WsRegister> registers = Flux.fromIterable(registerParams)
        .flatMap(r -> registerOne(r))
        .onErrorReturn(WsRegisterException.class, new WsRegister());
    return registers;
  }

  /**
   * Valide les attributs du paramètre WsRegisterParam
   *
   * @param registerParam
   */
  private void validateWsRegisterParam(WsRegisterParam registerParam) {

    // Validation de l'attribut webscolaireType
    Preconditions.checkNotNull(WebscolaireType.getTypeFromString(registerParam.getWebscolaireType()), WsRegisterError.WS_001.getCode());

    // Vérification du format de la date de naissance
    Preconditions.checkState(isValidDateOfBirth(registerParam.getDateOfBirth()), WsRegisterError.WS_002.getCode());

    // Validation du prénom
    Preconditions.checkState(!Strings.isNullOrEmpty(registerParam.getFirstname()), WsRegisterError.WS_006.getCode());

    if (registerParam.getWebscolaireType().equals(WebscolaireType.ETABLISSEMENT.name())) {
      registerParam.setSex(SexType.A.name());
    } else {
      // Validation du prénom
      Preconditions.checkState(!Strings.isNullOrEmpty(registerParam.getLastname()), WsRegisterError.WS_004.getCode());
    }
    // Validation de l'attribut 'sex'
    Preconditions.checkNotNull(SexType.getSexFromString(registerParam.getSex()), WsRegisterError.WS_003.getCode());
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
    } catch (ParseException | NullPointerException ex) {
      log.error(ExceptionUtils.getStackTrace(ex));
      return false;
    }
  }

  /**
   * Recherche une entité WsRegister
   *
   * @param sourceId
   * @param lastname
   * @param firstname
   * @param dateOfBirth
   * @param webscolaireType
   * @param sex
   * @return
   */
  private Mono<WsRegister> findRegister(String sourceId, String lastname, String firstname, String dateOfBirth,
      WebscolaireType webscolaireType, SexType sex) {

    var query = new Query();
    query.addCriteria(
        where(WEBSCOLAIRE_TYPE).is(webscolaireType)
            .and(SOURCE_ID).is(sourceId)
            .and(LASTNAME).is(lastname.toUpperCase())
            .and(FIRSTNAME).is(firstname.toUpperCase())
            .and(DATE_OF_BIRTH).is(dateOfBirth)
            .and(SEX).is(sex)
    );
    return reactiveMongoTemplate.findOne(query, WsRegister.class);
  }

  /**
   * Construit un numero unique webscolaire
   *
   * @param webscolaireType
   * @param dateOfBirth
   * @param serialNumber
   * @return String
   */
  private String buildGeneratedNumber(WebscolaireType webscolaireType, String dateOfBirth) {
    log.info("Building generated number.");

    // Génération d'un token
    var token = WsRegistryUtils.genToken(AUTHORIZED_NUMBERS, TOKEN_LENGTH);

    // Construction du numéro webscolaire unique
    return webscolaireType.getCode() + SEPARATOR + dateOfBirth + SEPARATOR + token;
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
   * Recherche des entités WsRegister par une même sourceId
   *
   * @param sourceId la sourceI de recherche
   * @return WsRegister
   */
  @Override
  public Flux<WsRegister> findRegisterBySourceId(String sourceId) {
    log.info("Request to find register by sourceId : {}", sourceId);
    return registerRepository.findBySourceId(sourceId);
  }

  @Override
  public Flux<WsRegister> findByDateOfBirth(String dateOfBirth) {
    log.info("Request to find register by dateOfBirth : {}", dateOfBirth);
    return registerRepository.findByDateOfBirth(dateOfBirth);
  }

  @Override
  public Flux<WsRegister> findBySourceIdAndWebscolaireType(String sourceId, WebscolaireType webscolaireType) {
    log.info("Request to find register by sourceId : {} and webscolaireType: {}", sourceId, webscolaireType);
    return registerRepository.findBySourceIdAndWebscolaireType(sourceId, webscolaireType);
  }

  @Override
  public Flux<WsRegister> findAllRegisters(Pageable page) {
    return registerRepository.findAll(page);
  }
}
