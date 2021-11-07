package com.webscolaire.registry.domain;

/**
 *
 * @since 11 avr. 2019
 * @author KABA N'faly
 */
public enum WebscolaireType {

    STUDENT("EE"), // ELEVE_ETUDIANT
    TEACHER("EP"), // ENSEIGNANT_PROF
    SCHOOL("ET"), // ETABLISSEMENT
    PARENT("PE"); // PARENT_ELEVE

    private final String code;

    WebscolaireType(String code) {
        this.code = code;
    }

    public static WebscolaireType getTypeFromString(String str) {
        for (WebscolaireType type : WebscolaireType.values()) {
            if (type.name().equals(str)) {
                return type;
            }
        }
        return null;
    }

    public String getCode() {
        return code;
    }
}
