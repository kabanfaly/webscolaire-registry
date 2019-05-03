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
import org.springframework.data.mongodb.core.MongoTemplate;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import com.webscolaire.registry.repository.UniqueGenKeyRepository;

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
  private MongoTemplate mongoTemplate;

  /**
   *
   * Enregistre une entité webScolaire {@link WebscolaireType} dans le registre.
   *
   * @param registerParam Les paramètre d'entrée
   * @return WsRegister
   * @throws WsRegisterException
   */
  @Override
  public Mono<WsRegister> registerOne(WsRegisterParam registerParam) {
    log.info("Request to register one : {}", registerParam);

    try {
      // Validation 
      validateWsRegisterParam(registerParam);

      val webscolaireType = WebscolaireType.getTypeFromString(registerParam.getWebscolaireType());

      // Recherche d'une entité existante
      var register = findRegister(registerParam.getSourceId(), registerParam.getLastname(), registerParam.getFirstname(),
          registerParam.getDateOfBirth(), webscolaireType, SexType.valueOf(registerParam.getSex()));

      // Enregistre si aucune entité existante trouvée.
      if (register == null) {
        val numeroGenere = buildGeneratedNumber(webscolaireType, registerParam.getDateOfBirth());

        val registerMono = uniqueGenKeyRepository.findOneByUniqueKey(registerParam.getSourceId())
            .flatMap(uniqueGenKey -> 
            {
              return registerRepository.save(new WsRegister()
                  .setWebscolaireType(webscolaireType)
                  .setDateOfBirth(registerParam.getDateOfBirth())
                  .setLastname(registerParam.getLastname().toUpperCase())
                  .setSex(SexType.valueOf(registerParam.getSex()))
                  .setFirstname(registerParam.getFirstname().toUpperCase())
                  .setGeneratedNumber(numeroGenere)
                  .setUniqueGenKey(uniqueGenKey)
                  .setSourceId(registerParam.getSourceId()));

            })
            .switchIfEmpty(Mono.error(new WsRegisterException(WsRegisterError.WS_005)));
        
        return registerMono;
      } else {
        log.info("Retrieving existing data : {} ", register);
      }
      return Mono.just(register);

    } catch (IllegalArgumentException | IllegalStateException | NullPointerException ex) {
      log.error(ExceptionUtils.getStackTrace(ex));
      WsRegisterError registerError = WsRegisterError.getErrorFromCodeStr(ex.getMessage());
      log.error("{} --- {}", registerError.toString(), registerParam);
      throw new WsRegisterException(registerError);
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
    log.info("Request to register Many : {}", registerParams);
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

    // Validation de l'attribut 'sex'
    Preconditions.checkNotNull(SexType.getSexFromString(registerParam.getSex()), WsRegisterError.WS_003.getCode());

    // Validation de l'attribut 'lastname'
    Preconditions.checkArgument(!Strings.isNullOrEmpty(registerParam.getLastname()), WsRegisterError.WS_004.getCode());

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
  private WsRegister findRegister(String sourceId, String lastname, String firstname, String dateOfBirth,
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
    return mongoTemplate.findOne(query, WsRegister.class);
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

    // Construction du numéro webscolaire initial
    var generatedNumber = webscolaireType.getCode() + SEPARATOR + dateOfBirth + SEPARATOR + token;

    var isUnique = false;
    // Tant que le numéro webscolaire construit n'est pas unique en base on n'en contruit de nouveau
    while (!isUnique) {
      Query query = new Query();
      query.addCriteria(
          where(GENERATED_NUMBER).is(generatedNumber)
      );

      // Le numéro construit est unique
      if (mongoTemplate.findOne(query, WsRegister.class) == null) {
        isUnique = true;
        continue;
      }
      log.info("Building new generated number.");
      // construction d'un nouveau numéro webscolaire
      token = WsRegistryUtils.genToken(AUTHORIZED_NUMBERS, TOKEN_LENGTH);
      generatedNumber = webscolaireType.getCode() + SEPARATOR + dateOfBirth + SEPARATOR + token;
    }

    return generatedNumber;
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
}
