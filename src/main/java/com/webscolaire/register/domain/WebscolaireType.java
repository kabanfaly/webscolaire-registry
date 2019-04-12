package com.webscolaire.register.domain;

/**
 *
 * @since 11 avr. 2019
 * @author KABA N'faly
 */
public enum WebscolaireType {
    ETUDIANT_ELEVE,
    ENSEIGNANT_PROF,
    ECOLE_UNIVERSITE,
    PARENT;
    
    public static WebscolaireType typeFromString(String str){
      for(WebscolaireType type : WebscolaireType.values()){
        if(type.name().equals(str)){
          return type;
        }
      }
      return null;
    }
}
