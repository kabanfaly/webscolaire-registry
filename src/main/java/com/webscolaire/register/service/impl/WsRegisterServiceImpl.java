package com.webscolaire.register.service.impl;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.webscolaire.register.config.WsRegisterConstants;
import static com.webscolaire.register.config.WsRegisterConstants.DATE_FORMAT_DD_MM_YYYY;
import static com.webscolaire.register.config.WsRegisterConstants.SEPARATOR;
import com.webscolaire.register.domain.WebscolaireType;
import com.webscolaire.register.domain.WsRegister;
import com.webscolaire.register.exception.WsRegisterError;
import com.webscolaire.register.exception.WsRegisterException;
import com.webscolaire.register.repository.WsRegisterRepository;
import com.webscolaire.register.service.WsRegisterService;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.inject.Inject;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
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

    /**
     * Enregistre un entité webScolaire dans le registre
     * @param type
     * @param dateOfBirth
     * @return
     * @throws WsRegisterException 
     */
    @Override
    public Mono<WsRegister> register(String type, String dateOfBirth) throws WsRegisterException {
        log.info("Request to register {}, {}", type, dateOfBirth);

        try {            
            Preconditions.checkArgument(!Strings.isNullOrEmpty(type), WsRegisterError.WS_003.getCode());
            Preconditions.checkArgument(!Strings.isNullOrEmpty(dateOfBirth), WsRegisterError.WS_004.getCode());
           
            // Contruction du type
            WebscolaireType webscolaireType = WebscolaireType.getTypeFromString(type);
            Preconditions.checkNotNull(webscolaireType, WsRegisterError.WS_001.getCode());

            // Vérification du format de la date de naissance
            Preconditions.checkState(isValidDateOfBirth(dateOfBirth), WsRegisterError.WS_002.getCode());
            
            long serialNumber = getNextSerialNumber(webscolaireType, dateOfBirth);
            String generatedNumber = buildGeneratedNumber(webscolaireType, dateOfBirth, serialNumber);
            WsRegister register = new WsRegister().setWebscolaireType(webscolaireType)
                    .setDateOfBirth(dateOfBirth)
                    .setSerialNumber(serialNumber)
                    .setGeneratedNumber(generatedNumber);

            return registerRepository.save(register);
        } catch (IllegalArgumentException | IllegalStateException | NullPointerException ex) {
            throw new WsRegisterException(WsRegisterError.getErrorFromCodeStr(ex.getMessage()));
        }
    }

    /**
     * Vérifie que la date fournie est correcte.
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
     * @param webscolaireType
     * @param dateOfBirth
     * @return Long
     */
    private long getNextSerialNumber(WebscolaireType webscolaireType, String dateOfBirth){
         Flux<WsRegister> registers = registerRepository.findByWebscolaireTypeAndDateOfBirth(webscolaireType, dateOfBirth);
         int count = registers.collectList().block().size() + 1;
         return WsRegisterConstants.DEFAULT_NUMBER + count;
    }
    
    /**
     * 
     * Construit le numero webscolaire
     * @param webscolaireType
     * @param dateOfBirth
     * @param serialNumber
     * @return String
     */
    private String buildGeneratedNumber(WebscolaireType webscolaireType, String dateOfBirth, long serialNumber){
        return webscolaireType.getCode() + SEPARATOR + dateOfBirth + SEPARATOR + serialNumber;
    }
}
