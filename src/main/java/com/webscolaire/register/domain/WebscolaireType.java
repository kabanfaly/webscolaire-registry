package com.webscolaire.register.domain;

import lombok.Getter;

/**
 *
 * @since 11 avr. 2019
 * @author KABA N'faly
 */
public enum WebscolaireType {
    ETUDIANT_ELEVE("EE"),
    ENSEIGNANT_PROF("EP"),
    ECOLE_UNIVERSITE("EU"),
    PARENT_ELEVE("PE");
    
    @Getter
    private final String code;
    
    private WebscolaireType(String code){
        this.code = code;
    }
    
    public static WebscolaireType getTypeFromString(String str){
      for(WebscolaireType type : WebscolaireType.values()){
        if(type.name().equals(str)){
          return type;
        }
      }
      return null;
    }
}
